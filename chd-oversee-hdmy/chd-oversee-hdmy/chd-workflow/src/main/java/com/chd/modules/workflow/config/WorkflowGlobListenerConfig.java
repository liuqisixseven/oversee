package com.chd.modules.workflow.config;

import com.chd.modules.workflow.service.WorkflowGlobalProcessStartedListener;
import com.chd.modules.workflow.service.WorkflowGlobalProcistEndListener;
import com.chd.modules.workflow.service.WorkflowGlobalTaskCreateListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 流程事件监听
 */
@Configuration
public class WorkflowGlobListenerConfig implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private SpringProcessEngineConfiguration configuration;
    @Autowired
    private WorkflowGlobalTaskCreateListener workflowGlobalTaskCreateListener;
    @Autowired
    private WorkflowGlobalProcistEndListener workflowGlobalProcistEndListener;
    @Autowired
    private WorkflowGlobalProcessStartedListener workflowGlobalProcessStartedListener;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        //任务创建全局监听
        dispatcher.addEventListener(workflowGlobalTaskCreateListener, FlowableEngineEventType.TASK_CREATED);

        dispatcher.addEventListener(workflowGlobalProcessStartedListener, FlowableEngineEventType.PROCESS_STARTED);
        dispatcher.addEventListener(workflowGlobalProcistEndListener, FlowableEngineEventType.PROCESS_COMPLETED);
    }
}