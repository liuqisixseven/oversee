package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.issue.entity.SpecificIssues;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: specific_issues
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface ISpecificIssuesService extends IService<SpecificIssues> {

    int addOrUpdate(SpecificIssues specificIssues);

    int addOrUpdate(String specificIssuesContent,String updateUserId);

    int addOrUpdate(String specificIssuesContent,Long issueId, Integer id, String updateUserId);

    IPage<SpecificIssues> selectSpecificIssuesPageVo(Page<?> page, Map<String, Object> map);

    List<SpecificIssues> selectSpecificIssuesList(Map<String, Object> map);



}
