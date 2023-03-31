package com.chd.modules.oss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oss.entity.OssFile;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: oss云存储Mapper
 *
 */
public interface OssFileMapper extends BaseMapper<OssFile> {

    IPage<OssFile> selectOssFilePageVo(Page<?> page, @Param("map") Map<String, Object> map);

    List<OssFile> selectOssFileList(@Param("map") Map<String, Object> map);

}
