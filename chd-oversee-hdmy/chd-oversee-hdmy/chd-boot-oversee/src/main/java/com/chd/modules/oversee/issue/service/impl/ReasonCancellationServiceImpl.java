package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.api.CommonAPI;
import com.chd.common.system.vo.SysUserCacheInfo;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.mapper.MyOrgMapper;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.mapper.ReasonCancellationMapper;
import com.chd.modules.oversee.issue.service.*;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class ReasonCancellationServiceImpl extends ServiceImpl<ReasonCancellationMapper, ReasonCancellation> implements IReasonCancellationService {
    @Autowired
    ReasonCancellationMapper reasonCancellationMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    IAccountabilityHandlingService accountabilityHandlingService;

    @Autowired
    private IImproveRegulationsService improveRegulationsService;

    @Autowired
    private IRecoverFundsService recoverFundsService;

    @Autowired
    private IRectifyViolationsService rectifyViolationsService;

    @Autowired
    private CommonAPI commonAPI;

    @Autowired
    private IMorphologicalCategoriesService morphologicalCategoriesService;

    @Autowired
    private IMorphologicalSubclassService morphologicalSubclassService;

    @Autowired
    private MyOrgMapper myOrgMapper;



    @Override
    @Transactional
    public int addOrUpdateList(List<ReasonCancellation> reasonCancellationList) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=reasonCancellationList&&reasonCancellationList.size()>0),"请传递问责处理集合数据");
        for(ReasonCancellation reasonCancellation : reasonCancellationList){
            addOrUpdateCount = addOrUpdate(reasonCancellation);
        }

        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<ReasonCancellation> reasonCancellationList, Long issueId,String departId,String updateUserId,String taskId) {
        Assert.isTrue((null!=reasonCancellationList&&reasonCancellationList.size()>0),"请传递问责处理集合数据");
        for(ReasonCancellation reasonCancellation : reasonCancellationList){
            reasonCancellation.setIssueId(issueId);
            reasonCancellation.setOrgId(departId);
            reasonCancellation.setUpdateUserId(updateUserId);
            reasonCancellation.setTaskId(taskId);
        }
        return addOrUpdateList(reasonCancellationList);
    }

    @Override
    @Transactional
    public int addOrUpdate(ReasonCancellation reasonCancellation) {
        int addOrUpdateCount = 0;

        Assert.isTrue((null!=reasonCancellation),"请传递销号理由数据");
        Assert.isTrue((StringUtil.isNotEmpty(reasonCancellation.getOrgId())),"请传递销号理由责任部门id");
        Assert.isTrue((null!=reasonCancellation.getIssueId()&&reasonCancellation.getIssueId().longValue()>0L),"请传递销号理由问题id");
        Assert.isTrue((StringUtil.isNotEmpty(reasonCancellation.getUpdateUserId())),"请传递销号理由修改用户id");

        reasonCancellation.setUpdateTime(new Date());

        String redisKey = BaseConstant.REASON_CANCELLATION_EDIT_TASKID_REDIS_KEY + reasonCancellation.getTaskId();
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                reasonCancellation.setId(getDataIdByTaskId(reasonCancellation.getTaskId()));
                if(null!=reasonCancellation.getId()&&reasonCancellation.getId()>0){
                    addOrUpdateCount = reasonCancellationMapper.updateById(reasonCancellation);
                }else{
                    reasonCancellation.setCreateUserId(reasonCancellation.getUpdateUserId());
                    reasonCancellation.setCreateTime(new Date());
                    reasonCancellation.setDataType(1);
                    addOrUpdateCount = reasonCancellationMapper.insert(reasonCancellation);
                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }

        return addOrUpdateCount;
    }

    @Override
    public int selectCount(Map<String, Object> map) {
        return reasonCancellationMapper.selectCount(map);
    }

    @Override
    public Map<String, Object> getCancelANumber(Map<String,Object> selectMap) {
        Map<String, Object> map = Maps.newHashMap();

        LambdaQueryWrapper<ReasonCancellation> reasonCancellationWrappers = Wrappers.<ReasonCancellation>lambdaQuery().eq(ReasonCancellation::getDataType, 1);
        LambdaQueryWrapper<AccountabilityHandling> accountabilityHandlingWrappers = Wrappers.<AccountabilityHandling>lambdaQuery().eq(AccountabilityHandling::getDataType, 1);
        LambdaQueryWrapper<RectifyViolations> rectifyViolationsWrappers = Wrappers.<RectifyViolations>lambdaQuery().eq(RectifyViolations::getDataType, 1);
        LambdaQueryWrapper<RecoverFunds> recoverFundsWrappers = Wrappers.<RecoverFunds>lambdaQuery().eq(RecoverFunds::getDataType, 1);
        LambdaQueryWrapper<ImproveRegulations> improveRegulationsWrappers = Wrappers.<ImproveRegulations>lambdaQuery().eq(ImproveRegulations::getDataType, 1);
        if(null!=selectMap){
            Long overseeIssueId = (Long) selectMap.get("overseeIssueId");
            if(null!=overseeIssueId&&overseeIssueId.longValue()>0l){
                reasonCancellationWrappers.eq(ReasonCancellation::getIssueId, overseeIssueId);
                accountabilityHandlingWrappers.eq(AccountabilityHandling::getIssueId, overseeIssueId);
                rectifyViolationsWrappers.eq(RectifyViolations::getIssueId, overseeIssueId);
                recoverFundsWrappers.eq(RecoverFunds::getIssueId, overseeIssueId);
                improveRegulationsWrappers.eq(ImproveRegulations::getIssueId, overseeIssueId);
            }
            List<String> orgIdList = (List<String>) selectMap.get("orgIdList");
            if(CollectionUtils.isNotEmpty(orgIdList)){
                reasonCancellationWrappers.in(ReasonCancellation::getOrgId,orgIdList);
                accountabilityHandlingWrappers.in(AccountabilityHandling::getOrgId,orgIdList);
                rectifyViolationsWrappers.in(RectifyViolations::getOrgId,orgIdList);
                recoverFundsWrappers.in(RecoverFunds::getOrgId,orgIdList);
                improveRegulationsWrappers.in(ImproveRegulations::getOrgId,orgIdList);
            }

        }

        List<AccountabilityHandling> accountabilityHandlingList = accountabilityHandlingService.list(accountabilityHandlingWrappers);
        if(null!=accountabilityHandlingList&&accountabilityHandlingList.size()>0){
            for(AccountabilityHandling accountabilityHandling : accountabilityHandlingList){
                if(null!=accountabilityHandling){
                    if(StringUtil.isNotEmpty(accountabilityHandling.getUserId())){
                        SysUserCacheInfo sysUserById = commonAPI.getSysUserById(accountabilityHandling.getUserId());
                        if(null!=sysUserById){
                            accountabilityHandling.setUserName(sysUserById.getSysUserName());
                            accountabilityHandling.setJobTitle(sysUserById.getPost());
                        }
                    }
                    if(null!=accountabilityHandling.getMorphologicalCategoriesId()){
                        MorphologicalCategories morphologicalCategoriesById = morphologicalCategoriesService.getById(accountabilityHandling.getMorphologicalCategoriesId());
                        if(null!=morphologicalCategoriesById){
                            accountabilityHandling.setMorphologicalCategoriesName(morphologicalCategoriesById.getName());
                        }
                    }

                    if(null!=accountabilityHandling.getMorphologicalSubclassId()){
                        MorphologicalSubclass morphologicalSubclassById = morphologicalSubclassService.getById(accountabilityHandling.getMorphologicalSubclassId());
                        if(null!=morphologicalSubclassById){
                            accountabilityHandling.setMorphologicalSubclassName(morphologicalSubclassById.getName());
                        }
                    }
                }
            }
        }

        List<RectifyViolations> rectifyViolationsList = rectifyViolationsService.list(rectifyViolationsWrappers);
        if(null!=rectifyViolationsList&&rectifyViolationsList.size()>0){
            for(RectifyViolations rectifyViolations : rectifyViolationsList){
                if(null!=rectifyViolations){
                    if(StringUtil.isNotEmpty(rectifyViolations.getUserId())){
                        SysUserCacheInfo sysUserById = commonAPI.getSysUserById(rectifyViolations.getUserId());
                        if(null!=sysUserById){
                            rectifyViolations.setUserName(sysUserById.getSysUserName());
                            if(StringUtil.isEmpty(rectifyViolations.getOriginalPost())){
                                rectifyViolations.setOriginalPost(sysUserById.getPost());
                            }
                        }
                    }
                }
            }
        }

        List<RecoverFunds> recoverFundsList = recoverFundsService.list(recoverFundsWrappers);
        BigDecimal recoveryIllegalDisciplinaryFundsAllBigDecimal = new BigDecimal("0");
        BigDecimal recoverDamagesAllBigDecimal = new BigDecimal("0");
        if(null!=recoverFundsList&&recoverFundsList.size()>0){
            for(RecoverFunds recoverFunds : recoverFundsList){
                if(null!=recoverFunds){
                    if(null!=recoverFunds.getRecoveryIllegalDisciplinaryFundsNumber()){
                        recoveryIllegalDisciplinaryFundsAllBigDecimal = recoveryIllegalDisciplinaryFundsAllBigDecimal.add(recoverFunds.getRecoveryIllegalDisciplinaryFundsNumber());
                    }else  if(StringUtil.isNotEmpty(recoverFunds.getRecoveryIllegalDisciplinaryFunds())){
                        recoveryIllegalDisciplinaryFundsAllBigDecimal = recoveryIllegalDisciplinaryFundsAllBigDecimal.add(new BigDecimal(recoverFunds.getRecoveryIllegalDisciplinaryFunds()));
                    }

                    if(null!=recoverFunds.getRecoverDamagesNumber()){
                        recoverDamagesAllBigDecimal = recoverDamagesAllBigDecimal.add(recoverFunds.getRecoverDamagesNumber());
                    }else  if(StringUtil.isNotEmpty(recoverFunds.getRecoverDamages())){
                        recoverDamagesAllBigDecimal = recoverDamagesAllBigDecimal.add(new BigDecimal(recoverFunds.getRecoverDamages()).multiply(new BigDecimal("10000")));
                    }
                }
            }
        }
        List<ImproveRegulations> improveRegulationsList = improveRegulationsService.list(improveRegulationsWrappers);
        if(null!=improveRegulationsList&&improveRegulationsList.size()>0){
            for(ImproveRegulations improveRegulations : improveRegulationsList){
                if(null!=improveRegulations){

                }
            }
        }


        List<ReasonCancellation> reasonCancellationList = reasonCancellationMapper.selectList(reasonCancellationWrappers);
        if(null!=reasonCancellationList&&reasonCancellationList.size()>0){
            for(ReasonCancellation reasonCancellation : reasonCancellationList){
                if(null!=reasonCancellation){

                    if(StringUtil.isNotEmpty(reasonCancellation.getOrgId())){
                        MyOrg myOrg = myOrgMapper.selectById(reasonCancellation.getOrgId());
                        if(null!=myOrg){
                            reasonCancellation.setDepartName(StringUtil.isNotEmpty(myOrg.getOrgShortName())?myOrg.getOrgShortName():(StringUtil.isNotEmpty(myOrg.getOrgName())?myOrg.getOrgName():""));
                        }
                    }

                    if(StringUtil.isNotEmpty(reasonCancellation.getTaskId())){

                        BigDecimal recoveryIllegalDisciplinaryFundsSumBigDecimal = new BigDecimal("0");
                        BigDecimal recoverDamagesSumBigDecimal = new BigDecimal("0");
                        List<RecoverFunds> recoverFundss = new ArrayList<>();
                        if(null!=recoverFundsList&&recoverFundsList.size()>0){
                            for(RecoverFunds recoverFunds : recoverFundsList){
                                if(null!=recoverFunds&&reasonCancellation.getTaskId().equals(recoverFunds.getTaskId())){
                                    recoverFundss.add(recoverFunds);
                                    if(null!=recoverFunds.getRecoveryIllegalDisciplinaryFundsNumber()){
                                        recoveryIllegalDisciplinaryFundsSumBigDecimal = recoveryIllegalDisciplinaryFundsSumBigDecimal.add(recoverFunds.getRecoveryIllegalDisciplinaryFundsNumber());
                                    }else  if(StringUtil.isNotEmpty(recoverFunds.getRecoveryIllegalDisciplinaryFunds())){
                                        recoveryIllegalDisciplinaryFundsSumBigDecimal = recoveryIllegalDisciplinaryFundsSumBigDecimal.add(new BigDecimal(recoverFunds.getRecoveryIllegalDisciplinaryFunds()));
                                    }

                                    if(null!=recoverFunds.getRecoverDamagesNumber()){
                                        recoverDamagesSumBigDecimal = recoverDamagesSumBigDecimal.add(recoverFunds.getRecoverDamagesNumber());
                                    }else  if(StringUtil.isNotEmpty(recoverFunds.getRecoverDamages())){
                                        recoverDamagesSumBigDecimal = recoverDamagesSumBigDecimal.add(new BigDecimal(recoverFunds.getRecoverDamages()).multiply(new BigDecimal("10000")));
                                    }
                                }
                            }
                        }
                        reasonCancellation.setRecoverFundsList(recoverFundss);
                        reasonCancellation.setRecoveryIllegalDisciplinaryFundsSumBigDecimal(recoveryIllegalDisciplinaryFundsSumBigDecimal);
                        reasonCancellation.setRecoverDamagesSumBigDecimal(recoverDamagesSumBigDecimal);

                        List<ImproveRegulations> improveRegulationss = new ArrayList<>();
                        if(null!=improveRegulationsList&&improveRegulationsList.size()>0){
                            for(ImproveRegulations improveRegulations : improveRegulationsList){
                                if(null!=improveRegulations&&reasonCancellation.getTaskId().equals(improveRegulations.getTaskId())){
                                    improveRegulationss.add(improveRegulations);
                                }
                            }
                        }
                        reasonCancellation.setImproveRegulationsList(improveRegulationss);

                        List<RectifyViolations> rectifyViolationss = new ArrayList<>();
                        if(null!=rectifyViolationsList&&rectifyViolationsList.size()>0){
                            for(RectifyViolations rectifyViolations : rectifyViolationsList){
                                if(null!=rectifyViolations&&reasonCancellation.getTaskId().equals(rectifyViolations.getTaskId())){
                                    rectifyViolationss.add(rectifyViolations);
                                }
                            }
                        }
                        reasonCancellation.setRectifyViolationsList(rectifyViolationss);


                        List<AccountabilityHandling> accountabilityHandlings = new ArrayList<>();
                        if(null!=accountabilityHandlingList&&accountabilityHandlingList.size()>0){
                            for(AccountabilityHandling accountabilityHandling : accountabilityHandlingList){
                                if(null!=accountabilityHandling&&reasonCancellation.getTaskId().equals(accountabilityHandling.getTaskId())){
                                    accountabilityHandlings.add(accountabilityHandling);
                                }
                            }
                        }
                        reasonCancellation.setAccountabilityHandlingList(accountabilityHandlings);

                    }
                }
            }
        }


        map.put("accountabilityHandlingList",accountabilityHandlingList);
        map.put("rectifyViolationsList",rectifyViolationsList);
        map.put("recoverFundsList",recoverFundsList);
        map.put("improveRegulationsList",improveRegulationsList);
        map.put("reasonCancellationList",reasonCancellationList);
        map.put("recoveryIllegalDisciplinaryFundsAllBigDecimal",recoveryIllegalDisciplinaryFundsAllBigDecimal);
        map.put("recoverDamagesAllBigDecimal",recoverDamagesAllBigDecimal);

        return map;
    }


    Integer getDataIdByTaskId(String taskId){
        List<ReasonCancellation> reasonCancellation = reasonCancellationMapper.selectList(Wrappers.<ReasonCancellation>lambdaQuery().eq(ReasonCancellation::getTaskId, taskId).eq(ReasonCancellation::getDataType, 1));
        if(null!=reasonCancellation&&reasonCancellation.size()>0){
            if(null!=reasonCancellation.get(0)&&null!=reasonCancellation.get(0).getId()){
                return reasonCancellation.get(0).getId();
            }
        }
        return null;
    }

}
