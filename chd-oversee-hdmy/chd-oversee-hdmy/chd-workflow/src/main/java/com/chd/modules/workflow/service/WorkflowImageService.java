package com.chd.modules.workflow.service;

import com.chd.modules.workflow.service.impl.WorkflowProcessDiagramGenerator;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkflowImageService extends WorkflowProcessDiagramGenerator {


    private static final String annotationFontName="Serif";
    private static final String labelFontName="Serif";
    private static final String activityFontName="Serif";
    private static final String IMAGE_TYPE="png";

        @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private WorkflowModelService workflowModelService;


    /**
     * 流程图图片
     * @param processInstanceId
     * @return
     */
    public byte[] proccessImage(String processInstanceId) {
        //1.获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = null;
        List<String> activeActivityIds = new ArrayList<>();
        List<String> highLightedFlows = new ArrayList<>();
        //2.获取所有的历史轨迹线对象
        List<HistoricActivityInstance> historicSquenceFlows = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).list();
        historicSquenceFlows.forEach(historicActivityInstance -> highLightedFlows.add(historicActivityInstance.getActivityId()));
        //3. 获取流程定义id和高亮的节点id
        if (processInstance != null) {
            //3.1. 正在运行的流程实例
            processDefinitionId = processInstance.getProcessDefinitionId();
            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        } else {
            //3.2. 已经结束的流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            //3.3. 获取结束节点列表
            List<HistoricActivityInstance> historicEnds = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_EVENT_END).list();
            List<String> finalActiveActivityIds = activeActivityIds;
            historicEnds.forEach(historicActivityInstance -> finalActiveActivityIds.add(historicActivityInstance.getActivityId()));
        }
        //4. 获取bpmnModel对象
        BpmnModel bpmnModel = workflowModelService.getBpmnModelByProcessDefId(processDefinitionId);
        //5. 生成图片流
        InputStream inputStream = generateDiagram(bpmnModel, activeActivityIds, highLightedFlows);
        //6. 转化成byte便于网络传输
        byte[] datas = IoUtil.readInputStream(inputStream, "image inputStream name");
        return datas;
    }

    /**
     * 生成图片流
     *
     * @param bpmnModel 模型
     * @return
     */
    public InputStream generateDiagram(BpmnModel bpmnModel) {
        return generateDiagram(bpmnModel, IMAGE_TYPE, activityFontName, labelFontName, annotationFontName,null, 1.0, true);
    }
    /**
     * 生成图片流
     * @param bpmnModel             模型
     * @param highLightedActivities 活动节点
     * @param highLightedFlows      高亮线
     * @return
     */
    public InputStream generateDiagram(BpmnModel bpmnModel, List<String> highLightedActivities, List<String> highLightedFlows) {
        return generateDiagram(bpmnModel, IMAGE_TYPE, highLightedActivities,
                highLightedFlows, activityFontName, labelFontName, annotationFontName,
                null, 1.0, true);
    }
}
