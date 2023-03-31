package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_comment")
public class WorkflowComment {
    private String id;
    /**
     * 任务id
     */
    protected String taskId;
    protected String taskKey;
    /**
     * 添加人
     */
    protected String userId;
    /**
     * 用户的名称
     */
    protected String userName;

    /**
     * 流程实例id
     */
    protected String processInstanceId;
    /**
     * 意见信息
     */
    protected String comment;
    /**
     * 时间
     */
    protected Date createTime;

    private String taskType;

    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 表单ID
     */
    private String formId;
    private String formText;

    @TableField(exist = false)
    private String orgId;//部门id
}
