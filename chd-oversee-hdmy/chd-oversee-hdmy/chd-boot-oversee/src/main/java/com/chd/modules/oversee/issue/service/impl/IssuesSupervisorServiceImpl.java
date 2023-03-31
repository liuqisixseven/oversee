package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.util.StringUtil;

import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.entity.IssuesSupervisor;
import com.chd.modules.oversee.issue.entity.OverseeIssueAppendix;
import com.chd.modules.oversee.issue.mapper.IssuesAllocationMapper;
import com.chd.modules.oversee.issue.mapper.IssuesSupervisorMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.service.IIssuesAllocationService;
import com.chd.modules.oversee.issue.service.IIssuesSupervisorService;
import com.chd.modules.oversee.issue.service.IOverseeIssueAppendixService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class IssuesSupervisorServiceImpl extends ServiceImpl<IssuesSupervisorMapper, IssuesSupervisor> implements IIssuesSupervisorService {

    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    IssuesSupervisorMapper issuesSupervisorMapper;

    @Autowired
    private IOverseeIssueAppendixService overseeIssueAppendixService;

    @Autowired
    IMyOrgService myOrgService;

    @Autowired
    IssuesAllocationMapper issuesAllocationMapper;

    @Override
    public List<IssuesSupervisor> selectIssuesSupervisorList(Map<String, Object> map) {
        return issuesSupervisorMapper.selectIssuesSupervisorList(map);
    }

    @Override
    public int addOrUpdateOrgIds(String responsibleOrgIds, Long issueId, String updateUserId,List<IssuesSupervisor> issuesSupervisorList) {
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        Assert.isTrue((StringUtil.isNotEmpty(updateUserId)),"请传递updateUserId");
        List<IssuesSupervisor> issuesSupervisorNews = new ArrayList<>();
        if(null!=responsibleOrgIds){
            String[] responsibleOrgIdArray = responsibleOrgIds.split(",");
            if(null!=responsibleOrgIdArray&&responsibleOrgIdArray.length>0){
                for(String responsibleOrgId: responsibleOrgIdArray){
                    if(StringUtil.isNotEmpty(responsibleOrgId)){
                        MyOrg myOrgById = myOrgService.getById(responsibleOrgId);
                        if(null!=myOrgById){
                            IssuesSupervisor issuesSupervisor = new IssuesSupervisor();
                            issuesSupervisor.setIssueId(issueId);
                            issuesSupervisor.setDataType(1);
                            issuesSupervisor.setManageUserId(myOrgById.getManagerId());
                            issuesSupervisor.setSupervisorUserId(myOrgById.getUpperSupervisorId());
                            issuesSupervisor.setUpdateUserId(updateUserId);
                            issuesSupervisor.setSupervisorOrgId(responsibleOrgId);
                            if(null!=issuesSupervisorList&&issuesSupervisorList.size()>0){
                                for(IssuesSupervisor issuesSupervisorBindData : issuesSupervisorList){
                                    if(null!=issuesSupervisorBindData){
                                        if(responsibleOrgId.equals(issuesSupervisorBindData.getSupervisorOrgId())&&StringUtil.isNotEmpty(issuesSupervisorBindData.getBindResponsibleOrgIds())){
                                            issuesSupervisor.setBindResponsibleOrgIds(issuesSupervisorBindData.getBindResponsibleOrgIds());
                                        }
                                    }
                                }
                            }
                            issuesSupervisorNews.add(issuesSupervisor);
                        }
                    }
                }
            }
        }
        return addOrUpdateList(issuesSupervisorNews,issueId);
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<IssuesSupervisor> issuesSupervisorNews, Long issueId) {
        int addOrUpdateOrDeleteCount = 0;
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        if(null==issuesSupervisorNews) issuesSupervisorNews = new ArrayList<>();
        issuesSupervisorNews = issuesSupervisorNews.stream().filter((issuesSupervisor) -> null != issuesSupervisor && StringUtil.isNotEmpty(issuesSupervisor.getSupervisorOrgId())).collect(Collectors.toList());
        List<IssuesSupervisor> issuesAllocationOlds = issuesSupervisorMapper.selectList(Wrappers.<IssuesSupervisor>lambdaQuery().eq(IssuesSupervisor::getIssueId, issueId).eq(IssuesSupervisor::getDataType, 1));
        if(null==issuesAllocationOlds)issuesAllocationOlds= new ArrayList<>();

        List<IssuesSupervisor> finalIssuesAllocationOlds = issuesAllocationOlds;
        issuesSupervisorNews.stream().forEach((issuesSupervisor)-> {
            finalIssuesAllocationOlds.stream().forEach((issuesSupervisorOld)->{
                if(null!=issuesSupervisorOld&&null!=issuesSupervisor){
                    if (StringUtil.isNotEmpty(issuesSupervisorOld.getSupervisorOrgId()) && issuesSupervisorOld.getSupervisorOrgId().equals(issuesSupervisor.getSupervisorOrgId())) {
                        issuesSupervisor.setId(issuesSupervisorOld.getId());
                    }
                }
            });
        });

        List<IssuesSupervisor> finalIssuesAllocationNews = issuesSupervisorNews;
        List<IssuesSupervisor> deleteIssuesAllocationList = finalIssuesAllocationOlds.stream().filter((issuesSupervisorOld) -> {
            return !finalIssuesAllocationNews.stream().anyMatch((issuesSupervisorNew) -> null != issuesSupervisorNew && issuesSupervisorNew.getSupervisorOrgId().equals(issuesSupervisorOld.getSupervisorOrgId()));
        }).collect(Collectors.toList());

        if(null!=deleteIssuesAllocationList&&deleteIssuesAllocationList.size()>0){
            for(IssuesSupervisor deleteIssuesSupervisor : deleteIssuesAllocationList){
                addOrUpdateOrDeleteCount += deleteIssuesAllocation(deleteIssuesSupervisor);
            }
        }

        if(null!=issuesSupervisorNews&&issuesSupervisorNews.size()>0){
            for(IssuesSupervisor issuesSupervisor : issuesSupervisorNews){
                addOrUpdateOrDeleteCount += addOrUpdate(issuesSupervisor);
            }
        }

        return addOrUpdateOrDeleteCount;
    }

    @Override
    @Transactional
    public int addOrUpdate(IssuesSupervisor issuesSupervisor) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=issuesSupervisor),"请传递问题督办数据");
        Assert.isTrue((null!=issuesSupervisor.getIssueId()&&issuesSupervisor.getIssueId().intValue()>0),"请传递问题督办的问题id");
        Assert.isTrue((StringUtil.isNotEmpty(issuesSupervisor.getSupervisorOrgId())),"请传递问题督办的责任部门责任单位id");
        Assert.isTrue((StringUtil.isNotEmpty(issuesSupervisor.getUpdateUserId())),"请传递问题督办的updateUserId");

        if(null==issuesSupervisor.getIssuesProcessType()){
            issuesSupervisor.setIssuesProcessType(1);
        }

        if(null==issuesSupervisor.getShowType()){
            issuesSupervisor.setShowType(1);
        }

        if(issuesSupervisor.getIssuesProcessType().intValue() == 2){
            Assert.isTrue((StringUtil.isNotEmpty(issuesSupervisor.getReason())),"请传递问题督办理由");
        }

        issuesSupervisor.setUpdateTime(new Date());

        if(null!=issuesSupervisor.getId()&&issuesSupervisor.getId().intValue()>0){
            addOrUpdateCount = issuesSupervisorMapper.updateById(issuesSupervisor);
        }else{
            issuesSupervisor.setCreateTime(new Date());
            issuesSupervisor.setCreateUserId(issuesSupervisor.getUpdateUserId());
            Assert.isTrue((StringUtil.isNotEmpty(issuesSupervisor.getCreateUserId())),"请传递问题督办的createUserId");
            addOrUpdateCount = issuesSupervisorMapper.insert(issuesSupervisor);
        }
        if(addOrUpdateCount>0){
//            更新附件
            if(null!=issuesSupervisor.getAppendixList()&&issuesSupervisor.getAppendixList().size()>0){
                for(OverseeIssueAppendix overseeIssueAppendix : issuesSupervisor.getAppendixList()){
                    overseeIssueAppendix.setUpdateUserId(issuesSupervisor.getUpdateUserId());
                    overseeIssueAppendix.setIssueId(issuesSupervisor.getIssueId());
                }
            }
            overseeIssueAppendixService.addOrUpdateList(issuesSupervisor.getAppendixList(),issuesSupervisor.getIssueId(),OverseeIssueAppendix.PROBLEM_SUPERVISION_2_TYPE,issuesSupervisor.getUpdateUserId());
        }
        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int deleteIssuesAllocation(IssuesSupervisor issuesSupervisor) {
        Assert.isTrue((null!=issuesSupervisor),"请传递问题督办数据");
        Assert.isTrue((null!=issuesSupervisor.getId()&&issuesSupervisor.getId().intValue()>0),"请传递问题督办数据id");
        issuesSupervisor.setDataType(-1);
        issuesSupervisor.setUpdateTime(new Date());
        int i = issuesSupervisorMapper.updateById(issuesSupervisor);
        return i;
    }

    @Override
    public String getIssuesAllocationDepartmentNames(Long issuesId, Integer departmentType) {

        if(null!=issuesId && null!= departmentType){
            List<IssuesAllocation> issuesAllocationList = issuesAllocationMapper.selectList(Wrappers.<IssuesAllocation>lambdaQuery().eq(IssuesAllocation::getIssueId, issuesId).eq(IssuesAllocation::getDepartmentType, departmentType).eq(IssuesAllocation::getDataType, 1));
            if(CollectionUtils.isNotEmpty(issuesAllocationList)){
                List<String> issuesAllocationOrgIdList = issuesAllocationList.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId())).map((issuesAllocation) -> issuesAllocation.getResponsibleDepartmentOrgId()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(issuesAllocationOrgIdList)){



                    LambdaQueryWrapper<IssuesSupervisor> issuesSupervisorLambdaQueryWrapper = Wrappers.<IssuesSupervisor>lambdaQuery().eq(IssuesSupervisor::getIssueId,issuesId).eq(IssuesSupervisor::getDataType,1);
                    List<IssuesSupervisor> issuesSupervisorList = issuesSupervisorMapper.selectList(issuesSupervisorLambdaQueryWrapper);
                    List<IssuesSupervisor> issuesSupervisors = new ArrayList<>();
                    if(CollectionUtils.isNotEmpty(issuesSupervisorList)){
                        for(IssuesSupervisor issuesSupervisor : issuesSupervisorList){
                            if(null!=issuesSupervisor){
                                if(StringUtil.isNotEmpty(issuesSupervisor.getBindResponsibleOrgIds())){
                                    boolean isAdd = false;
                                    String[] bindResponsibleOrgIdArray = issuesSupervisor.getBindResponsibleOrgIds().split(",");
                                    if(null!=bindResponsibleOrgIdArray&&bindResponsibleOrgIdArray.length>0){
                                        List<String> bindResponsibleOrgIdList = Arrays.asList(bindResponsibleOrgIdArray);
                                        for(String issuesAllocationOrgId : issuesAllocationOrgIdList){
                                            if(StringUtil.isNotEmpty(issuesAllocationOrgId)&&bindResponsibleOrgIdList.contains(issuesAllocationOrgId)){
                                                isAdd = true;
                                            }
                                        }
                                    }
                                    if (isAdd) {
                                        issuesSupervisors.add(issuesSupervisor);
                                    }
                                }
                            }
                        }
                    }

//                    String findInSets = "";
//                    for(String orgId : issuesAllocationOrgIdList){
//                        if(StringUtil.isNotEmpty(orgId)){
//                            if(StringUtil.isEmpty(findInSets)){
//                                findInSets = "(";
//                            }
//                            if(StringUtil.isNotEmpty(findInSets)&&!findInSets.equals("(")){
//                                findInSets += " AND ";
//                            }
//                            findInSets += " FIND_IN_SET('"+orgId+"',bind_responsible_org_ids)";
//                        }
//                    }
//                    if(StringUtil.isNotEmpty(findInSets)){
//                        findInSets += " ) ";
//                        issuesSupervisorLambdaQueryWrapper.apply(StringUtil.isNotEmpty(findInSets),findInSets);
//                    }

                    if(CollectionUtils.isNotEmpty(issuesSupervisors)){
                        List<String> orgIdList = issuesSupervisors.stream().filter((issuesSupervisor) -> null != issuesSupervisor && StringUtil.isNotEmpty(issuesSupervisor.getSupervisorOrgId())).map((issuesSupervisor) -> issuesSupervisor.getSupervisorOrgId()).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(orgIdList)){
                            List<MyOrg> myOrgList = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().in(MyOrg::getOrgId, orgIdList));
                            if(CollectionUtils.isNotEmpty(myOrgList)){
                                String myOrgNames = myOrgList.stream().filter((myOrg) -> null != myOrg && StringUtil.isNotEmpty(myOrg.getOrgName())).map((myOrg) -> StringUtil.isNotEmpty(myOrg.getOrgShortName()) ? myOrg.getOrgShortName() : myOrg.getOrgName()).collect(Collectors.joining(","));
                                return myOrgNames;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


}
