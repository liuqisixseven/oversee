package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.AccountabilityHandling;
import com.chd.modules.oversee.issue.entity.ImproveRegulations;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueAppendix;
import com.chd.modules.oversee.issue.mapper.AccountabilityHandlingMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.service.IAccountabilityHandlingService;
import com.chd.modules.oversee.issue.service.IOverseeIssueAppendixService;
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
 * @Description: accountability_handling
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class AccountabilityHandlingServiceImpl extends ServiceImpl<AccountabilityHandlingMapper, AccountabilityHandling> implements IAccountabilityHandlingService {

    @Autowired
    AccountabilityHandlingMapper accountabilityHandlingMapper;

    @Autowired
    private IOverseeIssueAppendixService overseeIssueAppendixService;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    public int addOrUpdateList(List<AccountabilityHandling> accountabilityHandlingList) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=accountabilityHandlingList&&accountabilityHandlingList.size()>0),"请传递问责处理集合数据");
        for(AccountabilityHandling accountabilityHandling : accountabilityHandlingList){
            addOrUpdateCount = addOrUpdate(accountabilityHandling);
        }

        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<AccountabilityHandling> accountabilityHandlingList, Long issueId,String departId,String updateUserId,String taskId) {
        Assert.isTrue((null!=accountabilityHandlingList&&accountabilityHandlingList.size()>0),"请传递问责处理集合数据");
        for(AccountabilityHandling accountabilityHandling : accountabilityHandlingList){
            accountabilityHandling.setIssueId(issueId);
            accountabilityHandling.setOrgId(departId);
            accountabilityHandling.setUpdateUserId(updateUserId);
            accountabilityHandling.setTaskId(taskId);
        }
        return addOrUpdateList(accountabilityHandlingList);
    }

    @Override
    @Transactional
    public int addOrUpdate(AccountabilityHandling accountabilityHandling) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=accountabilityHandling),"请传递问责处理数据");
        Assert.isTrue((StringUtil.isNotEmpty(accountabilityHandling.getUserId())),"请传递问责处理的用户id");
        Assert.isTrue((StringUtil.isNotEmpty(accountabilityHandling.getOrgId())),"请传递问责处理责任部门id");
        Assert.isTrue((null!=accountabilityHandling.getIssueId()&&accountabilityHandling.getIssueId().longValue()>0L),"请传递问责处理问题id");
        Assert.isTrue((StringUtil.isNotEmpty(accountabilityHandling.getUpdateUserId())),"请传递问责处理修改用户id");
        Assert.isTrue((StringUtil.isNotEmpty(accountabilityHandling.getUuid())),"请传递问uuid");

        accountabilityHandling.setUpdateTime(new Date());

        String redisKey = "accountabilityHandling::" + (StringUtil.isNotEmpty(accountabilityHandling.getUuid())?accountabilityHandling.getUuid(): UUID.randomUUID());
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                accountabilityHandling.setId(getDataIdByUUId(accountabilityHandling.getUuid(),accountabilityHandling.getIssueId()));

                if(null!=accountabilityHandling.getId()&&accountabilityHandling.getId()>0){
                    addOrUpdateCount = accountabilityHandlingMapper.updateById(accountabilityHandling);
                }else{
                    accountabilityHandling.setCreateUserId(accountabilityHandling.getUpdateUserId());
                    accountabilityHandling.setCreateTime(new Date());
                    accountabilityHandling.setDataType(1);
                    addOrUpdateCount = accountabilityHandlingMapper.insert(accountabilityHandling);
                }
                if(addOrUpdateCount>0){
//                  更新附件
                    if(null!=accountabilityHandling.getFiles()&&accountabilityHandling.getFiles().size()>0){
                        for(OverseeIssueAppendix overseeIssueAppendix : accountabilityHandling.getFiles()){
                            overseeIssueAppendix.setUpdateUserId(accountabilityHandling.getUpdateUserId());
                            overseeIssueAppendix.setIssueId(accountabilityHandling.getIssueId());
                        }
                    }
                    overseeIssueAppendixService.addOrUpdateList(accountabilityHandling.getFiles(),accountabilityHandling.getIssueId(),OverseeIssueAppendix.CANCEL_A_NUMBER_TYPE,accountabilityHandling.getUpdateUserId());
                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }
        return addOrUpdateCount;
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return accountabilityHandlingMapper.selectCount(map);
    }


    Integer getDataIdByUUId(String uuid,Long issueId){
        List<AccountabilityHandling> accountabilityHandlingList = accountabilityHandlingMapper.selectList(Wrappers.<AccountabilityHandling>lambdaQuery().eq(AccountabilityHandling::getUuid, uuid).eq(AccountabilityHandling::getIssueId,issueId).eq(AccountabilityHandling::getDataType, 1));
        if(null!=accountabilityHandlingList&&accountabilityHandlingList.size()>0){
            if(null!=accountabilityHandlingList.get(0)&&null!=accountabilityHandlingList.get(0).getId()){
                return accountabilityHandlingList.get(0).getId();
            }
        }
        return null;
    }

}
