package com.chd.modules.oversee.issue.service;

import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueFlowOrg;
import com.chd.modules.workflow.vo.WorkflowProcessDetailVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import liquibase.pro.packaged.M;

import java.util.List;
import java.util.Map;

/**
 * 巡视巡察流程
 */
public interface IOverseeWorkflowService {

    /**
     * 流程发起
     * @param issueId
     * @param loginUser
     * @return
     */
    boolean overseeIssueLaunchProcess(Long issueId,LoginUser loginUser);

    boolean overseeIssueLaunchProcess(Long issueId,LoginUser loginUser,Integer issueLaunchType);

    boolean overseeIssueLaunchProcess(Map<String,Object> map);
    boolean overseeOldIssueLaunchProcess(Long issueId,LoginUser loginUser);

}
