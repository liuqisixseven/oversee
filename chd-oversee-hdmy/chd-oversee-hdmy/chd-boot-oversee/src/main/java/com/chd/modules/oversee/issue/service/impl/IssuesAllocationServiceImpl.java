package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.mapper.IssuesAllocationMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.service.IIssuesAllocationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class IssuesAllocationServiceImpl extends ServiceImpl<IssuesAllocationMapper, IssuesAllocation> implements IIssuesAllocationService {

    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    IssuesAllocationMapper issuesAllocationMapper;

    @Autowired
    IMyOrgService myOrgService;

    @Override
    @Transactional
    public int addOrUpdateOrgIds(String responsibleOrgIds, Long issueId, String updateUserId, String responsibleMainOrgIds, String responsibleCoordinationOrgIds) {
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        Assert.isTrue((StringUtil.isNotEmpty(updateUserId)),"请传递updateUserId");
        List<IssuesAllocation> issuesAllocationNews = new ArrayList<>();
        if(null!=responsibleOrgIds){
            String[] responsibleOrgIdArray = responsibleOrgIds.split(",");
            List<String> responsibleMainOrgIdList = StringUtil.isNotEmpty(responsibleMainOrgIds)?Arrays.asList(responsibleMainOrgIds.split(",")):new ArrayList<>();
            List<String> responsibleCoordinationOrgIdList = StringUtil.isNotEmpty(responsibleCoordinationOrgIds)?Arrays.asList(responsibleCoordinationOrgIds.split(",")):new ArrayList<>();

            if(null!=responsibleOrgIdArray&&responsibleOrgIdArray.length>0){
                for(String responsibleOrgId: responsibleOrgIdArray){
                    if(StringUtil.isNotEmpty(responsibleOrgId)){
                        responsibleOrgId = responsibleOrgId.trim();
                        MyOrg myOrgById = myOrgService.getById(responsibleOrgId);
                        if(null!=myOrgById){
                            IssuesAllocation issuesAllocation = new IssuesAllocation();
                            issuesAllocation.setIssueId(issueId);
                            issuesAllocation.setDataType(1);
                            issuesAllocation.setManageUserId(myOrgById.getManagerId());
                            issuesAllocation.setSupervisorUserId(myOrgById.getUpperSupervisorId());
                            issuesAllocation.setUpdateUserId(updateUserId);
                            issuesAllocation.setResponsibleDepartmentOrgId(responsibleOrgId);
                            if(responsibleCoordinationOrgIdList.contains(responsibleOrgId)){
                                issuesAllocation.setDepartmentType(1);
                            }
                            if(responsibleMainOrgIdList.contains(responsibleOrgId)){
                                issuesAllocation.setDepartmentType(2);
                            }
                            issuesAllocationNews.add(issuesAllocation);
                        }
                    }
                }
            }
        }
        return addOrUpdateList(issuesAllocationNews,issueId);
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<IssuesAllocation> issuesAllocationNews, Long issueId) {
        int addOrUpdateOrDeleteCount = 0;
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        if(null==issuesAllocationNews) issuesAllocationNews = new ArrayList<>();
        issuesAllocationNews = issuesAllocationNews.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId())).collect(Collectors.toList());
        List<IssuesAllocation> issuesAllocationOlds = issuesAllocationMapper.selectList(Wrappers.<IssuesAllocation>lambdaQuery().eq(IssuesAllocation::getIssueId, issueId).eq(IssuesAllocation::getDataType, 1));
        if(null==issuesAllocationOlds)issuesAllocationOlds= new ArrayList<>();

        List<IssuesAllocation> finalIssuesAllocationOlds = issuesAllocationOlds;
        issuesAllocationNews.stream().forEach((issuesAllocation)-> {
            finalIssuesAllocationOlds.stream().forEach((issuesAllocationOld)->{
                if(null!=issuesAllocationOld&&null!=issuesAllocation){
                    if (StringUtil.isNotEmpty(issuesAllocationOld.getResponsibleDepartmentOrgId()) && issuesAllocationOld.getResponsibleDepartmentOrgId().equals(issuesAllocation.getResponsibleDepartmentOrgId())) {
                        issuesAllocation.setId(issuesAllocationOld.getId());
                    }
                }
            });
        });

        List<IssuesAllocation> finalIssuesAllocationNews = issuesAllocationNews;
        List<IssuesAllocation> deleteIssuesAllocationList = finalIssuesAllocationOlds.stream().filter((issuesAllocationOld) -> {
            return !finalIssuesAllocationNews.stream().anyMatch((issuesAllocationNew) -> null != issuesAllocationNew && issuesAllocationNew.getResponsibleDepartmentOrgId().equals(issuesAllocationOld.getResponsibleDepartmentOrgId()));
        }).collect(Collectors.toList());

        if(null!=deleteIssuesAllocationList&&deleteIssuesAllocationList.size()>0){
            for(IssuesAllocation deleteIssuesAllocation : deleteIssuesAllocationList){
                addOrUpdateOrDeleteCount += deleteIssuesAllocation(deleteIssuesAllocation);
            }
        }

        if(null!=issuesAllocationNews&&issuesAllocationNews.size()>0){
            for(IssuesAllocation issuesAllocation : issuesAllocationNews){
                addOrUpdateOrDeleteCount += addOrUpdate(issuesAllocation);
            }
        }

        return addOrUpdateOrDeleteCount;
    }

    @Override
    @Transactional
    public int addOrUpdate(IssuesAllocation issuesAllocation) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=issuesAllocation),"请传递问题分配数据");
        Assert.isTrue((null!=issuesAllocation.getIssueId()&&issuesAllocation.getIssueId().intValue()>0),"请传递问题分配的问题id");
        Assert.isTrue((StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId())),"请传递问题分配的责任部门责任单位id");
        Assert.isTrue((StringUtil.isNotEmpty(issuesAllocation.getUpdateUserId())),"请传递问题分配的updateUserId");

        issuesAllocation.setUpdateTime(new Date());

        if(null!=issuesAllocation.getId()&&issuesAllocation.getId().intValue()>0){
            addOrUpdateCount = issuesAllocationMapper.updateById(issuesAllocation);
        }else{
            issuesAllocation.setCreateTime(new Date());
            issuesAllocation.setCreateUserId(issuesAllocation.getUpdateUserId());
            Assert.isTrue((StringUtil.isNotEmpty(issuesAllocation.getCreateUserId())),"请传递问题分配的createUserId");
            addOrUpdateCount = issuesAllocationMapper.insert(issuesAllocation);
        }

        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int deleteIssuesAllocation(IssuesAllocation issuesAllocation) {
        Assert.isTrue((null!=issuesAllocation),"请传递问题分配数据");
        Assert.isTrue((null!=issuesAllocation.getId()&&issuesAllocation.getId().intValue()>0),"请传递问题分配数据id");
        issuesAllocation.setDataType(-1);
        issuesAllocation.setUpdateTime(new Date());
        int i = issuesAllocationMapper.updateById(issuesAllocation);
        return i;
    }

    @Override
    public String getIssuesAllocationDepartmentNames(Long issuesId, Integer departmentType) {

        if(null!=issuesId && null!= departmentType){
            List<IssuesAllocation> issuesAllocationMainList = issuesAllocationMapper.selectList(Wrappers.<IssuesAllocation>lambdaQuery().eq(IssuesAllocation::getIssueId, issuesId).eq(IssuesAllocation::getDepartmentType, departmentType).eq(IssuesAllocation::getDataType, 1));
            if(CollectionUtils.isNotEmpty(issuesAllocationMainList)){
                List<String> orgIdList = issuesAllocationMainList.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId())).map((issuesAllocation) -> issuesAllocation.getResponsibleDepartmentOrgId()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(orgIdList)){
                    List<MyOrg> myOrgList = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().in(MyOrg::getOrgId, orgIdList));
                    if(CollectionUtils.isNotEmpty(myOrgList)){
                        String myOrgNames = myOrgList.stream().filter((myOrg) -> null != myOrg && StringUtil.isNotEmpty(myOrg.getOrgName())).map((myOrg) -> StringUtil.isNotEmpty(myOrg.getOrgShortName()) ? myOrg.getOrgShortName() : myOrg.getOrgName()).collect(Collectors.joining(","));
                        return myOrgNames;
                    }
                }
            }
        }
        return null;
    }


}
