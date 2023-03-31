package com.chd.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.system.entity.SysPosition;

/**
 * @Description: 职务表
 * 
 * @Date: 2019-09-19
 * @Version: V1.0
 */
public interface ISysPositionService extends IService<SysPosition> {

    /**
     * 通过code查询
     * @param code 职务编码
     * @return SysPosition
     */
    SysPosition getByCode(String code);

}
