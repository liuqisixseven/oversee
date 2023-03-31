package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_category
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IMorphologicalCategoriesService extends IService<MorphologicalCategories> {

    int addOrUpdate(MorphologicalCategories overseeIssueCategory);

    IPage<MorphologicalCategories> selectMorphologicalCategoriesPageVo(Page<?> page, Map<String, Object> map);

    List<MorphologicalCategories> selectMorphologicalCategoriesList(Map<String, Object> map);

    MorphologicalCategories createOrReplaceCategory(String categoryName, String subCategoryName, LoginUser user);

}
