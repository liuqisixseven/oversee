package com.chd.modules.oversee.issue.service.impl;

import com.chd.common.util.JsonUtils;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.workflow.service.WorkflowBusinessProcessService;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *问题上报流程业务
 */
@Service
public class OverseeIssueProcessServiceImpl implements WorkflowBusinessProcessService {

    @Autowired
    private IOverseeIssueService overseeIssueService;

    @Override
    public Map<String,Object> getProcessVariables(String category, String bizId, WorkflowUserVo userVo) {
        Map<String, Object> variables=null;
        OverseeIssue overseeIssue = overseeIssueService.getRedisCacheOverseeIssue(Long.valueOf(bizId));
        if(overseeIssue!=null) {
            variables = JsonUtils.fromJson(JsonUtils.toJsonStr(overseeIssue), Map.class);
            variables.put(WorkflowConstants.FLOW_USER_OWNER, userVo.getId());
        }
        return variables;
    }


}
