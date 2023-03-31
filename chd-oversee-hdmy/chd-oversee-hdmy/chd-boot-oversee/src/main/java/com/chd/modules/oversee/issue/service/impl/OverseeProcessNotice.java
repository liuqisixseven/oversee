package com.chd.modules.oversee.issue.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.base.BaseMap;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.modules.redis.listener.ChdRedisListener;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.JsonUtils;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.mapper.*;
import com.chd.modules.oversee.issue.service.*;
import com.chd.modules.oversee.issue.util.SynchronizationProcessDataUtils;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import com.google.common.collect.Maps;
import com.mchange.lang.IntegerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OverseeProcessNotice implements ChdRedisListener {

    @Autowired
    private OverseeIssueMapper overseeIssueMapper;
    @Autowired
    private IOverseeIssueService overseeIssueService;
    @Autowired
    private IOverseeIssueFlowOrgService overseeIssueFlowOrgService;
    @Autowired
    private OverseeIssueSpecificMapper overseeIssueSpecificMapper;
    @Autowired
    SpecificIssuesMapper specificIssuesMapper;

    @Autowired
    IAccountabilityHandlingService accountabilityHandlingService;

    @Autowired
    private IOverseeProcessFormDataService overseeProcessFormDataService;

    @Autowired
    private IImproveRegulationsService improveRegulationsService;

    @Autowired
    private IRecoverFundsService recoverFundsService;

    @Autowired
    private IRectifyViolationsService rectifyViolationsService;

    @Autowired
    private IReasonCancellationService reasonCancellationService;

    @Autowired
    private IIssuesSupervisorService issuesSupervisorService;

    @Autowired
    private IOverseeIssueTodoService overseeIssueTodoService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IMyUserService myUserService;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    private void printLog(BaseMap message){
        try {
//            log.info("====接收到流程消息通知:{}", JsonUtils.toJsonStr(message));
        }catch (Exception ex){
          log.error("打印日志异常",ex);

        }
    }

    @Override
    public void onMessage(BaseMap message) {

        String msgType= MapUtils.getString(message,WorkflowConstants.NOTICE_PARAM_KEY.MESSAGE_TYPE);
        String bizId=MapUtils.getString(message,WorkflowConstants.NOTICE_PARAM_KEY.BIZ_ID);
        String time=MapUtils.getString(message,WorkflowConstants.NOTICE_PARAM_KEY.TIME);
        if(StringUtils.isNotBlank(msgType) && StringUtils.isNotBlank(bizId)){
            if(WorkflowConstants.NOTICE_MESSAGE_TYPE.TASK_FORM_DATA.equals(msgType)){//提交表单数据通知
                onTaskFormData(bizId,message);
                printLog(message);
            }else if(WorkflowConstants.NOTICE_MESSAGE_TYPE.NEXT_TASK_LIST.equals(msgType)){
                //下一任务集合通知
                onNextTaskList(bizId,message);
            }else if(WorkflowConstants.NOTICE_MESSAGE_TYPE.PROCESS_COMPLETED.equals(msgType)){//流程完成通知
                onProcessCompleted(bizId,time,OverseeConstants.SubmitState.Complete,(((Map<String,Object>) message.get(WorkflowConstants.NOTICE_PARAM_KEY.DATA))));
            }
        }
    }

    private void onTaskFormData(String bizId,BaseMap message){
        try {
            if (StringUtils.isNotBlank(bizId)) {
                WorkflowTaskVo task = message.get(WorkflowConstants.NOTICE_PARAM_KEY.TASK);
                WorkflowUserVo user = message.get(WorkflowConstants.NOTICE_PARAM_KEY.USER);
                WorkflowTaskFormVo formData = message.get(WorkflowConstants.NOTICE_PARAM_KEY.DATA);

                String taskKey = task.getTaskKey();
                String taskName = task.getTaskName();

                Long issueId = Long.valueOf(bizId);
                OverseeIssue overseeIssue = overseeIssueMapper.selectById(issueId);
                if (overseeIssue == null) {
                    return;
                }

//                处理待办推送
                overseeIssueTodoService.updateSendStatusByTaskId(task.getTaskId(),user.getId());

                overseeProcessFormDataService.receiveTaskFormData(overseeIssue,user,formData,task);
                String formId = formData.getFormId();

                if (WorkflowConstants.TASK_FORMID.PIN_NUMBER.equals(formId)) {
                    //判断是否需要转为日常管理
                    Map<String, Object> form = formData.getForm();
                    Integer submitState = (Integer) form.get("submitState");
                    if(null!=submitState&&OverseeConstants.SubmitState.DAILY_MANAGEMENT==submitState.intValue()){
                        //转为日常管理
                        onProcessCompleted(bizId,null,OverseeConstants.SubmitState.DAILY_MANAGEMENT);
                    }
                    //发起销号 录入数据库数据  应该只在提交时才录入到数据库 并确保数据事务的原子性
                    handleCancelANumberData(formData,overseeIssue);
                }else if (WorkflowConstants.TASK_FORMID.ISSUE_APPROVED.equals(formId)||"Activity_01idu85".equals(taskKey)||"Activity_10q5o0h".equals(taskKey)||"第一阶段：牵头部门分管领导审批".equals(taskName)||"第一阶段：本部牵头部门分管领导审批".equals(taskName)) {
                    // 问题审核通过
                    OverseeIssue overseeIssueU = new OverseeIssue();
                    overseeIssueU.setId(Long.valueOf(bizId));
                    overseeIssueU.setUpdateUserId(user.getId());
                    overseeIssueU.setSubmitState(OverseeConstants.SubmitState.Submit);
                    overseeIssueService.addOrUpdate(overseeIssueU);
                }else if (WorkflowConstants.TASK_FORMID.DISTRIBUTION_SUPERVISION_DEPARTMENT.equals(formId)) {
                    OverseeIssue overseeIssueU = new OverseeIssue();
                    overseeIssueU.setId(overseeIssue.getId());
                    Map<String, Object> form = formData.getForm();
                    if(null!=form.get("severity")){
                        overseeIssueU.setSeverity(Integer.parseInt(form.get("severity").toString()));
                    }
                    if(null!=form.get("isSupervise")){
                        overseeIssueU.setIsSupervise(Integer.parseInt(form.get("isSupervise").toString()));
                    }

                    overseeIssueU.setSupervisorOrgIds((String) form.get("supervisorOrgIds"));
                    overseeIssueU.setUpdateTime(new Date());
                    overseeIssueU.setUpdateUserId(null!=user&&StringUtils.isNotBlank(user.getId())?user.getId():"admin");
                    overseeIssueService.addOrUpdate(overseeIssueU);
//                    overseeIssueService.redisCacheOverseeIssue(overseeIssue.getId());
                }else if (WorkflowConstants.TASK_FORMID.PROBLEM_STATUS_MODI_FICATION.equals(formId)) {
                    Map<String, Object> form = formData.getForm();
                    Integer submitState = (Integer) form.get("submitState");
                    if(null!=submitState&&OverseeConstants.SubmitState.DAILY_MANAGEMENT==submitState.intValue()){
                        //转为日常管理
                        onProcessCompleted(bizId,null,OverseeConstants.SubmitState.DAILY_MANAGEMENT);
                    }
                }if (WorkflowConstants.TASK_FORMID.FINISH_ISSUE_PROCESS.equals(formId)) {
                    //结束流程
                    onProcessCompleted(bizId,null,OverseeConstants.SubmitState.NoRectification);
                }

//              同步督办部门责任分配部门数据
                SynchronizationProcessDataUtils.synchronization(overseeIssue.getId());
            }
        }catch (Exception ex){
            log.error("接收问题审批表单异常:",ex);
        }
    }

    private void onNextTaskList(String bizId,BaseMap message){
        if(StringUtils.isNotEmpty(bizId)){
            long issueId = Long.parseLong(bizId);
            if(null!=message){
                Map<String, Object> map = message.get(WorkflowConstants.NOTICE_PARAM_KEY.DATA);
                if(null!=map){
                    List<WorkflowUserTaskVo> userTasks = (List<WorkflowUserTaskVo>) map.get(WorkflowConstants.USER_TASKS_KEY);
                    List<OverseeIssueTodo> overseeIssueTodoList = new ArrayList<>();
                    if(null!=userTasks&&userTasks.size()>0){
                        OverseeIssueTodo overseeIssueTodo = new OverseeIssueTodo();
                        overseeIssueTodo.setIssueId(issueId);
                        overseeIssueTodo.setSendStatus(-1);
                        for (WorkflowUserTaskVo userTask : userTasks) {
                            overseeIssueTodo.setTaskId(userTask.getTaskId());
                            if (CollectionUtils.isNotEmpty(userTask.getUsers())) {
                                for (WorkflowUserVo user : userTask.getUsers()) {
                                    overseeIssueTodo.setUserId(user.getId());
                                }
                            }
                            if (CollectionUtils.isNotEmpty(userTask.getDeparts())) {
                                for (WorkflowDepart depart : userTask.getDeparts()) {
                                    if (CollectionUtils.isNotEmpty(depart.getUsers())) {
                                        for (WorkflowUserVo user : depart.getUsers()) {
                                            overseeIssueTodo.setUserId(user.getId());
                                            overseeIssueTodo.setDepartId(depart.getDepartId());
                                        }
                                    }
                                }
                            }

                            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(userTask.getTaskId()).singleResult();
                            Map<String, Object> taskLocalVariables = historicTaskInstance.getTaskLocalVariables();
                            if(null!=taskLocalVariables){
                                overseeIssueTodo.setPreviousUserIds((String) taskLocalVariables.get(WorkflowConstants.ExecutionVariableLocalKey.PREVIOUS_TASK_USER_IDS));
                            }

                            if(StringUtils.isNotEmpty(overseeIssueTodo.getUserId())){
                                overseeIssueTodo.setUpdateUserId(overseeIssueTodo.getUserId());
                                overseeIssueTodoList.add(overseeIssueTodo);
                            }


                        }
                    }
                    if(null!=overseeIssueTodoList&&overseeIssueTodoList.size()>0){
                        for(OverseeIssueTodo overseeIssueTodo :overseeIssueTodoList){
                            if(StringUtils.isNotEmpty(overseeIssueTodo.getTaskId())&&null!=overseeIssueTodo.getSendStatus()){
                                if(overseeIssueTodoService.count(Wrappers.<OverseeIssueTodo>lambdaQuery().eq(OverseeIssueTodo::getTaskId,overseeIssueTodo.getTaskId()).eq(OverseeIssueTodo::getSendStatus,overseeIssueTodo.getSendStatus()).eq(OverseeIssueTodo::getDataType,1))<=0){
                                    overseeIssueTodoService.addOrUpdate(overseeIssueTodo);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private void handleCancelANumberData(WorkflowTaskFormVo workflowTaskFormVo,OverseeIssue overseeIssue){
        Map<String, Object> formData = workflowTaskFormVo.getForm();
        if(null!=formData){
            String departId = (String) formData.get(WorkflowConstants.DEPART_ID_KEY);
            String updateUserId = (String) formData.get(WorkflowConstants.UPDATE_USERID_KEY);
            String taskId = workflowTaskFormVo.getTaskId();
            if(StringUtils.isNotBlank(departId)&&StringUtils.isNotBlank(updateUserId)&&StringUtils.isNotBlank(taskId)){

                if(StringUtils.isEmpty(departId)){
                    MyUser myUserById = myUserService.getById(updateUserId);
                    if(null!=myUserById&& org.apache.commons.lang3.StringUtils.isNotEmpty(myUserById.getOrgId())){
                        departId = myUserById.getOrgId();
                    }
                }

                if(StringUtils.isNotEmpty(departId)){
                    reasonCancellationService.update(null, new LambdaUpdateWrapper<ReasonCancellation>().eq(ReasonCancellation::getIssueId,overseeIssue.getId()).eq(ReasonCancellation::getOrgId,departId).eq(ReasonCancellation::getDataType,1).set(ReasonCancellation::getDataType,-1));
                    accountabilityHandlingService.update(null, new LambdaUpdateWrapper<AccountabilityHandling>().eq(AccountabilityHandling::getIssueId,overseeIssue.getId()).eq(AccountabilityHandling::getOrgId,departId).eq(AccountabilityHandling::getDataType,1).set(AccountabilityHandling::getDataType,-1));
                    improveRegulationsService.update(null, new LambdaUpdateWrapper<ImproveRegulations>().eq(ImproveRegulations::getIssueId,overseeIssue.getId()).eq(ImproveRegulations::getOrgId,departId).eq(ImproveRegulations::getDataType,1).set(ImproveRegulations::getDataType,-1));
                    rectifyViolationsService.update(null, new LambdaUpdateWrapper<RectifyViolations>().eq(RectifyViolations::getIssueId,overseeIssue.getId()).eq(RectifyViolations::getOrgId,departId).eq(RectifyViolations::getDataType,1).set(RectifyViolations::getDataType,-1));
                    recoverFundsService.update(null, new LambdaUpdateWrapper<RecoverFunds>().eq(RecoverFunds::getIssueId,overseeIssue.getId()).eq(RecoverFunds::getOrgId,departId).eq(RecoverFunds::getDataType,1).set(RecoverFunds::getDataType,-1));
                }


                String reason = (String) formData.get("text");
                ReasonCancellation reasonCancellation = new ReasonCancellation();
                reasonCancellation.setIssueId(overseeIssue.getId());
                reasonCancellation.setOrgId(departId);
                reasonCancellation.setTaskId(taskId);
                reasonCancellation.setReason(reason);
                reasonCancellation.setUpdateUserId(updateUserId);
                reasonCancellationService.addOrUpdate(reasonCancellation);


                List<Map<String, Object>> accountabilityHandlingList = (List<Map<String, Object>>) formData.get(WorkflowConstants.PIN_NUMBER_LIST_ITEM_NAME.ACCOUNTABILITY_HANDLING_LIST);
                if(null!=accountabilityHandlingList&&accountabilityHandlingList.size()>0){
                    List<AccountabilityHandling> accountabilityHandlings = JSONArray.parseArray(JSONObject.toJSONString(accountabilityHandlingList), AccountabilityHandling.class);
                    if(null!=accountabilityHandlings&&accountabilityHandlings.size()>0){
                        accountabilityHandlingService.addOrUpdateList(accountabilityHandlings,overseeIssue.getId(),departId,updateUserId,taskId);
                    }
                }

                List<Map<String, Object>> rectifyViolationsList = (List<Map<String, Object>>) formData.get(WorkflowConstants.PIN_NUMBER_LIST_ITEM_NAME.IMPROVE_REGULATIONS_LIST);
                if(null!=rectifyViolationsList&&rectifyViolationsList.size()>0){
                    List<ImproveRegulations> rectifyViolationss = JSONArray.parseArray(JSONObject.toJSONString(rectifyViolationsList), ImproveRegulations.class);
                    if(null!=rectifyViolationss&&rectifyViolationss.size()>0){
                        improveRegulationsService.addOrUpdateList(rectifyViolationss,overseeIssue.getId(),departId,updateUserId,taskId);
                    }
                }

                List<Map<String, Object>> improveRegulationsList = (List<Map<String, Object>>) formData.get(WorkflowConstants.PIN_NUMBER_LIST_ITEM_NAME.RECTIFY_VIOLATIONS_LIST);
                if(null!=improveRegulationsList&&improveRegulationsList.size()>0){
                    List<RectifyViolations> improveRegulationss = JSONArray.parseArray(JSONObject.toJSONString(improveRegulationsList), RectifyViolations.class);
                    if(null!=improveRegulationss&&improveRegulationss.size()>0){
                        rectifyViolationsService.addOrUpdateList(improveRegulationss,overseeIssue.getId(),departId,updateUserId,taskId);
                    }
                }

                List<Map<String, Object>> recoverFundsList = (List<Map<String, Object>>) formData.get(WorkflowConstants.PIN_NUMBER_LIST_ITEM_NAME.RECOVER_FUNDS_LIST);
                if(null!=recoverFundsList&&recoverFundsList.size()>0){
                    List<RecoverFunds> recoverFundss = JSONArray.parseArray(JSONObject.toJSONString(recoverFundsList), RecoverFunds.class);
                    if(null!=recoverFundss&&recoverFundss.size()>0){
                        recoverFundsService.addOrUpdateList(recoverFundss,overseeIssue.getId(),departId,updateUserId,taskId);
                    }
                }

            }
        }
    }



    private void onProcessCompleted(String bizId,String time,Integer submitState){
        onProcessCompleted(bizId,time,submitState,null);
    }

    /**
     * 流程完成处理
     * @param bizId
     * @param time
     */
    private void onProcessCompleted(String bizId,String time,Integer submitState,Map<String,Object> dataMap){
        String processCategoryIssues = BaseConstant.PROCESS_CATEGORY_ISSUES;
        if(null!=dataMap){
            if(null!=dataMap.get(BaseConstant.PROCESS_CATEGORY_ISSUES_KEY)&&StringUtils.isNotBlank((String) dataMap.get(BaseConstant.PROCESS_CATEGORY_ISSUES_KEY))){
                processCategoryIssues = (String) dataMap.get(BaseConstant.PROCESS_CATEGORY_ISSUES_KEY);
            }
        }
        if(BaseConstant.PROCESS_CATEGORY_ISSUES_LIST.contains(processCategoryIssues)){
            if(StringUtils.isNotBlank(bizId)&&null!=submitState){
                String redisKey = "oversee::issue::update::id" + bizId;
                if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
                    try{
                        OverseeIssue overseeIssue= overseeIssueMapper.selectById(Long.valueOf(bizId));
                        if(overseeIssue!=null){
                            if(null!=overseeIssue.getSubmitState()&&overseeIssue.getSubmitState().intValue()!=OverseeConstants.SubmitState.NoRectification){
                                if(StringUtils.isNotBlank(time)){
                                    overseeIssue.setCompletedTime(DateUtils.parseDate(time, DateUtils.FORMAT_DATETIME));
                                }
                                overseeIssue.setSubmitState(submitState);
                                overseeIssue.setUpdateTime(new Date());
                                overseeIssueService.addOrUpdate(overseeIssue);
                            }
                        }
                    }finally {
                        redisTemplate.delete(redisKey);
                    }
                }
            }
        }else if(BaseConstant.PROCESS_INITIATE_SUPERVISION.equals(processCategoryIssues)){
            Integer issuesSupervisorId = (Integer) dataMap.get(BaseConstant.ISSUES_SUPERVISOR_ID_KEY);
            if(null!=issuesSupervisorId&&issuesSupervisorId.intValue()>0){
                IssuesSupervisor issuesSupervisor = new IssuesSupervisor();
                issuesSupervisor.setId(issuesSupervisorId);
                issuesSupervisor.setShowType(1);
                issuesSupervisorService.updateById(issuesSupervisor);
            }

        }

    }
}
