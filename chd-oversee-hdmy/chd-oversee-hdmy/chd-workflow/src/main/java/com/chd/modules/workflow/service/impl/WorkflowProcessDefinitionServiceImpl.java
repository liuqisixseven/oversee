package com.chd.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.exception.ServiceException;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
import com.chd.modules.workflow.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.repository.EngineDeployment;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class WorkflowProcessDefinitionServiceImpl implements WorkflowProcessDefinitionService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private WorkflowModelService workflowModelService;
    @Autowired
    private WorkflowManageService workflowManageService;

    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;
    @Autowired
    private WorkflowVariablesService workflowVariablesService;
    @Override
    public ProcessDefinition getProcessDefinitionByProcessDefId(String processDefId) {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefId);
        return processDefinition;
    }
    @Override
    public BpmnModel getModelByProcessDefId(String processDefId) {
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefId);
        return repositoryService.getBpmnModel(processDefinition.getId());
    }

    @Override
    public byte[] getProcessDefImage(String processDefId){
        ProcessDefinition processDefinition = getProcessDefinitionByProcessDefId(processDefId);
        InputStream imageStream = repositoryService.getProcessDiagram(processDefinition.getId());
        if (imageStream != null) {
            try {
                return IOUtils.toByteArray(imageStream);
            }catch (Exception ex){
                throw new ServiceException(ex);
            }
        }
        return null;
    }

    @Override
    public List<UserTask> getProcessDefUserTask(String processDefId){
        BpmnModel bpmnModel=getModelByProcessDefId(processDefId);
        return workflowModelService.getUserTaskByBpmnModel(bpmnModel);
    }
    @Override
    public ProcessDefinition lastVersionProcessDefByCategory(String category){
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.processDefinitionCategory(category).latestVersion().orderByProcessDefinitionVersion().desc();
        List<ProcessDefinition> processDefinitionList= processDefinitionQuery.list();
        if(CollectionUtils.isNotEmpty(processDefinitionList)){
            return processDefinitionList.get(0);
        }
        return null;
    }

    @Override
    public List<WorkflowUserVo> processDefVariableUser(ProcessDefinition processDefinition) {
        List<WorkflowUserVo> variables=new ArrayList<>();
        WorkflowProcessDetail processDefDetail = getProcessDefDetail(processDefinition,null);
        WorkflowProcessDetailVo result = getProcessDefDetailView(processDefDetail);
        if(CollectionUtils.isNotEmpty( result.getUserTasks())){
            List<WorkflowUserTaskVo> userTasks=result.getUserTasks();
            if (CollectionUtils.isNotEmpty(userTasks)) {
                for (WorkflowUserTaskVo userTask : userTasks) {
                    if (CollectionUtils.isNotEmpty(userTask.getVariableUser())) {
                        variables.addAll(userTask.getVariableUser());
                    }
                }
            }
        }
        return variables;
    }

    @Override
    public WorkflowProcessDetail getProcessDefDetail(ProcessDefinition processDefinition,Map variables){
        WorkflowProcessDetail result=new WorkflowProcessDetail();
        WorkflowProcessVo processVo=new WorkflowProcessVo();
        result.setProcess(processVo);
        processVo.setCategory(processDefinition.getCategory());
        processVo.setDeploymentId(processDefinition.getDeploymentId());
        processVo.setName(processDefinition.getName());
        processVo.setKey(processDefinition.getKey());
        processVo.setId(processDefinition.getId());
        BpmnModel bpmnModel= repositoryService.getBpmnModel(processDefinition.getId());
//        List<FlowElement> flowElements= workflowBpmnService.getApprovePath(bpmnModel,null);
        List<FlowElement> flowElements= WorkflowFlowElementUtils.getProcessPath(bpmnModel,variables);
//        List<UserTask> userTasks= workflowModelService.getUserTaskByBpmnModel(bpmnModel);
        List<UserTask> userTasks=WorkflowFlowElementUtils.getUserTaskFromActivity(flowElements);
        result.setUserTaskList(userTasks);
        return result;
    }
    @Override
    public WorkflowProcessDetailVo getProcessDefDetailView(WorkflowProcessDetail processDetail){
        WorkflowProcessDetailVo result=new WorkflowProcessDetailVo();
        if(processDetail!=null){
            result.setProcess(processDetail.getProcess());
            List<WorkflowUserTaskVo> userTasks= workflowManageService.convertUserTask(processDetail.getUserTaskList());
            result.setUserTasks(userTasks);
        }
        return result;
    }

    @Override
    public WorkflowProcessDetailVo getProcessDefDetailView(WorkflowProcessDetail processDetail, Map variables) {
        WorkflowProcessDetailVo result=getProcessDefDetailView(processDetail);
        if(CollectionUtils.isNotEmpty( result.getUserTasks())){
            List<WorkflowUserTaskVo> userTasks=result.getUserTasks();
                result.setUserTasks(workflowVariablesService.setUserTaskListVariables(userTasks,variables));

        }
        return result;
    }

    @Override
    public List<WorkflowUserTaskVo> setUserTaskListWithVariables(List<WorkflowUserTaskVo> userTasks, Map variables) {
        if (CollectionUtils.isNotEmpty(userTasks)) {
            Map<String,List<WorkflowUserVo>> variableUserMap=new HashMap<>();//流程用户变量
            for (WorkflowUserTaskVo userTask : userTasks) {
                if (CollectionUtils.isNotEmpty(userTask.getVariableUser())) {
                    Map<String,List<WorkflowUserVo>> taskVariableMap=new HashMap<>();
                    Iterator<WorkflowUserVo> users = userTask.getVariableUser().iterator();
                    while (users.hasNext()) {
                        WorkflowUserVo user = users.next();
                        if(taskVariableMap.containsKey(user.getId())){
                            users.remove();
                            continue;
                        }
                        List<WorkflowUserVo> userList= variableUserMap.get(user.getId());
                        if(userList!=null){
                            userTask.getUsers().addAll(userList);
                            taskVariableMap.put(user.getId(),userList);
                            users.remove();
                            continue;
                        }
                        userList=new ArrayList<>();
                        variableUserMap.put(user.getId(),userList);
                        taskVariableMap.put(user.getId(),userList);
                        Object value= variables.get(user.getId());
                        if(value!=null){
                            if(value instanceof List) {
                                List valueList = (List) value;
                                boolean hasValue=false;
                                for (Object varUser : valueList) {
                                    if (varUser instanceof WorkflowUserVo) {
                                        userList.add((WorkflowUserVo) varUser);
                                        hasValue=true;
                                    }else if(varUser instanceof WorkflowDepart){
                                        WorkflowDepart depart=(WorkflowDepart) varUser;
                                        List<WorkflowUserVo> value_users= workflowProcessDepartService.getDepartUsers(depart.getDepartId(),depart.getRole());
                                        if(CollectionUtils.isNotEmpty(value_users)) {
                                            userList.addAll(value_users);
                                            hasValue=true;
                                        }
                                    }
                                }
                                if(hasValue){
                                    userTask.getUsers().addAll(userList);
                                    users.remove();
                                }
                            }else if(value instanceof  WorkflowUserVo){
                                userList.add((WorkflowUserVo) value);
                                userTask.getUsers().addAll(userList);
                                users.remove();
                            }else if(value instanceof WorkflowDepart){
                                WorkflowDepart depart=(WorkflowDepart) value;
                                List<WorkflowUserVo> value_users= workflowProcessDepartService.getDepartUsers(depart.getDepartId(),depart.getRole());
                                if(CollectionUtils.isNotEmpty(value_users)) {
                                    userList.addAll(value_users);
                                    userTask.getUsers().addAll(value_users);
                                    users.remove();
                                }
                            }
                        }

                    }

                }
            }
           // result.setUserTasks(userTasks);
        }
        return userTasks;
    }

    /**
     * 列表定义列表
     * @param query
     * @return
     */
    @Override
    public IPage<WorkflowProcessVo> processDefinitionList(WorkflowProcessVo query) {
        // 获取查询流程定义对对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionKey();
        if (query.isLastVersion()) {
            processDefinitionQuery.latestVersion();
        }
        // 排序
        // if (Order.ascending.equals(page.getOrder())) {
        //     processDefinitionQuery.asc();
        // } else {
        processDefinitionQuery.desc();
        // }
        // 动态查询
        if (StringUtils.isNotBlank(query.getCategory())) {
            processDefinitionQuery.processDefinitionCategoryLike( query.getCategory() );
        }
        if (StringUtils.isNotBlank(query.getName())) {
            processDefinitionQuery.processDefinitionNameLike(query.getName() );
        }
        if (StringUtils.isNotBlank(query.getKey())) {
            processDefinitionQuery.processDefinitionKeyLike(query.getKey() );
        }
        Page<WorkflowProcessVo> page=new Page<>( query.getCurrent(), query.getSize());
        page.setTotal(processDefinitionQuery.count());
        if(page.getTotal()>0) {
            List<ProcessDefinition> result = processDefinitionQuery.listPage(query.getOffset(), query.getSize());
            // 部署时间查询
            Map<String, Date> deployTimeMap = repositoryService
                    .createDeploymentQuery()
                    .deploymentIds(result.stream().map(ProcessDefinition::getDeploymentId).collect(Collectors.toList()))
                    .list()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    EngineDeployment::getId,
                                    item -> item.getDeploymentTime(),
                                    (a, b) -> a
                            )
                    );
            page.setRecords(
                    result
                            .stream()
                            .map(item -> new WorkflowProcessVo(item, deployTimeMap.get(item.getDeploymentId())))
                            .collect(Collectors.toList())
            );
        }
        return page;
    }

    /**
     * 激活/挂起流程定义
     * @param processDefinitionId
     * @param suspensionState
     * @return
     */
    @Override
    public boolean suspendOrActivateProcessDefinition(String processDefinitionId,WorkflowProcessVo.SuspensionState suspensionState) {
        if (WorkflowProcessVo.SuspensionState.active.equals(suspensionState)){
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            return true;
        }else if(WorkflowProcessVo.SuspensionState.suspended.equals(suspensionState)){
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            return true;
        }
        return false;
    }
}
