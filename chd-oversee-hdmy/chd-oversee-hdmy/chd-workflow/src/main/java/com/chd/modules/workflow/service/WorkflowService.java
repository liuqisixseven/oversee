package com.chd.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.vo.*;
import org.flowable.bpmn.model.UserTask;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public interface WorkflowService {


    WorkflowProcess getProcessById(String id);

    Map<String,Object> updateProcessTaskInfoById(String id);
    /**
     * 发起流程
     * @param process
     * @return
     */
    WorkflowProcess launchProcess(WorkflowProcess process);

    WorkflowProcess launchProcess(String bizId, String bizUrl, String title, String category, String startUserId, WorkflowVariablesVo variables);
    void launchCancelProcess(WorkflowProcess processInstance);



    /**
     * 撤回流程
     * @param process
     * @return
     */
    boolean revokeProcess(WorkflowProcess process);


    /**
     * 挂起/激活流程
     * @param processId
     * @param suspensionState
     * @return
     */
    boolean suspendOrActivateProcessInstanceById(String processId,WorkflowProcessVo.SuspensionState suspensionState);

    /**
     * 我的申请流程
     * @param query
     * @return
     */
    IPage<WorkflowProcess> involvedUserProcessList(WorkflowQueryVo query,String userId);

    /**
     * 待办任务
     * @param query
     * @param userId
     * @return
     */
//    IPage<WorkflowTaskVo> todoTaskList(WorkflowQueryVo query,String userId);

    WorkflowTaskVo findTaskById(String taskId,LoginUser sysUser);

    void getPreviousTaskDataByCurrentTask(String taskId, WorkflowTaskVo workflowTaskVo);

    void getPreviousTaskDataByCurrentTask(Task task, WorkflowTaskVo workflowTaskVo);

    List<WorkflowUserTaskVo> findNextTaskByProcessId(String processId);



    /**
     * 通过流程实例id获取流程实例的待办任务审批人列表
     * @param processInstanceId
     * @return
     */
    List<LoginUser> processApprovers(String processInstanceId);

    /**
     * 流程详情
     * @param processId
     * @return
     */
    WorkflowProcessDetailVo processDetail(String processId);

    WorkflowProcessDetailVo processDetailByBizId(String bizId);

    public Map<String,Object> getSupervisorDataByTask(Task task,Long issueId);

}
