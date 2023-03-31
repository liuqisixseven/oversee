package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_depart")
public class WorkflowDepart {
    private String id;//ID
    private String departId;//部门ID
    private String role;//角色
    private String createBy;//创建者
    private Date createTime;//创建时间
    private String updateBy;//更新者
    private Date updateTime;//更新时间

    @TableField(exist = false)
    private String departName;//部门名称
    @TableField(exist = false)
    List<WorkflowUserVo> users;


}
