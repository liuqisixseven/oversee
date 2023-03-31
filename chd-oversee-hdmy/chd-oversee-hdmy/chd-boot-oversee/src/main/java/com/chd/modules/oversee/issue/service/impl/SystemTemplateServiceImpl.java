package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.SystemTemplate;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.chd.modules.oversee.issue.mapper.SystemTemplateMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSubcategoryMapper;
import com.chd.modules.oversee.issue.service.ISystemTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_category
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class SystemTemplateServiceImpl extends ServiceImpl<SystemTemplateMapper, SystemTemplate> implements ISystemTemplateService {

    @Autowired
    SystemTemplateMapper systemTemplateMapper;



    @Override
    @Transactional
    public int addOrUpdate(SystemTemplate systemTemplate) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=systemTemplate),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(systemTemplate.getName())),"请传递名称");
        Assert.isTrue((StringUtil.isNotEmpty(systemTemplate.getAppendixPath())),"请上传模板附件");
        Assert.isTrue((StringUtil.isNotEmpty(systemTemplate.getUpdateUserId())),"未获取到修改用户id");
        if(null==systemTemplate.getSort()){
            systemTemplate.setSort(100);
        }

        systemTemplate.setUpdateTime(new Date());

        if(null!=systemTemplate.getId()&&systemTemplate.getId().intValue()>0){
            addOrUpdateCount = systemTemplateMapper.updateById(systemTemplate);
        }else {

            systemTemplate.setCreateUserId(systemTemplate.getUpdateUserId());
            systemTemplate.setCreateTime(systemTemplate.getUpdateTime());
            systemTemplate.setDataType(OverseeConstants.DataType.Enable);
            addOrUpdateCount = systemTemplateMapper.insert(systemTemplate);
        }
        return addOrUpdateCount;
    }

    @Override
    public IPage<SystemTemplate> selectSystemTemplatePageVo(Page<?> page, Map<String, Object> map) {
        return systemTemplateMapper.selectSystemTemplatePageVo(page,map);
    }

    @Override
    public List<SystemTemplate> selectSystemTemplateList(Map<String, Object> map) {
        return systemTemplateMapper.selectSystemTemplateList(map);
    }


}
