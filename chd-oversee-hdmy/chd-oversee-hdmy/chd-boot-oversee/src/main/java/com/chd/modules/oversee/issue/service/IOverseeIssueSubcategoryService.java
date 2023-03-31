package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_subcategory
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IOverseeIssueSubcategoryService extends IService<OverseeIssueSubcategory> {

    int addOrUpdate(OverseeIssueSubcategory overseeIssueSubcategory);

    IPage<OverseeIssueSubcategory> selectOverseeIssueSubcategoryPageVo(Page<?> page, Map<String, Object> map);

    List<OverseeIssueSubcategory> selectOverseeIssueSubcategoryList(Map<String, Object> map);

}
