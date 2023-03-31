package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.ImproveRegulations;
import com.chd.modules.oversee.issue.entity.ImproveRegulations;
import com.chd.modules.oversee.issue.entity.RectifyViolations;
import com.chd.modules.oversee.issue.mapper.ImproveRegulationsMapper;
import com.chd.modules.oversee.issue.mapper.ImproveRegulationsMapper;
import com.chd.modules.oversee.issue.service.IImproveRegulationsService;
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
 * @Description: improve_regulations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class ImproveRegulationsServiceImpl extends ServiceImpl<ImproveRegulationsMapper, ImproveRegulations> implements IImproveRegulationsService {
    @Autowired
    ImproveRegulationsMapper improveRegulationsMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    public int addOrUpdateList(List<ImproveRegulations> improveRegulationsList) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=improveRegulationsList&&improveRegulationsList.size()>0),"请传递问责处理集合数据");
        for(ImproveRegulations improveRegulations : improveRegulationsList){
            addOrUpdateCount = addOrUpdate(improveRegulations);
        }
        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<ImproveRegulations> improveRegulationsList, Long issueId,String departId,String updateUserId,String taskId) {
        Assert.isTrue((null!=improveRegulationsList&&improveRegulationsList.size()>0),"请传递问责处理集合数据");
        for(ImproveRegulations improveRegulations : improveRegulationsList){
            improveRegulations.setIssueId(issueId);
            improveRegulations.setOrgId(departId);
            improveRegulations.setUpdateUserId(updateUserId);
            improveRegulations.setTaskId(taskId);
        }
        return addOrUpdateList(improveRegulationsList);
    }

    @Override
    @Transactional
    public int addOrUpdate(ImproveRegulations improveRegulations) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=improveRegulations),"请传递问责处理数据");
        Assert.isTrue((StringUtil.isNotEmpty(improveRegulations.getOrgId())),"请传递问责处理责任部门id");
        Assert.isTrue((null!=improveRegulations.getIssueId()&&improveRegulations.getIssueId().longValue()>0L),"请传递问责处理问题id");
        Assert.isTrue((StringUtil.isNotEmpty(improveRegulations.getUpdateUserId())),"请传递问责处理修改用户id");
        Assert.isTrue((StringUtil.isNotEmpty(improveRegulations.getUuid())),"请传递问uuid");

        improveRegulations.setUpdateTime(new Date());

        String redisKey = "improveRegulations::" + (StringUtil.isNotEmpty(improveRegulations.getUuid())?improveRegulations.getUuid(): UUID.randomUUID().toString());
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                improveRegulations.setId(getDataIdByUUId(improveRegulations.getUuid(),improveRegulations.getIssueId()));

                if(null!=improveRegulations.getId()&&improveRegulations.getId()>0){
                    addOrUpdateCount = improveRegulationsMapper.updateById(improveRegulations);
                }else{
                    improveRegulations.setCreateUserId(improveRegulations.getUpdateUserId());
                    improveRegulations.setCreateTime(new Date());
                    improveRegulations.setDataType(1);
                    addOrUpdateCount = improveRegulationsMapper.insert(improveRegulations);
                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }

        return addOrUpdateCount;
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return improveRegulationsMapper.selectCount(map);
    }

    Integer getDataIdByUUId(String uuid,Long issueId){
        List<ImproveRegulations> improveRegulationsList = improveRegulationsMapper.selectList(Wrappers.<ImproveRegulations>lambdaQuery().eq(ImproveRegulations::getUuid, uuid).eq(ImproveRegulations::getIssueId,issueId).eq(ImproveRegulations::getDataType, 1));
        if(null!=improveRegulationsList&&improveRegulationsList.size()>0){
            if(null!=improveRegulationsList.get(0)&&null!=improveRegulationsList.get(0).getId()){
                return improveRegulationsList.get(0).getId();
            }
        }
        return null;
    }

}
