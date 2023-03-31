package com.chd.modules.oversee.issue.mapper;

import com.chd.modules.oversee.issue.entity.IssueAnalysisInfoVo;
import com.chd.modules.oversee.issue.entity.IssueAnalysisItemVo;
import com.chd.modules.oversee.issue.entity.IssueAnalysisQueryVo;

import java.util.List;

public interface OverseeIssueAnalysisMapper {

    /**
     * 统计问题上报的基本信息
     * @param query
     * @return
     */
    IssueAnalysisInfoVo issueBaseInfo(IssueAnalysisQueryVo query);

    /**
     * 按问题上报的来源
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> issueSource(IssueAnalysisQueryVo query);

    /**
     * 按问题上报的分类
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> issueCategory(IssueAnalysisQueryVo query);

    /**
     * 按问题上报的检查时间统计
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> issueCheckTime(IssueAnalysisQueryVo query);

    /**
     * 销号时间统计
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> issueCompletedTime(IssueAnalysisQueryVo query);

    /**
     * 上报时间
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> issueCreateTime(IssueAnalysisQueryVo query);
    List<IssueAnalysisItemVo> issueReportTime(IssueAnalysisQueryVo query);

    /**
     * 责任单位统计
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> responsibleUnit(IssueAnalysisQueryVo query);

    /**
     * 督办部门
     * @param query
     * @return
     */
    List<IssueAnalysisItemVo> supervisorOrg(IssueAnalysisQueryVo query);
    List<IssueAnalysisItemVo> issueSubmitState(IssueAnalysisQueryVo query);

    List<IssueAnalysisItemVo> onlyCategory(IssueAnalysisQueryVo query);

    IssueAnalysisItemVo overdueSituation(IssueAnalysisQueryVo query);
}
