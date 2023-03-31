package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.OverseeIssueFlowOrg;

import java.util.List;

public interface OverseeIssueFlowOrgMapper extends BaseMapper<OverseeIssueFlowOrg> {

    List<OverseeIssueFlowOrg> orgListByUserId(String userId);
}
