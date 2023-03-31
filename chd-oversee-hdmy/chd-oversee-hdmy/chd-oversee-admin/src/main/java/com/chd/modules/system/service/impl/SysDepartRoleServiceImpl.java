package com.chd.modules.system.service.impl;

import com.chd.modules.system.entity.SysDepartRole;
import com.chd.modules.system.mapper.SysDepartRoleMapper;
import com.chd.modules.system.service.ISysDepartRoleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 部门角色
 * 
 * @Date:   2020-02-12
 * @Version: V1.0
 */
@Service
public class SysDepartRoleServiceImpl extends ServiceImpl<SysDepartRoleMapper, SysDepartRole> implements ISysDepartRoleService {

    @Override
    public List<SysDepartRole> queryDeptRoleByDeptAndUser(String orgCode, String userId) {
        return this.baseMapper.queryDeptRoleByDeptAndUser(orgCode,userId);
    }
}
