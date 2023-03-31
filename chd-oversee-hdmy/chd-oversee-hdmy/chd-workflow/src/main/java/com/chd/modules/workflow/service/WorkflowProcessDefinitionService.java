package com.chd.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.exception.ServiceException;
import com.chd.modules.workflow.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.repository.EngineDeployment;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 流程定义
 */
public interface WorkflowProcessDefinitionService {



     ProcessDefinition getProcessDefinitionByProcessDefId(String processDefId);

     BpmnModel getModelByProcessDefId(String processDefId);


     byte[] getProcessDefImage(String processDefId);


     List<UserTask> getProcessDefUserTask(String processDefId);

     ProcessDefinition lastVersionProcessDefByCategory(String category);

    /**
     * 获取流程的任务变量用户
     * @param processDefinition
     * @return
     */
     List<WorkflowUserVo> processDefVariableUser(ProcessDefinition processDefinition);

     WorkflowProcessDetail getProcessDefDetail(ProcessDefinition processDefinition,Map variables);

     WorkflowProcessDetailVo getProcessDefDetailView(WorkflowProcessDetail processDetail);

     WorkflowProcessDetailVo getProcessDefDetailView(WorkflowProcessDetail processDetail,Map variables);

    List<WorkflowUserTaskVo> setUserTaskListWithVariables( List<WorkflowUserTaskVo> userTaskList,Map variables);
    /**
     * 列表定义列表
     * @param query
     * @return
     */
     IPage<WorkflowProcessVo> processDefinitionList(WorkflowProcessVo query);

    /**
     * 激活/挂起流程定义
     * @param processDefinitionId
     * @param suspensionState
     * @return
     */
     boolean suspendOrActivateProcessDefinition(String processDefinitionId,WorkflowProcessVo.SuspensionState suspensionState);
}
