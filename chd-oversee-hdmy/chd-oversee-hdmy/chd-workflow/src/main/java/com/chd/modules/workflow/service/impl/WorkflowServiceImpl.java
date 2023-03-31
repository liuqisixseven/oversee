//package com.chd.modules.workflow.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.chd.common.exception.BizException;
//import com.chd.common.system.vo.LoginUser;
//import com.chd.common.util.BaseConstant;
//import com.chd.common.util.DateUtils;
//import com.chd.common.util.SpringContextUtils;
//import com.chd.common.util.UUIDGenerator;
//import com.chd.modules.workflow.entity.*;
//import com.chd.modules.workflow.mapper.WorkflowDeploymentMapper;
//import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
//import com.chd.modules.workflow.service.*;
//import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
//import com.chd.modules.workflow.vo.*;
//import com.google.common.collect.Maps;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.flowable.bpmn.converter.BpmnXMLConverter;
//import org.flowable.bpmn.model.*;
//import org.flowable.bpmn.model.Process;
//import org.flowable.engine.*;
//import org.flowable.engine.history.HistoricProcessInstance;
//import org.flowable.engine.history.HistoricProcessInstanceQuery;
//import org.flowable.engine.impl.persistence.entity.ActivityInstanceEntity;
//import org.flowable.engine.repository.Deployment;
//import org.flowable.engine.repository.Model;
//import org.flowable.engine.repository.ProcessDefinition;
//import org.flowable.engine.repository.ProcessDefinitionQuery;
//import org.flowable.engine.runtime.ActivityInstance;
//import org.flowable.engine.runtime.Execution;
//import org.flowable.engine.runtime.ProcessInstance;
//import org.flowable.form.api.FormInfo;
//import org.flowable.form.api.FormRepositoryService;
//import org.flowable.identitylink.api.IdentityLink;
//import org.flowable.idm.api.User;
//import org.flowable.task.api.Task;
//import org.flowable.task.api.history.HistoricTaskInstance;
//import org.flowable.task.service.impl.HistoricTaskInstanceQueryProperty;
//import org.flowable.task.service.impl.persistence.entity.TaskEntity;
//import org.flowable.variable.api.history.HistoricVariableInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLStreamReader;
//import java.io.ByteArrayInputStream;
//import java.io.InputStreamReader;
//import java.io.Serializable;
//import java.util.*;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//public class WorkflowServiceImpl implements WorkflowService {
//
//    @Autowired
//    private WorkflowUserService workflowUserService;
//    @Autowired
//    private RepositoryService repositoryService;
//    @Autowired
//    private IdentityService identityService;
//    @Autowired
//    private RuntimeService runtimeService;
//    @Autowired
//    protected HistoryService historyService;
//    @Autowired
//    private WorkflowProcessMapper workflowProcessMapper;
//    @Autowired
//    private TaskService taskService;
//    @Autowired
//    private WorkflowModelService workflowModelService;
//    @Autowired
//    private ManagementService managementService;
//    @Autowired
//    private WorkflowCommentService workflowCommentService;
//    @Autowired
//    private FormRepositoryService formRepositoryService;
//    @Autowired
//    private WorkflowDeploymentMapper workflowDeploymentMapper;
//    @Autowired
//    private WorkflowManageService workflowManageService;
//    @Autowired
//    private WorkflowProcessDepartService workflowProcessDepartService;
//    @Autowired
//    private WorkflowCandidateGroupService workflowCandidateGroupService;
//    @Autowired
//    private WorkflowVariablesService workflowVariablesService;
//    @Autowired
//    private ScheduledExecutorService scheduledExecutorService;
//
//    @Autowired
//    private WorkflowMessageService workflowMessageService;
//    @Autowired
//    private WorkflowProcessUsersSerivce workflowProcessUsersSerivce;
//
//    @Override
//    public WorkflowProcess getProcessById(String id) {
//        return workflowProcessMapper.selectById(id);
//    }
//
//    @Override
//    public Map<String, Object> updateProcessTaskInfoById(String id) {
//        Map<String, Object> returnMap = Maps.newHashMap();
//        WorkflowProcess process = workflowProcessMapper.selectById(id);
//        if (process != null) {
//            try {
//                List<WorkflowUserTaskVo> userTasks = findNextTaskByProcessId(id);
//                if (CollectionUtils.isNotEmpty(userTasks)) {
//                    List<Map<String, Object>> userTaskListMap = new ArrayList<>();
//                    List<String> nextUserTasks = new ArrayList<>();
//                    for (WorkflowUserTaskVo userTask : userTasks) {
//                        Map<String, Object> userTaskMap = Maps.newHashMap();
//                        userTaskMap.put("taskId", userTask.getTaskId());
//                        if (CollectionUtils.isNotEmpty(userTask.getUsers())) {
//                            for (WorkflowUserVo user : userTask.getUsers()) {
//                                nextUserTasks.add(user.getName());
//                                mapAddStringArrayValue("userTaskName", user.getName(), userTaskMap);
//                                mapAddStringArrayValue("userName", user.getName(), userTaskMap);
//                                mapAddStringArrayValue("userId", user.getId(), userTaskMap);
//                            }
//                        }
//                        if (CollectionUtils.isNotEmpty(userTask.getDeparts())) {
//                            for (WorkflowDepart depart : userTask.getDeparts()) {
//                                if (CollectionUtils.isNotEmpty(depart.getUsers())) {
//                                    for (WorkflowUserVo user : depart.getUsers()) {
//                                        nextUserTasks.add(depart.getDepartName() + ":" + user.getName());
//                                        mapAddStringArrayValue("userTaskName", (depart.getDepartName() + ":" + user.getName()), userTaskMap);
//                                        mapAddStringArrayValue("userName", (depart.getDepartName() + ":" + user.getName()), userTaskMap);
//                                        mapAddStringArrayValue("userId", user.getId(), userTaskMap);
//                                    }
//                                } else {
//                                    nextUserTasks.add(depart.getDepartName());
//                                    mapAddStringArrayValue("userTaskName", (depart.getDepartName()), userTaskMap);
//                                    mapAddStringArrayValue("departName", depart.getDepartName(), userTaskMap);
//                                }
//                            }
//                        }
//                        userTaskListMap.add(userTaskMap);
//                    }
//                    if (CollectionUtils.isNotEmpty(nextUserTasks)) {
//                        String nextUserTask = StringUtils.join(nextUserTasks);
//                        process.setNextUserTask(nextUserTask);
//                        process.setNextTaskTime(new Date());
//                        process.setUpdateTime(new Date());
//                        workflowProcessMapper.updateById(process);
//                        returnMap.put("process", process);
//                        returnMap.put("userTasks", userTasks);
////                        workflowMessageService.sendNextTaskList(process.getBizId(),userTasks);
//                    }
//                }
//            } catch (Exception ex) {
//                log.error("处理流程下个节点异常，processId=" + id, ex);
//            }
//        }
//        return null;
//    }
//
//    private void mapAddStringArrayValue(String key, String value, Map<String, Object> map) {
//        if (null != map) {
//            List<String> stringArrayValue = (List<String>) map.get(key);
//            if (null == stringArrayValue) {
//                stringArrayValue = new ArrayList<>();
//                map.put(key, stringArrayValue);
//            }
//            stringArrayValue.add(value);
//        }
//    }
//
//    @Override
//    public WorkflowProcess launchProcess(WorkflowProcess process) {
//        WorkflowUserVo user = workflowUserService.getUserById(process.getStartUserId());
////        FlowUserDepart depart=userService.getUserDepart(process.getStartUserId());
////        if(depart==null){
////            throw new BizException(WorkflowErrorCode.USER_DEPART_NO_FOUND);
////        }
//        Map<String, Object> variables = new HashMap<>();
//        variables.put(WorkflowConstants.FLOW_USER_OWNER, process.getStartUserId());//添加默认自己用户
//        variables.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);//启用用户节点"跳过表达式"功能
//
//        if (StringUtils.isBlank(process.getProcessDefKey()) && StringUtils.isNotBlank(process.getProcessCategory())) {
//            List<WorkflowDeployment> deployments = workflowDeploymentMapper.queryListByCategory(process.getProcessCategory());
//            if (CollectionUtils.isNotEmpty(deployments)) {
//                Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deployments.get(0).getId()).singleResult();
//                if (deployment != null) {
//                    process.setProcessDefKey(deployment.getKey());
//                }
//            }
//        }
//        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
//        if (StringUtils.isNotBlank(process.getProcessDefId())) {
//            processDefinitionQuery.processDefinitionId(process.getProcessDefId());
//        }
//        if (StringUtils.isNotBlank(process.getProcessDefKey())) {
//            processDefinitionQuery.processDefinitionKey(process.getProcessDefKey());
//        }
//        if (StringUtils.isNotBlank(process.getProcessCategory())) {
//            processDefinitionQuery.processDefinitionCategory(process.getProcessCategory());
//        }
//        processDefinitionQuery.latestVersion().orderByProcessDefinitionVersion().desc();
//        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
//        if (CollectionUtils.isEmpty(processDefinitionList)) {
//            throw new BizException(WorkflowErrorCode.PROCESS_DEFINITION_NO_FOUND);
//        }
//        ProcessDefinition processDefinition = processDefinitionList.get(0);
//        if (processDefinition.isSuspended()) {
//            throw new BizException(WorkflowErrorCode.PROCESS_SUSPENDED);
//        }
//        // 设置流程发起人，act_hi_procinst 表中中的START_USER_ID_字段
//        identityService.setAuthenticatedUserId(process.getStartUserId());
//        //TODO 权限校验用户是否可以提交流程
//        boolean hasProcessDepart = false;
//        if (process.getVariables() != null) {
//            if (MapUtils.isNotEmpty(process.getVariables().getVariables())) {
//                variables.putAll(process.getVariables().getVariables());
//            }
//            if (CollectionUtils.isNotEmpty(process.getVariables().getDepartList())) {
//                Map<String, List<String>> processDepartVariables = workflowProcessDepartService.getProcessDepartVariableMap(process.getVariables().getDepartList());
//                variables.putAll(processDepartVariables);
//                hasProcessDepart = true;
//            }
//        }
//
////        设置督办人
//        variables.put(WorkflowConstants.SUPERVISOR_LIST_KEY, workflowVariablesService.getSubprocessUserDataList((String) variables.get("supervisorOrgIds")));
////        设置责任部门
//        variables.put(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY, workflowVariablesService.getSubprocessUserDataList((String) variables.get("responsibleOrgIds")));
//
//        // 启动流程,TODO 流程变量设置
//        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
//                .processDefinitionId(processDefinition.getId())
//                .name(process.getTitle())
//                .businessKey(process.getBizId())
//                .variables(variables)
////                .startFormVariables(process.getVariables())
//                .start();
////        ProcessInstance processInstance = runtimeService.startProcessInstanceWithForm(processDefinition.getId(),
////                process.getBizId(), process.getVariables(), process.getTitle());
//        Integer issueLaunchType = (Integer) variables.get("issueLaunchType");
//        if (null != issueLaunchType && issueLaunchType.intValue() == 1) {
//            if (StringUtils.isNotEmpty(processInstance.getSuperExecutionId())) {
//                runtimeService.setVariable(processInstance.getSuperExecutionId(), BaseConstant.OVERSEE_ISSUE_PROCESS_ID, processInstance.getId());
//                runtimeService.setVariable(processInstance.getSuperExecutionId(), BaseConstant.OVERSEE_ISSUE_PROCESS_DEF_ID, processDefinition.getId());
//            }
//        }
//
//
//        // 更新业务表流程实例ID
//        Date now = new Date();
//        process.setApplyTime(now);
//        process.setStartUserName(user.getName());
//        process.setProcessDefId(processDefinition.getId());
//        process.setId(processInstance.getId());
//        process.setCreateBy(user.getName());
//        process.setCreateTime(now);
//        process.setUpdateTime(now);
//        process.setUpdateBy(user.getName());
//        process.setState(WorkflowConstants.processState.START);
//
//        Integer result = workflowProcessMapper.insertProcess(process);
//        if (hasProcessDepart) {
//            workflowProcessDepartService.batchAddProcessDepart(process.getVariables().getDepartList(), process.getId());
//        }
//        workflowCommentService.addStartProcessComment(processInstance.getId(), process.getStartUserId(), null, "提交", variables);
//        //把流程参数透传下去，跳转到销号开始节点
//        Object oldIssueObj = process.getVariables().getVariables().get(BaseConstant.PROCESS_OLD_ISSUES_KEY);
//        if(oldIssueObj!=null && Integer.parseInt(oldIssueObj.toString())==1){
//            process.getVariables().getVariables().putAll(variables);
//        }
//        return result != null && result.intValue() > 0 ? process : null;
//    }
//
//    /*BACKPOINT_Activity_06l24ki 第五阶段：责任单位责任部门业务人员发起销号申请 flowable:candidateUsers="${responsibleUnitResponsibleDepartment.userId}"
//    Activity_01c91ld 第五阶段：责任单位责任部门负责人审核并发起会签 flowable:candidateGroups="${responsibleUnitResponsibleDepartment.manageUserId}"
//    Activity_1feheze 第五阶段：责任单位责任部门分管领导审核 flowable:candidateGroups="${responsibleUnitResponsibleDepartment.supervisorUserId}"
//    Activity_0y71kmc 第五阶段：责任单位牵头部门经办人审核 flowable:candidateUsers="${RESPONSIBLE_HANDLE_EXECUTOR}"
//    Activity_1w18nj9 第五阶段：责任单位牵头部门负责人审核 flowable:candidateGroups="${RESPONSIBLE_HANDLE_MANAGER}"
//    Activity_1tfyw94 第五阶段：责任单位牵头部门分管领导审核 flowable:candidateGroups="${RESPONSIBLE_HANDLE_SUPERVISOR}"
//    Activity_0zu1g62 第五阶段：责任单位党委书记审批  flowable:candidateUsers="${RESPONSIBLE_EXECUTE_SECRETARY}"
//    Activity_1a8ahjx 第五阶段：本部督办部门经办人审核 flowable:candidateUsers="${SUPERVISOR_EXECUTOR}"
//    Activity_1w7daw1 第五阶段：本部督办部门负责人审核并发起会签 flowable:candidateGroups="${supervisor.manageUserId}"
//    Activity_1lp65yj 第五阶段：本部督办部门分管领导审核 flowable:candidateGroups="${supervisor.supervisorUserId}"
//    Activity_14bgyi3 第五阶段：本部牵头部门经办人审核 flowable:assignee="${USER_OWNER}"
//    Activity_0kli5pv 第五阶段：本部牵头部门负责人审核 flowable:candidateGroups="${INITIATOR_MANAGER}"
//    Activity_152wda7 第五阶段：本部牵头部门分管领导审批 flowable:candidateGroups="${INITIATOR_SUPERVISOR}"*/
//    public void launchCancelProcess(WorkflowProcess processInstance) {
//        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
//        task.setAssignee("e9ca23d68d884d4ebb19d07889727dae");
//        taskService.saveTask(task);
//        Map<String, Object> variables = processInstance.getVariables().getVariables();
//        WorkflowFlowElementUtils.setOldIssueVariable(variables);
//        String cancelNodeKey = "BACKPOINT_Activity_06l24ki";
//        runtimeService.createChangeActivityStateBuilder()
//                .processInstanceId(processInstance.getId())
//                .moveActivityIdTo(task.getTaskDefinitionKey(), cancelNodeKey)
//                .processVariables(variables)
//                .changeState();
////        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
////        task.setAssignee(userId);
////        taskService.saveTask(task);
//    }
//
//    private String findCancelResponsibleLead(String processId, String cancelNodeKey) {
//        WorkflowProcessDetailVo result = processDetail(processId);
//        WorkflowProcessVo processVo = result.getProcess();
//        if (CollectionUtils.isNotEmpty(result.getUserTasks())) {
//            List<WorkflowUserTaskVo> userTasks = result.getUserTasks();
//            Map<String, Object> taskVariables = new HashMap<>();
//            taskVariables.put(WorkflowConstants.FLOW_USER_OWNER, new WorkflowUserVo(processVo.getStartUserId(), processVo.getStartUserName()));
//            Map<String, List<WorkflowDepart>> bizVariableDepart = workflowUserService.getBizDepartVariables(processVo.getCategory(), processVo.getBizId(), processVo.getStartUserId());
//            if (MapUtils.isNotEmpty(bizVariableDepart)) {
//                taskVariables.putAll(bizVariableDepart);
//            }
//            Map<String, List<WorkflowUserVo>> processUserList = workflowProcessUsersSerivce.getProcessUsersVariables(processId, null, null);
//            if (MapUtils.isNotEmpty(processUserList)) {
//                taskVariables.putAll(processUserList);
//            }
//            List<WorkflowProcessDepart> processDeparts = workflowProcessDepartService.getProcessDepartListByProcessId(processId);
//            if (CollectionUtils.isNotEmpty(processDeparts)) {
//                Map<String, List<WorkflowDepart>> departList = workflowProcessDepartService.getProcessDepartVariable(processDeparts);
//                if (MapUtils.isNotEmpty(departList)) {
//                    taskVariables.putAll(departList);
//                }
//            }
//            userTasks = workflowVariablesService.setUserTaskListVariables(userTasks, taskVariables);
//            userTasks = userTasks.stream().filter(a -> a.getTaskKey().equals(cancelNodeKey)).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(userTasks)) {
//                WorkflowUserVo user = userTasks.get(0).getUsers().get(0);
//                return user.getId();
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public WorkflowProcess launchProcess(String bizId, String bizUrl, String title, String category, String startUserId, WorkflowVariablesVo variables) {
//        WorkflowUserVo user = workflowUserService.getUserById(startUserId);
//        WorkflowProcess process = new WorkflowProcess();
//        process.setBizUrl(bizUrl);
//        process.setBizId(bizId);
//        process.setTitle(title);
//        process.setProcessCategory(category);
//        process.setStartUserId(startUserId);
//        process.setStartUserName(user.getName());
//        process.setVariables(variables);
//        WorkflowProcess result = launchProcess(process);
//        return result;
//    }
//
//    @Override
//    public boolean revokeProcess(WorkflowProcess process) {
//        String processId = process.getId();
//        Map<String, Object> taskForm = new HashMap<>();
//
//        taskForm.put(WorkflowConstants.taskStatusKey, WorkflowConstants.taskStatus.APPROVED);
//        WorkflowProcess workflowProcess = workflowProcessMapper.selectById(processId);
//        Assert.notNull(workflowProcess, "审批流程不存在");
//        Assert.isTrue(workflowProcess.getStartUserId().equals(process.getStartUserId()), "无法撤回");
//
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//                .processInstanceId(process.getId()).singleResult();
//        if (processInstance != null) {
//            //3.执行撤回
//            Activity disActivity = workflowModelService.findActivityByName(processInstance.getProcessDefinitionId(), WorkflowConstants.FLOW_SUBMITTER);
//            Assert.notNull(disActivity, "撤回失败");
//            //1.添加撤回意见
//            workflowCommentService.addComment(null, null, "撤回", process.getStartUserId(), process.getId(),
//                    WorkflowConstants.CommentTypeEnum.CH, process.getRemark());
//            //2.设置提交人
//            runtimeService.setVariable(process.getId(), WorkflowConstants.FLOW_SUBMITTER_VAR, processInstance.getStartUserId());
//            //4.删除运行和历史的节点信息
//            deleteActivity(disActivity.getId(), process.getId());
//            //5.执行跳转
//            List<Execution> executions = runtimeService.createExecutionQuery().parentId(process.getId()).list();
//            List<String> executionIds = new ArrayList<>();
//            executions.forEach(execution -> executionIds.add(execution.getId()));
//            runtimeService.createChangeActivityStateBuilder()
//                    .moveExecutionsToSingleActivityId(executionIds, disActivity.getId())
//                    .changeState();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean suspendOrActivateProcessInstanceById(String processId, WorkflowProcessVo.SuspensionState suspensionState) {
//        if (WorkflowProcessVo.SuspensionState.active.equals(suspensionState)) {
//            runtimeService.activateProcessInstanceById(processId);
//            return true;
//        } else if (WorkflowProcessVo.SuspensionState.suspended.equals(suspensionState)) {
//            runtimeService.suspendProcessInstanceById(processId);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public IPage<WorkflowProcess> involvedUserProcessList(WorkflowQueryVo query, String userId) {
//        HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery();
//
//        if (!workflowUserService.isWorkflowSuperUser(userId)) {
//            instanceQuery.involvedUser(userId);
//        }
//
//        if (StringUtils.isNotBlank(query.getProcessCategory())) {
//            instanceQuery.processDefinitionCategory(query.getProcessCategory());
//        }
//        if (StringUtils.isNotBlank(query.getTitle())) {
//            instanceQuery.processInstanceNameLike("%" + query.getTitle() + "%");
//        }
//        String state = query.getState();
//        if (StringUtils.isNotBlank(state)) {
//            if ("running".equals(state)) {
//                instanceQuery.unfinished();
//            } else if ("completed".equals(state)) {
//                instanceQuery.finished();
//            } else if (!"all".equals(state)) {
//                throw new BizException("Illegal state filter value passed, only 'running', 'completed' or 'all' are supported");
//            }
//        } else {
//            instanceQuery.unfinished();
//        }
//
//
//        // Sort and ordering
//        String sort = query.getSort();
//        if (StringUtils.isNotBlank(sort)) {
//
//            if ("created-desc".equals(sort)) {
//                instanceQuery.orderByProcessInstanceStartTime().desc();
//            } else if ("created-asc".equals(sort)) {
//                instanceQuery.orderByProcessInstanceStartTime().asc();
//            } else if ("ended-desc".equals(sort)) {
//                instanceQuery.orderByProcessInstanceEndTime().desc();
//            } else if ("ended-asc".equals(sort)) {
//                instanceQuery.orderByProcessInstanceEndTime().asc();
//            }
//        } else {
//
//            instanceQuery.orderByProcessInstanceStartTime().desc();
//        }
//
//        Page<WorkflowProcess> result = new Page<>(query.getCurrent(), query.getSize());
//        result.setTotal(instanceQuery.count());
//        if (result.getTotal() > 0) {
//            List<HistoricProcessInstance> instances = instanceQuery.listPage(query.getOffset(),
//                    query.getPageSize());
//            List<WorkflowProcess> workflowProcessList = convertInstanceList(instances);
//            result.setRecords(workflowProcessList);
//
//            scheduledExecutorService.schedule(new TimerTask() {
//                @Override
//                public void run() {
////                    for(WorkflowProcess process:workflowProcessList){
//                    //已经完成的不需要有待处理人
//                    workflowProcessList.stream().filter(a -> !a.getState().equals(WorkflowConstants.processState.COMPLETED)).forEach(
//                            process -> {
//                                updateProcessTaskInfoById(process.getId());
//                            }
//                    );
////                    }
//                }
//            }, 2, TimeUnit.SECONDS);
//
//        }
//        return result;
//    }
//
//
//    @Override
//    public WorkflowTaskVo findTaskById(String taskId) {
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        if (task != null) {
//            WorkflowTaskVo workflowTaskVo = new WorkflowTaskVo(task);
//            getPreviousTaskDataByCurrentTask(task, workflowTaskVo);
//            WorkflowProcess process = workflowProcessMapper.selectById(task.getProcessInstanceId());
//            if (process != null) {
//                workflowTaskVo.setBizId(process.getBizId());
//            }
//            if (null == workflowTaskVo.getMyTaskVariables()) {
//                workflowTaskVo.setMyTaskVariables(Maps.newHashMap());
//            }
//
//            if (WorkflowConstants.TASK_FORMID.DISTRIBUTION_SUPERVISION_DEPARTMENT.equals(task.getFormKey())) {
//                Map<String, Object> myTaskVariables = workflowTaskVo.getMyTaskVariables();
//                List<HistoricVariableInstance> isSupervises = historyService.createHistoricVariableInstanceQuery().processInstanceId(workflowTaskVo.getProcessId()).variableName(WorkflowConstants.IS_SUPERVISE_KEY).list();
//                if (null != isSupervises && isSupervises.size() > 0) {
//                    HistoricVariableInstance historicVariableInstance = isSupervises.get(0);
//                    if (null != historicVariableInstance) {
//                        myTaskVariables.put(WorkflowConstants.IS_SUPERVISE_KEY, historicVariableInstance.getValue());
//                    }
//                }
//                List<HistoricVariableInstance> supervisorOrgIdss = historyService.createHistoricVariableInstanceQuery().processInstanceId(workflowTaskVo.getProcessId()).variableName(WorkflowConstants.SUPERVISOR_ORG_IDS_KEY).list();
//                if (null != supervisorOrgIdss && supervisorOrgIdss.size() > 0) {
//                    HistoricVariableInstance historicVariableInstance = supervisorOrgIdss.get(0);
//                    if (null != historicVariableInstance) {
//                        myTaskVariables.put(WorkflowConstants.SUPERVISOR_ORG_IDS_KEY, historicVariableInstance.getValue());
//                    }
//                }
//                List<HistoricVariableInstance> severitys = historyService.createHistoricVariableInstanceQuery().processInstanceId(workflowTaskVo.getProcessId()).variableName(WorkflowConstants.SEVERITY_KEY).list();
//                if (null != severitys && severitys.size() > 0) {
//                    HistoricVariableInstance historicVariableInstance = severitys.get(0);
//                    if (null != historicVariableInstance) {
//                        myTaskVariables.put(WorkflowConstants.SEVERITY_KEY, historicVariableInstance.getValue());
//                    }
//                }
//            }
//            if (WorkflowConstants.TASK_FORMID.DEPTS_SELECT.equals(task.getFormKey())) {
//                Map<String, Object> myTaskVariables = workflowTaskVo.getMyTaskVariables();
//                List<HistoricVariableInstance> responsibleUnitOrgId = historyService.createHistoricVariableInstanceQuery().processInstanceId(workflowTaskVo.getProcessId()).variableName(WorkflowConstants.RESPONSIBLE_UNIT_ORG_ID_KEY).list();
//                if (null != responsibleUnitOrgId && responsibleUnitOrgId.size() > 0) {
//                    HistoricVariableInstance historicVariableInstance = responsibleUnitOrgId.get(0);
//                    if (null != historicVariableInstance) {
//                        myTaskVariables.put(WorkflowConstants.RESPONSIBLE_UNIT_ORG_ID_KEY, historicVariableInstance.getValue());
//                    }
//                }
//                List<HistoricVariableInstance> responsibleLeadDepartmentOrgId = historyService.createHistoricVariableInstanceQuery().processInstanceId(workflowTaskVo.getProcessId()).variableName(WorkflowConstants.RESPONSIBLE_LEAD_DEPARTMENT_ORG_ID_KEY).list();
//                if (null != responsibleLeadDepartmentOrgId && responsibleLeadDepartmentOrgId.size() > 0) {
//                    HistoricVariableInstance historicVariableInstance = responsibleLeadDepartmentOrgId.get(0);
//                    if (null != historicVariableInstance) {
//                        myTaskVariables.put(WorkflowConstants.RESPONSIBLE_LEAD_DEPARTMENT_ORG_ID_KEY, historicVariableInstance.getValue());
//                    }
//                }
//            }
//
//            if (WorkflowConstants.TASK_FORMID.PIN_NUMBER.equals(task.getFormKey()) || WorkflowConstants.TASK_FORMID.TEXT_AND_TIME.equals(task.getFormKey())) {
//                Map<String, Object> myTaskVariables = workflowTaskVo.getMyTaskVariables();
//                Map<String, Object> responsibleUnitResponsibleDepartment = (Map<String, Object>) runtimeService.getVariableLocal(runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult().getParentId(), WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY);
//                if (null != responsibleUnitResponsibleDepartment) {
//                    myTaskVariables.put(WorkflowConstants.DEPART_ID_KEY, responsibleUnitResponsibleDepartment.get(WorkflowConstants.DEPART_ID_KEY));
//                }
//            }
////             ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
////             List<UserTask> userTasks= workflowModelService.getDepolymentUserTask(processDefinition.getDeploymentId());
////             if(CollectionUtils.isNotEmpty(userTasks)){
////                 for(UserTask userTask:userTasks){
////                     if(userTask.getId().equals(task.getTaskDefinitionKey())){
////                         workflowTaskVo.setFormKey(userTask.getFormKey());
////                         if(WorkflowConstants.TASK_FORMID.PIN_NUMBER.equals(task.getFormKey())){
//            Map<String, Object> myTaskVariables = workflowTaskVo.getMyTaskVariables();
//            myTaskVariables.put(WorkflowConstants.FORM_MAP_KEY, taskService.getVariableLocal(task.getId(), WorkflowConstants.FORM_MAP_KEY));
//            workflowTaskVo.setMyTaskVariables(myTaskVariables);
////                         }
////                     }
////                 }
////             }
//            return workflowTaskVo;
//        }
//        return null;
//    }
//
//    @Override
//    public void getPreviousTaskDataByCurrentTask(String taskId, WorkflowTaskVo workflowTaskVo) {
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        if (null != task) {
//            getPreviousTaskDataByCurrentTask(task, workflowTaskVo);
//        }
//    }
//
//    @Override
//    public void getPreviousTaskDataByCurrentTask(Task task, WorkflowTaskVo workflowTaskVo) {
//        if (task != null && null != workflowTaskVo) {
//            Map<String, Object> selectMap = Maps.newHashMap();
//            selectMap.put("processId", task.getProcessInstanceId());
//            selectMap.put("executionId", task.getExecutionId());
//            String previousTaskDataSrc = workflowProcessMapper.getPreviousTaskDataSrc(selectMap);
//            //流程启动时，只会有提交人，不会有task
//            if (StringUtils.isNotEmpty(previousTaskDataSrc)) {
//                workflowTaskVo.setPreviousTaskDataSrc(previousTaskDataSrc);
//                String[] array = workflowTaskVo.getPreviousTaskDataSrc().split(",");
//                if (null != array && array.length > 0) {
//                    workflowTaskVo.setPreviousTaskUserNames(array[0]);
//                    if (array.length > 2) {
//                        workflowTaskVo.setPreviousTaskUserEndTimes(array[2]);
//                    }
//                }
//            } else {
//                workflowTaskVo.setPreviousTaskUserNames(workflowTaskVo.getStartUserName());
//                String time = DateFormatUtils.format(workflowTaskVo.getApplyTime(), DateUtils.FORMAT_DATETIME);
//                workflowTaskVo.setPreviousTaskUserEndTimes(time);
//            }
//            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).executionId(task.getExecutionId()).orderBy(HistoricTaskInstanceQueryProperty.END).desc().listPage(0, 1);
//            HistoricTaskInstance historicTaskInstance = null;
//            if (null != historicTaskInstances && historicTaskInstances.size() > 0) {
//                historicTaskInstance = historicTaskInstances.get(0);
//            }
//            if (null != historicTaskInstance) {
//                String assignee = historicTaskInstance.getAssignee();
//                if (null != assignee) {
//                    WorkflowUserVo userById = workflowUserService.getUserById(assignee);
//                    if (null != userById && StringUtils.isNotBlank(userById.getName())) {
//                        workflowTaskVo.setPreviousTaskUserNames(userById.getName());
//                    }
//                }
//                if (null != historicTaskInstance.getEndTime()) {
//                    workflowTaskVo.setPreviousTaskUserEndTimes(DateUtils.formatDate(historicTaskInstance.getEndTime(), DateUtils.FORMAT_DATETIME));
//                }
//                List<WorkflowComment> workflowCommentList = workflowCommentService.findListByTaskKey(task.getProcessInstanceId(), historicTaskInstance.getId());
//                if (null != workflowCommentList && workflowCommentList.size() > 0) {
//                    workflowTaskVo.setPreviousTaskUserComments(workflowCommentList.stream().filter((workflowComment) -> null != workflowComment && StringUtils.isNotBlank(workflowComment.getComment())).map((workflowComment) -> workflowComment.getComment()).collect(Collectors.joining(",")));
//                }
//            }
//        }
//    }
//
//    @Override
//    public List<WorkflowUserTaskVo> findNextTaskByProcessId(String processId) {
//        List<WorkflowUserTaskVo> userTaskList = new ArrayList<>();
//        List<WorkflowTaskVo> taskList = workflowProcessMapper.findNextTaskNode(processId);
//        if (CollectionUtils.isNotEmpty(taskList)) {
////            for(WorkflowTaskVo task:taskList){
////                WorkflowUserTaskVo userTask=new WorkflowUserTaskVo();
////                userTask.setTaskId(task.getTaskId());
////                userTask.setTaskKey(task.getTaskKey());
////                userTask.set
////                if(StringUtils.isBlank( task.getAssignee())){
////                    workflowUserService.getUserById(task.getAssignee());
////                }
////            }
//            userTaskList = workflowManageService.convertTask(taskList);
//        }
//        return userTaskList;
//    }
//
//    private List<WorkflowProcess> convertInstanceList(List<HistoricProcessInstance> processInstanceList) {
//        if (CollectionUtils.isNotEmpty(processInstanceList)) {
//            List<String> processIds = new ArrayList<>();
//            for (HistoricProcessInstance processInstance : processInstanceList) {
//                processIds.add(processInstance.getId());
//            }
//
//            List<WorkflowProcess> processList = workflowProcessMapper.processListByIds(processIds);
//            processList.forEach(a -> {
//                String userNameStr = a.getNextUserTask();
//                if (StringUtils.isNotBlank(userNameStr)) {
//                    String userName = userNameStr.substring(userNameStr.indexOf(":") + 1, userNameStr.length() - 1);
//                    a.setNextUserTask(userName);
//                }
//            });
//            //排序
////            List<WorkflowProcess> result=new ArrayList<>();//排序返回
////            if(CollectionUtils.isNotEmpty(processList)){
////                for(String processId:processIds){
////                    for(WorkflowProcess process:processList){
////                        if(process.getId().equals(processId)){
////                            result.add(process);
////                        }
////                    }
////                }
////            }
//            return processList;
//        }
//        return null;
//    }
//
//    private Map<String, Object> taskVariables(WorkflowTaskFormVo form, String processId) {
//        Map<String, Object> myTaskVariables = new HashMap<>();
//        if (MapUtils.isNotEmpty(form.getForm())) {
//            myTaskVariables.putAll(form.getForm());
//        }
//        if ("deptsSelect".equals(form.getFormId())) {//部门分配
//            Object depts = form.getForm().get("depts");
//            String departIds = MapUtils.getString(form.getForm(), "depts");
//            if (StringUtils.isNotBlank(departIds)) {
//                List<WorkflowProcessDepart> processDepartList = workflowProcessDepartService.toProcessDepart(departIds, WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE);
//                if (CollectionUtils.isNotEmpty(processDepartList)) {
//                    workflowProcessDepartService.batchAddProcessDepart(processDepartList, processId);
//                }
//            }
//        }
//        List<WorkflowProcessDepart> processDepartList = workflowProcessDepartService.getProcessDepartListByProcessId(processId);
//        Map<String, List<String>> departVariables = workflowProcessDepartService.getProcessDepartVariableMap(processDepartList);
//        if (MapUtils.isNotEmpty(departVariables)) {
//            myTaskVariables.putAll(departVariables);
//        }
//        return myTaskVariables;
//    }
//
//
//    @Override
//    public List<LoginUser> processApprovers(String processInstanceId) {
//        List<LoginUser> users = new ArrayList<>();
//
//        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
//        if (CollectionUtils.isNotEmpty(list)) {
//            list.forEach(task -> {
//                if (StringUtils.isNotBlank(task.getAssignee())) {
//                    //1.审批人ASSIGNEE_是用户id
//                    WorkflowUserVo user = workflowUserService.getUserById(task.getAssignee());
//                    LoginUser assignee = new LoginUser();
//                    assignee.setId(task.getAssignee());
//                    assignee.setUsername(user.getName());
//                    users.add(assignee);
////                    User user = identityService.createUserQuery().userId(task.getAssignee()).singleResult();
////                    if (user != null) {
////                        users.add(user);
////                    }
//                    //2.审批人ASSIGNEE_是组id
////                    List<User> gusers = identityService.createUserQuery().memberOfGroup(task.getAssignee()).list();
////                    if (CollectionUtils.isNotEmpty(gusers)) {
////                        users.addAll(gusers);
////                    }
//                } else {
//                    List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
//                    if (CollectionUtils.isNotEmpty(identityLinks)) {
//                        identityLinks.forEach(identityLink -> {
//                            //3.审批人ASSIGNEE_为空,用户id
//                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
//                                User user = identityService.createUserQuery().userId(identityLink.getUserId()).singleResult();
//                                if (user != null) {
////                                    users.add(user);
//                                }
//                            } else {
//                                //4.审批人ASSIGNEE_为空,组id
//                                List<User> gusers = identityService.createUserQuery().memberOfGroup(identityLink.getGroupId()).list();
//                                if (CollectionUtils.isNotEmpty(gusers)) {
////                                    users.addAll(gusers);
//                                }
//                            }
//                        });
//                    }
//                }
//            });
//        }
//        return users;
//    }
//
//    @Override
//    public WorkflowProcessDetailVo processDetail(String processId) {
//        WorkflowProcessDetailVo result = new WorkflowProcessDetailVo();
//        WorkflowProcess process = workflowProcessMapper.selectById(processId);
//        Map<String, Object> processVariables = null;
//        if (process != null) {
//            result.setProcess(new WorkflowProcessVo(process));
//            WorkflowBusinessProcessService bizProcess = SpringContextUtils.getBean(WorkflowBusinessProcessService.class);
//            if (bizProcess != null) {
//                processVariables = bizProcess.getProcessVariables(process.getProcessCategory(), process.getBizId(), new WorkflowUserVo(process.getStartUserId(), process.getStartUserName()));
//            }
//        }
//        List<WorkflowComment> comments = workflowCommentService.getCommentByProcessInstanceId(processId);
//        result.setComments(comments);
//        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
//        if (processInstance != null) {
//            BpmnModel bpmnModel = workflowModelService.getBpmnModelByProcessDefId(processInstance.getProcessDefinitionId());
////             BpmnModel bpmnModel = workflowModelService.getDeploymentModel(processInstance.getDeploymentId());
//            List<FlowElement> sequenceFlows = WorkflowFlowElementUtils.getProcessPath(bpmnModel, processVariables);
//            List<UserTask> userTasks = WorkflowFlowElementUtils.getUserTaskFromActivity(sequenceFlows);
////        List<UserTask> userTasks= workflowModelService.getDepolymentUserTask(processInstance.getDeploymentId());
//            List<WorkflowUserTaskVo> userTaskVos = workflowManageService.convertUserTask(userTasks, processId);
//            result.setUserTasks(userTaskVos);
//        }
//        return result;
//    }
//
//    @Override
//    public WorkflowProcessDetailVo processDetailByBizId(String bizId) {
//        List<WorkflowProcess> processList = workflowProcessMapper.findByBiz(bizId);
//        if (CollectionUtils.isNotEmpty(processList)) {
//            return processDetail(processList.get(0).getId());
//        }
//        return null;
//    }
//
//    protected TaskEntity createSubTask(TaskEntity ptask, String assignee) {
//        return this.createSubTask(ptask, ptask.getId(), assignee);
//    }
//
//    /**
//     * 创建子任务
//     *
//     * @param ptask    创建子任务
//     * @param assignee 子任务的执行人
//     * @return
//     */
//    protected TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
//        TaskEntity task = null;
//        if (ptask != null) {
//            //1.生成子任务
//            task = (TaskEntity) taskService.newTask(UUIDGenerator.generate());
//            task.setCategory(ptask.getCategory());
//            task.setDescription(ptask.getDescription());
//            task.setTenantId(ptask.getTenantId());
//            task.setAssignee(assignee);
//            task.setName(ptask.getName());
//            task.setParentTaskId(ptaskId);
//            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
//            task.setProcessInstanceId(ptask.getProcessInstanceId());
//            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
//            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
//            task.setPriority(ptask.getPriority());
//            task.setCreateTime(new Date());
//            taskService.saveTask(task);
//        }
//        return task;
//    }
//
//    protected void deleteActivity(String disActivityId, String processInstanceId) {
//        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
//        String sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and t.ACT_ID_ = #{disActivityId} " +
//                " order by t.END_TIME_ ASC";
//        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql)
//                .parameter("processInstanceId", processInstanceId)
//                .parameter("disActivityId", disActivityId).list();
//        //删除运行时和历史节点信息
//        if (CollectionUtils.isNotEmpty(disActivities)) {
//            ActivityInstance activityInstance = disActivities.get(0);
//            sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
//            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId)
//                    .parameter("endTime", activityInstance.getEndTime()).list();
//            List<String> runActivityIds = new ArrayList<>();
//            if (CollectionUtils.isNotEmpty(datas)) {
//                datas.forEach(ai -> runActivityIds.add(ai.getId()));
//            }
//        }
//    }
//
//    protected FormInfo getStartForm(ProcessDefinition processDefinition) {
//        FormInfo formInfo = null;
//
//        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
//        Model model = repositoryService.createModelQuery().modelKey(deployment.getKey()).singleResult();
//        byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
//        try {
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(modelEditorSource);
//            XMLInputFactory xif = XMLInputFactory.newInstance();
//            InputStreamReader in = new InputStreamReader(inputStream, "UTF-8");
//            XMLStreamReader xtr = xif.createXMLStreamReader(in);
//            BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
//            BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
//            Process process = bpmnModel.getProcessById(processDefinition.getKey());
//            FlowElement startElement = process.getInitialFlowElement();
//            if (startElement instanceof UserTask) {
//                UserTask userTask = (UserTask) startElement;
//                String formkey = userTask.getFormKey();
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
//        Process process = bpmnModel.getProcessById(processDefinition.getKey());
//        FlowElement startElement = process.getInitialFlowElement();
//        if (startElement instanceof UserTask) {
//            UserTask userTask = (UserTask) startElement;
//            String formkey = userTask.getFormKey();
//
//        }
//
//        return formInfo;
//    }
//
//
//}
