package com.chd.modules.oversee.issue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Data
@TableName("oversee_issue_todo")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oversee_issue_todo对象", description="oversee_issue_todo")
public class OverseeIssueTodo implements Serializable {
    private static final long serialVersionUID = 1L;


	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**问题ID*/
    @Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private Long issueId;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
    /**上一步任务集合userIds*/
    @Excel(name = "上一步任务集合userIds", width = 15)
    @ApiModelProperty(value = "上一步任务集合userIds")
    private String previousUserIds;
    /**部门id*/
    @Excel(name = "部门id", width = 15)
    @ApiModelProperty(value = "部门id")
    private String departId;
    /**发送状态 -1待办未发送 1待办已发送 2已办未处理 3已办已发送 4已办理不再发送*/
    @Excel(name = "发送状态 -1待办未发送 1待办已发送 2已办未处理 3已办已发送 4已办理不再发送", width = 15)
    @ApiModelProperty(value = "发送状态 -1待办未发送 1待办已发送 2已办未处理 3已办已发送 4已办理不再发送")
    private Integer sendStatus;
    /**发送时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "发送时间")
    private Date sendTime;
    /**角色 暂不使用 :EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员*/
    @Excel(name = "角色 暂不使用 :EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员", width = 15)
    @ApiModelProperty(value = "角色 暂不使用 :EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员")
    private String role;
	/**创建用户id*/
	@Excel(name = "创建用户id", width = 15)
    @ApiModelProperty(value = "创建用户id")
    private String createUserId;
	/**修改用户id*/
	@Excel(name = "修改用户id", width = 15)
    @ApiModelProperty(value = "修改用户id")
    private String updateUserId;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**数据状态 -1 无效 1 有效*/
	@Excel(name = "数据状态 -1 无效 1 有效", width = 15)
    @ApiModelProperty(value = "数据状态 -1 无效 1 有效")
    private Integer dataType;

    @TableField(exist = false)
    private Integer issueTodoCount;

    @TableField(exist = false)
    private String previousTaskUserNames;
}
