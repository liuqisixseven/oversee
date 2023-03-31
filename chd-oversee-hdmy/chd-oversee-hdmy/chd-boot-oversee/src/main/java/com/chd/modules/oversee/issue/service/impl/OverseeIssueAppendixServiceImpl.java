package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.entity.OverseeIssueAppendix;
import com.chd.modules.oversee.issue.mapper.IssuesAllocationMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueAppendixMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueAppendixService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: oversee_issue_appendix
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class OverseeIssueAppendixServiceImpl extends ServiceImpl<OverseeIssueAppendixMapper, OverseeIssueAppendix> implements IOverseeIssueAppendixService {


    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    OverseeIssueAppendixMapper overseeIssueAppendixMapper;


    @Override
    public List<OverseeIssueAppendix> selectOverseeIssueAppendixList(Map<String, Object> map) {
        return overseeIssueAppendixMapper.selectOverseeIssueAppendixList(map);
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<OverseeIssueAppendix> overseeIssueAppendixs, Long issueId, Integer type) {
        return addOrUpdateList(overseeIssueAppendixs,issueId,type,null,null);
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<OverseeIssueAppendix> overseeIssueAppendixs, Long issueId, Integer type,String updateUserId) {
        return addOrUpdateList(overseeIssueAppendixs,issueId,type,updateUserId,null);
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<OverseeIssueAppendix> overseeIssueAppendixs, Long issueId, Integer type,String updateUserId,String dataId) {
        int addOrUpdateOrDeleteCount = 0;
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        if(null==overseeIssueAppendixs) overseeIssueAppendixs = new ArrayList<>();
        overseeIssueAppendixs = overseeIssueAppendixs.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getAppendixPath())).collect(Collectors.toList());
        List<OverseeIssueAppendix> overseeIssueAppendixOlds = null;
        // TODO 不为问题录入时 可重复录入 只有问题录入需要判断之前的数据
        if(type==OverseeIssueAppendix.PROBLEM_ENTRY_TYPE){
            overseeIssueAppendixOlds = overseeIssueAppendixMapper.selectList(Wrappers.<OverseeIssueAppendix>lambdaQuery().eq(OverseeIssueAppendix::getIssueId, issueId).eq(OverseeIssueAppendix::getType,type).eq(OverseeIssueAppendix::getDataType, 1));
        }
        if(null==overseeIssueAppendixOlds)overseeIssueAppendixOlds= new ArrayList<>();

        List<OverseeIssueAppendix> finalOverseeIssueAppendixOlds = overseeIssueAppendixOlds;
        overseeIssueAppendixs.stream().forEach((issuesAllocation)-> {
            finalOverseeIssueAppendixOlds.stream().forEach((issuesAllocationOld)->{
                if(null!=issuesAllocationOld&&null!=issuesAllocation){
                    if (StringUtil.isNotEmpty(issuesAllocationOld.getAppendixPath()) && issuesAllocationOld.getAppendixPath().equals(issuesAllocation.getAppendixPath())) {
                        issuesAllocation.setId(issuesAllocationOld.getId());
                    }
                }
            });
            issuesAllocation.setType(type);
            issuesAllocation.setDataId(dataId);
            issuesAllocation.setUpdateUserId(StringUtil.isNotEmpty(updateUserId)?updateUserId: BaseConstant.DEFAULT_USER);
        });

        List<OverseeIssueAppendix> finalOverseeIssueAppendixNews = overseeIssueAppendixs;
        List<OverseeIssueAppendix> deleteOverseeIssueAppendixList = finalOverseeIssueAppendixOlds.stream().filter((issuesAllocationOld) -> {
            return !finalOverseeIssueAppendixNews.stream().anyMatch((issuesAllocationNew) -> null != issuesAllocationNew && issuesAllocationNew.getAppendixPath().equals(issuesAllocationOld.getAppendixPath()));
        }).collect(Collectors.toList());

        if(null!=deleteOverseeIssueAppendixList&&deleteOverseeIssueAppendixList.size()>0){
            for(OverseeIssueAppendix deleteOverseeIssueAppendix : deleteOverseeIssueAppendixList){
                addOrUpdateOrDeleteCount += deleteIssuesAllocation(deleteOverseeIssueAppendix);
            }
        }

        if(null!=overseeIssueAppendixs&&overseeIssueAppendixs.size()>0){
            for(OverseeIssueAppendix overseeIssueAppendix : overseeIssueAppendixs){
                addOrUpdateOrDeleteCount += addOrUpdate(overseeIssueAppendix);
            }
        }

        return addOrUpdateOrDeleteCount;
    }

    @Override
    @Transactional
    public int addOrUpdate(OverseeIssueAppendix overseeIssueAppendix) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=overseeIssueAppendix),"请传递问题附件数据");
        Assert.isTrue((null!=overseeIssueAppendix.getIssueId()&&overseeIssueAppendix.getIssueId().intValue()>0),"请传递问题附件的问题id");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueAppendix.getAppendixPath())),"请传递文件路径");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueAppendix.getUpdateUserId())),"请传递问题附件的updateUserId");

        overseeIssueAppendix.setUpdateTime(new Date());

        if(StringUtil.isEmpty(overseeIssueAppendix.getFileName())){
            overseeIssueAppendix.setFileName(getOssFileNameByAppendixPath(overseeIssueAppendix.getAppendixPath()));
        }

        if(null!=overseeIssueAppendix.getId()&&overseeIssueAppendix.getId().intValue()>0){
            addOrUpdateCount = overseeIssueAppendixMapper.updateById(overseeIssueAppendix);
        }else{
            overseeIssueAppendix.setCreateTime(new Date());
            overseeIssueAppendix.setCreateUserId(overseeIssueAppendix.getUpdateUserId());
            overseeIssueAppendix.setDataType(1);
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssueAppendix.getCreateUserId())),"请传递问题分配的createUserId");
            addOrUpdateCount = overseeIssueAppendixMapper.insert(overseeIssueAppendix);
        }

        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int deleteIssuesAllocation(OverseeIssueAppendix overseeIssueAppendix) {
        Assert.isTrue((null!=overseeIssueAppendix),"请传递问题分配数据");
        Assert.isTrue((null!=overseeIssueAppendix.getId()&&overseeIssueAppendix.getId().intValue()>0),"请传递问题分配数据id");
        overseeIssueAppendix.setDataType(-1);
        overseeIssueAppendix.setUpdateTime(new Date());
        int i = overseeIssueAppendixMapper.updateById(overseeIssueAppendix);
        return i;
    }

    @Override

    public int synchronizationOverseeIssueAppendixListFileName(List<OverseeIssueAppendix> overseeIssueAppendixList) {
        if(CollectionUtils.isNotEmpty(overseeIssueAppendixList)){
            for(OverseeIssueAppendix overseeIssueAppendix : overseeIssueAppendixList){
                if(null!=overseeIssueAppendix){
                    if(StringUtil.isEmpty(overseeIssueAppendix.getFileName())&&StringUtil.isNotEmpty(overseeIssueAppendix.getAppendixPath())){
                        Map<String, Object> ossFileMap = getOssFileByAppendixPath(overseeIssueAppendix.getAppendixPath());
                        if(null!=ossFileMap){
                            String fileName = (String) ossFileMap.get("file_name");
                            if(StringUtil.isNotEmpty(fileName)){
                                overseeIssueAppendix.setFileName(fileName);
                            }

                            String relativePath = (String) ossFileMap.get("relative_path");
                            if(StringUtil.isNotEmpty(relativePath)){
                                overseeIssueAppendix.setRelativePath(relativePath);
                            }
                        }
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public List<Map<String, Object>> getOssFileList(Map<String, Object> map) {
        return overseeIssueAppendixMapper.getOssFileList(map);
    }

    private String getOssFileNameByAppendixPath(String appendixPath){
        String ossFileName = null;
        if(StringUtil.isNotEmpty(appendixPath)){
            Map<String, Object> selectMap = Maps.newHashMap();
            selectMap.put("id",appendixPath);
            List<Map<String, Object>> ossFileList = overseeIssueAppendixMapper.getOssFileList(selectMap);
            if(CollectionUtils.isNotEmpty(ossFileList)){
                Map<String, Object> map = ossFileList.get(0);
                if(null!=map){
                    String file_name = (String) map.get("file_name");
                    if(StringUtil.isNotEmpty(file_name)){
                        ossFileName = file_name;
                        return ossFileName;
                    }
                }
            }
        }
        return ossFileName;
    }

    private Map<String, Object> getOssFileByAppendixPath(String appendixPath){
        String ossFileName = null;
        if(StringUtil.isNotEmpty(appendixPath)){
            Map<String, Object> selectMap = Maps.newHashMap();
            selectMap.put("id",appendixPath);
            List<Map<String, Object>> ossFileList = overseeIssueAppendixMapper.getOssFileList(selectMap);
            if(CollectionUtils.isNotEmpty(ossFileList)){
                Map<String, Object> map = ossFileList.get(0);
                if(null!=map){
                    return map;
                }
            }
        }
        return null;
    }
}
