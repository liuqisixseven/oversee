package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.mapper.IssuesAllocationMapper;
import com.chd.modules.oversee.issue.mapper.IssuesSupervisorMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueRoleMapper;
import com.chd.modules.oversee.issue.service.*;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class OverseeIssueRoleServiceImpl extends ServiceImpl<OverseeIssueRoleMapper, OverseeIssueRole> implements IOverseeIssueRoleService {

    @Autowired
    OverseeIssueRoleMapper overseeIssueRoleMapper;

    @Autowired
    IssuesAllocationMapper issuesAllocationMapper;

    @Autowired
    IssuesSupervisorMapper issuesSupervisorMapper;

    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void synchronization(Long overseeIssueId){

        Assert.isTrue((null!=overseeIssueId&&overseeIssueId.longValue()>0L),"请传递问题id");

        String updateUserId = BaseConstant.DEFAULT_USER;

        OverseeIssue overseeIssue = overseeIssueMapper.selectById(overseeIssueId);
        Assert.isTrue((null!=overseeIssue&&null!=overseeIssue.getDataType()&&overseeIssue.getDataType().intValue()==1),"不存在当前问题");
        Assert.isTrue((null!=overseeIssue.getId()&&overseeIssue.getId().longValue() == overseeIssueId.longValue()),"不存在当前问题");

//        问题创建用户
        if(StringUtil.isNotEmpty(overseeIssue.getCreateUserId())){
            addOrUpdate(overseeIssue.getCreateUserId(),OverseeIssueRole.ROLE_TYPE_USER,overseeIssueId,updateUserId,WorkflowConstants.DEPART_ROLE.LAUNCHUSER);
        }

//        问题上报用户
        if(StringUtil.isNotEmpty(overseeIssue.getReportUserId())){
            addOrUpdate(overseeIssue.getReportUserId(),OverseeIssueRole.ROLE_TYPE_USER,overseeIssueId,updateUserId,WorkflowConstants.DEPART_ROLE.LAUNCHUSER,WorkflowConstants.DEPART_SOURCE.INITIATOR);
        }

//        本部牵头部门
        if(StringUtil.isNotEmpty(overseeIssue.getHeadquartersLeadDepartmentOrgId())){
            addOrUpdate(overseeIssue.getHeadquartersLeadDepartmentOrgId(),OverseeIssueRole.ROLE_TYPE_DEPART,overseeIssueId,updateUserId,null,WorkflowConstants.DEPART_SOURCE.INITIATOR);
        }
//        本部牵头部门经办人
        if(StringUtil.isNotEmpty(overseeIssue.getHeadquartersLeadDepartmentManagerUserId())){
            addOrUpdate(overseeIssue.getHeadquartersLeadDepartmentManagerUserId(),OverseeIssueRole.ROLE_TYPE_USER,overseeIssueId,updateUserId,WorkflowConstants.DEPART_ROLE.EXECUTOR,WorkflowConstants.DEPART_SOURCE.INITIATOR);
        }


//        责任单位牵头部门
        if(StringUtil.isNotEmpty(overseeIssue.getResponsibleLeadDepartmentOrgId())){
            addOrUpdate(overseeIssue.getResponsibleLeadDepartmentOrgId(),OverseeIssueRole.ROLE_TYPE_DEPART,overseeIssueId,updateUserId,null,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE);
        }

//        责任单位牵头部门经办人
        if(StringUtil.isNotEmpty(overseeIssue.getResponsibleLeadDepartmentUserId())){
            addOrUpdate(overseeIssue.getResponsibleLeadDepartmentUserId(),OverseeIssueRole.ROLE_TYPE_USER,overseeIssueId,updateUserId,null,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE);
        }


//        查询责任单位
        List<IssuesAllocation> issuesAllocations = issuesAllocationMapper.selectList(Wrappers.<IssuesAllocation>lambdaQuery().eq(IssuesAllocation::getIssueId, overseeIssueId).eq(IssuesAllocation::getDataType, 1));
        if(null!=issuesAllocations&&issuesAllocations.size()>0){
            for(IssuesAllocation issuesAllocation : issuesAllocations){
                if(null!=issuesAllocation){
                    if(StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId())){
                        addOrUpdate(issuesAllocation.getResponsibleDepartmentOrgId(),OverseeIssueRole.ROLE_TYPE_DEPART,overseeIssueId,updateUserId,null,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE);
                    }
                    if(StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentManagerUserId())){
                        addOrUpdate(issuesAllocation.getResponsibleDepartmentManagerUserId(),OverseeIssueRole.ROLE_TYPE_USER,overseeIssueId,updateUserId,WorkflowConstants.DEPART_ROLE.EXECUTOR,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE);
                    }
                }
            }
        }


        //        查询督办单位
        List<IssuesSupervisor> issuesSupervisors = issuesSupervisorMapper.selectList(Wrappers.<IssuesSupervisor>lambdaQuery().eq(IssuesSupervisor::getIssueId, overseeIssueId).eq(IssuesSupervisor::getIssuesProcessType,1).eq(IssuesSupervisor::getDataType, 1));
        if(null!=issuesSupervisors&&issuesSupervisors.size()>0){
            for(IssuesSupervisor issuesSupervisor : issuesSupervisors){
                if(null!=issuesSupervisor){
                    if(StringUtil.isNotEmpty(issuesSupervisor.getSupervisorOrgId())){
                        addOrUpdate(issuesSupervisor.getSupervisorOrgId(),OverseeIssueRole.ROLE_TYPE_DEPART,overseeIssueId,updateUserId,null,WorkflowConstants.DEPART_SOURCE.SUPERVISOR);
                    }
                    if(StringUtil.isNotEmpty(issuesSupervisor.getUserId())){
                        addOrUpdate(issuesSupervisor.getUserId(),OverseeIssueRole.ROLE_TYPE_USER,overseeIssueId,updateUserId,WorkflowConstants.DEPART_ROLE.EXECUTOR,WorkflowConstants.DEPART_SOURCE.SUPERVISOR);
                    }
                }
            }
        }


    }

    @Override
    @Transactional
    public int isAuthorizeSupervise(Long issueId, String orgIds, String userId){
        String[] sources = new String[]{WorkflowConstants.DEPART_SOURCE.INITIATOR,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE,WorkflowConstants.DEPART_SOURCE.SUPERVISOR};
        return isAuthorizeSupervise(issueId,orgIds,userId, Arrays.asList(sources).stream().collect(Collectors.joining(",")));
    }

    @Override
    @Transactional
    public int isAuthorizeSupervise(Long issueId, String orgIds, String userId, String sources){
        int isAuthorizeSupervise = -1;
        try{
            Map<String, Object> map = Maps.newHashMap();
            map.put("issueId",issueId);
            map.put("orgIds",StringUtil.isNotEmpty(orgIds)?Arrays.asList(orgIds.split(",")):null);
            map.put("userId",userId);
            map.put("sources",StringUtil.isNotEmpty(sources)?Arrays.asList(sources.split(",")):null);
            List<OverseeIssueRole> authorizeSuperviseRoleList = overseeIssueRoleMapper.getAuthorizeSuperviseRoleList(map);
            isAuthorizeSupervise = CollectionUtils.isNotEmpty(authorizeSuperviseRoleList)?1:-1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return isAuthorizeSupervise;
    }

    @Override
    @Transactional
    public int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId){
        return addOrUpdate(dataId,roleType,issueId,updateUserId,null);
    }

    @Override
    @Transactional
    public int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId, String role){
        return addOrUpdate(dataId,roleType,issueId,updateUserId,role,null);
    }

    @Override
    @Transactional
    public int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId, String role, String source){
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        Assert.isTrue((null!=roleType&&roleType.intValue()>0),"请传递roleType");
        Assert.isTrue((StringUtil.isNotEmpty(dataId)),"请传递dataId");
        Assert.isTrue((StringUtil.isNotEmpty(updateUserId)),"请传递updateUserId");
        OverseeIssueRole overseeIssueRole = new OverseeIssueRole();
        overseeIssueRole.setIssueId(issueId);
        overseeIssueRole.setRoleType(roleType);
        overseeIssueRole.setDataId(dataId);
        overseeIssueRole.setRole(role);
        overseeIssueRole.setSource(source);
        overseeIssueRole.setUpdateUserId(updateUserId);
        return  addOrUpdate(overseeIssueRole);
    }


    @Override
    @Transactional
    public int addOrUpdate(OverseeIssueRole overseeIssueRole) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=overseeIssueRole),"请传递overseeIssueRole数据");
        Assert.isTrue((null!=overseeIssueRole.getIssueId()&&overseeIssueRole.getIssueId().intValue()>0),"请传递问题id");
        Assert.isTrue((null!=overseeIssueRole.getRoleType()&&overseeIssueRole.getRoleType().intValue()>0),"请传递roleType");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueRole.getDataId())),"请传递dataId");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueRole.getUpdateUserId())),"请传递updateUserId");

        overseeIssueRole.setUpdateTime(new Date());

        String redisKey = BaseConstant.OVERSEE_ISSUE_ROLE_EDIT_ISSUEID_ROLETYPE_DATAID_REDIS_KEY + overseeIssueRole.getIssueId() + overseeIssueRole.getRoleType() + overseeIssueRole.getDataId();
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                if(null==overseeIssueRole.getId()||overseeIssueRole.getId().intValue()<=0){
                    LambdaQueryWrapper<OverseeIssueRole> overseeIssueRoleWrappers = Wrappers.<OverseeIssueRole>lambdaQuery().eq(OverseeIssueRole::getIssueId, overseeIssueRole.getIssueId()).eq(OverseeIssueRole::getRoleType, overseeIssueRole.getRoleType()).eq(OverseeIssueRole::getDataId, overseeIssueRole.getDataId()).eq(OverseeIssueRole::getDataType, 1);
                    if(StringUtil.isNotEmpty(overseeIssueRole.getSource())){
                        overseeIssueRoleWrappers.eq(OverseeIssueRole::getSource,overseeIssueRole.getSource());
                    }
                    if(StringUtil.isNotEmpty(overseeIssueRole.getRole())){
                        overseeIssueRoleWrappers.eq(OverseeIssueRole::getRole,overseeIssueRole.getRole());
                    }else{
                        overseeIssueRoleWrappers.isNull(OverseeIssueRole::getRole);
                    }
                    List<OverseeIssueRole> overseeIssueRoles = overseeIssueRoleMapper.selectList(overseeIssueRoleWrappers);
                    if(null!=overseeIssueRoles&&overseeIssueRoles.size()>0){
                        OverseeIssueRole overseeIssueRoleS = overseeIssueRoles.get(0);
                        if(null!=overseeIssueRoleS&&null!=overseeIssueRoleS.getId()&&overseeIssueRoleS.getId().intValue()>0){
                            overseeIssueRole.setId(overseeIssueRoleS.getId());
                        }
                    }
                }



                if(null!=overseeIssueRole.getId()&&overseeIssueRole.getId().intValue()>0){
                    addOrUpdateCount = overseeIssueRoleMapper.updateById(overseeIssueRole);
                }else{
                    overseeIssueRole.setCreateUserId(overseeIssueRole.getUpdateUserId());
                    overseeIssueRole.setCreateTime(new Date());
                    addOrUpdateCount = overseeIssueRoleMapper.insert(overseeIssueRole);

                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }


        return addOrUpdateCount;
    }
}
