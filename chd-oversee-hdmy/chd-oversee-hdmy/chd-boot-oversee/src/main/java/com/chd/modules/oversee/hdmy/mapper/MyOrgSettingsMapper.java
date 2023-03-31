package com.chd.modules.oversee.hdmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.hdmy.entity.MyOrgSettings;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
public interface MyOrgSettingsMapper extends BaseMapper<MyOrgSettings> {

    List<MyOrgSettings> selectMyOrgSettingsList(@Param("map") Map<String, Object> map);

    IPage<MyOrgSettings> selectMyOrgSettingsPageVo(Page<?> page, @Param("map") Map<String, Object> map);



}
