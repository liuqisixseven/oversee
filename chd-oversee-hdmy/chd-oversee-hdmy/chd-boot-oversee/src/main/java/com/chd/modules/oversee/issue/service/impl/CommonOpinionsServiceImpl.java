package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.mapper.IssuesAllocationMapper;
import com.chd.modules.oversee.issue.mapper.IssuesSupervisorMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.mapper.CommonOpinionsMapper;
import com.chd.modules.oversee.issue.service.ICommonOpinionsService;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class CommonOpinionsServiceImpl extends ServiceImpl<CommonOpinionsMapper, CommonOpinions> implements ICommonOpinionsService {

    @Autowired
    CommonOpinionsMapper commonOpinionsMapper;


    @Override
    @Transactional
    public int addOrUpdate(CommonOpinions commonOpinions) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=commonOpinions),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(commonOpinions.getValue())),"请传递常用意见");
        Assert.isTrue((StringUtil.isNotEmpty(commonOpinions.getUpdateUserId())),"未获取到修改用户id");
        commonOpinions.setUserId(commonOpinions.getUpdateUserId());
        if(null==commonOpinions.getSort()){
            commonOpinions.setSort(100);
        }

        commonOpinions.setUpdateTime(new Date());

        if(null!=commonOpinions.getId()&&commonOpinions.getId().intValue()>0){
            addOrUpdateCount = commonOpinionsMapper.updateById(commonOpinions);
        }else {
            commonOpinions.setCreateUserId(commonOpinions.getUpdateUserId());
            commonOpinions.setCreateTime(commonOpinions.getUpdateTime());
            commonOpinions.setDataType(OverseeConstants.DataType.Enable);
            addOrUpdateCount = commonOpinionsMapper.insert(commonOpinions);
        }
        return addOrUpdateCount;
    }
}
