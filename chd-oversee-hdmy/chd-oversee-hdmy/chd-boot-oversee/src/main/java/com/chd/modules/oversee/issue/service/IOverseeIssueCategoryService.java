package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_category
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IOverseeIssueCategoryService extends IService<OverseeIssueCategory> {

    int addOrUpdate(OverseeIssueCategory overseeIssueCategory);

    IPage<OverseeIssueCategory> selectOverseeIssueCategoryPageVo(Page<?> page, Map<String, Object> map);

    List<OverseeIssueCategory> selectOverseeIssueCategoryList(Map<String, Object> map);

    OverseeIssueCategory createOrReplaceCategory(String categoryName, String subCategoryName, LoginUser user);

}
