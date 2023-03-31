package com.chd.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.modules.system.entity.SysPosition;
import com.chd.modules.system.mapper.SysPositionMapper;
import com.chd.modules.system.service.ISysPositionService;
import org.springframework.stereotype.Service;

/**
 * @Description: 职务表
 * 
 * @Date: 2019-09-19
 * @Version: V1.0
 */
@Service
public class SysPositionServiceImpl extends ServiceImpl<SysPositionMapper, SysPosition> implements ISysPositionService {

    @Override
    public SysPosition getByCode(String code) {
        LambdaQueryWrapper<SysPosition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPosition::getCode, code);
        return super.getOne(queryWrapper);
    }

}
