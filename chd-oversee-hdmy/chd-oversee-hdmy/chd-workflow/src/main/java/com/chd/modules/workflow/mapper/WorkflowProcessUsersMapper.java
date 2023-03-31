package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.workflow.entity.WorkflowProcessUsers;

import java.util.List;

/**
 * 流程执行用户组
 */
public interface WorkflowProcessUsersMapper extends BaseMapper<WorkflowProcessUsers> {

    List<WorkflowProcessUsers> getUserListByProcessId(WorkflowProcessUsers processUsers);
}
