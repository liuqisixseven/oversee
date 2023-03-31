package com.chd.modules.oversee.issue.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: oversee_issue_category
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface OverseeIssueCategoryMapper extends BaseMapper<OverseeIssueCategory> {

    IPage<OverseeIssueCategory> selectOverseeIssueCategoryPageVo(Page<?> page, @Param("map") Map<String, Object> map);

    List<OverseeIssueCategory> selectOverseeIssueCategoryList(@Param("map") Map<String, Object> map);

    OverseeIssueCategory findByName(String name);

}
