package com.chd.modules.oversee.issue.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: oversee_issue_subcategory
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface OverseeIssueSubcategoryMapper extends BaseMapper<OverseeIssueSubcategory> {

    IPage<OverseeIssueSubcategory> selectOverseeIssueSubcategoryPageVo(Page<?> page, @Param("map") Map<String, Object> map);

    List<OverseeIssueSubcategory> selectOverseeIssueSubcategoryList(@Param("map") Map<String, Object> map);

}
