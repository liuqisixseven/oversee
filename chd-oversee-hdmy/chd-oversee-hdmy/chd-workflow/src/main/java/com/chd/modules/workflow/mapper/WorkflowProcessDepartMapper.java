package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkflowProcessDepartMapper extends BaseMapper<WorkflowProcessDepart> {


    List<WorkflowProcessDepart> processDepartListByProcessId(@Param("processId") String processId,@Param("subProcessId")String subProcessId,@Param("code") String candiateGroupId);

    List<WorkflowProcessDepart> getProcessDepartBySource(@Param("processId") String processId,@Param("source") String source,@Param("role") String role);



}
