package com.chd.modules.workflow;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.CommonAPI;
import com.chd.common.constant.CommonConstant;
import com.chd.common.constant.CommonSendStatus;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.constant.SymbolConstant;
import com.chd.common.exception.BizException;
import com.chd.common.system.vo.DictModel;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.system.vo.SysDepartModel;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.JsonUtils;
import com.chd.common.util.StringUtil;
import com.chd.common.util.UUIDGenerator;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueDetailVo;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.system.entity.SysAnnouncement;
import com.chd.modules.system.service.ISysAnnouncementService;
import com.chd.modules.system.util.XssUtils;
import com.chd.modules.workflow.entity.WorkflowComment;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
import com.chd.modules.workflow.vo.*;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkflowTaskServiceImpl implements WorkflowTaskService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private WorkflowUserService workflowUserService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private WorkflowProcessMapper workflowProcessMapper;
    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;
    @Autowired
    private WorkflowVariablesService workflowVariablesService;
    @Autowired
    private WorkflowCommentService workflowCommentService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private WorkflowModelService workflowModelService;
    @Autowired
    private WorkflowMessageService workflowMessageService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private WorkflowProcessDefinitionService workflowProcessDefinitionService;
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Lazy
    protected CommonAPI commonAPI;
    @Autowired
    private ISysAnnouncementService sysAnnouncementService;
    @Autowired
    private IOverseeIssueService overseeIssueService;
    @Autowired
    private WorkflowProcessUsersSerivce workflowProcessUsersSerivce;
    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Override
    public IPage<WorkflowTaskVo> todoTaskList(WorkflowQueryVo query, String userId, Map<String, Object> map) {
        List<String> userGroups = workflowProcessDepartService.userDepartVariables(userId);
        Page<WorkflowTaskVo> result = new Page<>(query.getCurrent(), query.getSize());
        if(query.isExportAll()){
            result = new Page<>(1L, 10000000000L);
        }
        if(null!=map&&null!=query){
            map.put("idArray",query.getIdArray());
        }
        WorkflowTaskVo queryTask = new WorkflowTaskVo();
        queryTask.setTitle(query.getTitle());
        queryTask.setAssignee(userId);
        queryTask.setCandidateGroup(userGroups);
//        map.put("title",query.getTitle());
//        map.put("assignee",userId);
//        map.put("candidateGroup",userGroups);
        IPage<WorkflowTaskVo> records = workflowProcessMapper.todoListByMap(result, queryTask, map);
        if (records.getTotal() > 0) {
            for (WorkflowTaskVo task : records.getRecords()) {

                if (StringUtils.isNotBlank(task.getTaskKey())) {
                    String processStage = (String) taskService.getVariableLocal(task.getTaskId(), BaseConstant.PROCESS_STAGE_KEY);
                    if (null == processStage) {
                        processStage = "";
                        List<ExtensionAttribute> extensionAttribute = WorkflowFlowElementUtils.getExtensionAttribute(task.getTaskKey(), task.getProcessDefKey(), BaseConstant.PROCESS_STAGE_KEY);
                        if (null != extensionAttribute && extensionAttribute.size() > 0) {
                            processStage = extensionAttribute.get(0).getValue();
                        }
                        taskService.setVariableLocal(task.getTaskId(), BaseConstant.PROCESS_STAGE_KEY, processStage);
                    }
                    task.setProcessStage(processStage);
                }

                if (null == task.getMyTaskVariables()) {
                    task.setMyTaskVariables(Maps.newHashMap());
                }

                //            督办环节查询绑定责任部门ids
                task.getMyTaskVariables().put(WorkflowConstants.SUPERVISOR_DATA_KEY, workflowService.getSupervisorDataByTask(taskService.createTaskQuery().taskId(task.getTaskId()).singleResult(), Long.parseLong(task.getBizId())));


                Map<String, Object> overseeIssueMap = Maps.newHashMap();
                Map<String, Object> overseeIssueRedisMap = (Map<String, Object>) redisTemplate.opsForValue().get(BaseConstant.OVERSEE_ISSUE_MAP_ID_DATA_PREFIX + task.getBizId());
                if (null == overseeIssueRedisMap) {
                    //缓存没有，去DB查一把
                    OverseeIssue overseeIssue = overseeIssueMapper.selectById(Long.parseLong(task.getBizId()));
                    if (StringUtil.isNotEmpty(overseeIssue.getResponsibleUnitOrgId())) {
                        SysDepartModel sysDepartById = commonAPI.getSysDepartById(overseeIssue.getResponsibleUnitOrgId());
                        if (null != sysDepartById && StringUtil.isNotEmpty(sysDepartById.getDepartName())) {
                            overseeIssue.setResponsibleUnitOrgName(sysDepartById.getDepartName());
                        }
                    }
                    overseeIssueRedisMap = JSONObject.parseObject(JSONObject.toJSONString(overseeIssue)).getInnerMap();
                }
                overseeIssueMap.put("source", overseeIssueRedisMap.get("source"));
                overseeIssueMap.put("responsibleUnitOrgName", overseeIssueRedisMap.get("responsibleUnitOrgName"));
                overseeIssueMap.put("reportTime", overseeIssueRedisMap.get("reportTime"));
                overseeIssueMap.put("checkTime", overseeIssueRedisMap.get("checkTime"));
                task.setOverseeIssueMap(overseeIssueMap);
                if (StringUtils.isNotEmpty(task.getPreviousTaskDataSrc())) {
                    String[] array = task.getPreviousTaskDataSrc().split(",");
                    if (null != array && array.length > 0) {
                        task.setPreviousTaskUserNames(array[0]);
                        if (array.length > 2) {
                            task.setPreviousTaskUserEndTimes(array[2]);
                        }
                    }
                    if (StringUtils.isEmpty(task.getPreviousTaskUserNames())) {
                        List<WorkflowComment> comments = workflowCommentService.getCommentByProcessInstanceId(task.getProcessId());
                        task.setPreviousTaskUserNames(comments.get(0).getUserName());
                    }
                } else {
                    workflowService.getPreviousTaskDataByCurrentTask(task.getTaskId(), task);
                }

                String userNameStr = task.getNextUserTask();
                if (StringUtils.isNotBlank(userNameStr)) {
                    String userName = userNameStr.substring(userNameStr.indexOf(":") + 1, userNameStr.length());
                    task.setNextUserTask(userName);
                }

            }
            scheduledExecutorService.execute(new TimerTask() {
                @Override
                public void run() {
//                    for (WorkflowTaskVo task : records.getRecords()) {
                    records.getRecords().stream().filter(a -> !a.getState().equals(WorkflowConstants.processState.COMPLETED)).forEach(
                            process -> {
                                workflowService.updateProcessTaskInfoById(process.getProcessId());
                            }
                    );

//                    }
                }
            });
        }
        return records;
//            TaskQuery todoTaskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId);
//            if(CollectionUtils.isNotEmpty(userGroups)){
//                todoTaskQuery.taskCandidateGroupIn(userGroups);
//            }
//            todoTaskQuery.active()
//                    .includeProcessVariables()
//                    .orderByDueDateNullsLast()
//                    .desc().orderByTaskCreateTime().desc();
//
//            if(StringUtils.isNotBlank(query.getTitle())){
//                todoTaskQuery.processDefinitionNameLike("%"+query.getTitle()+"%");
//            }
//
//            Page<WorkflowTaskVo> result=new Page<>(query.getCurrent(),query.getSize());
//            List<Task> tasks = todoTaskQuery.listPage(query.getOffset(),query.getPageSize());
//            result.setTotal(todoTaskQuery.count());
//            if(!CollectionUtils.isEmpty(tasks)){
//                List<WorkflowTaskVo> resultList=new ArrayList<>(tasks.size());
//                List<String> processIds=new ArrayList<>();
//                Map<String,WorkflowTaskVo> processInstanceMap=new HashMap<>();
//                for(Task task:tasks){
//                    processIds.add(task.getProcessInstanceId());
//                    WorkflowTaskVo workflowTaskVo=new WorkflowTaskVo();
//                    workflowTaskVo.setProcessId(task.getProcessInstanceId());
//                    workflowTaskVo.setTaskId(task.getId());
//                    workflowTaskVo.setTaskName(task.getName());
//                    processInstanceMap.put(task.getProcessInstanceId(),workflowTaskVo);
//                    resultList.add(workflowTaskVo);
//                }
//                List<WorkflowProcess> processInstanceList= workflowProcessMapper.processListByIds(processIds);
//
//                for(WorkflowProcess process:processInstanceList){
//                    WorkflowTaskVo workflowTaskVo=  processInstanceMap.get(process.getId());
//                    if(workflowTaskVo!=null){
//                        workflowTaskVo.setTitle(process.getTitle());
//                        workflowTaskVo.setBizId(process.getBizId());
//                        workflowTaskVo.setProcessCategory(process.getProcessCategory());
//                        workflowTaskVo.setProcessDefKey(process.getProcessDefKey());
//                        workflowTaskVo.setStartUserName(process.getStartUserName());
//                        workflowTaskVo.setBizUrl(process.getBizUrl());
//                        workflowTaskVo.setApplyTime(process.getApplyTime());
//                    }
//
//                }
//                result.setRecords(resultList);
//            }
//            return result;
    }


    @Override
    public boolean tasksApprove(WorkflowTaskFormVo form, WorkflowUserVo user) {
        Object obj = form.getForm().get("responsibleUnitResponsibleDepartmentU");
        String taskId = form.getTaskId();
        String processId = form.getProcessId();
        String comment = form.getComment();

        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");

        TaskEntity task = (TaskEntity) taskService.createTaskQuery().processInstanceId(processId).taskId(taskId).singleResult();
        Map<String, Object> myTaskVariables = workflowVariablesService.taskVariables(form, processId, task, user);
        myTaskVariables.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.APPROVED);
        // 提交任务
        identityService.setAuthenticatedUserId(user.getId());
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            // 如果是委派状态
            //2.1创建子任务
            if (StringUtils.isBlank(task.getName())) {
                task.setName("委托");
            }
            TaskEntity subTask = createSubTask(task, task.getAssignee());
            taskService.complete(subTask.getId(), myTaskVariables);
//            taskId = subTask.getId();
            //2.2执行委派
            taskService.resolveTask(form.getTaskId(), myTaskVariables);
            workflowCommentService.addComment(subTask, user.getId(), processId,
                    WorkflowConstants.CommentTypeEnum.SP, form.getComment(), form.getForm());
            Task curTask = taskService.createTaskQuery().taskId(task.getId()).singleResult();
            if (curTask != null) {
                String taskName = curTask.getName();
                boolean modifyTask = false;
                if (taskName.indexOf("(驳回") > 0 && taskName.endsWith(")")) {
                    curTask.setName(taskName.substring(0, taskName.indexOf("(驳回")));
                    modifyTask = true;
                }
                String formKey = curTask.getFormKey();
                if (StringUtils.isNotBlank(formKey) && formKey.indexOf(";") >= 0) {
                    modifyTask = true;
                    if (formKey.indexOf(";") == 0) {
                        curTask.setFormKey("");
                    } else {
                        curTask.setFormKey(formKey.substring(0, formKey.indexOf(";")));

                    }
                }
                if (modifyTask) {
                    taskService.saveTask(curTask);
                }
            }

        } else {
            WorkflowFlowElementUtils.setParentExecutionStringVariableLocal(task.getExecutionId(), WorkflowConstants.ExecutionVariableLocalKey.USER_IDS, user.getId(), processId);
            //3.1修改执行人 其实我这里就相当于签收了
            taskService.setAssignee(taskId, user.getId());
            if (StringUtils.isNotBlank(task.getTaskDefinitionKey()) && task.getTaskDefinitionKey().indexOf(WorkflowConstants.DECIDE_REJECT_NODE_KEY) >= 0) {
//                判断是否需要驳回
                decideReject(workflowProcess, processId, myTaskVariables);
            }
            //3.2执行任务
            taskService.complete(taskId, myTaskVariables);
            //4.处理加签父任务
            String parentTaskId = task.getParentTaskId();
            if (StringUtils.isNotBlank(parentTaskId)) {
                String tableName = managementService.getTableName(TaskEntity.class);
                String sql = "select count(1) from " + tableName + " where PARENT_TASK_ID_=#{parentTaskId}";
                long subTaskCount = taskService.createNativeTaskQuery().sql(sql).parameter("parentTaskId", parentTaskId).count();
                if (subTaskCount == 0) {
                    Task parentTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
                    //处理前后加签的任务
                    taskService.resolveTask(parentTaskId);
                    if ("after".equals(parentTask.getScopeType())) {
                        taskService.complete(parentTaskId);
                    }
                }
            }
            if (StringUtils.isNotEmpty(task.getFormKey())) {
                //销号申请仅在提交时才发送同步消息 需要传递销号申请部门id或者用户id来对子流程进行区分
                if (WorkflowConstants.TASK_FORMID.PIN_NUMBER.equals(task.getFormKey()) || WorkflowConstants.TASK_FORMID.TEXT_AND_TIME.equals(task.getFormKey())) {
                    form.getForm().put(WorkflowConstants.DEPART_ID_KEY, myTaskVariables.get(WorkflowConstants.TASK_DEPART_ID_KEY));
                    form.getForm().put(WorkflowConstants.UPDATE_USERID_KEY, user.getId());
//                    form.getForm().put(WorkflowConstants.TASK_ID_KEY,task.getId());
//                    workflowMessageService.sendTaskForm(workflowProcess.getBizId(),task,user,form);
                } else if (WorkflowConstants.TASK_FORMID.FINISH_ISSUE_PROCESS.equals(task.getFormKey())) {
                    finishIssueProcess(form, user, workflowProcess, processId, myTaskVariables, task);
                }
            }


            workflowCommentService.addComment(task, user.getId(), processId,
                    WorkflowConstants.CommentTypeEnum.SP, form.getComment(), form.getForm());
        }
        workflowProcessMapper.processStateStart2Pending(workflowProcess.getId());
        if (form.getForm().get(WorkflowConstants.DEPART_ID_KEY) == null) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            form.getForm().put(WorkflowConstants.DEPART_ID_KEY, sysUser.getHdmyOrgId());
            form.getForm().put(WorkflowConstants.UPDATE_USERID_KEY, sysUser.getHdmyUserId());
        }
        workflowMessageService.sendTaskForm(workflowProcess.getBizId(), task, user, form);

        //可以流程环节指派副主任处理
        if (obj != null && ((Map) obj).get("manageUserId") != null) {
            try {
                Map map = (Map) obj;
                scheduledExecutorService.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Map<String, Object> dataMap = new HashMap<String, Object>();
                        dataMap.put("id", task.getProcessInstanceId());
                        dataMap.put("userId", map.get("manageUserId"));
                        workflowProcessMapper.upTaskAssignee(dataMap);

//                        TaskEntity currTask = (TaskEntity) taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//                        taskService.setAssignee(currTask.getId(), map.get("manageUserId").toString());
                    }
                }, 5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                log.error("处理当前节点异常：processId=" + task.getProcessInstanceId(), ex);
            }
        }
        superintendMsg(form, workflowProcess);
        return true;
    }

    private void superintendMsg(WorkflowTaskFormVo form, WorkflowProcess workflowProcess) {
        //督办提醒通知消息发送
        if (form.getSuperintendFlag() == 1) {
            List<DictModel> superintendList = commonAPI.queryDictItemsByCode(BaseConstant.SUPERINTEND_MSG_TIP);
            List<String> rl = new ArrayList<String>();
            if (CollectionUtils.isNotEmpty(superintendList)) {
                //根据问题id查找问题流程节点负责人信息
                Long issueId = Long.parseLong(workflowProcess.getBizId());
                OverseeIssueDetailVo detailVo = overseeIssueService.getIssueDetailById(issueId);
                OverseeIssue overseeIssue = overseeIssueService.getRedisCacheOverseeIssue(issueId);
                if (overseeIssue != null) {
                    String title = "";
                    String summary = "";
                    String date = "";
                    String content = "";
                    //得到数据字典配置信息
                    for (DictModel dictModel : superintendList) {
                        if (null != dictModel && StringUtil.isNotEmpty(dictModel.getText())
                                && dictModel.getText().contains("superintend_task_key")) {
                            rl.add(dictModel.getValue());
                        }
                        if (dictModel.getText().equals("superintend_msg_title")) {
                            title = dictModel.getValue();
                        }
                        if (dictModel.getText().equals("superintend_msg_summary")) {
                            summary = dictModel.getValue();
                        }
                        if (dictModel.getText().equals("superintend_msg_date")) {
                            date = dictModel.getValue();
                        }
                        if (dictModel.getText().equals("superintend_msg_content")) {
                            //<p>${date},${depart}就${issue}问题发起督办;</p> <p>督办理由:</p> <p>${issueConent}</p>
                            content = dictModel.getDescription();
                            Map<String, String> valuesMap = new HashMap();
                            valuesMap.put("date", DateFormatUtils.format(workflowProcess.getCreateTime(), "yyyy-MM-dd"));
                            valuesMap.put("depart", detailVo.getHeadquartersLeadDepartmentOrg());
                            valuesMap.put("issue", detailVo.getNum());
                            valuesMap.put("issueConent", detailVo.getSpecificIssuesContent());
//                                        String str = "<p>${date},${depart}就${issue}问题发起督办;</p> <p>督办理由:</p> <p>${issueConent}</p>";
                            StrSubstitutor sb = new StrSubstitutor(valuesMap);
                            content = sb.replace(content);
                        }
                    }
                    String processId = overseeIssue.getProcessId();
                    WorkflowProcessDetailVo result = workflowService.processDetail(processId);
                    WorkflowProcessVo processVo = result.getProcess();
                    if (CollectionUtils.isNotEmpty(result.getUserTasks())) {
                        List<WorkflowUserTaskVo> userTasks = result.getUserTasks();
                        List<WorkflowUserTaskVo> msgUserTasks = userTasks.stream().filter(user -> rl.contains(user.getTaskKey())).collect(Collectors.toList());
                        Map<String, Object> taskVariables = new HashMap<>();
                        taskVariables.put(WorkflowConstants.FLOW_USER_OWNER, new WorkflowUserVo(processVo.getStartUserId(), processVo.getStartUserName(),null));
                        Map<String, List<WorkflowDepart>> bizVariableDepart = workflowUserService.getBizDepartVariables(processVo.getCategory(), processVo.getBizId(), processVo.getStartUserId());
                        if (MapUtils.isNotEmpty(bizVariableDepart)) {
                            taskVariables.putAll(bizVariableDepart);
                        }
                        Map<String, List<WorkflowUserVo>> processUserList = workflowProcessUsersSerivce.getProcessUsersVariables(processId, null, null);
                        if (MapUtils.isNotEmpty(processUserList)) {
                            taskVariables.putAll(processUserList);
                        }
                        List<WorkflowProcessDepart> processDeparts = workflowProcessDepartService.getProcessDepartListByProcessId(processId);
                        if (CollectionUtils.isNotEmpty(processDeparts)) {
                            Map<String, List<WorkflowDepart>> departList = workflowProcessDepartService.getProcessDepartVariable(processDeparts);
                            if (MapUtils.isNotEmpty(departList)) {
                                taskVariables.putAll(departList);
                            }
                        }
                        msgUserTasks = workflowVariablesService.setUserTaskListVariables(msgUserTasks, taskVariables);
                        //发送督办消息
                        if (CollectionUtils.isNotEmpty(msgUserTasks)) {
                            String finalTitle = title;
                            String finalSummary = summary;
                            String finalDate = date;
                            String finalContent = content;
                            Set<String> userIds = new HashSet<>();
                            msgUserTasks.stream().forEach(a -> {
                                a.getUsers().forEach(b -> userIds.add(b.getId()));
                                a.getDeparts().forEach(c ->
                                        c.getUsers().forEach(d ->
                                                userIds.add(d.getId())
                                        ));
                                a.getVariableUser().forEach(d -> {
                                    if (d.getId().indexOf("responsible") < 0) {
                                        userIds.add(d.getId());
                                    }
                                });
                            });
                            StringBuilder sb = new StringBuilder();
                            userIds.forEach(a -> sb.append(a).append(","));
                            SysAnnouncement sysAnnouncement = new SysAnnouncement();
//                                String title = XssUtils.scriptXss(sysAnnouncement.getTitile());
                            sysAnnouncement.setTitile(finalTitle);
                            sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
                            //未发布
                            sysAnnouncement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);
                            sysAnnouncement.setMsgAbstract(finalSummary);
                            sysAnnouncement.setMsgContent(finalContent);
                            sysAnnouncement.setUserIds(sb.toString());
                            sysAnnouncement.setMsgType(CommonConstant.MSG_TYPE_UESR);
                            sysAnnouncement.setPriority(CommonConstant.PRIORITY_H);
                            sysAnnouncement.setSender("admin");
                            sysAnnouncement.setCreateBy("admin");
                            sysAnnouncement.setSendTime(new Date());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                sysAnnouncement.setEndTime(sdf.parse(finalDate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            log.info("sysAnnouncement:" + JsonUtils.toJsonStr(sysAnnouncement));
                            sysAnnouncementService.saveAnnouncement(sysAnnouncement);
                        }
                    }
                } else {
                    log.warn("督办任务无法找到问题,问题id=" + issueId);
                }
            }
        }
    }

    //    转为日常管理
    private void decideReject(WorkflowProcess workflowProcess, String processId, Map<String, Object> myTaskVariables) {
        Assert.isTrue((null != workflowProcess && StringUtils.isNotEmpty(workflowProcess.getProcessDefId())), "未找到流程定义id");
        Assert.isTrue((null != workflowProcess && StringUtils.isNotEmpty(processId)), "未找到流程id");

        Integer submitState = (Integer) myTaskVariables.get("submitState");
        if (null != submitState && submitState.intValue() == OverseeConstants.SubmitState.DAILY_MANAGEMENT) {
            List<FlowElement> jumpNodes = findFlowElementList(workflowProcess.getProcessDefId(), 1);
            Assert.isTrue((null != jumpNodes && jumpNodes.size() > 0), "未找到结束节点");
            if (null != jumpNodes && jumpNodes.size() > 0) {
                String jumpId = jumpNodes.get(0).getId();
                //2、执行终止
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(processId).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
//              根据流程id 和流程运行实例id 以及需要驳回等节点定义id  进行驳回
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, jumpId).changeState();
            }
        }
    }

    private void finishIssueProcess(WorkflowTaskFormVo form, WorkflowUserVo user, WorkflowProcess workflowProcess, String processId, Map<String, Object> myTaskVariables, TaskEntity task) {
        String overseeIssueProcessId = null;
        String overseeIssueProcessDefId = null;
        if (null != myTaskVariables) {
            overseeIssueProcessId = (String) myTaskVariables.get(BaseConstant.OVERSEE_ISSUE_PROCESS_ID);
            overseeIssueProcessDefId = (String) myTaskVariables.get(BaseConstant.OVERSEE_ISSUE_PROCESS_DEF_ID);
        }
        if (StringUtils.isEmpty(overseeIssueProcessId) && StringUtils.isEmpty(overseeIssueProcessDefId)) {
            overseeIssueProcessId = processId;
            overseeIssueProcessDefId = workflowProcess.getProcessDefId();
        }
        if (StringUtils.isNotEmpty(overseeIssueProcessId) && StringUtils.isNotEmpty(overseeIssueProcessDefId)) {
            List<EndEvent> endNodes = findFlowElementList(overseeIssueProcessDefId);
            Assert.isTrue((null != endNodes && endNodes.size() > 0), "未找到结束节点");
            if (null != endNodes && endNodes.size() > 0) {
                String endId = endNodes.get(0).getId();
                //2、执行终止
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(overseeIssueProcessId).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds, endId).changeState();
//                  发送结束流程
//                saveTaskFormData(form,user,1,task);
                workflowMessageService.sendTaskForm(workflowProcess.getBizId(), task, user, form);
            }
        }
    }

    public List findFlowElementList(String processDefId) {
        return findFlowElementList(processDefId, 0);
    }

    public List findFlowElementList(String processDefId, int type) {
        Process mainProcess = repositoryService.getBpmnModel(processDefId).getMainProcess();
        Collection<FlowElement> list = mainProcess.getFlowElements();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        List<FlowElement> flowElementList = null;
        if (type == 0) {
            flowElementList = list.stream().filter(f -> f instanceof EndEvent).collect(Collectors.toList());
        } else if (type == 1) {
            flowElementList = list.stream().filter(f -> f.getId().indexOf(WorkflowConstants.REJECT_TRUE_NODE_KEY) != -1).collect(Collectors.toList());
        }
        return flowElementList;
    }

    @Override
    public boolean tasksReject(WorkflowTaskFormVo form, String userId) {
        String taskId = form.getTaskId();
        String processId = form.getProcessId();
        String comment = form.getComment();
        Map<String, Object> taskForm = form.getForm();
        if (taskForm == null) {
            taskForm = new HashMap<>();
        }
        taskForm.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.REJECTED);
        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        Assert.notNull(processInstance, "审批流程不存在");


        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        Assert.notNull(task, "获取任务不存在");


        List<EndEvent> endNodes = workflowModelService.findEndFlowElement(processInstance.getProcessDefinitionId());
        if (CollectionUtils.isNotEmpty(endNodes)) {
            // 添加意见
            if (StringUtils.isNotBlank(processId) && StringUtils.isNotBlank(comment)) {
                taskService.addComment(taskId, processId, comment);
            }

            String endId = endNodes.get(0).getId();
            //2、执行终止
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(processId).list();
            List<String> executionIds = new ArrayList<>();
            executions.forEach(execution -> executionIds.add(execution.getId()));
            //1、完成当前任务
            taskService.complete(taskId, taskForm, true);

            runtimeService.createChangeActivityStateBuilder()
                    .moveExecutionsToSingleActivityId(executionIds, endId)
                    .changeState();

            workflowCommentService.addTaskComment(task, userId, processId,
                    WorkflowConstants.CommentTypeEnum.JJ, null, form.getComment(), form.getForm());
            workflowProcess.setState(WorkflowConstants.processState.REJECTED);//设置为拒绝
            workflowProcessMapper.updateById(workflowProcess);
            return true;
        }
        return false;
    }


    @Override
    public boolean tasksDelegate(WorkflowTaskFormVo form, WorkflowUserVo user) {
        String taskId = form.getTaskId();
        String processId = form.getProcessId();
        String comment = form.getComment();
        Map<String, Object> taskForm = form.getForm();
        if (taskForm == null) {
            taskForm = new HashMap<>();
        }
        taskForm.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.DELEGATE);
        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");

        TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(form.getTaskId()).singleResult();
        Assert.notNull(currTask, "没有运行时的任务实例,请确认!");
        //1.添加审批意见
//            if (StringUtils.isNotBlank(processId) && StringUtils.isNotBlank(form.getComment())) {
//                taskService.addComment(form.getTaskId(), processId, form.getComment());
//            }
        WorkflowUserVo delegateUser = workflowUserService.getUserById(form.getDelegateUserId());
        Assert.notNull(delegateUser, "找不到委派用户");
        workflowCommentService.addTaskComment(currTask, user.getId(), processId,
                WorkflowConstants.CommentTypeEnum.WP, delegateUser.getName(), form.getComment(), form.getForm());
        //2.设置审批人就是当前登录人
        taskService.setAssignee(form.getTaskId(), user.getId());
        //3.执行委派
        taskService.delegateTask(form.getTaskId(), form.getDelegateUserId());
        workflowProcess.setState(WorkflowConstants.processState.PENDING);//设置为审批中
        workflowProcessMapper.updateById(workflowProcess);
        workflowMessageService.sendTaskForm(workflowProcess.getBizId(), currTask, user, form);
        return true;

    }

    @Override
    public boolean tasksBackStart(WorkflowTaskFormVo form, WorkflowUserVo user) {
        String processId = form.getProcessId();
        Map<String, Object> taskForm = form.getForm();
        if (taskForm == null) {
            taskForm = new HashMap<>();
        }
        taskForm.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.DELEGATE);
        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");

        TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(form.getTaskId()).singleResult();
        Assert.notNull(currTask, "没有运行时的任务实例,请确认!");
        //1.添加审批意见
//            if (StringUtils.isNotBlank(processId) && StringUtils.isNotBlank(form.getComment())) {
//                taskService.addComment(form.getTaskId(), processId, form.getComment());
//            }

        workflowCommentService.addTaskComment(currTask, user.getId(), processId,
                WorkflowConstants.CommentTypeEnum.BS, workflowProcess.getStartUserName(), form.getComment(), form.getForm());
        //2.设置审批人就是当前登录人
        taskService.setAssignee(form.getTaskId(), user.getId());
        //3.执行委派给发起者
        taskService.delegateTask(form.getTaskId(), workflowProcess.getStartUserId());
        workflowProcess.setState(WorkflowConstants.processState.PENDING);//设置为审批中
        workflowProcessMapper.updateById(workflowProcess);
        workflowMessageService.sendTaskForm(workflowProcess.getBizId(), currTask, user, form);
        return true;
    }


    @Override
    @Transactional
    public boolean tasksDelegateBackNodes(WorkflowTaskFormVo form, WorkflowUserVo user) {
        String processId = form.getProcessId();
        Map<String, Object> taskForm = form.getForm();
        if (taskForm == null) {
            taskForm = new HashMap<>();
        }
        taskForm.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.DELEGATE);
        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");
        TaskEntity currTask = (TaskEntity) taskService.createTaskQuery().processInstanceId(processId).taskId(form.getTaskId()).singleResult();
        Assert.notNull(currTask, "没有运行时的任务实例,请确认!");
        if (StringUtils.isNotBlank(currTask.getTaskDefinitionKey()) && currTask.getTaskDefinitionKey().indexOf(WorkflowConstants.FLOW_BACK_NODE_KEY) >= 0) {
            throw new BizException("当前节点设置不支持操作驳回");
        }

        Map<String, Object> myTaskVariables = workflowVariablesService.taskVariables(form, processId, currTask, user);
        String processDefId = currTask.getProcessDefinitionId();
        BpmnModel bpmnModel = workflowProcessDefinitionService.getModelByProcessDefId(processDefId);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        String taskkey = currTask.getTaskDefinitionKey();
        String backFormKey = null;

        List<WorkflowUserVo> workflowUserVos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowElements) && StringUtils.isNotBlank(taskkey)) {
            FlowElement taskElement = WorkflowFlowElementUtils.getFlowElementById(flowElements, taskkey);
            UserTask userTaskElement = null;
            if (taskElement != null && taskElement instanceof UserTask) {
                userTaskElement = (UserTask) taskElement;
            }
            List<FlowElement> backElements = new ArrayList<>();
            WorkflowFlowElementUtils.findModelBackNodeByElementId(backElements, flowElements, userTaskElement, myTaskVariables, WorkflowConstants.FLOW_BACK_NODE_KEY);
//            UserTask backUserTask= WorkflowFlowElementUtils.getFristUserTask(backElements);
            if (CollectionUtils.isNotEmpty(backElements)) {
                for (FlowElement element : backElements) {
                    if (element instanceof UserTask) {
                        backFormKey = ((UserTask) element).getFormKey();
                        //findListByTaskKey 改用如下方法一并把人的部门id获取到
                        List<WorkflowComment> historyCommentList = workflowCommentService.findOrgidListByTaskKey(workflowProcess.getId(), element.getId());
                        if(StringUtils.isNotBlank(form.getRejectOrgId())) {
                            if(historyCommentList.size()==1){
                                WorkflowComment workflowComment =historyCommentList.get(0);
                                workflowUserVos.add(new WorkflowUserVo(workflowComment.getUserId(), workflowComment.getUserName(),workflowComment.getTaskKey()));
                            }else {
                                historyCommentList = historyCommentList.stream().filter(a -> a.getOrgId().equals(form.getRejectOrgId())).collect(Collectors.toList());
                                historyCommentList.forEach(a -> {
                                    workflowUserVos.add(new WorkflowUserVo(a.getUserId(), a.getUserName(),a.getTaskKey()));
                                });
                            }
                        }else {
                            if (CollectionUtils.isNotEmpty(historyCommentList)) {
                                WorkflowComment backComment = historyCommentList.get(0);
                                workflowUserVos.add(new WorkflowUserVo(backComment.getUserId(), backComment.getUserName(),backComment.getTaskKey()));
                                break;
                            }
                        }
                    }
                }
            }
        }
        tasksDelegateBackNodes(form, workflowUserVos, workflowProcess, backFormKey, currTask, user);

        return true;
    }

    private void tasksDelegateBackNodes(WorkflowTaskFormVo form, List<WorkflowUserVo> workflowUserVos, WorkflowProcess workflowProcess, String backFormKey, TaskEntity currTask, WorkflowUserVo user) {
        String taskName = currTask.getName();
        WorkflowUserVo backPointUser;
        if (CollectionUtils.isEmpty(workflowUserVos)) {//没有找到驳回节点，就驳回到发起人
            backPointUser = new WorkflowUserVo(workflowProcess.getStartUserId(), workflowProcess.getStartUserName(),null);
            backFormKey = WorkflowConstants.TASK_FORMID.PROCESS_OWNER_FORM;
            currTask.setName(currTask.getName() + "(驳回发起者)");//注意格式一定按"(驳回XXX)"规则设置
        } else {
            backPointUser = workflowUserVos.get(0);
            currTask.setName(taskName + "(驳回" + backPointUser.getName() + ")");//注意格式一定按"(驳回XXX)"规则设置
        }
        if (backPointUser.getId().equals(user.getId())) {
            throw new BizException("不能驳回自己操作");
        }
        //以下驳回处理表单KEY,创建新的表单KEY格式="原来的表单KEY;驳回节点表单KEY"
        String currFormKey = currTask.getFormKey();
        if (StringUtils.isBlank(currFormKey) || currFormKey.indexOf(SymbolConstant.SEMICOLON) == 0) {
            String newFormKey = SymbolConstant.SEMICOLON + backFormKey;
            currTask.setFormKey(newFormKey);
        } else {
            if (currFormKey.indexOf(SymbolConstant.SEMICOLON) > 0) {
                String newFormKey = currFormKey.split(SymbolConstant.SEMICOLON)[0] + SymbolConstant.SEMICOLON + backFormKey;
                currTask.setFormKey(newFormKey);
            } else {
                String newFormKey = currFormKey + SymbolConstant.SEMICOLON + backFormKey;
                currTask.setFormKey(newFormKey);
            }
        }
        TaskEntity tmpCurrTask = (TaskEntity) taskService.createTaskQuery().processInstanceId(form.getProcessId()).taskId(form.getTaskId()).singleResult();
        currTask.setRevision(tmpCurrTask.getRevision());
        taskService.saveTask(currTask);
        //1.添加审批意见
        workflowCommentService.addTaskComment(currTask, user.getId(), form.getProcessId(),
                WorkflowConstants.CommentTypeEnum.BH, backPointUser.getName(), form.getComment(), form.getForm());

        //2.设置审批人就是当前登录人
        taskService.setAssignee(form.getTaskId(), user.getId());
        //驳回设置责任部门环节不能使用委派驳回方式
        String backNodeKey = "BACKPOINT_Activity_0kyk3u3";
        if(backNodeKey.equals(backPointUser.getTaskKey())) {
            //        WorkflowUtils.setOldIssueVariable(variables);
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(tmpCurrTask.getProcessInstanceId())
                    .moveActivityIdTo(tmpCurrTask.getTaskDefinitionKey(), backNodeKey)
//                .processVariables(variables)
                    .changeState();
        }else{
            //3.执行委派给驳回的用户
            taskService.delegateTask(form.getTaskId(), backPointUser.getId());
        }
        workflowProcess.setState(WorkflowConstants.processState.PENDING);//设置为审批中
        workflowProcessMapper.updateById(workflowProcess);
        workflowMessageService.sendTaskForm(workflowProcess.getBizId(), currTask, user, form);
    }


    @Override
    public boolean turnTask(WorkflowTaskFormVo form, WorkflowUserVo user) {
        String processId = form.getProcessId();
        Map<String, Object> taskForm = form.getForm();
        if (taskForm == null) {
            taskForm = new HashMap<>();
        }
        taskForm.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.TURN);
        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");

        TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(form.getTaskId()).singleResult();
        Assert.notNull(currTask, "没有运行时的任务实例,请确认!");
        if (currTask != null) {
            //1.生成历史记录
            TaskEntity task = this.createSubTask(currTask, user.getId());
            //2.添加审批意见
            workflowCommentService.addTaskComment(currTask, user.getId(), processId,
                    WorkflowConstants.CommentTypeEnum.ZB, workflowProcess.getStartUserName(), form.getComment(), form.getForm());

            taskService.complete(task.getId());
            //3.转办
            taskService.setAssignee(form.getTaskId(), form.getDelegateUserId());
            taskService.setOwner(form.getTaskId(), user.getId());
            return true;
        }
        return false;
    }

    protected TaskEntity createSubTask(TaskEntity ptask, String assignee) {
        return this.createSubTask(ptask, ptask.getId(), assignee);
    }

    /**
     * 创建子任务
     *
     * @param ptask    创建子任务
     * @param assignee 子任务的执行人
     * @return
     */
    protected TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
        TaskEntity task = null;
        if (ptask != null) {
            //1.生成子任务
            task = (TaskEntity) taskService.newTask(UUIDGenerator.generate());
            task.setCategory(ptask.getCategory());
            task.setDescription(ptask.getDescription());
            task.setTenantId(ptask.getTenantId());
            task.setAssignee(assignee);
            task.setName(ptask.getName());
            task.setParentTaskId(ptaskId);
            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
            task.setProcessInstanceId(ptask.getProcessInstanceId());
            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
            task.setPriority(ptask.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

    @Override
    public WorkflowUserAnalysisVo userProcessAnalysis(String userId) {
        WorkflowUserAnalysisVo analysisVo = new WorkflowUserAnalysisVo();
        List<String> userGroups = workflowProcessDepartService.userDepartVariables(userId);
        analysisVo.setTodoTotal(workflowProcessMapper.countUserTodoTotal(userId, userGroups));
        analysisVo.setDoneTotal(workflowProcessMapper.countUserDoneTotal(userId, userGroups));
        return analysisVo;
    }

    @Override
    public Long countUserTodoTotal(String userId) {
        List<String> userGroups = workflowProcessDepartService.userDepartVariables(userId);
        return workflowProcessMapper.countUserTodoTotal(userId, userGroups);
    }

    public boolean saveTaskFormData(WorkflowTaskFormVo form, WorkflowUserVo user) {
        return saveTaskFormData(form, user, 0);
    }


    @Override
    public boolean saveTaskFormData(WorkflowTaskFormVo form, WorkflowUserVo user, int isSubmitState) {
        String taskId = form.getTaskId();
        String processId = form.getProcessId();
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().processInstanceId(processId).taskId(taskId).singleResult();
        saveTaskFormData(form, user, isSubmitState, task);
        return true;
    }

    public boolean saveTaskFormData(WorkflowTaskFormVo form, WorkflowUserVo user, int isSubmitState, TaskEntity task) {

        String processId = form.getProcessId();
        String comment = form.getComment();

        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
        Assert.notNull(workflowProcess, "审批流程不存在");
//        Assert.notNull(task,"流程任务不存在");
        if (null != task && (WorkflowConstants.TASK_FORMID.PIN_NUMBER.equals(task.getFormKey()) || WorkflowConstants.TASK_FORMID.TEXT_AND_TIME.equals(task.getFormKey())))
            setVariableLocalFormMap(task, form, WorkflowConstants.FORM_MAP_KEY);
        Map<String, Object> myTaskVariables = workflowVariablesService.taskVariables(form, processId, task, user);
        myTaskVariables.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.APPROVED);
        // 提交任务
        identityService.setAuthenticatedUserId(user.getId());
        workflowCommentService.addComment(task, user.getId(), processId,
                WorkflowConstants.CommentTypeEnum.ZC, form.getComment(), form.getForm());

//        销号申请和问题整改只在提交时发起同步
        if ((null != task && !WorkflowConstants.TASK_FORMID.PIN_NUMBER.equals(task.getFormKey()) && !WorkflowConstants.TASK_FORMID.TEXT_AND_TIME.equals(task.getFormKey()) && !WorkflowConstants.TASK_FORMID.FINISH_ISSUE_PROCESS.equals(task.getFormKey())) || isSubmitState == 1) {
            workflowMessageService.sendTaskForm(workflowProcess.getBizId(), task, user, form);
        }

        return true;
    }

    @Override
    public String getPreviousTaskDataSrc(Map<String, Object> map) {
        return workflowProcessMapper.getPreviousTaskDataSrc(map);
    }

    private void setVariableLocalFormMap(TaskEntity task, WorkflowTaskFormVo form, String... variableNames) {
        if (null != task && null != form.getForm() && null != variableNames) {
            for (String variableName : variableNames) {
//                子流程使用runtimeService 确保子流程中其它节点都能看到
//                runtimeService.setVariableLocal(runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult().getParentId(),variableName,form.getForm());
                taskService.setVariableLocal(task.getId(), variableName, form.getForm());
            }
        }
    }

    private void setFormItemVariables(TaskEntity task, WorkflowTaskFormVo form, String... variableNames) {
        if (null != task && null != form.getForm() && null != variableNames) {
            for (String variableName : variableNames) {
                if (null != form.getForm().get(variableName)) {
                    taskService.setVariableLocal(task.getId(), variableName, form.getForm().get(variableName));
                }
            }
        }
    }
}
