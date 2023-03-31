package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.workflow.entity.WorkflowCandidateGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface WorkflowCandidateGroupMapper extends BaseMapper<WorkflowCandidateGroup> {

    List<WorkflowCandidateGroup> queryList(WorkflowCandidateGroup candidateGroup);

    Integer updateCandidateGroupById(WorkflowCandidateGroup candidateGroup);

    WorkflowCandidateGroup findById(String id);

    Integer batchDeleteByIds(List<String> ids);

}
