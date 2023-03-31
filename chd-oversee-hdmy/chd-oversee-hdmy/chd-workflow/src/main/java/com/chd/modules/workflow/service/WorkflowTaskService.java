package com.chd.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import com.chd.modules.workflow.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface WorkflowTaskService{


    IPage<WorkflowTaskVo> todoTaskList(WorkflowQueryVo query, String userId, Map<String, Object> map);

    /**
     * 审批通过
     * @param form
     * @param user 审批人
     * @return
     */
    boolean tasksApprove(WorkflowTaskFormVo form, WorkflowUserVo user);

    /**
     * 审批拒绝
     * @param form
     * @param userId
     * @return
     */
    boolean tasksReject(WorkflowTaskFormVo form,String userId);
    /**
     * 委派任务
     * @param form
     * @param user
     * @return
     */
    boolean tasksDelegate(WorkflowTaskFormVo form,WorkflowUserVo user);

    /**
     * 打回发起者
     * @param form
     * @param user
     * @return
     */
    boolean tasksBackStart(WorkflowTaskFormVo form,WorkflowUserVo user);

    /**
     * 驳回（打回设置驳回的节点）
     * @param form
     * @param user
     * @return
     */
    boolean tasksDelegateBackNodes(WorkflowTaskFormVo form,WorkflowUserVo user);

    /**
     * 转办
     * @param form
     * @param user
     * @return
     */
    boolean turnTask(WorkflowTaskFormVo form,WorkflowUserVo user);

    /**
     * 用户流程分析
     * @param userId
     * @return
     */
    WorkflowUserAnalysisVo userProcessAnalysis(String userId);

    Long countUserTodoTotal(String userId);

    /**
     *  保存表单
     * @param form
     * @param user
     * @return
     */
    boolean saveTaskFormData(WorkflowTaskFormVo form, WorkflowUserVo user);
    boolean saveTaskFormData(WorkflowTaskFormVo form, WorkflowUserVo user,int isSubmitState);

    public boolean saveTaskFormData(WorkflowTaskFormVo form, WorkflowUserVo user, int isSubmitState, TaskEntity task);

    String getPreviousTaskDataSrc(Map<String,Object> map);
}
