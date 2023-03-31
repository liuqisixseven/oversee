package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.IssuesSupervisor;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;

import java.util.List;
import java.util.Map;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IIssuesSupervisorService extends IService<IssuesSupervisor> {

    List<IssuesSupervisor> selectIssuesSupervisorList(Map<String, Object> map);

    int addOrUpdateOrgIds(String responsibleOrgIds, Long issueId, String updateUserId,List<IssuesSupervisor> issuesSupervisorList);

    int addOrUpdateList(List<IssuesSupervisor> issuesSupervisorsNews, Long issueId);

    int addOrUpdate(IssuesSupervisor issuesSupervisor);

    int deleteIssuesAllocation(IssuesSupervisor issuesSupervisor);

    String getIssuesAllocationDepartmentNames(Long issuesId,Integer departmentType);

}
