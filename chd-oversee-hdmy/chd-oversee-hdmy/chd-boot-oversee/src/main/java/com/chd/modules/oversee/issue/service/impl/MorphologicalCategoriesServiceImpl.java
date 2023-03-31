package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.chd.modules.oversee.issue.mapper.MorphologicalCategoriesMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSubcategoryMapper;
import com.chd.modules.oversee.issue.service.IMorphologicalCategoriesService;
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
public class MorphologicalCategoriesServiceImpl extends ServiceImpl<MorphologicalCategoriesMapper, MorphologicalCategories> implements IMorphologicalCategoriesService {

    @Autowired
    MorphologicalCategoriesMapper morphologicalCategoriesMapper;
    @Autowired
    private OverseeIssueSubcategoryMapper overseeIssueSubcategoryMapper;


    @Override
    @Transactional
    public int addOrUpdate(MorphologicalCategories morphologicalCategories) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=morphologicalCategories),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(morphologicalCategories.getName())),"请传递形态大类名称");
        Assert.isTrue((StringUtil.isNotEmpty(morphologicalCategories.getUpdateUserId())),"未获取到修改用户id");
        if(null==morphologicalCategories.getSort()){
            morphologicalCategories.setSort(100);
        }

        morphologicalCategories.setUpdateTime(new Date());

        if(null!=morphologicalCategories.getId()&&morphologicalCategories.getId().intValue()>0){
            addOrUpdateCount = morphologicalCategoriesMapper.updateById(morphologicalCategories);
        }else {

            morphologicalCategories.setCreateUserId(morphologicalCategories.getUpdateUserId());
            morphologicalCategories.setCreateTime(morphologicalCategories.getUpdateTime());
            morphologicalCategories.setDataType(OverseeConstants.DataType.Enable);
            addOrUpdateCount = morphologicalCategoriesMapper.insert(morphologicalCategories);
        }
        return addOrUpdateCount;
    }

    @Override
    public IPage<MorphologicalCategories> selectMorphologicalCategoriesPageVo(Page<?> page, Map<String, Object> map) {
        return morphologicalCategoriesMapper.selectMorphologicalCategoriesPageVo(page,map);
    }

    @Override
    public List<MorphologicalCategories> selectMorphologicalCategoriesList(Map<String, Object> map) {
        return morphologicalCategoriesMapper.selectMorphologicalCategoriesList(map);
    }

    private Integer insertCategory(MorphologicalCategories category){
        Date now=new Date();
        category.setCreateUserId(category.getUpdateUserId());
        category.setUpdateTime(now);
        category.setCreateTime(now);
        if(category.getSort()==null) {
            category.setSort(100);
        }
        category.setDataType(OverseeConstants.DataType.Enable);
        return morphologicalCategoriesMapper.insert(category);
    }
    @Override
    public MorphologicalCategories createOrReplaceCategory(String categoryName, String subCategoryName, LoginUser user) {
        Date now=new Date();
        MorphologicalCategories category=morphologicalCategoriesMapper.findByName(categoryName);
        if(category==null){
            category=new MorphologicalCategories();
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
        return category;
    }
}
