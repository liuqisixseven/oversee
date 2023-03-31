package com.chd.modules.workflow.service;

import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

public interface WorkflowMessageService {


    void sendMessage(String msgType,String bizId, Map<String,Object> param);

     void sendTaskForm(String bizId, Task task, WorkflowUserVo user, WorkflowTaskFormVo formData);

    void sendNextTaskList(String bizId, List<WorkflowUserTaskVo> userTasks);
//
//    /**
//     * 分发任务提交的表单
//     * @param bizId
//     * @param task
//     * @param user
//     * @param formData
//     */
//    void dispatchTaskForm(String bizId, Task task, WorkflowUserVo user, WorkflowTaskFormVo formData);
//
//    /**
//     * 流程结束
//     * @param bizId
//     * @param processState
//     */
//    void processCompleted(String bizId,String processState);
}
