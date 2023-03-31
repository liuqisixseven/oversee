package com.chd.modules.workflow.service;

import com.chd.modules.workflow.entity.WorkflowCandidateGroup;
import com.chd.modules.workflow.entity.WorkflowDepart;

import java.util.List;

public interface WorkflowCandidateGroupService {

    /**
     * 流程候选组列表
     * @param candidateGroup
     * @return
     */
    List<WorkflowCandidateGroup> candidateGroupList(WorkflowCandidateGroup candidateGroup);

    WorkflowCandidateGroup getCandidateGroup(String source,String role);

    /**
     * 修改流程候选组记录
     * @param candidateGroup
     * @return
     */
    Integer updateCandidateGroupById(WorkflowCandidateGroup candidateGroup);

    /**
     * 批量删除流程候选组
     * @param ids
     * @return
     */
    Integer batchDeleteByIds(List<String> ids);

    Integer saveCandidateGroup(WorkflowCandidateGroup candidateGroup);

    WorkflowCandidateGroup findById(String id);





}
