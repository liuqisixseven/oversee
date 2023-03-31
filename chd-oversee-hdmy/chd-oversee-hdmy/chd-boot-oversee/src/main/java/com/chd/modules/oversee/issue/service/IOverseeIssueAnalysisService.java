package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.IssueAnalysisInfoVo;
import com.chd.modules.oversee.issue.entity.IssueAnalysisItemVo;
import com.chd.modules.oversee.issue.entity.IssueAnalysisQueryVo;
import com.chd.modules.oversee.issue.entity.OverseeIssueQueryVo;

import java.util.List;

public interface IOverseeIssueAnalysisService {

    /**
     * 问题上报统计信息
     * @param query
     * @return
     */
    IssueAnalysisInfoVo issueInfo(IssueAnalysisQueryVo query);


    List<IssueAnalysisItemVo> issueListByTime(IssueAnalysisQueryVo query);

    List<IssueAnalysisItemVo> issueListByType(IssueAnalysisQueryVo query);

    List<IssueAnalysisItemVo> overdueSituation(IssueAnalysisQueryVo query);

    IssueAnalysisInfoVo newInfo(OverseeIssueQueryVo query);
}
