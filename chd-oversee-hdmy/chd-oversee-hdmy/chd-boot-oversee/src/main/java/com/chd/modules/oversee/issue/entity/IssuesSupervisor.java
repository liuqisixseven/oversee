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
import java.util.List;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Data
@TableName("issues_supervisor")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="issues_supervisor", description="issues_supervisor")
public class IssuesSupervisor implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String appendixListKey = "appendixList";
    public static String reasonKey = "text";

    public static int issuesProcessMainType = 1;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**问题ID*/
	@Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private Long issueId;
    /**显示状态 -1 不显示 1 显示*/
	@Excel(name = "显示状态 -1 不显示 1 显示", width = 15)
    @ApiModelProperty(value = "显示状态 -1 不显示 1 显示")
    private Integer showType;
	/**督办部门id*/
	@Excel(name = "督办部门id", width = 15)
    @ApiModelProperty(value = "督办部门id")
    private String supervisorOrgId;
    /**督办部门负责人user_id*/
    @Excel(name = "督办部门负责人user_id", width = 15)
    @ApiModelProperty(value = "督办部门负责人user_id")
    private String manageUserId;
    /**督办部门分管领导user_id*/
    @Excel(name = "督办部门分管领导user_id", width = 15)
    @ApiModelProperty(value = "督办部门分管领导user_id")
    private String supervisorUserId;
	/**督办部门经办人user_id*/
	@Excel(name = "督办部门经办人user_id", width = 15)
    @ApiModelProperty(value = "督办部门经办人user_id")
    private String userId;
    /**督办流程类型 1问题主流程 2 独立子流程*/
	@Excel(name = "督办流程类型 1问题主流程 2 独立子流程", width = 15)
    @ApiModelProperty(value = "督办流程类型 1问题主流程 2 独立子流程")
    private Integer issuesProcessType;
    /**督办部门绑定的责任部门ids*/
    @Excel(name = "督办部门绑定的责任部门ids", width = 15)
    @ApiModelProperty(value = "督办部门绑定的责任部门ids")
    private String bindResponsibleOrgIds;
    /**督办理由*/
    @Excel(name = "督办理由", width = 15)
    @ApiModelProperty(value = "督办理由")
    private String reason;
	/**排序 越小越靠前*/
	@Excel(name = "排序 越小越靠前", width = 15)
    @ApiModelProperty(value = "排序 越小越靠前")
    private Integer sort;
	/**创建用户id*/
	@Excel(name = "创建用户id", width = 15)
    @ApiModelProperty(value = "创建用户id")
    private String createUserId;
	/**修改用户id*/
	@Excel(name = "修改用户id", width = 15)
    @ApiModelProperty(value = "修改用户id")
    private String updateUserId;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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
    private List<OverseeIssueAppendix> appendixList;

    /**督办部门名称*/
    @TableField(exist = false)
    private String departName;
}
