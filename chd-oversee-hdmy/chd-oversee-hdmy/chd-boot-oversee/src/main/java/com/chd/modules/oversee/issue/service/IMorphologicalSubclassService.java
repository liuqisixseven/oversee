package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.MorphologicalSubclass;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_subcategory
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IMorphologicalSubclassService extends IService<MorphologicalSubclass> {

    int addOrUpdate(MorphologicalSubclass overseeIssueSubcategory);

    IPage<MorphologicalSubclass> selectMorphologicalSubclassPageVo(Page<?> page, Map<String, Object> map);

    List<MorphologicalSubclass> selectMorphologicalSubclassList(Map<String, Object> map);

}
