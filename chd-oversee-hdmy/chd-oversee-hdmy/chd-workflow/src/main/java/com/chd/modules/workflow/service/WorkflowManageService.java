package com.chd.modules.workflow.service;

import com.chd.modules.workflow.entity.WorkflowTaskFormTag;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.flowable.bpmn.model.UserTask;

import java.util.*;

/**
 * 流程管理
 */
public interface WorkflowManageService {


     List<WorkflowTaskFormTag> taskFormTagList(WorkflowTaskFormTag query);

      WorkflowTaskFormTag getTaskFormTag(String code);

     int saveTaskFormTag(WorkflowTaskFormTag taskFormTag);

     int deleteTaskFormTag(String code);



     List<WorkflowUserVo> approveUserList(int pageNo,int pageSize);

     List<WorkflowUserTaskVo> convertUserTask(List<UserTask> userTasks);
     List<WorkflowUserTaskVo> convertUserTask(List<UserTask> userTasks,String processId);
     List<WorkflowUserTaskVo> convertTask(List<WorkflowTaskVo> userTasks);



     List<WorkflowUserVo> orgUserListByIds(List<String> ids);
}
