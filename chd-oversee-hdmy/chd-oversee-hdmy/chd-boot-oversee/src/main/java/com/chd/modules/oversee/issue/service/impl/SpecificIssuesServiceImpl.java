package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.SpecificIssues;
import com.chd.modules.oversee.issue.mapper.SpecificIssuesMapper;
import com.chd.modules.oversee.issue.service.ISpecificIssuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: specific_issues
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class SpecificIssuesServiceImpl extends ServiceImpl<SpecificIssuesMapper, SpecificIssues> implements ISpecificIssuesService {

    @Autowired
    SpecificIssuesMapper specificIssuesMapper;

    @Override
    @Transactional
    public int addOrUpdate(SpecificIssues specificIssues) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=specificIssues),"请传递问题内容数据");
        Assert.isTrue((StringUtil.isNotEmpty(specificIssues.getUpdateUserId())),"未获取到修改用户id");
        specificIssues.setUpdateTime(new Date());
        if(null==specificIssues.getId()||specificIssues.getId().intValue()<=0){
            specificIssues.setCreateUserId(specificIssues.getUpdateUserId());
            specificIssues.setCreateTime(new Date());
            addOrUpdateCount = specificIssuesMapper.insert(specificIssues);
        }else{
            addOrUpdateCount = specificIssuesMapper.updateById(specificIssues);
        }

        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdate(String specificIssuesContent, String updateUserId) {
        return addOrUpdate(specificIssuesContent,updateUserId);
    }

    @Override
    @Transactional
    public int addOrUpdate(String specificIssuesContent, Long issueId, Integer id, String updateUserId) {
        SpecificIssues specificIssues = new SpecificIssues();
        specificIssues.setId(id);
        specificIssues.setIssueId(issueId);
        specificIssues.setSpecificIssuesContent(specificIssuesContent);
        specificIssues.setUpdateUserId(updateUserId);
        return addOrUpdate(specificIssues);
    }

    @Override
    public IPage<SpecificIssues> selectSpecificIssuesPageVo(Page<?> page, Map<String, Object> map) {
        return null;
    }

    @Override
    public List<SpecificIssues> selectSpecificIssuesList(Map<String, Object> map) {
        return null;
    }
}
