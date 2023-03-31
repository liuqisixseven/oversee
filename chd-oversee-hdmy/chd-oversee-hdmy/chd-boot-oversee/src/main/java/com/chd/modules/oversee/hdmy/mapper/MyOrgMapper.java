package com.chd.modules.oversee.hdmy.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
public interface MyOrgMapper extends BaseMapper<MyOrg> {

    List<MyOrg> selectMyOrgList(@Param("map") Map<String, Object> map);

}
