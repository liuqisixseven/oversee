package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程执行部门表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_process_depart")
public class WorkflowProcessDepart {
    @TableId
    private String id;
    private String processId;
    private String subProcessId;
    private String departId;
    private String source;//来源：上报问题本部牵头部门,责任单位牵头部门, 责任单位责任部门,督办部门
    private String role;//角色
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
}
