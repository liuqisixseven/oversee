package com.chd.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.util.StringUtil;
import com.chd.modules.workflow.entity.ProcessClassification;
import com.chd.modules.workflow.mapper.ProcessClassificationMapper;
import com.chd.modules.workflow.service.IProcessClassificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Slf4j
@Service
public class ProcessClassificationServiceImpl extends ServiceImpl<ProcessClassificationMapper, ProcessClassification> implements IProcessClassificationService {


    @Autowired
    ProcessClassificationMapper processClassificationMapper;

    @Override
    public int addOrUpdate(ProcessClassification processClassification) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=processClassification),"请传递流程数据");
//        Assert.isTrue((StringUtil.isNotEmpty(processClassification.getName())),"请传递流程管理名称");
        Assert.isTrue((StringUtil.isNotEmpty(processClassification.getValue())),"请传递流程分类");
        Assert.isTrue((StringUtil.isNotEmpty(processClassification.getUpdateUserId())),"请传递修改用户id");

        if(null==processClassification.getType()||processClassification.getType()<=0){
            processClassification.setType(1);
        }

        processClassification.setUpdateTime(new Date());

        if(null!=processClassification.getId()&&processClassification.getId().intValue()>0){
            addOrUpdateCount = processClassificationMapper.updateById(processClassification);
        }else {
            processClassification.setCreateTime(new Date());
            processClassification.setCreateUserId(processClassification.getUpdateUserId());
            addOrUpdateCount = processClassificationMapper.insert(processClassification);
        }

        return addOrUpdateCount;
    }

    @Override
    public IPage<ProcessClassification> selectProcessClassificationPageVo(Page<?> page, Map<String, Object> map) {
        return processClassificationMapper.selectProcessClassificationPageVo(page,map);
    }

    @Override
    public List<ProcessClassification> selectProcessClassificationList(Map<String, Object> map) {
        return processClassificationMapper.selectProcessClassificationList(map);
    }
}

