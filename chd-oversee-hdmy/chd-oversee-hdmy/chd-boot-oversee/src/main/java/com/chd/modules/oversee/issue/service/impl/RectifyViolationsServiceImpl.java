package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.RecoverFunds;
import com.chd.modules.oversee.issue.entity.RectifyViolations;
import com.chd.modules.oversee.issue.entity.RectifyViolations;
import com.chd.modules.oversee.issue.mapper.RectifyViolationsMapper;
import com.chd.modules.oversee.issue.mapper.RectifyViolationsMapper;
import com.chd.modules.oversee.issue.service.IRectifyViolationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: rectify_violations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class RectifyViolationsServiceImpl extends ServiceImpl<RectifyViolationsMapper, RectifyViolations> implements IRectifyViolationsService {

    @Autowired
    RectifyViolationsMapper rectifyViolationsMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    public int addOrUpdateList(List<RectifyViolations> rectifyViolationsList) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=rectifyViolationsList&&rectifyViolationsList.size()>0),"请传递问责处理集合数据");
        for(RectifyViolations rectifyViolations : rectifyViolationsList){
            addOrUpdateCount = addOrUpdate(rectifyViolations);
        }
        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<RectifyViolations> rectifyViolationsList, Long issueId,String departId,String updateUserId,String taskId) {
        Assert.isTrue((null!=rectifyViolationsList&&rectifyViolationsList.size()>0),"请传递问责处理集合数据");
        for(RectifyViolations rectifyViolations : rectifyViolationsList){
            rectifyViolations.setIssueId(issueId);
            rectifyViolations.setOrgId(departId);
            rectifyViolations.setUpdateUserId(updateUserId);
            rectifyViolations.setTaskId(taskId);
        }
        return addOrUpdateList(rectifyViolationsList);
    }

    @Override
    @Transactional
    public int addOrUpdate(RectifyViolations rectifyViolations) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=rectifyViolations),"请传递问责处理数据");
        Assert.isTrue((StringUtil.isNotEmpty(rectifyViolations.getOrgId())),"请传递问责处理责任部门id");
        Assert.isTrue((null!=rectifyViolations.getIssueId()&&rectifyViolations.getIssueId().longValue()>0L),"请传递问责处理问题id");
        Assert.isTrue((StringUtil.isNotEmpty(rectifyViolations.getUpdateUserId())),"请传递问责处理修改用户id");
        Assert.isTrue((StringUtil.isNotEmpty(rectifyViolations.getUuid())),"请传递问uuid");
        rectifyViolations.setUpdateTime(new Date());

        String redisKey = "rectifyViolations::" + (StringUtil.isNotEmpty(rectifyViolations.getUuid())?rectifyViolations.getUuid(): UUID.randomUUID().toString());
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                rectifyViolations.setId(getDataIdByUUId(rectifyViolations.getUuid(),rectifyViolations.getIssueId()));

                if(null!=rectifyViolations.getId()&&rectifyViolations.getId()>0){
                    addOrUpdateCount = rectifyViolationsMapper.updateById(rectifyViolations);
                }else{
                    rectifyViolations.setCreateUserId(rectifyViolations.getUpdateUserId());
                    rectifyViolations.setCreateTime(new Date());
                    rectifyViolations.setDataType(1);
                    addOrUpdateCount = rectifyViolationsMapper.insert(rectifyViolations);
                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }

        return addOrUpdateCount;
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return rectifyViolationsMapper.selectCount(map);
    }

    Integer getDataIdByUUId(String uuid,Long issueId){
        List<RectifyViolations> rectifyViolationsList = rectifyViolationsMapper.selectList(Wrappers.<RectifyViolations>lambdaQuery().eq(RectifyViolations::getUuid, uuid).eq(RectifyViolations::getId,issueId).eq(RectifyViolations::getDataType, 1));
        if(null!=rectifyViolationsList&&rectifyViolationsList.size()>0){
            if(null!=rectifyViolationsList.get(0)&&null!=rectifyViolationsList.get(0).getId()){
                return rectifyViolationsList.get(0).getId();
            }
        }
        return null;
    }
}
