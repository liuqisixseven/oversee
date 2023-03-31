package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.mapper.*;
import com.chd.modules.oversee.issue.service.IIssuesRectificationMeasureService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import org.flowable.engine.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: issues_rectification_measure
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class IssuesRectificationMeasureServiceImpl extends ServiceImpl<IssuesRectificationMeasureMapper, IssuesRectificationMeasure> implements IIssuesRectificationMeasureService {


    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    @Lazy
    IOverseeIssueService overseeIssueService;

    @Autowired
    IssuesRectificationMeasureMapper issuesRectificationMeasureMapper;

    @Autowired
    IMyOrgService myOrgService;


    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    IssuesAllocationMapper issuesAllocationMapper;

    @Autowired
    OverseeIssueSpecificMapper overseeIssueSpecificMapper;

    @Override
    @Transactional
    public int addOrUpdateList(List<IssuesRectificationMeasure> issuesRectificationMeasureList) {
        int addOrUpdateCount = 0;
        if(null!=issuesRectificationMeasureList&&issuesRectificationMeasureList.size()>0){
            for(IssuesRectificationMeasure issuesRectificationMeasure : issuesRectificationMeasureList){
                addOrUpdateCount = addOrUpdate(issuesRectificationMeasure);
            }
        }
        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateList(List<IssuesRectificationMeasure> issuesRectificationMeasureList, Long issueId, String departId, String updateUserId, String taskId,String updateBy) {
        if(null!=issuesRectificationMeasureList&&issuesRectificationMeasureList.size()>0){
            for(IssuesRectificationMeasure issuesRectificationMeasure : issuesRectificationMeasureList){
                issuesRectificationMeasure.setIssueId(issueId);
                issuesRectificationMeasure.setOrgId(departId);
                issuesRectificationMeasure.setUpdateUserId(updateUserId);
                issuesRectificationMeasure.setTaskId(taskId);
                issuesRectificationMeasure.setUpdateBy(updateBy);
            }
        }
        return addOrUpdateList(issuesRectificationMeasureList);
    }

    @Override
    @Transactional
    public int addOrUpdate(IssuesRectificationMeasure issuesRectificationMeasure) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=issuesRectificationMeasure),"请传递问题整改数据");
        Assert.isTrue((StringUtil.isNotEmpty(issuesRectificationMeasure.getOrgId())),"请传递问题整改责任部门id");
        Assert.isTrue((null!=issuesRectificationMeasure.getIssueId()&&issuesRectificationMeasure.getIssueId().longValue()>0L),"请传递问题整改问题id");
        Assert.isTrue((StringUtil.isNotEmpty(issuesRectificationMeasure.getRectificationMeasureContent())),"请传递整改措施");
        Assert.isTrue((null!=issuesRectificationMeasure.getRectificationTimeLimit()),"请传递整改时间");
        Assert.isTrue((StringUtil.isNotEmpty(issuesRectificationMeasure.getUpdateUserId())),"请传递问题整改修改用户id");
//        Assert.isTrue((StringUtil.isNotEmpty(issuesRectificationMeasure.getUuid())),"请传递问uuid");

        List<IssuesAllocation> issuesAllocations = issuesAllocationMapper.selectList(Wrappers.<IssuesAllocation>lambdaQuery().eq(IssuesAllocation::getResponsibleDepartmentOrgId, issuesRectificationMeasure.getOrgId()).eq(IssuesAllocation::getIssueId, issuesRectificationMeasure.getIssueId()).eq(IssuesAllocation::getDataType, 1));
        if(null!=issuesAllocations&&issuesAllocations.size()>0){
            issuesRectificationMeasure.setIssuesAllocationId(issuesAllocations.get(0).getId());
        }

        issuesRectificationMeasure.setUpdateTime(new Date());

        String redisKey = "issuesRectificationMeasure::" + (StringUtil.isNotEmpty(issuesRectificationMeasure.getUuid())?issuesRectificationMeasure.getUuid():new Date().getTime());
        if(StringUtil.isEmpty(issuesRectificationMeasure.getUuid()) || redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                issuesRectificationMeasure.setId(getDataIdByUUId(issuesRectificationMeasure.getUuid(),issuesRectificationMeasure.getIssueId()));
                if(null!=issuesRectificationMeasure.getId()&&issuesRectificationMeasure.getId()>0){
                    addOrUpdateCount = issuesRectificationMeasureMapper.updateById(issuesRectificationMeasure);
                }else{
                    issuesRectificationMeasure.setCreateUserId(issuesRectificationMeasure.getUpdateUserId());
                    issuesRectificationMeasure.setCreateTime(new Date());
                    issuesRectificationMeasure.setDataType(1);
                    addOrUpdateCount = issuesRectificationMeasureMapper.insert(issuesRectificationMeasure);
                }

                if(addOrUpdateCount>0){
                    OverseeIssue overseeIssue = overseeIssueMapper.selectById(issuesRectificationMeasure.getIssueId());
                    if(null!=overseeIssue&&null!=issuesRectificationMeasure.getRectificationTimeLimit()&&(overseeIssue.getExpectTime() == null || issuesRectificationMeasure.getRectificationTimeLimit().compareTo(overseeIssue.getExpectTime()) > 0)){
                        OverseeIssue overseeIssueU = new OverseeIssue();
                        overseeIssueU.setId(issuesRectificationMeasure.getIssueId());
                        overseeIssueU.setExpectTime(issuesRectificationMeasure.getRectificationTimeLimit());
                        overseeIssueU.setUpdateTime(new Date());
                        overseeIssueU.setUpdateUserId(issuesRectificationMeasure.getUpdateUserId());
                        overseeIssueService.addOrUpdate(overseeIssueU);
                    }

                    OverseeIssueSpecific specific = new OverseeIssueSpecific();
                    specific.setIssuesRectificationMeasureId(issuesRectificationMeasure.getId());
                    List<OverseeIssueSpecific> overseeIssueSpecifics = overseeIssueSpecificMapper.queryList(specific);
                    if(null!=overseeIssueSpecifics&&overseeIssueSpecifics.size()>0){
                        if(null!=overseeIssueSpecifics.get(0).getId()&&overseeIssueSpecifics.get(0).getId().intValue()>0){
                            specific.setId(overseeIssueSpecifics.get(0).getId());
                        }
                    }

                    specific.setIssueId(issuesRectificationMeasure.getIssueId());
                    specific.setSpecificType(OverseeConstants.SpecificType.IMPROVE_ACTION);
                    specific.setOrgId(issuesRectificationMeasure.getOrgId());
                    specific.setContent(issuesRectificationMeasure.getRectificationMeasureContent());
                    specific.setUserId(issuesRectificationMeasure.getUpdateUserId());
                    specific.setUpdateBy(issuesRectificationMeasure.getUpdateBy());
                    specific.setUpdateTime(new Date());
                    specific.setCreateTime(specific.getUpdateTime());
                    specific.setCreateBy(specific.getUpdateBy());
                    specific.setExpectCorrectTime(issuesRectificationMeasure.getRectificationTimeLimit());
                    if(null!=specific.getId()&&specific.getId().intValue()>0){
                        overseeIssueSpecificMapper.updateById(specific);
                    }else {
                        overseeIssueSpecificMapper.insert(specific);
                    }

                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }
        return addOrUpdateCount;
    }




    Integer getDataIdByUUId(String uuid,Long issueId){
        if(StringUtil.isNotEmpty(uuid)){
            List<IssuesRectificationMeasure> issuesRectificationMeasureList = issuesRectificationMeasureMapper.selectList(Wrappers.<IssuesRectificationMeasure>lambdaQuery().eq(IssuesRectificationMeasure::getUuid, uuid).eq(IssuesRectificationMeasure::getIssueId,issueId).eq(IssuesRectificationMeasure::getDataType, 1));
            if(null!=issuesRectificationMeasureList&&issuesRectificationMeasureList.size()>0){
                if(null!=issuesRectificationMeasureList.get(0)&&null!=issuesRectificationMeasureList.get(0).getId()){
                    return issuesRectificationMeasureList.get(0).getId();
                }
            }
        }
        return null;
    }


    @Override
    public List<IssuesRectificationMeasure> srcToIssuesRectificationMeasureList(String str, Date rectificationTimeLimit){
        List<IssuesRectificationMeasure> issuesRectificationMeasureList = null;
        if(StringUtil.isNotEmpty(str)){

            issuesRectificationMeasureList = new ArrayList<>();
            try{
                Pattern pattern = Pattern.compile("(（?\\s*\\d\\s*(\\.|）)).*");
                //2.选择匹配对象
                Matcher matcher = pattern.matcher(str);
                //与谁匹配？与参数字符串str匹配
                int count = 0;
                while (matcher.find())//matcher.find()用于查找是否有这个字符，有的话返回true
                {
//                    System.out.println("第" + (++count) + "次找到");
                    //start()返回上一个匹配项的起始索引
                    //end()返回上一个匹配项的末尾索引。
//                System.out.println(str.substring(matcher.start(),matcher.end()));
                    String rectificationMeasureContent = str.substring(matcher.start(), matcher.end());
                    if(StringUtil.isNotEmpty(rectificationMeasureContent)){
                        rectificationMeasureContent = rectificationMeasureContent.replaceAll("\\n","<br>");
                        IssuesRectificationMeasure issuesRectificationMeasure = new IssuesRectificationMeasure();
//                        issuesRectificationMeasure.setRectificationMeasureContent(str.substring(matcher.start(),matcher.end()));
                        issuesRectificationMeasure.setRectificationMeasureContent(rectificationMeasureContent);
                        issuesRectificationMeasure.setRectificationTimeLimit(rectificationTimeLimit);
                        issuesRectificationMeasureList.add(issuesRectificationMeasure);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if(null==issuesRectificationMeasureList||issuesRectificationMeasureList.size()<=0){
                IssuesRectificationMeasure issuesRectificationMeasure = new IssuesRectificationMeasure();
                str = str.replaceAll("\\n","<br>");
                issuesRectificationMeasure.setRectificationMeasureContent(str);
                issuesRectificationMeasure.setRectificationTimeLimit(rectificationTimeLimit);
                issuesRectificationMeasureList.add(issuesRectificationMeasure);
            }
        }

        return issuesRectificationMeasureList;
    }

    @Override
    public List<IssuesRectificationMeasure> selectIssuesRectificationMeasureList(Map<String, Object> map) {
        return issuesRectificationMeasureMapper.selectIssuesRectificationMeasureList(map);
    }


}
