package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.workflow.entity.WorkflowComment;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkflowCommentMapper extends BaseMapper<WorkflowComment> {

    Integer insertComment(WorkflowComment workflowComment);

    Integer insertCommentFormData(@Param("id") String id,@Param("formData") String formData);

    List<WorkflowComment> commentListByProcessId(@Param("processInstanceId") String processInstanceId);

    List<WorkflowComment> findListByTaskKey(@Param("processInstanceId") String processId,@Param("taskKey") String taskKey);
    List<WorkflowComment> findListByTaskKeys(@Param("processInstanceId") String processId,@Param("taskKeyList") List<String> taskKeyList);

    List<WorkflowComment> findOrgidListByTaskKey(@Param("processInstanceId") String processId,@Param("taskKey") String taskKey);
}
