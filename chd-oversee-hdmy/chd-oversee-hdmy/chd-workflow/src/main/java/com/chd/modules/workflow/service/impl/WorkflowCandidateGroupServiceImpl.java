package com.chd.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chd.modules.workflow.entity.WorkflowCandidateGroup;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.mapper.WorkflowCandidateGroupMapper;
import com.chd.modules.workflow.mapper.WorkflowProcessDepartMapper;
import com.chd.modules.workflow.service.WorkflowCandidateGroupService;
import com.chd.modules.workflow.service.WorkflowProcessDepartService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class WorkflowCandidateGroupServiceImpl implements WorkflowCandidateGroupService {

    @Autowired
    WorkflowCandidateGroupMapper workflowCandidateGroupMapper;
    @Autowired
    WorkflowProcessDepartMapper workflowProcessDepartMapper;
    @Autowired
    WorkflowProcessDepartService workflowProcessDepartService;

    @Override
    public List<WorkflowCandidateGroup> candidateGroupList(WorkflowCandidateGroup candidateGroup) {
        return workflowCandidateGroupMapper.queryList(candidateGroup);
    }

    @Override
    public WorkflowCandidateGroup getCandidateGroup(String source, String role) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("source",source);
        queryWrapper.eq("role",role);
        return workflowCandidateGroupMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer updateCandidateGroupById(WorkflowCandidateGroup candidateGroup) {
        return workflowCandidateGroupMapper.updateCandidateGroupById(candidateGroup);
    }

    @Override
    public Integer batchDeleteByIds(List<String> ids) {
        return workflowCandidateGroupMapper.batchDeleteByIds(ids);
    }

    @Override
    public Integer saveCandidateGroup(WorkflowCandidateGroup candidateGroup) {
        Date now=new Date();
        candidateGroup.setUpdateTime(now);
        WorkflowCandidateGroup dbCandidateGroup= workflowCandidateGroupMapper.findById(candidateGroup.getId());
        if(dbCandidateGroup==null){
            candidateGroup.setCreateTime(candidateGroup.getUpdateTime());
            candidateGroup.setCreateBy(candidateGroup.getUpdateBy());
            return workflowCandidateGroupMapper.insert(candidateGroup);
        }
        return workflowCandidateGroupMapper.updateCandidateGroupById(candidateGroup);

    }

    @Override
    public WorkflowCandidateGroup findById(String id) {
        return workflowCandidateGroupMapper.findById(id);
    }
}
