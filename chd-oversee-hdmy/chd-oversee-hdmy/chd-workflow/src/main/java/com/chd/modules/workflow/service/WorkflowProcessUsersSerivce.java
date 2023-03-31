package com.chd.modules.workflow.service;

import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.vo.WorkflowUserVo;

import java.util.List;
import java.util.Map;

public interface WorkflowProcessUsersSerivce {

    int saveProcessUsersForm(Map<String,Object> form, String departId, String processId, WorkflowUserVo user);
    Map<String, List<String>> getProcessUsersVariableMap(String processId,String subProcessId,String departId);

    Map<String, List<WorkflowUserVo>> getProcessUsersVariables(String processId,String subProcessId,String departId);
}
