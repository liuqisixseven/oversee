package com.chd.modules.workflow.service;

import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WorkflowGlobalProcessStartedListener extends AbstractFlowableEngineEventListener {
    private static Logger logger = LoggerFactory.getLogger(WorkflowGlobalProcessStartedListener.class);

    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {
        logger.info("进入流程开始监听器------------------------Start---------------------->");

        String eventName = event.getType().name();

        FlowableEntityEventImpl flowableEntityEvent = (FlowableEntityEventImpl) event;
        ExecutionEntityImpl processInstance = (ExecutionEntityImpl) flowableEntityEvent.getEntity();

        Date startTime = processInstance.getStartTime();
        String processDefinitionKey = processInstance.getProcessDefinitionKey();
        String processInstanceId = processInstance.getProcessInstanceId();
        String processInstanceBusinessKey = processInstance.getProcessInstanceBusinessKey();
        int suspensionState = processInstance.getSuspensionState();

        logger.info("流程事件类型->{}", eventName);
        logger.info("流程开始时间->{}", startTime);
        logger.info("流程定义Key->{}", processDefinitionKey);
        logger.info("流程实例ID->{}", processInstanceId);
        logger.info("流程业务key->{}", processInstanceBusinessKey);
        logger.info("流程是否挂起标志->{}", suspensionState);
        logger.info("流程开始监听器------------------------End---------------------->");

    }
}