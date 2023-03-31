package com.chd.modules.workflow.service;

import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import com.chd.modules.workflow.vo.WorkflowVariablesVo;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

public interface WorkflowUserService {


    List<String> roleNameListByRoleId(List<String> roleId);

    List<WorkflowUserVo> userListByIds(List<String> userIds);
    WorkflowUserVo getUserById(String userId);

    boolean isWorkflowSuperUser(String userId);

    List<WorkflowUserVo> allUserList(int pageNo,int pageSize);

    List<String> getTaskGroupIdsByUserId(String userId);

    List<WorkflowUserTaskVo> getBizVariableUser(List<WorkflowUserTaskVo> userTasks,String category,String bizId,String startUserId);

    Map<String, List<WorkflowUserVo>> getBizUserVariables(String category, String bizId, String startUserId);
    Map<String, List<WorkflowDepart>> getBizDepartVariables(String category, String bizId, String startUserId);


}
