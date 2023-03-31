package com.chd.modules.oversee.issue.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.exception.AssertBizException;
import com.chd.common.exception.BizException;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.HTMLUtils;
import com.chd.common.util.JsonUtils;
import com.chd.modules.oversee.hdmy.service.impl.MyOrgServiceImpl;
import com.chd.modules.oversee.hdmy.service.impl.MyUserServiceImpl;
import com.chd.modules.oversee.issue.entity.IssuesSupervisor;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueFlowOrg;
import com.chd.modules.oversee.issue.mapper.OverseeIssueFlowOrgMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueFlowOrgService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.IOverseeWorkflowService;
import com.chd.modules.oversee.issue.util.SynchronizationProcessDataUtils;
import com.chd.modules.workflow.entity.ProcessClassification;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.vo.*;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OverseeWorkflowServiceImpl implements IOverseeWorkflowService {

    @Autowired
    private WorkflowProcessDefinitionService workflowProcessDefinitionService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private IOverseeIssueService overseeIssueService;
    @Autowired
    private MyOrgServiceImpl myOrgService;
    @Autowired
    private MyUserServiceImpl myUserService;
    @Autowired
    private OverseeIssueFlowOrgMapper overseeIssueFlowOrgMapper;
    @Autowired
    private IOverseeIssueFlowOrgService overseeIssueFlowOrgService;
    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;
    @Autowired
    private WorkflowCandidateGroupService workflowCandidateGroupService;



    @Autowired
    private IProcessClassificationService processClassificationService;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WorkflowVariablesService workflowVariablesService;

    private static final String PROCESS_CATEGORY_ISSUES="ISSUES";
    private static final String PROCESS_CATEGORY_NO_RECTIFICATION="NoRectification";
    private static final String PROCESS_INITIATE_SUPERVISION="InitiateSupervision";

    private static final String PROCESS_CATEGORY_ISSUES_URL="issue/components/IssueDetail";




    public boolean overseeIssueLaunchProcess(Long issueId,LoginUser loginUser) {
        return overseeIssueLaunchProcess(issueId,loginUser,1);
    }

    public boolean overseeIssueLaunchProcess(Long issueId,LoginUser loginUser,Integer issueLaunchType) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("issueId",issueId);
        map.put("loginUser",loginUser);
        map.put("issueLaunchType",issueLaunchType);
        return overseeIssueLaunchProcess(map);
    }

    public boolean overseeOldIssueLaunchProcess(Long issueId,LoginUser loginUser) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("issueId",issueId);
        map.put("loginUser",loginUser);
        map.put("issueLaunchType",1);
        map.put(BaseConstant.PROCESS_OLD_ISSUES_KEY,1);
        return overseeIssueLaunchProcess(map);
    }

    @Override
    public boolean overseeIssueLaunchProcess(Map<String, Object> map) {
        Long issueId = (Long) map.get("issueId");
        LoginUser loginUser = (LoginUser) map.get("loginUser");
        Integer issueLaunchType = (Integer) map.get("issueLaunchType");

        issueLaunchType = null!=issueLaunchType?issueLaunchType:1;
        OverseeIssue overseeIssue = overseeIssueService.getRedisCacheOverseeIssue(issueId);
        if(overseeIssue!=null){

            Map<String,Object> variables= JsonUtils.fromJson(JsonUtils.toJsonStr(overseeIssue),Map.class);
            variables.put("issueLaunchType",issueLaunchType);

            String processCategoryIssues = BaseConstant.PROCESS_CATEGORY_ISSUES;
            String title = HTMLUtils.getTitle(overseeIssue.getSpecificIssuesContent());

            List<WorkflowProcessDepart> processDepartList=getOverseeIssueProcessDepartList(overseeIssue);

            if(issueLaunchType.intValue() == 1){
                if(overseeIssue.getSubmitState()!=null && overseeIssue.getSubmitState().intValue()!=0){
                    throw new BizException("不能重复上报");
                }
                Integer source = overseeIssue.getSource();
                AssertBizException.isTrue((null!=source),"请传递来源");
                List<ProcessClassification> processClassificationList = processClassificationService.list(Wrappers.<ProcessClassification>lambdaQuery().eq(ProcessClassification::getType, 1).eq(ProcessClassification::getSubcategory, source).eq(ProcessClassification::getDataType, 1).orderByAsc(ProcessClassification::getSort));
                if(null!=processClassificationList&&processClassificationList.size()>0){
                    if(StringUtils.isNotEmpty(processClassificationList.get(0).getValue())){
                        processCategoryIssues = processClassificationList.get(0).getValue();
                    }
                }
            }else if(issueLaunchType.intValue() == -1){
                processCategoryIssues = BaseConstant.PROCESS_CATEGORY_NO_RECTIFICATION;
                title = "不再纳入整改申请 : " + title;
                variables.put(BaseConstant.OVERSEE_ISSUE_PROCESS_ID,overseeIssue.getProcessId());
                variables.put(BaseConstant.OVERSEE_ISSUE_PROCESS_DEF_ID,overseeIssue.getProcessDefId());
            } else if(issueLaunchType.intValue() == 2){
                processCategoryIssues = BaseConstant.PROCESS_INITIATE_SUPERVISION;
                title = "督办 : " + title;
                variables.put(BaseConstant.OVERSEE_ISSUE_PROCESS_ID,overseeIssue.getProcessId());
                variables.put(BaseConstant.OVERSEE_ISSUE_PROCESS_DEF_ID,overseeIssue.getProcessDefId());
                IssuesSupervisor issuesSupervisor = (IssuesSupervisor) map.get("issuesSupervisor");
                if(null!=issuesSupervisor){
                    if(StringUtils.isNotEmpty(issuesSupervisor.getSupervisorOrgId())){
                        List<WorkflowProcessDepart> processDeparts_initiator=workflowProcessDepartService.toProcessDepart(issuesSupervisor.getSupervisorOrgId(),WorkflowConstants.DEPART_SOURCE.LAUNCH);
                        if(CollectionUtils.isNotEmpty(processDeparts_initiator)){
                            processDepartList.addAll(processDeparts_initiator);
                        }
                        Map<String,Object> jsonMap = JsonUtils.fromJson(JsonUtils.toJsonStr(issuesSupervisor), Map.class);
                        if(null!=jsonMap){
                            jsonMap.put(BaseConstant.ISSUES_SUPERVISOR_ID_KEY,jsonMap.get("id"));
                            variables.put(BaseConstant.ISSUES_SUPERVISOR_ID_KEY,jsonMap.get("id"));
                            jsonMap.remove("id");
                            variables.put("issuesSupervisor",jsonMap);

                        }
                    }
                }
            }
            variables.put(BaseConstant.PROCESS_CATEGORY_ISSUES_KEY,processCategoryIssues);
            variables.put(BaseConstant.PROCESS_OLD_ISSUES_KEY,map.get(BaseConstant.PROCESS_OLD_ISSUES_KEY));
            WorkflowProcess process=new WorkflowProcess();
            process.setBizId(String.valueOf(issueId));
            process.setProcessCategory(processCategoryIssues);
            process.setStartUserId(loginUser.getId());
            process.setStartUserName(loginUser.getRealname());
            process.setBizUrl(PROCESS_CATEGORY_ISSUES_URL);
            process.setTitle(title);
            process.setUpdateTime(new Date());
            process.setUpdateBy(loginUser.getRealname());
            WorkflowVariablesVo variablesVo=new WorkflowVariablesVo();
            variablesVo.setVariables(variables);

            if(CollectionUtils.isNotEmpty(processDepartList)) {
                variablesVo.setDepartList(processDepartList);
            }
            process.setVariables(variablesVo);
            variables.put(WorkflowConstants.FLOW_USER_OWNER,loginUser.getId());
            workflowService.launchProcess(process);
            if(issueLaunchType.intValue() == 1){
                //跳转到销号开始节点
                Object oldIssueObj = variables.get(BaseConstant.PROCESS_OLD_ISSUES_KEY);
                if(oldIssueObj!=null && Integer.parseInt(oldIssueObj.toString())==1) {
                    overseeIssue.setSubmitState(OverseeConstants.SubmitState.Submit);
                }else {
                    overseeIssue.setSubmitState(OverseeConstants.SubmitState.UnderReview);  //提交之后状态改为审核中
                }
                overseeIssue.setReportTime(new Date());
                overseeIssue.setReportUserId(loginUser.getId());
                overseeIssue.setUpdateUserId(loginUser.getId());
                overseeIssue.setProcessId(process.getId());
                overseeIssue.setProcessDefId(process.getProcessDefId());
                overseeIssueService.modifySubmitState(overseeIssue);
//                workflowMessageService.sendTaskForm(workflowProcess.getBizId(),task,user,form);
                SynchronizationProcessDataUtils.synchronization(overseeIssue);
                if(oldIssueObj!=null && Integer.parseInt(oldIssueObj.toString())==1){
                    workflowService.launchCancelProcess(process);
                }
            }
            return true;
        }
        return false;
    }



    private List<WorkflowProcessDepart> getOverseeIssueProcessDepartList(OverseeIssue overseeIssue){
        List<WorkflowProcessDepart> processDepartList=new ArrayList<>();
        String orgIds_initiator = overseeIssue.getHeadquartersLeadDepartmentOrgId();//本部牵头部门
        String orgIds_supervisor = overseeIssue.getSupervisorOrgIds();//督办
        String orgIds_responseible = overseeIssue.getResponsibleLeadDepartmentOrgId();//责任单位牵头部门
        String orgIds_responseible_exec = overseeIssue.getResponsibleOrgIds();//责任单位责任部门
         List<WorkflowProcessDepart> processDeparts_initiator=workflowProcessDepartService.toProcessDepart(orgIds_initiator,WorkflowConstants.DEPART_SOURCE.INITIATOR);
         if(CollectionUtils.isNotEmpty(processDeparts_initiator)){
             processDepartList.addAll(processDeparts_initiator);
         }
         List<WorkflowProcessDepart> processDeparts_supervisor=workflowProcessDepartService.toProcessDepart(orgIds_supervisor,WorkflowConstants.DEPART_SOURCE.SUPERVISOR);
        if(CollectionUtils.isNotEmpty(processDeparts_supervisor)){
            processDepartList.addAll(processDeparts_supervisor);
        }
         List<WorkflowProcessDepart> processDeparts_responseible=workflowProcessDepartService.toProcessDepart(orgIds_responseible,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE);
        if(CollectionUtils.isNotEmpty(processDeparts_responseible)){
            processDepartList.addAll(processDeparts_responseible);
        }
        if(orgIds_responseible_exec!=null){
            processDeparts_responseible=workflowProcessDepartService.toProcessDepart(orgIds_responseible_exec,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE);
            if(CollectionUtils.isNotEmpty(processDeparts_responseible)){
                processDepartList.addAll(processDeparts_responseible);
            }
        }
        return processDepartList;
    }

    public Map<String,Object> getOverseeIssueProcessDefUserVariable(OverseeIssue overseeIssue,String category){
        Map<String,Object> variableUserMap=new HashMap<>();
        ProcessDefinition processDefinition=workflowProcessDefinitionService.lastVersionProcessDefByCategory(category);
        if(processDefinition!=null) {
            WorkflowProcessDetail processDefDetail = workflowProcessDefinitionService.getProcessDefDetail(processDefinition,null);
            WorkflowProcessDetailVo result = workflowProcessDefinitionService.getProcessDefDetailView(processDefDetail);
            if(CollectionUtils.isNotEmpty( result.getUserTasks())) {
                for (WorkflowUserTaskVo userTask : result.getUserTasks()) {
                    if (CollectionUtils.isNotEmpty(userTask.getVariableUser())) {
                        Iterator<WorkflowUserVo> users = userTask.getVariableUser().iterator();
                        while (users.hasNext()){
                            WorkflowUserVo variableUser=users.next();
                            String orgIds=overseeIssueFlowOrgService.getOverseeIssueOrgIds(overseeIssue,variableUser.getId());
                            if(StringUtils.isNotBlank(orgIds)){
                                String[] orgIdArr=orgIds.split(",");
                                for(String orgId:orgIdArr){
                                    OverseeIssueFlowOrg flowOrg=overseeIssueFlowOrgService.getOverseeIssueFlowOrg(orgId,variableUser.getId());
                                    if(flowOrg!=null){
                                        Object value= variableUserMap.get(variableUser.getId());
                                        if(value!=null){
                                            if(value instanceof  String){
                                                List<String> valueList=new ArrayList<>();
                                                valueList.add((String)value);
                                                valueList.add(flowOrg.getId());
                                                variableUserMap.put(variableUser.getId(),valueList);
                                            }else {
                                                List valueList = (List<String>) value;
                                                valueList.add(flowOrg.getId());
                                            }
                                        }else {
                                            variableUserMap.put(variableUser.getId(), flowOrg.getId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return variableUserMap;
    }





}
