package com.chd.modules.workflow;

import com.chd.common.util.JsonUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowProcessDepartService;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.*;

@Slf4j
public class WorkflowUtils {
    public static void setOldIssueVariable(Map variables) {
        try {
            IOverseeIssueService overseeIssueService = SpringContextUtils.getBean(IOverseeIssueService.class);
            RuntimeService runtimeService = SpringContextUtils.getBean(RuntimeService.class);
            WorkflowProcessDepartService workflowProcessDepartService = SpringContextUtils.getBean(WorkflowProcessDepartService.class);
            String processId = (String) variables.get("processId");
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            OverseeIssue overseeIssue = overseeIssueService.getRedisCacheOverseeIssue(Long.parseLong(processInstance.getBusinessKey()));
            if (overseeIssue != null) {
                Map<String, Object> issue = JsonUtils.fromJson(JsonUtils.toJsonStr(overseeIssue), Map.class);
                variables.putAll(issue);
            }
            //责任单位责任部门
            String responsibleOrgId = (String) variables.get("responsibleOrgIds");
            List<WorkflowUserVo> workflowUserVos1 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.MANAGER);
//        List<WorkflowUserVo> workflowUserVos2 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            String responsibleManager = workflowUserVos1.get(0).getId();
            Map<String, Object> responsibleUnitResponsibleDepartment = new HashMap<>();
            //BACKPOINT_Activity_06l24ki 第五阶段：责任单位责任部门业务人员发起销号申请 flowable:candidateUsers="${responsibleUnitResponsibleDepartment.userId}
            responsibleUnitResponsibleDepartment.put("userId", responsibleManager);
//            variables.put("RESPONSIBLE_EXECUTE_SPECIALIST", responsibleManager);//责任单位责任业务员变量已废弃
            //Activity_01c91ld 第五阶段：责任单位责任部门负责人审核并发起会签 flowable:candidateGroups="${responsibleUnitResponsibleDepartment.manageUserId}"
            responsibleUnitResponsibleDepartment.put("manageUserId", variables.get("RESPONSIBLE_EXECUTE_MANAGER"));
            //Activity_1feheze 第五阶段：责任单位责任部门分管领导审核 flowable:candidateGroups="${responsibleUnitResponsibleDepartment.supervisorUserId}"
            responsibleUnitResponsibleDepartment.put("supervisorUserId", variables.get("RESPONSIBLE_EXECUTE_SUPERVISOR"));
//            responsibleUnitResponsibleDepartment.put("departId",responsibleOrgId);  ?
            variables.put("responsibleUnitResponsibleDepartment", responsibleUnitResponsibleDepartment);
            variables.put("RESPONSIBLE_EXECUTE_MANAGER", variables.get("RESPONSIBLE_EXECUTE_MANAGER"));//第五阶段：责任单位责任部门负责人审核并发起会签,条件需要
            List dataList = new ArrayList();
            Map dataMap = new HashMap();
            dataMap.put("departId", responsibleOrgId);
            dataMap.put("userId", Arrays.asList(responsibleManager));
            dataMap.put("manageUserId", variables.get("RESPONSIBLE_EXECUTE_MANAGER"));
            dataMap.put("supervisorUserId", variables.get("RESPONSIBLE_EXECUTE_SUPERVISOR"));
            dataList.add(dataMap);
            variables.put("responsibleUnitResponsibleDepartmentList", dataList);

            //责任单位牵头部门
            responsibleOrgId = (String) variables.get("responsibleLeadDepartmentOrgId");
            workflowUserVos1 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.MANAGER);
//        workflowUserVos2 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            responsibleManager = workflowUserVos1.get(0).getId();
//            Activity_0y71kmc 第五阶段：责任单位牵头部门经办人审核 flowable:candidateUsers="${RESPONSIBLE_HANDLE_EXECUTOR}"
            variables.put("RESPONSIBLE_HANDLE_EXECUTOR", responsibleManager);
//            Activity_1w18nj9 第五阶段：责任单位牵头部门负责人审核 flowable:candidateGroups="${RESPONSIBLE_HANDLE_MANAGER}"
            variables.put("RESPONSIBLE_HANDLE_MANAGER", variables.get("RESPONSIBLE_HANDLE_MANAGER"));

//            Activity_1tfyw94 第五阶段：责任单位牵头部门分管领导审核 flowable:candidateGroups="${RESPONSIBLE_HANDLE_SUPERVISOR}"
            variables.put("RESPONSIBLE_HANDLE_SUPERVISOR", variables.get("RESPONSIBLE_HANDLE_SUPERVISOR"));

//            Activity_0zu1g62 第五阶段：责任单位党委书记审批  flowable:candidateUsers="${RESPONSIBLE_EXECUTE_SECRETARY}"
//            variables.put("RESPONSIBLE_EXECUTE_SECRETARY", "");//后续流程分配

            //本部督办部门
            responsibleOrgId = (String) variables.get("supervisorOrgIds");
            workflowUserVos1 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.MANAGER);
//        workflowUserVos2 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            responsibleManager = workflowUserVos1.get(0).getId();
//            Activity_1a8ahjx 第五阶段：本部督办部门经办人审核 flowable:candidateUsers="${SUPERVISOR_EXECUTOR}"
//            variables.put("SUPERVISOR_EXECUTOR", responsibleManager);//已废弃
            Map<String, Object> supervisor = new HashMap<>();
            supervisor.put("userId", responsibleManager);
//            Activity_1w7daw1 第五阶段：本部督办部门负责人审核并发起会签 flowable:candidateGroups="${supervisor.manageUserId}"
            supervisor.put("manageUserId", variables.get("SUPERVISOR_MANAGER"));
//            Activity_1lp65yj 第五阶段：本部督办部门分管领导审核 flowable:candidateGroups="${supervisor.supervisorUserId}"
            supervisor.put("supervisorUserId", variables.get("SUPERVISOR_SUPERVISOR"));
            variables.put("supervisor", supervisor);
            dataList = new ArrayList();
            dataMap = new HashMap();
            dataMap.put("departId", responsibleOrgId);
            dataMap.put("userId", Arrays.asList(responsibleManager));
            dataMap.put("manageUserId", variables.get("SUPERVISOR_MANAGER"));
            dataMap.put("supervisorUserId", variables.get("SUPERVISOR_SUPERVISOR"));
            dataList.add(dataMap);
            variables.put("supervisorList", dataList);

            //本部牵头部门
            responsibleOrgId = (String) variables.get("headquartersLeadDepartmentOrgId");
            workflowUserVos1 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.MANAGER);
//        workflowUserVos2 = workflowProcessDepartService.getDepartUsers(responsibleOrgId, WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            responsibleManager = workflowUserVos1.get(0).getId();
//            Activity_14bgyi3 第五阶段：本部牵头部门经办人审核 flowable:assignee="${USER_OWNER}"
            variables.put("USER_OWNER", responsibleManager);
//            Activity_0kli5pv 第五阶段：本部牵头部门负责人审核 flowable:candidateGroups="${INITIATOR_MANAGER}"
            variables.put("INITIATOR_MANAGER", variables.get("INITIATOR_MANAGER"));

//            Activity_152wda7 第五阶段：本部牵头部门分管领导审批 flowable:candidateGroups="${INITIATOR_SUPERVISOR}"
            variables.put("INITIATOR_SUPERVISOR", variables.get("INITIATOR_SUPERVISOR"));
        } catch (Exception e) {
            log.error("问题直接销号流程数据设置失败了", e);
        }
    }
}
