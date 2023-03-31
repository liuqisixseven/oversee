package com.chd.modules.workflow.utils;

import com.alibaba.fastjson.JSONObject;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowTaskService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


public class WorkflowFlowElementUtils {
    private static Logger log = LoggerFactory.getLogger(WorkflowFlowElementUtils.class);

    /**
     * 查找匹配之前节点ID匹配的节点(注：碰上网关就返回)
     *
     * @param result         返回结果列表
     * @param flowElements   流程节点列表
     * @param curFlowElement 查找起点
     * @param variableMap    流程变量
     * @param variableMap    节点ID匹配关键词
     */
    public static void findModelBackNodeByElementId(List<FlowElement> result, Collection<FlowElement> flowElements, FlowElement curFlowElement, Map<String, Object> variableMap, String elementIdKey) {
        if (curFlowElement != null) {
            List<SequenceFlow> incomingFlows = null;
            if (curFlowElement instanceof Activity) {
                incomingFlows = ((Activity) curFlowElement).getIncomingFlows();
            } else if (curFlowElement instanceof Gateway) {
                //并行节点不能当作驳回节点
                if (!(curFlowElement instanceof ParallelGateway || curFlowElement instanceof InclusiveGateway)) {
                    Gateway gateway = (Gateway) curFlowElement;
                    incomingFlows = gateway.getIncomingFlows();
                }
            }

            if (CollectionUtils.isNotEmpty(incomingFlows)) {
                for (SequenceFlow incomingFlow : incomingFlows) {
                    String sourceRef = incomingFlow.getSourceRef();
                    FlowElement targetElement = getFlowElementById(flowElements, sourceRef);
                    if (targetElement != null) {
                        if (targetElement.getId().indexOf(elementIdKey) >= 0) {
                            //找到子流程为驳回点，则还需要继续查找子流程里面的各个节点
                            if (targetElement instanceof SubProcess) {
                                SubProcess subProcess = (SubProcess) targetElement;
                                List<FlowElement> flowElementList = subProcess.getFlowElements().stream().filter(a -> a.getId().indexOf(WorkflowConstants.FLOW_BACK_NODE_KEY) != -1).collect(Collectors.toList());
                                result.addAll(flowElementList);
                            } else {
                                result.add(targetElement);
                            }
                        } else {
                            findSubProcessComplete(result, flowElements, targetElement,variableMap, elementIdKey);
                        }
                    }
                }
//               String sourceRef = incomingFlows.get(0).getSourceRef();
//               if (incomingFlows.size() > 1) {
//                   // 找到表达式成立的sequenceFlow
//                   SequenceFlow sequenceFlow = getSequenceFlow(incomingFlows, variableMap);
//                   sourceRef = sequenceFlow.getSourceRef();
//               }
//               // 根据ID找到FlowElement
//               FlowElement targetElement = getFlowElementById(flowElements, sourceRef);
//               if (targetElement != null) {
//                   if (targetElement.getId().indexOf(elementIdKey) >= 0) {
//                       result.add(targetElement);
//                   }
//                   findModelBackNodeByElementId(result, flowElements, targetElement, variableMap, elementIdKey);
//               }
            }
        }
    }

    private static void findSubProcessComplete(List<FlowElement> result, Collection<FlowElement> flowElements, FlowElement targetElement, Map<String, Object> variableMap,String elementIdKey) {
        //子流程节点完成，需要继续往前查找
        if (targetElement instanceof StartEvent && targetElement.getParentContainer() instanceof SubProcess) {
            SubProcess tmpSubProcess = (SubProcess) targetElement.getParentContainer();
            String tmpSourceRef = tmpSubProcess.getIncomingFlows().get(0).getSourceRef();
            targetElement = getFlowElementById(flowElements, tmpSourceRef);
            if (targetElement.getId().indexOf(elementIdKey) >= 0) {
                //找到子流程为驳回点，则还需要继续查找子流程里面的各个节点
                if (targetElement instanceof SubProcess) {
                    SubProcess subProcess = (SubProcess) targetElement;
                    List<FlowElement> flowElementList = subProcess.getFlowElements().stream().filter(a -> a.getId().indexOf(WorkflowConstants.FLOW_BACK_NODE_KEY) != -1).collect(Collectors.toList());
                    result.addAll(flowElementList);
                } else {
                    result.add(targetElement);
                }
            } else {
                findSubProcessComplete(result, flowElements, targetElement,variableMap, elementIdKey);
            }
        }else{
            findModelBackNodeByElementId(result, flowElements, targetElement, variableMap, elementIdKey);
        }
    }


    /**
     * 获取流程路径
     *
     * @param bpmnModel
     * @param variableMap
     * @return
     */
    public static List<FlowElement> getProcessPath(BpmnModel bpmnModel, Map<String, Object> variableMap) {
//        if(variableMap==null){
//            variableMap=new HashMap<String, Object>();
//        }
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        List<FlowElement> passElements = new LinkedList<>();
        findStartElement(passElements, flowElements, variableMap);
        return passElements;
    }


    public static List<UserTask> getUserTaskFromActivity(Collection<FlowElement> flowElements) {
        List<UserTask> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowElements)) {
            for (FlowElement element : flowElements) {
                if (element instanceof UserTask) {
                    result.add((UserTask) element);
                } else if (element instanceof SubProcess) {
                    SubProcess subProcess = (SubProcess) element;
                    List<UserTask> subUserTasks = getUserTaskFromActivity(subProcess.getFlowElements());
                    if (CollectionUtils.isNotEmpty(subUserTasks)) {
                        result.addAll(subUserTasks);
                    }
                }
            }
        }
        return result;
    }


    public static void findStartElement(List<FlowElement> passElements, Collection<FlowElement> flowElements, Map<String, Object> variableMap) {

        if (CollectionUtils.isNotEmpty(flowElements)) {
            List<FlowElement> startElements = new ArrayList<>();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof StartEvent) {
                    startElements.add(flowElement);
                }
            }
            for (FlowElement startElement : startElements) {
                List<SequenceFlow> outgoingFlows = ((StartEvent) startElement).getOutgoingFlows();
                for (SequenceFlow sequenceFlow : outgoingFlows) {
                    String targetRef = sequenceFlow.getTargetRef();
                    // 根据ID找到FlowElement
//                FlowElement targetElementOfStartElement = getFlowElement(flowElements, targetRef);
//                if (targetElementOfStartElement!=null  && targetElementOfStartElement instanceof UserTask) {
//                     getPassElementList(passElements, flowElements, targetElementOfStartElement, variableMap);
//                }
                    findTargetRefElement(passElements, flowElements, targetRef, variableMap);
                }
            }
        }

    }

    public static void findTargetRefElement(List<FlowElement> passElements, Collection<FlowElement> flowElements, String targetRef, Map<String, Object> variableMap) {
        List<FlowElement> targetElements = getFlowElementByTargetRef(flowElements, targetRef, variableMap);
        if (CollectionUtils.isNotEmpty(targetElements)) {
            for (FlowElement element : targetElements) {
                getPassElementList(passElements, flowElements, element, variableMap);
            }
        }
    }

    private static void getPassElementList(List<FlowElement> passElements, Collection<FlowElement> flowElements, FlowElement curFlowElement, Map<String, Object> variableMap) {
        // 任务节点
        if (curFlowElement instanceof UserTask) {
            dueUserTaskElement(passElements, flowElements, curFlowElement, variableMap);
            return;
        }
        // 排他网关
        if (curFlowElement instanceof ExclusiveGateway) {
            dueExclusiveGateway(passElements, flowElements, curFlowElement, variableMap);
            return;
        }
        // 并行网关
        if (curFlowElement instanceof ParallelGateway) {
            dueParallelGateway(passElements, flowElements, curFlowElement, variableMap);
        }
        if (curFlowElement instanceof SubProcess) {
            dueSubProcessElement(passElements, flowElements, curFlowElement, variableMap);

        }
    }

    private static void dueUserTaskElement(List<FlowElement> passElements, Collection<FlowElement> flowElements, FlowElement curFlowElement, Map<String, Object> variableMap) {
        if (WorkflowFlowElementUtils.getFlowElementById(passElements, curFlowElement.getId()) == null && curFlowElement instanceof UserTask) {
            passElements.add(curFlowElement);
        }
        List<SequenceFlow> outgoingFlows = ((UserTask) curFlowElement).getOutgoingFlows();
        if (CollectionUtils.isEmpty(outgoingFlows)) {
            return;
        }
        for (SequenceFlow outgoingFlow : outgoingFlows) {
//            List<SequenceFlow> sequenceFlow = getSequenceFlow( outgoingFlows,variableMap);
//            for(SequenceFlow nextSequenceFlow:sequenceFlow){
            findTargetRefElement(passElements, flowElements, outgoingFlow.getTargetRef(), variableMap);
//            }
        }
//        String targetRef = outgoingFlows.get(0).getTargetRef();
//        if (outgoingFlows.size() > 1) {
//            // 找到表达式成立的sequenceFlow
//            SequenceFlow sequenceFlow = getSequenceFlow( outgoingFlows,variableMap);
//            targetRef = sequenceFlow.getTargetRef();
//        }
        // 根据ID找到FlowElement
//        FlowElement targetElement = getFlowElement(flowElements, targetRef);
//        this.getPassElementList(passElements, flowElements, targetElement, variableMap);
//        findTargetRefElement(passElements, flowElements, targetRef, variableMap);
    }

    private static void dueExclusiveGateway(List<FlowElement> passElements, Collection<FlowElement> flowElements, FlowElement curFlowElement, Map<String, Object> variableMap) {
        // 获取符合条件的sequenceFlow的目标FlowElement
//        List<SequenceFlow> exclusiveGatewayOutgoingFlows = ((ExclusiveGateway) curFlowElement).getOutgoingFlows();
//        flowElements.remove(curFlowElement);
//        // 找到表达式成立的sequenceFlow
//        SequenceFlow sequenceFlow = getSequenceFlow( exclusiveGatewayOutgoingFlows,variableMap);
//        // 根据ID找到FlowElement
////        FlowElement targetElement = getFlowElement(flowElements, sequenceFlow.getTargetRef());
////        this.getPassElementList(passElements, flowElements, targetElement, variableMap);
//        findTargetRefElement(passElements, flowElements, sequenceFlow.getTargetRef(), variableMap);

        List<SequenceFlow> outgoingFlows = ((ExclusiveGateway) curFlowElement).getOutgoingFlows();
        if (CollectionUtils.isEmpty(outgoingFlows)) {
            return;
        }
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            List<SequenceFlow> sequenceFlow = getSequenceFlow(outgoingFlows, variableMap);
            for (SequenceFlow nextSequenceFlow : sequenceFlow) {
                findTargetRefElement(passElements, flowElements, nextSequenceFlow.getTargetRef(), variableMap);
            }
        }
    }

    private static void dueParallelGateway(List<FlowElement> passElements, Collection<FlowElement> flowElements, FlowElement curFlowElement, Map<String, Object> variableMap) {
        FlowElement targetElement;
        List<SequenceFlow> parallelGatewayOutgoingFlows = ((ParallelGateway) curFlowElement).getOutgoingFlows();
        for (SequenceFlow sequenceFlow : parallelGatewayOutgoingFlows) {
//            targetElement = getFlowElement(flowElements, sequenceFlow.getTargetRef());
//            this.getPassElementList(passElements, flowElements, targetElement, variableMap);
            findTargetRefElement(passElements, flowElements, sequenceFlow.getTargetRef(), variableMap);
        }
    }

    private static void dueSubProcessElement(List<FlowElement> passElements, Collection<FlowElement> flowElements, FlowElement curFlowElement, Map<String, Object> variableMap) {
        SubProcess subProcess = (SubProcess) curFlowElement;
        if (CollectionUtils.isNotEmpty(subProcess.getFlowElements())) {
            List<FlowElement> newPassElements = new LinkedList<>();
            findStartElement(newPassElements, subProcess.getFlowElements(), variableMap);
            if (CollectionUtils.isNotEmpty(newPassElements)) {
                passElements.addAll(newPassElements);
            }
        }
        List<SequenceFlow> outgoingFlows = ((SubProcess) curFlowElement).getOutgoingFlows();
        for (SequenceFlow sequenceFlow : outgoingFlows) {
//            targetElement = getFlowElement(flowElements, sequenceFlow.getTargetRef());
//            this.getPassElementList(passElements, flowElements, targetElement, variableMap);
            findTargetRefElement(passElements, flowElements, sequenceFlow.getTargetRef(), variableMap);
        }

//        String targetRef = outgoingFlows.get(0).getTargetRef();
//        if (outgoingFlows.size() > 1) {
//            // 找到表达式成立的sequenceFlow
//            SequenceFlow sequenceFlow = getSequenceFlow(outgoingFlows,variableMap);
//            targetRef = sequenceFlow.getTargetRef();
//        }
        // 根据ID找到FlowElement
//        FlowElement targetElement = getFlowElement(flowElements, targetRef);
//        this.getPassElementList(passElements, flowElements, targetElement, variableMap);
//        findTargetRefElement(passElements, flowElements, targetRef, variableMap);
    }

    /**
     * 获取第一个UserTask节点
     *
     * @param elements
     * @return
     */
    public static UserTask getFristUserTask(List<FlowElement> elements) {
        UserTask result = null;
        if (CollectionUtils.isNotEmpty(elements)) {
            for (FlowElement element : elements) {
                if (element instanceof UserTask) {
                    return (UserTask) element;
                }
            }
        }
        return result;
    }

    /**
     * 根据ID找节点
     *
     * @param flowElements
     * @param elementId
     * @return
     */
    public static FlowElement getFlowElementById(Collection<FlowElement> flowElements, String elementId) {
        FlowElement result = null;
        if (CollectionUtils.isEmpty(flowElements)) {
            return result;
        }
        for (FlowElement element : flowElements) {
            if (element instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) element;
                if (subProcess.getId().equals(elementId)) {
                    return subProcess;
                }
                FlowElement subResult = getFlowElementById(subProcess.getFlowElements(), elementId);
                if (subResult != null) {
                    result = subResult;
                    return result;
                }
            } else if (element.getId().equals(elementId)) {
                result = element;

            }
        }
        return result;
//        return flowElements.stream().filter(flowElement -> elementId.equals(flowElement.getId())).findFirst().orElse(null);
    }

    /**
     * 根据目标找节点
     *
     * @param flowElements
     * @param targetRef
     * @return
     */
    public static List<FlowElement> getFlowElementByTargetRef(Collection<FlowElement> flowElements, String targetRef, Map<String, Object> variableMap) {
        List<FlowElement> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(flowElements)) {
            return result;
        }
        FlowElement activeElement = getFlowElementById(flowElements, targetRef);
        if (activeElement != null) {
            result.add(activeElement);
            return result;
        }
        for (FlowElement element : flowElements) {
            if (element instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) element;
                List<FlowElement> subResult = getFlowElementByTargetRef(subProcess.getFlowElements(), targetRef, variableMap);
                if (CollectionUtils.isNotEmpty(subResult)) {
                    result.addAll(subResult);
                }
            } else {
                if (element instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) element;
                    if (sequenceFlow.getSourceRef().equals(targetRef)) {
                        if (StringUtils.isBlank(sequenceFlow.getConditionExpression()) || getExpressionValue(sequenceFlow.getConditionExpression(), variableMap)) {
                            List<FlowElement> subResult = getFlowElementByTargetRef(flowElements, ((SequenceFlow) element).getTargetRef(), variableMap);
                            if (CollectionUtils.isNotEmpty(subResult)) {
                                result.addAll(subResult);
                            }
                        }
                    }
                }
            }
        }
        return result;
//        return flowElements.stream().filter(flowElement -> elementId.equals(flowElement.getId())).findFirst().orElse(null);
    }

    /**
     * 获取线路
     *
     * @param outgoingFlows
     * @param variableMap
     * @return
     */
    public static List<SequenceFlow> getSequenceFlow(List<SequenceFlow> outgoingFlows, Map<String, Object> variableMap) {
        List<SequenceFlow> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(outgoingFlows)) {
            for (SequenceFlow sequenceFlow : outgoingFlows) {
                if (StringUtils.isBlank(sequenceFlow.getConditionExpression()) || variableMap == null || getExpressionValue(sequenceFlow.getConditionExpression(), variableMap)) {
                    result.add(sequenceFlow);
                }
            }
        }
        return result;
//        Optional<SequenceFlow> sequenceFlowOpt = outgoingFlows.stream().filter(item -> {
//            try {
//                if(StringUtils.isBlank( item.getConditionExpression())){
//                    return true;
//                }
//                return getExpressionValue(item.getConditionExpression(), variableMap);
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                return false;
//            }
//        }).findFirst();
//        return sequenceFlowOpt.orElse(outgoingFlows.get(0));
    }

    /**
     * 获取表达式值
     *
     * @param exp
     * @param variableMap
     * @return
     */
    public static boolean getExpressionValue(String exp, Map<String, Object> variableMap) {
        try {
            //如果没有设置变量，默认返回true;
            if (variableMap == null) {
                return true;
            }
            Expression expression = getExpression(exp);
            ExecutionEntity executionEntity = new ExecutionEntityImpl();
//            executionEntity.setVariables(variableMap);
            executionEntity.setTransientVariables(variableMap);
            Object value = expression.getValue(executionEntity);
            return value != null && "true".equals(value.toString());
        } catch (Exception ex) {
            log.warn("获取流程表达式计算值失败", ex.getMessage());
            return false;
        }
    }

    /**
     * 创建表达式
     *
     * @param conditionExpression
     * @return
     */
    public static Expression getExpression(String conditionExpression) {
        CommandContext commandContext = Context.getCommandContext();
        Expression expression = null;
        if (commandContext == null) {
            ProcessEngineConfigurationImpl processEngineConfiguration = SpringContextUtils.getBean(ProcessEngineConfigurationImpl.class);
            expression = processEngineConfiguration.getExpressionManager().createExpression(conditionExpression);

        } else {
            expression = CommandContextUtil.getProcessEngineConfiguration(commandContext).getExpressionManager().createExpression(conditionExpression);
        }


        return expression;
    }

    private static RepositoryService repositoryService;
    private static TaskService taskService;
    private static RuntimeService runtimeService;

    private static WorkflowTaskService workflowTaskService;


    public static FlowElement getFlowElementByActivityIdAndProcessDefinitionId(String flowElementId, String processDefinitionId) {
//        getServices();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            for (Process process : processes) {
//                if(null!=process.getFlowElementMap().get(flowElementId)){
//                    return process.getFlowElementMap().get(flowElementId);
//                }
//                FlowElement flowElement = process.getFlowElement(flowElementId);
                List<FlowElement> flowElementList = (List<FlowElement>) process.getFlowElements();
                FlowElement flowElement = getFlowElementByList(flowElementList, flowElementId);
                if (flowElement != null) {
                    return flowElement;
                }
            }
        }
        return null;
    }

    private static FlowElement getFlowElementByList(List<FlowElement> flowElementList, String flowElementId) {
        if (null != flowElementList && flowElementList.size() > 0) {
            for (FlowElement flowElement : flowElementList) {
                if (null != flowElement) {
                    if (flowElement.getId().equals(flowElementId)) {
                        return flowElement;
                    }
                    if (flowElement instanceof SubProcess) {
                        SubProcess subProcess = (SubProcess) flowElement;
                        List<FlowElement> subflowElementList = (List<FlowElement>) subProcess.getFlowElements();
                        for (FlowElement tmpFlowElement : subflowElementList) {
                            if (null != tmpFlowElement) {
                                if (tmpFlowElement.getId().equals(flowElementId)) {
                                    return tmpFlowElement;
                                }
                            }
                        }
//                        return getFlowElementByList((List<FlowElement>) subProcess.getFlowElements(),flowElementId);
                    }
                }
            }
        }
        return null;
    }


    public static List<ExtensionAttribute> getExtensionAttribute(String flowElementId, String processDefinitionId, String customPropertyName) {
        FlowElement flowElement = getFlowElementByActivityIdAndProcessDefinitionId(flowElementId, processDefinitionId);
        if (flowElement != null && flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            Map<String, List<ExtensionAttribute>> attributes = userTask.getAttributes();
            if (MapUtils.isNotEmpty(attributes)) {
                List<ExtensionAttribute> values = attributes.get(customPropertyName);
                if (CollectionUtils.isNotEmpty(values)) {
                    return values;
                }
            }
        }
        return null;
    }


    /**
     * 获取自定义属性值
     *
     * @param flowElementId       节点定义id
     * @param processDefinitionId 流程定义id
     * @param customPropertyName  属性名
     * @return
     */
    public static List<ExtensionElement> getCustomProperty(String flowElementId, String processDefinitionId, String customPropertyName) {
        FlowElement flowElement = getFlowElementByActivityIdAndProcessDefinitionId(flowElementId, processDefinitionId);
        if (flowElement != null && flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
            if (MapUtils.isNotEmpty(extensionElements)) {
                List<ExtensionElement> values = extensionElements.get(customPropertyName);
                if (CollectionUtils.isNotEmpty(values)) {
                    return values;
                }
            }
        }
        return null;
    }

    public static Map<String, Object> getProcessVariables(String processId) {
//        getServices();
        if (StringUtils.isNotBlank(processId)) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            if (null != processInstance) {
                Map<String, Object> processVariables = processInstance.getProcessVariables();
                return processVariables;
            }
        }
        return null;
    }


    public static void executionDataToStorageTaskIdLocal(String taskId, String executionId, String processId) {
        try {
            if (StringUtils.isNotEmpty(taskId) && StringUtil.isNotEmpty(executionId)) {
                String parentExecutionId = getParentExecutionIdByExecutionId(executionId);
                if (StringUtils.isNotEmpty(parentExecutionId)) {
                    String previousTaskId = (String) runtimeService.getVariableLocal(parentExecutionId, WorkflowConstants.ExecutionVariableLocalKey.TASK_ID);
                    String previousUserId = (String) runtimeService.getVariableLocal(parentExecutionId, WorkflowConstants.ExecutionVariableLocalKey.USER_IDS);
                    if (StringUtil.isNotEmpty(processId)) {
                        if (StringUtil.isEmpty(previousTaskId)) {
                            previousTaskId = (String) runtimeService.getVariable(processId, WorkflowConstants.ExecutionVariableLocalKey.TASK_ID);
                        }
                        if (StringUtil.isEmpty(previousUserId)) {
                            previousUserId = (String) runtimeService.getVariable(processId, WorkflowConstants.ExecutionVariableLocalKey.USER_IDS);
                        }
                        runtimeService.setVariable(processId, WorkflowConstants.ExecutionVariableLocalKey.TASK_ID, taskId);
                        Map<String, Object> selectMap = Maps.newHashMap();
                        selectMap.put("processId", processId);
                        selectMap.put("executionId", executionId);
                        String previousTaskDataSrc = workflowTaskService.getPreviousTaskDataSrc(selectMap);
                        if (StringUtils.isNotEmpty(previousTaskDataSrc)) {
                            taskService.setVariableLocal(taskId, WorkflowConstants.ExecutionVariableLocalKey.PREVIOUS_TASK_DATA_SRC, previousTaskDataSrc);
                            String[] previousTaskDataArray = previousTaskDataSrc.split(",");
                            if (null != previousTaskDataArray && previousTaskDataArray.length > 0) {
                                taskService.setVariableLocal(taskId, WorkflowConstants.ExecutionVariableLocalKey.PREVIOUS_TASK_NAMES, previousTaskDataArray[0]);
                                if (previousTaskDataArray.length > 1) {
                                    taskService.setVariableLocal(taskId, WorkflowConstants.ExecutionVariableLocalKey.PREVIOUS_TASK_USER_IDS, previousTaskDataArray[1]);
                                }
                            }
                        }
                    }

                    taskService.setVariableLocal(taskId, WorkflowConstants.ExecutionVariableLocalKey.PREVIOUS_TASK_ID, previousTaskId);
                    taskService.setVariableLocal(taskId, WorkflowConstants.ExecutionVariableLocalKey.PREVIOUS_USER_IDS, previousUserId);
                    runtimeService.setVariableLocal(parentExecutionId, WorkflowConstants.ExecutionVariableLocalKey.TASK_ID, taskId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getParentExecutionIdByExecutionId(String executionId) {
        String parentExecutionId = runtimeService.createExecutionQuery().executionId(executionId).singleResult().getParentId();
        return StringUtils.isNotEmpty(parentExecutionId) ? parentExecutionId : executionId;
    }

    public static void setParentExecutionStringVariableLocal(String executionId, String key, String value, String processId) {
        try {
            if (StringUtil.isNotEmpty(value)) {
                String parentExecutionId = WorkflowFlowElementUtils.getParentExecutionIdByExecutionId(executionId);
                if (StringUtil.isNotEmpty(parentExecutionId)) {
                    String variableValueLocal = (String) runtimeService.getVariableLocal(parentExecutionId, key);
                    if (StringUtil.isNotEmpty(variableValueLocal) && !variableValueLocal.equals(variableValueLocal)) {
                        value += ("," + variableValueLocal);
                    }
                    runtimeService.setVariableLocal(parentExecutionId, key, value);
                    if (StringUtil.isNotEmpty(processId)) {
                        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
                        if (null != execution) {
                            if (execution instanceof ExecutionEntityImpl) {
                                ExecutionEntityImpl executionEntityImpl = (ExecutionEntityImpl) execution;
//                                System.out.println("isConcurrent : " + executionEntityImpl.isConcurrent());
                            }
//                            System.out.println(execution.getParentId());
                        }
                        runtimeService.setVariable(processId, key, value);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    static {
        if (null == repositoryService) {
            repositoryService = SpringContextUtils.getBean(RepositoryService.class);
        }
        if (null == taskService) {
            taskService = SpringContextUtils.getBean(TaskService.class);
        }
        if (null == runtimeService) {
            runtimeService = SpringContextUtils.getBean(RuntimeService.class);
        }
        if (null == workflowTaskService) {
            workflowTaskService = SpringContextUtils.getBean(WorkflowTaskService.class);
        }
    }

//    private static void getServices(){
//        if(null==repositoryService){
//            repositoryService = SpringContextUtils.getBean(RepositoryService.class);
//            runtimeService = SpringContextUtils.getBean(RuntimeService.class);
//        }
//    }

}
