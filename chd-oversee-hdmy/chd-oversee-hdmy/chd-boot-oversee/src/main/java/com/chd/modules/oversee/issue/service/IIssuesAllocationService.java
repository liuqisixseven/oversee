package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IIssuesAllocationService extends IService<IssuesAllocation> {



    int addOrUpdateOrgIds(String responsibleOrgIds, Long issueId, String updateUserId,String responsibleMainOrgIds,String responsibleCoordinationOrgIds);

    int addOrUpdateList(List<IssuesAllocation> issuesAllocationNews, Long issueId);

    int addOrUpdate(IssuesAllocation issuesAllocation);

    int deleteIssuesAllocation(IssuesAllocation issuesAllocation);

    String getIssuesAllocationDepartmentNames(Long issuesId,Integer departmentType);

}
