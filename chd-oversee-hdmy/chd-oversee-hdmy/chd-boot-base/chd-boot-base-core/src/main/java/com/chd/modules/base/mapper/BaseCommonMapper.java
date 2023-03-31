package com.chd.modules.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import com.chd.common.api.dto.LogDTO;

/**
 * @Description: BaseCommonMapper
 * 
 */
public interface BaseCommonMapper {

    /**
     * 保存日志
     * @param dto
     */
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto")LogDTO dto);

}
