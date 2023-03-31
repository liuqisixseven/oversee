package com.chd.modules.oversee.issue.mapper;

import com.chd.modules.oversee.issue.entity.IssuesRectificationMeasure;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: issues_rectification_measure
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IssuesRectificationMeasureMapper extends BaseMapper<IssuesRectificationMeasure> {

    List<IssuesRectificationMeasure> selectIssuesRectificationMeasureList(@Param("map") Map<String, Object> map);

}
