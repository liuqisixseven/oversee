package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chd.modules.workflow.vo.WorkflowVariablesVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_process")
public class WorkflowProcess {
    private String id;
    private String title;//标题
    private String startUserId;//发起用户ID
    private String startUserName;//发起用户ID
    private Date   applyTime;//申请时间
    private String bizId;//业务ID
    private String bizUrl;//浏览详情地址
    private String processCategory;//流程类型;
    private String state;//流程状态
    private String processDefId;//流程定义ID
    private String processDefKey;//流程编码,部门ID
    private String remark;//备注
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private String tenantId;
    private String nextUserTask;
    private Date nextTaskTime;
    @TableField(exist = false)
    private WorkflowVariablesVo variables;//流程参数

}
