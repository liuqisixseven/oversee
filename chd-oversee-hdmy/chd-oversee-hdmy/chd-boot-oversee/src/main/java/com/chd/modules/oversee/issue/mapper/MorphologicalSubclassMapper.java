package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.issue.entity.MorphologicalSubclass;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_subcategory
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface MorphologicalSubclassMapper extends BaseMapper<MorphologicalSubclass> {

    IPage<MorphologicalSubclass> selectMorphologicalSubclassPageVo(Page<?> page, @Param("map") Map<String, Object> map);

    List<MorphologicalSubclass> selectMorphologicalSubclassList(@Param("map") Map<String, Object> map);

}
