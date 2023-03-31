package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.entity.OverseeIssueAppendix;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_appendix
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IOverseeIssueAppendixService extends IService<OverseeIssueAppendix> {

    List<OverseeIssueAppendix> selectOverseeIssueAppendixList(Map<String, Object> map);

    int addOrUpdateList(List<OverseeIssueAppendix> overseeIssueAppendixs, Long issueId, Integer type);
    int addOrUpdateList(List<OverseeIssueAppendix> overseeIssueAppendixs, Long issueId, Integer type,String updateUserId);

    int addOrUpdateList(List<OverseeIssueAppendix> overseeIssueAppendixs, Long issueId, Integer type,String updateUserId,String dataId);

    int addOrUpdate(OverseeIssueAppendix overseeIssueAppendix);

    int deleteIssuesAllocation(OverseeIssueAppendix overseeIssueAppendix);

    int synchronizationOverseeIssueAppendixListFileName(List<OverseeIssueAppendix> overseeIssueAppendixList);

    List<Map<String,Object>> getOssFileList(@Param("map")  Map<String, Object> map);

}
