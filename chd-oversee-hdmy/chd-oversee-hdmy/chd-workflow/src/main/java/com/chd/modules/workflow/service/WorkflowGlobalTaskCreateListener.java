package com.chd.modules.workflow.service;

import com.chd.common.util.JsonUtils;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableProcessEngineEvent;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.variable.api.event.FlowableVariableEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务创建全局监听
 */
@Component
public class WorkflowGlobalTaskCreateListener extends AbstractFlowableEngineEventListener {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WorkflowProcessMapper workflowProcessMapper;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private WorkflowMessageService workflowMessageService;

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        if (event instanceof FlowableEntityEventImpl){
            //得到流程定义id
            String processDefinitionId = event.getProcessDefinitionId();
            //得到流程实例id
            String processInstanceId = event.getProcessInstanceId();
            FlowableEntityEventImpl eventImpl = (FlowableEntityEventImpl) event;
            //得到任务实例
            TaskEntity entity = (TaskEntity)eventImpl.getEntity();
            if(null!=entity){
                WorkflowFlowElementUtils.executionDataToStorageTaskIdLocal(entity.getId(),entity.getExecutionId(),processInstanceId);
            }


            //1、授权

            //2、相邻节点自动跳过

            //3、发送消息

            String taskId = entity.getId();
            try {
                scheduledExecutorService.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Map<String, Object> map = workflowService.updateProcessTaskInfoById(processInstanceId);
                        if(null!=map){
                            WorkflowProcess process = (WorkflowProcess) map.get("process");
                            List<WorkflowUserTaskVo> userTasks = (List<WorkflowUserTaskVo>) map.get("userTasks");
                            if(null!=process&&null!=userTasks){
//                                只在创建时推送
                                workflowMessageService.sendNextTaskList(process.getBizId(),userTasks);
                            }
                        }
                    }
                },10, TimeUnit.SECONDS);
            }catch (Exception ex){
                logger.error("处理下个节点异常：processId="+processInstanceId,ex);
            }

//            Process myprocess = ProcessDefinitionUtil.getProcess(processDefinitionId);
//            //遍历整个process,找到endEventId是什么，与当前taskId作对比
//            List<FlowElement> flowElements = (List<FlowElement>) myprocess.getFlowElements();
//            for (FlowElement flowElement : flowElements) {
//                if (flowElement instanceof SequenceFlow) {
//                    SequenceFlow flow = (SequenceFlow) flowElement;
//                    FlowElement sourceFlowElement = flow.getSourceFlowElement();
//                    FlowElement targetFlowElement = flow.getTargetFlowElement();
//                    //如果当前边的下一个节点是endEvent，那么获取当前边
//                    if(targetFlowElement instanceof EndEvent && sourceFlowElement.getId().equals(entity.getTaskDefinitionKey()))
//                    {
//                        System.out.println("下一个是结束节点！！");
//                        WorkflowProcess process= workflowProcessMapper.selectById(processInstanceId);
//                        if(process!=null){
//                            logger.info("-------------流程结束:processId={},title={},startUserName=",processInstanceId,process.getTitle(),process.getStartUserName());
//                            if(WorkflowConstants.processState.PENDING.equals( process.getState())) {
//                                process.setState(WorkflowConstants.processState.APPROVED);
//                                process.setUpdateTime(new Date());
//                                workflowProcessMapper.updateById(process);
//                            }
//                        }
//                    }
//                }
//            }


        }

    }


    @Override
    protected void variableCreated(FlowableVariableEvent event) {
        super.variableCreated(event);
        logger.info("-------------流程变量创建监听:variableCreated={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void processCompletedWithErrorEnd(FlowableEngineEntityEvent event) {
        super.processCompletedWithErrorEnd(event);
        logger.info("-------------流程失败结束监听:processCompletedWithErrorEnd={}",event);
    }

    @Override
    protected void processCompletedWithTerminateEnd(FlowableEngineEntityEvent event) {
        super.processCompletedWithTerminateEnd(event);
        logger.info("-------------流程结束监听:processCompletedWithTerminateEnd={}",event);
    }

    @Override
    protected DelegateExecution getExecution(FlowableEngineEvent event) {
        logger.info("-------------流程getExecution:getExecution={}",event);
        return super.getExecution(event);

    }

    @Override
    protected void entityCreated(FlowableEngineEntityEvent event) {
        super.entityCreated(event);
        logger.info("-------------流程entityCreated监听:entityCreated={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void taskAssigned(FlowableEngineEntityEvent event) {
        super.taskAssigned(event);
        logger.info("-------------流程taskAssigned监听:taskAssigned={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void jobExecutionFailure(FlowableEngineEntityEvent event) {
        super.jobExecutionFailure(event);
        logger.info("-------------流程jobExecutionFailure监听:jobExecutionFailure={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void activityStarted(FlowableActivityEvent event) {
        super.activityStarted(event);
        logger.info("-------------流程activityStarted监听:activityStarted={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void jobRetriesDecremented(FlowableEngineEntityEvent event) {
        super.jobRetriesDecremented(event);
        logger.info("-------------流程jobRetriesDecremented监听:jobRetriesDecremented={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void custom(FlowableEngineEvent event) {
        super.custom(event);
        logger.info("-------------流程custom监听:custom={}",JsonUtils.toJsonStr(event));
    }

    @Override
    protected void engineCreated(FlowableProcessEngineEvent event) {
        super.engineCreated(event);
        logger.info("-------------流程engineCreated监听:engineCreated={}",JsonUtils.toJsonStr(event));
    }

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        super.onEvent(flowableEvent);
//        logger.info("-------------流程onEvent监听:onEvent={}",JsonUtils.toJsonStr(flowableEvent));

    }
}
