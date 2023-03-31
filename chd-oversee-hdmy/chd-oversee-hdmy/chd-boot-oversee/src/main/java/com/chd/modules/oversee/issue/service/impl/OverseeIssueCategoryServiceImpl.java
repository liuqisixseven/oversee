package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.chd.modules.oversee.issue.mapper.OverseeIssueCategoryMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSubcategoryMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueCategoryService;
import liquibase.pro.packaged.Q;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
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
public class OverseeIssueCategoryServiceImpl extends ServiceImpl<OverseeIssueCategoryMapper, OverseeIssueCategory> implements IOverseeIssueCategoryService {

    @Autowired
    OverseeIssueCategoryMapper overseeIssueCategoryMapper;
    @Autowired
    private OverseeIssueSubcategoryMapper overseeIssueSubcategoryMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    public int addOrUpdate(OverseeIssueCategory overseeIssueCategory) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=overseeIssueCategory),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueCategory.getName())),"请传递问题大类名称");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueCategory.getUpdateUserId())),"未获取到修改用户id");
        if(null==overseeIssueCategory.getSort()){
            overseeIssueCategory.setSort(100);
        }

        overseeIssueCategory.setUpdateTime(new Date());

        if(null!=overseeIssueCategory.getId()&&overseeIssueCategory.getId().intValue()>0){
            addOrUpdateCount = overseeIssueCategoryMapper.updateById(overseeIssueCategory);
        }else {

            overseeIssueCategory.setCreateUserId(overseeIssueCategory.getUpdateUserId());
            overseeIssueCategory.setCreateTime(overseeIssueCategory.getUpdateTime());
            overseeIssueCategory.setDataType(OverseeConstants.DataType.Enable);
            addOrUpdateCount = overseeIssueCategoryMapper.insert(overseeIssueCategory);
        }
        return addOrUpdateCount;
    }

    @Override
    public IPage<OverseeIssueCategory> selectOverseeIssueCategoryPageVo(Page<?> page, Map<String, Object> map) {
        return overseeIssueCategoryMapper.selectOverseeIssueCategoryPageVo(page,map);
    }

    @Override
    public List<OverseeIssueCategory> selectOverseeIssueCategoryList(Map<String, Object> map) {
        return overseeIssueCategoryMapper.selectOverseeIssueCategoryList(map);
    }

    private Integer insertCategory(OverseeIssueCategory category){
        Date now=new Date();
        category.setCreateUserId(category.getUpdateUserId());
        category.setUpdateTime(now);
        category.setCreateTime(now);
        if(category.getSort()==null) {
            category.setSort(100);
        }
        category.setDataType(OverseeConstants.DataType.Enable);
        return overseeIssueCategoryMapper.insert(category);
    }
    @Override
    public OverseeIssueCategory createOrReplaceCategory(String categoryName, String subCategoryName, LoginUser user) {
        OverseeIssueCategory category = null;
        if(StringUtil.isNotEmpty(categoryName)){
            String redisKey = BaseConstant.OVERSEE_ISSUE_CATEGORY_EDIT_CATEGORY_NAME_PREFIX + categoryName;
            if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(2))){
                try{
                    Date now=new Date();
                    category=overseeIssueCategoryMapper.findByName(categoryName);
                    if(category==null){
                        category=new OverseeIssueCategory();
                        category.setName(categoryName);
                        category.setUpdateUserId(user.getId());
                        insertCategory(category);
                    }
                    if(category.getId()!=null) {
                        List<OverseeIssueSubcategory> subcategoryList=new ArrayList<>();
                        QueryWrapper query = new QueryWrapper();
                        query.eq("issue_category_id", category.getId());
                        query.eq("name", subCategoryName);
                        OverseeIssueSubcategory subcategory= overseeIssueSubcategoryMapper.selectOne(query);
                        if(subcategory==null){
                            subcategory=new OverseeIssueSubcategory();
                            subcategory.setIssueCategoryId(category.getId());
                            subcategory.setName(subCategoryName);
                            subcategory.setUpdateUserId(user.getId());
                            subcategory.setCreateUserId(category.getUpdateUserId());
                            subcategory.setUpdateTime(now);
                            subcategory.setCreateTime(now);
                            if(subcategory.getSort()==null) {
                                subcategory.setSort(100);
                            }
                            subcategory.setDataType(OverseeConstants.DataType.Enable);
                            overseeIssueSubcategoryMapper.insert(subcategory);
                        }
                        subcategoryList.add(subcategory);
                        category.setSubcategoryList(subcategoryList);
                    }
                }finally {
                    redisTemplate.delete(redisKey);
                }
            }
        }
        return category;
    }
}
