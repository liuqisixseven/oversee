package com.chd.modules.workflow.service;

import com.chd.common.util.BaseConstant;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import org.apache.commons.collections.MapUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.history.HistoricProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkflowGlobalProcistEndListener extends AbstractFlowableEngineEventListener {

    private static Logger logger= LoggerFactory.getLogger(WorkflowGlobalProcistEndListener.class);

    @Autowired
    private WorkflowProcessMapper workflowProcessMapper;
    @Autowired
    private WorkflowMessageService workflowMessageService;

    @Autowired
    protected HistoryService historyService;

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        logger.info("进入流程完成监听器------------------------Start---------------------->");
        logger.info("进入流程完成监听器---processInstanceId="+event.getProcessInstanceId());
        try {
            WorkflowProcess process = workflowProcessMapper.selectById(event.getProcessInstanceId());
            if (process != null) {
                Date now=new Date();
                if (WorkflowConstants.processState.PENDING.equals(process.getState()) || WorkflowConstants.processState.START.equals(process.getState())) {
                    process.setState(WorkflowConstants.processState.COMPLETED);
                    process.setUpdateTime(now);
                    //流程完成，不再需要待办人
                    process.setNextUserTask("");
                    workflowProcessMapper.updateById(process);
                }
                Map<String,Object> noticeData=new HashMap<>();
                noticeData.put("processId",event.getProcessInstanceId());
                noticeData.put("processState",WorkflowConstants.processState.COMPLETED);
                noticeData.put("time",now);
                try{
                    HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(event.getProcessInstanceId()).singleResult();
                    if(processInstance!=null){
                        Map<String,Object> processVariables=processInstance.getProcessVariables();
                        if(MapUtils.isNotEmpty( processVariables)){
                            noticeData.put(BaseConstant.PROCESS_CATEGORY_ISSUES_KEY,processVariables.get(BaseConstant.PROCESS_CATEGORY_ISSUES_KEY));
                            noticeData.put(BaseConstant.ISSUES_SUPERVISOR_ID_KEY,processVariables.get(BaseConstant.ISSUES_SUPERVISOR_ID_KEY));
                        }
                    }
                }catch (Exception e){

                }
                workflowMessageService.sendMessage(WorkflowConstants.NOTICE_MESSAGE_TYPE.PROCESS_COMPLETED,process.getBizId(),noticeData);
            }

        }catch (Exception ex){
            logger.error("流程完成处理异常,processId="+event.getProcessInstanceId(),ex);
        }
        logger.info("进入流程完成监听器------------------------End---------------------->");
    }
}
