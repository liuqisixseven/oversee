package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_task_role_group")
public class WorkflowProcessRoleGroup {
    private String id;
    private String processId;
    private String roleId;
    private String roleType;


}
