package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.RecoverFunds;
import com.chd.modules.oversee.issue.mapper.RecoverFundsMapper;
import com.chd.modules.oversee.issue.service.IRecoverFundsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class RecoverFundsServiceImpl extends ServiceImpl<RecoverFundsMapper, RecoverFunds> implements IRecoverFundsService {
    @Autowired
    RecoverFundsMapper recoverFundsMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    public int addOrUpdateList(List<RecoverFunds> recoverFundsList) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=recoverFundsList&&recoverFundsList.size()>0),"请传递问责处理集合数据");
        for(RecoverFunds recoverFunds : recoverFundsList){
            addOrUpdateCount = addOrUpdate(recoverFunds);
        }

        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<RecoverFunds> recoverFundsList, Long issueId,String departId,String updateUserId,String taskId) {
        Assert.isTrue((null!=recoverFundsList&&recoverFundsList.size()>0),"请传递问责处理集合数据");
        for(RecoverFunds recoverFunds : recoverFundsList){
            recoverFunds.setIssueId(issueId);
            recoverFunds.setOrgId(departId);
            recoverFunds.setUpdateUserId(updateUserId);
            recoverFunds.setTaskId(taskId);
        }
        return addOrUpdateList(recoverFundsList);
    }

    @Override
    @Transactional
    public int addOrUpdate(RecoverFunds recoverFunds) {
        int addOrUpdateCount = 0;

        Assert.isTrue((null!=recoverFunds),"请传递问责处理数据");
        Assert.isTrue((StringUtil.isNotEmpty(recoverFunds.getOrgId())),"请传递问责处理责任部门id");
        Assert.isTrue((null!=recoverFunds.getIssueId()&&recoverFunds.getIssueId().longValue()>0L),"请传递问责处理问题id");
        Assert.isTrue((StringUtil.isNotEmpty(recoverFunds.getUpdateUserId())),"请传递问责处理修改用户id");
        Assert.isTrue((StringUtil.isNotEmpty(recoverFunds.getUuid())),"请传递问uuid");

        recoverFunds.setUpdateTime(new Date());

        if(StringUtil.isNotEmpty(recoverFunds.getRecoverDamages())){
            recoverFunds.setRecoverDamagesNumber((new BigDecimal(recoverFunds.getRecoverDamages()).multiply(new BigDecimal("10000"))));
        }

        if(StringUtil.isNotEmpty(recoverFunds.getRecoveryIllegalDisciplinaryFunds())){
            recoverFunds.setRecoveryIllegalDisciplinaryFundsNumber((new BigDecimal(recoverFunds.getRecoveryIllegalDisciplinaryFunds()).multiply(new BigDecimal("10000"))));
        }

        String redisKey = "recoverFunds::" + (StringUtil.isNotEmpty(recoverFunds.getUuid())?recoverFunds.getUuid(): UUID.randomUUID().toString());
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                recoverFunds.setId(getDataIdByUUId(recoverFunds.getUuid(),recoverFunds.getIssueId()));
                if(null!=recoverFunds.getId()&&recoverFunds.getId()>0){
                    addOrUpdateCount = recoverFundsMapper.updateById(recoverFunds);
                }else{
                    recoverFunds.setCreateUserId(recoverFunds.getUpdateUserId());
                    recoverFunds.setCreateTime(new Date());
                    recoverFunds.setDataType(1);
                    addOrUpdateCount = recoverFundsMapper.insert(recoverFunds);
                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }

        return addOrUpdateCount;
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return recoverFundsMapper.selectCount(map);
    }

    @Override
    public BigDecimal selectRecoveryIllegalDisciplinaryFundsNumberSum(Map<String, Object> map) {
        return recoverFundsMapper.selectRecoveryIllegalDisciplinaryFundsNumberSum(map);
    }

    @Override
    public BigDecimal selectRecoverDamagesNumberSum(Map<String, Object> map) {
        return recoverFundsMapper.selectRecoverDamagesNumberSum(map);
    }


    Integer getDataIdByUUId(String uuid,Long issueId){
        List<RecoverFunds> recoverFunds = recoverFundsMapper.selectList(Wrappers.<RecoverFunds>lambdaQuery().eq(RecoverFunds::getUuid, uuid).eq(RecoverFunds::getIssueId,issueId).eq(RecoverFunds::getDataType, 1));
        if(null!=recoverFunds&&recoverFunds.size()>0){
            if(null!=recoverFunds.get(0)&&null!=recoverFunds.get(0).getId()){
                return recoverFunds.get(0).getId();
            }
        }
        return null;
    }

}
