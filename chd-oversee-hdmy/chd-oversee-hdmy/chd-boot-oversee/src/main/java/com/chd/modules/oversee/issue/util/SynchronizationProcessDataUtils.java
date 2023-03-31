package com.chd.modules.oversee.issue.util;

import com.alibaba.fastjson.JSONObject;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.entity.IssuesSupervisor;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.service.IIssuesAllocationService;
import com.chd.modules.oversee.issue.service.IIssuesSupervisorService;
import com.chd.modules.oversee.issue.service.IOverseeIssueRoleService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowProcessDepartService;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SynchronizationProcessDataUtils {

    // 创建定长线程池
    public static ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);

    private static HistoryService historyService;

    private static IIssuesAllocationService issuesAllocationService;

    private static IIssuesSupervisorService issuesSupervisorService;


    private static IOverseeIssueService overseeIssueService;

    private static WorkflowProcessDepartService workflowProcessDepartService;

    private static IOverseeIssueRoleService overseeIssueRoleService;


    public static void synchronization(Long overseeIssueId){
        getServices();
        if(null!=overseeIssueId&&overseeIssueId.intValue()>0){
            OverseeIssue redisCacheOverseeIssue = overseeIssueService.getRedisCacheOverseeIssue(overseeIssueId);
            synchronization(redisCacheOverseeIssue);
        }
    }

    public static void synchronization(OverseeIssue overseeIssue){
        getServices();
        try{
            if(null!=overseeIssue&& StringUtil.isNotEmpty(overseeIssue.getProcessId())){
                HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(overseeIssue.getProcessId()).singleResult();
                if(processInstance!=null){
                    Map<String,Object> processVariables=processInstance.getProcessVariables();
                    if(MapUtils.isNotEmpty(processVariables)){
                        handleIssuesAllocation(processVariables,overseeIssue.getId());
                        handleIssuesSupervisor(processVariables,overseeIssue.getId());
                        overseeIssueRoleService.synchronization(overseeIssue.getId());
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public static void handleIssuesAllocation(Map<String,Object> processVariables,Long overseeIssueId){
        if(MapUtils.isNotEmpty(processVariables)){
            List<Map<String,Object>> responsibleUnitResponsibleDepartmentList = (List<Map<String, Object>>) processVariables.get(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY);
            if(null!=responsibleUnitResponsibleDepartmentList&&responsibleUnitResponsibleDepartmentList.size()>0){
                List<IssuesAllocation> issuesAllocations = new ArrayList<>();
                for(Map<String,Object> responsibleUnitResponsibleDepartment : responsibleUnitResponsibleDepartmentList){
                    String departId = (String) responsibleUnitResponsibleDepartment.get("departId");
                    if(StringUtil.isNotEmpty(departId)){
                        IssuesAllocation issuesAllocation = new IssuesAllocation();
                        issuesAllocation.setIssueId(overseeIssueId);
                        issuesAllocation.setResponsibleDepartmentOrgId(departId);
                        issuesAllocation.setManageUserId(getWorkflowProcessDepartUserIds((List<String>) responsibleUnitResponsibleDepartment.get(WorkflowConstants.MANAGE_USER_ID_KEY)));
                        issuesAllocation.setSupervisorUserId(getWorkflowProcessDepartUserIds((List<String>) responsibleUnitResponsibleDepartment.get(WorkflowConstants.SUPERVISOR_USER_ID_KEY)));
                        List<String> userIds = (List<String>) responsibleUnitResponsibleDepartment.get(WorkflowConstants.USER_ID_KEY);
                        if(null!=userIds&&userIds.size()>0){
                            issuesAllocation.setResponsibleDepartmentManagerUserId(userIds.stream().collect(Collectors.joining(",")));
                        }

//                        TODO 暂时设置为admin
                        issuesAllocation.setCreateUserId("admin");
                        issuesAllocation.setUpdateUserId("admin");
                        issuesAllocations.add(issuesAllocation);
                    }
                }
                if(null!=issuesAllocations&&issuesAllocations.size()>0){
//                    String collect = issuesAllocations.stream().map((issuesAllocation) -> issuesAllocation.getResponsibleDepartmentOrgId()).collect(Collectors.joining(","));
                    issuesAllocationService.addOrUpdateList(issuesAllocations,overseeIssueId);
                }
            }
        }
    }


    public static void handleIssuesSupervisor(Map<String,Object> processVariables,Long overseeIssueId){
        if(MapUtils.isNotEmpty(processVariables)){
            List<Map<String,Object>> supervisorList = (List<Map<String, Object>>) processVariables.get(WorkflowConstants.SUPERVISOR_LIST_KEY);
            if(null!=supervisorList&&supervisorList.size()>0){
                List<IssuesSupervisor> issuesSupervisors = new ArrayList<>();
                for(Map<String,Object> responsibleUnitResponsibleDepartment : supervisorList){
                    String departId = (String) responsibleUnitResponsibleDepartment.get("departId");
                    if(StringUtil.isNotEmpty(departId)){
                        IssuesSupervisor issuesSupervisor = new IssuesSupervisor();
                        issuesSupervisor.setIssueId(overseeIssueId);
                        issuesSupervisor.setSupervisorOrgId(departId);
                        issuesSupervisor.setManageUserId(getWorkflowProcessDepartUserIds((List<String>) responsibleUnitResponsibleDepartment.get(WorkflowConstants.MANAGE_USER_ID_KEY)));
                        issuesSupervisor.setSupervisorUserId(getWorkflowProcessDepartUserIds((List<String>) responsibleUnitResponsibleDepartment.get(WorkflowConstants.SUPERVISOR_USER_ID_KEY)));
                        List<String> userIds = (List<String>) responsibleUnitResponsibleDepartment.get(WorkflowConstants.USER_ID_KEY);
                        if(null!=userIds&&userIds.size()>0){
                            issuesSupervisor.setUserId(userIds.stream().collect(Collectors.joining(",")));
                        }

                        List<Map<String,Object>> issuesSupervisorListMapList = (List<Map<String, Object>>) processVariables.get(WorkflowConstants.ISSUES_SUPERVISOR_LIST_KEY);
                        if(null!=issuesSupervisorListMapList&&issuesSupervisorListMapList.size()>0){
                            List<IssuesSupervisor> issuesSupervisorList = JSONObject.parseArray(JSONObject.toJSONString(issuesSupervisorListMapList), IssuesSupervisor.class);
                            if(CollectionUtils.isNotEmpty(issuesSupervisorList)){
                                for(IssuesSupervisor issuesSupervisorBind : issuesSupervisorList){
                                    if(null!=issuesSupervisorBind){
                                        if(departId.equals(issuesSupervisorBind.getSupervisorOrgId())){
                                            issuesSupervisor.setBindResponsibleOrgIds(issuesSupervisorBind.getBindResponsibleOrgIds());
                                        }
                                    }
                                }
                            }
                        }

//                        TODO 暂时设置为admin
                        issuesSupervisor.setCreateUserId("admin");
                        issuesSupervisor.setUpdateUserId("admin");
                        issuesSupervisors.add(issuesSupervisor);
                    }
                }
                if(null!=issuesSupervisors&&issuesSupervisors.size()>0){
                    issuesSupervisorService.addOrUpdateList(issuesSupervisors,overseeIssueId);
                }
            }
        }
    }

    private static String getWorkflowProcessDepartUserIds(List<String> workflowProcessDepartIdList){
        String userIds = null;
        if(null!=workflowProcessDepartIdList&&workflowProcessDepartIdList.size()>0){
            List<String> userList = new ArrayList<String>();
            for(String workflowProcessDepartId : workflowProcessDepartIdList){
                WorkflowDepart departById = workflowProcessDepartService.getDepartById(workflowProcessDepartId);
                if (null != departById) {
                    List<WorkflowUserVo> departUsers = workflowProcessDepartService.getDepartUsers(departById.getDepartId(), departById.getRole());
                    if (null != departUsers && departUsers.size() > 0) {
                        userList.add(departUsers.get(0).getId());
                    }
                }
            }
            if(null!=userList&&userList.size()>0){
                userIds = userList.stream().collect(Collectors.joining(","));
            }
//            userIds = workflowProcessDepartIdList.stream().map((workflowProcessDepartId) -> {
//                WorkflowDepart departById = workflowProcessDepartService.getDepartById("");
//                if (null != departById) {
//                    List<WorkflowUserVo> departUsers = workflowProcessDepartService.getDepartUsers(departById.getDepartId(), departById.getRole());
//                    if (null != departUsers && departUsers.size() > 0) {
//                        return departUsers.get(0);
//                    }
//                }
//                return null;
//            }).filter((workflowUserVo) -> null != workflowUserVo && StringUtils.isNotEmpty(workflowUserVo.getId())).map((workflowUserVo) -> workflowUserVo.getId()).collect(Collectors.joining(","));
        }
        return userIds;
    }


    private static void getServices(){
        historyService = SpringContextUtils.getBean(HistoryService.class);
        issuesAllocationService = SpringContextUtils.getBean(IIssuesAllocationService.class);
        issuesSupervisorService = SpringContextUtils.getBean(IIssuesSupervisorService.class);
        overseeIssueService = SpringContextUtils.getBean(IOverseeIssueService.class);
        workflowProcessDepartService = SpringContextUtils.getBean(WorkflowProcessDepartService.class);
        overseeIssueRoleService = SpringContextUtils.getBean(IOverseeIssueRoleService.class);
    }

}
