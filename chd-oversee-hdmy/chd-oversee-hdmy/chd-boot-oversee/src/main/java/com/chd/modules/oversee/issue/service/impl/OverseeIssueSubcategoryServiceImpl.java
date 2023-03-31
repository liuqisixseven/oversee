package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSubcategoryMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueSubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_subcategory
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class OverseeIssueSubcategoryServiceImpl extends ServiceImpl<OverseeIssueSubcategoryMapper, OverseeIssueSubcategory> implements IOverseeIssueSubcategoryService {

    @Autowired
    OverseeIssueSubcategoryMapper overseeIssueSubcategoryMapper;

    @Override
    @Transactional
    public int addOrUpdate(OverseeIssueSubcategory overseeIssueSubcategory) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=overseeIssueSubcategory),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueSubcategory.getName())),"请传递问题小类名称");
        Assert.isTrue((null!=overseeIssueSubcategory.getIssueCategoryId()&&overseeIssueSubcategory.getIssueCategoryId().intValue()>0),"请选择对应的问题大类");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueSubcategory.getUpdateUserId())),"未获取到修改用户id");
        overseeIssueSubcategory.setUpdateTime(new Date());
        if(null==overseeIssueSubcategory.getSort()){
            overseeIssueSubcategory.setSort(100);
        }

        overseeIssueSubcategory.setUpdateTime(new Date());

        if(null!=overseeIssueSubcategory.getId()&&overseeIssueSubcategory.getId().intValue()>0){
            addOrUpdateCount = overseeIssueSubcategoryMapper.updateById(overseeIssueSubcategory);
        }else {
            overseeIssueSubcategory.setCreateUserId(overseeIssueSubcategory.getUpdateUserId());
            overseeIssueSubcategory.setCreateTime(new Date());
            overseeIssueSubcategory.setDataType(1);
            addOrUpdateCount = overseeIssueSubcategoryMapper.insert(overseeIssueSubcategory);
        }
        return addOrUpdateCount;
    }

    @Override
    public IPage<OverseeIssueSubcategory> selectOverseeIssueSubcategoryPageVo(Page<?> page, Map<String, Object> map) {
        return overseeIssueSubcategoryMapper.selectOverseeIssueSubcategoryPageVo(page,map);
    }

    @Override
    public List<OverseeIssueSubcategory> selectOverseeIssueSubcategoryList(Map<String, Object> map) {
        return overseeIssueSubcategoryMapper.selectOverseeIssueSubcategoryList(map);
    }
}
