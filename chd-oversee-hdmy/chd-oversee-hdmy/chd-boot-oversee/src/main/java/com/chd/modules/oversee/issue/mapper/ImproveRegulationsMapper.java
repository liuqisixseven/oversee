package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.ImproveRegulations;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Description: improve_regulations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface ImproveRegulationsMapper extends BaseMapper<ImproveRegulations> {

    int selectCount(@Param("map") Map<String, Object> map);

}
