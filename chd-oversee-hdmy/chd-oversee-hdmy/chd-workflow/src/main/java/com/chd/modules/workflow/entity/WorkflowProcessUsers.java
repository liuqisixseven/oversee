package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 流程执行候选执行人
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_process_users")
public class WorkflowProcessUsers {

    @TableId
    private String id;//ID
    private String processId;//流程ID
    private String subProcessId;//子流程ID
    private String departId;//部门ID
    private String source;//来源
    private String role;//角色:EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员
    private String userId;//指定用户ID
    private String createBy;//创建者
    private Date createTime;//创建时间
    private String updateBy;//更新者
    private Date updateTime;//更新时间

}
