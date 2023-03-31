package com.chd.modules.workflow.service;

import com.chd.modules.workflow.vo.WorkflowUserVo;
import com.chd.modules.workflow.vo.WorkflowVariablesVo;

import java.util.Map;

/**
 * 业务
 */
public interface WorkflowBusinessProcessService {

    Map<String,Object> getProcessVariables(String category, String bizId, WorkflowUserVo userVo);
}
