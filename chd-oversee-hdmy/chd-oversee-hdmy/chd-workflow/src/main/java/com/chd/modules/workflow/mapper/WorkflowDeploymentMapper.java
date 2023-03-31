package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.workflow.entity.WorkflowDeployment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkflowDeploymentMapper extends BaseMapper<WorkflowDeployment> {

    List<WorkflowDeployment> queryListByCategory(String category);

}
