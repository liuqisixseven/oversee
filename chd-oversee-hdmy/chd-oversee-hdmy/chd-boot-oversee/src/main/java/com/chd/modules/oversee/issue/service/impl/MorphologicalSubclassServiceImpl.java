package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.MorphologicalSubclass;
import com.chd.modules.oversee.issue.mapper.MorphologicalSubclassMapper;
import com.chd.modules.oversee.issue.service.IMorphologicalSubclassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class MorphologicalSubclassServiceImpl extends ServiceImpl<MorphologicalSubclassMapper, MorphologicalSubclass> implements IMorphologicalSubclassService {

    @Autowired
    MorphologicalSubclassMapper morphologicalSubclassMapper;

    @Override
    @Transactional
    public int addOrUpdate(MorphologicalSubclass morphologicalSubclass) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=morphologicalSubclass),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(morphologicalSubclass.getName())),"请传递小类名称");
        Assert.isTrue((null!=morphologicalSubclass.getMorphologicalCategoriesId()&&morphologicalSubclass.getMorphologicalCategoriesId().intValue()>0),"请选择对应的大类");
        Assert.isTrue((StringUtil.isNotEmpty(morphologicalSubclass.getUpdateUserId())),"未获取到修改用户id");
        morphologicalSubclass.setUpdateTime(new Date());
        if(null==morphologicalSubclass.getSort()){
            morphologicalSubclass.setSort(100);
        }

        morphologicalSubclass.setUpdateTime(new Date());

        if(null!=morphologicalSubclass.getId()&&morphologicalSubclass.getId().intValue()>0){
            addOrUpdateCount = morphologicalSubclassMapper.updateById(morphologicalSubclass);
        }else {
            morphologicalSubclass.setCreateUserId(morphologicalSubclass.getUpdateUserId());
            morphologicalSubclass.setCreateTime(new Date());
            morphologicalSubclass.setDataType(1);
            addOrUpdateCount = morphologicalSubclassMapper.insert(morphologicalSubclass);
        }
        return addOrUpdateCount;
    }

    @Override
    public IPage<MorphologicalSubclass> selectMorphologicalSubclassPageVo(Page<?> page, Map<String, Object> map) {
        return morphologicalSubclassMapper.selectMorphologicalSubclassPageVo(page,map);
    }

    @Override
    public List<MorphologicalSubclass> selectMorphologicalSubclassList(Map<String, Object> map) {
        return morphologicalSubclassMapper.selectMorphologicalSubclassList(map);
    }
}
