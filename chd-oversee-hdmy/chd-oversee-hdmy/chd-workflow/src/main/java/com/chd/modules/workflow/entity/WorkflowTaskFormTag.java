package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 任务审批表单标识
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_task_form_tag")
public class WorkflowTaskFormTag {
    @TableId
    private String code;
    private String name;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
}
