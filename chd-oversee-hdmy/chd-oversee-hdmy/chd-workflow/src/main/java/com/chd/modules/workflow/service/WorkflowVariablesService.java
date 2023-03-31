package com.chd.modules.workflow.service;

import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.List;
import java.util.Map;

public interface WorkflowVariablesService {

    /**
     * 任务变量
     * @param form
     * @param processId
     * @return
     */
    Map<String,Object> taskVariables(WorkflowTaskFormVo form, String processId, TaskEntity task, WorkflowUserVo user);

    /**
     * 从变量中提取审批用户/用户组
     * @param userTasks 流程审批节点
     * @param variables 流程审批用户变量
     * @return
     */
    List<WorkflowUserTaskVo> setUserTaskListVariables(List<WorkflowUserTaskVo> userTasks, Map variables);

    List<Map<String, Object>> getSubprocessUserDataList(String OrgIds,String orgDescribe);

    List<String> getUserIdDataGroup(String ids);
}
