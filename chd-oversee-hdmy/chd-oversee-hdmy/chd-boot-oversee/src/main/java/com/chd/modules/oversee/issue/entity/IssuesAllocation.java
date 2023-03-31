package com.chd.modules.oversee.issue.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Data
@TableName("issues_allocation")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="issues_allocation对象", description="issues_allocation")
public class IssuesAllocation implements Serializable {
    private static final long serialVersionUID = 1L;

    public static int COOPERATE_RESPONSIBLE_DEPARTMENT = 1;

    public static int MAIN_RESPONSIBLE_DEPARTMENT = 2;


	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private java.lang.Integer id;
	/**问题ID*/
	@Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private Long issueId;
	/**责任单位的责任部门id*/
	@Excel(name = "责任单位的责任部门id", width = 15)
    @ApiModelProperty(value = "责任单位的责任部门id")
    private java.lang.String responsibleDepartmentOrgId;
    /**责任部门类型 1 配合部门 2 主责任部门*/
    @Excel(name = "责任部门类型 1 配合部门 2 主责任部门", width = 15)
    @ApiModelProperty(value = "责任部门类型 1 配合部门 2 主责任部门")
    private Integer departmentType;
    /**责任单位的责任部门负责人id*/
    @Excel(name = "责任单位的责任部门负责人id", width = 15)
    @ApiModelProperty(value = "责任单位的责任部门负责人id")
    private java.lang.String manageUserId;
    /**责任单位的责任部门分管领导id*/
    @Excel(name = "责任单位的责任部门分管领导id", width = 15)
    @ApiModelProperty(value = "责任单位的责任部门分管领导id")
    private java.lang.String supervisorUserId;
	/**责任单位的责任部门经办人user_id*/
	@Excel(name = "责任单位的责任部门经办人user_id", width = 15)
    @ApiModelProperty(value = "责任单位的责任部门经办人user_id")
    private java.lang.String responsibleDepartmentManagerUserId;
	/**排序 越小越靠前*/
	@Excel(name = "排序 越小越靠前", width = 15)
    @ApiModelProperty(value = "排序 越小越靠前")
    private java.lang.Integer sort;
	/**创建用户id*/
	@Excel(name = "创建用户id", width = 15)
    @ApiModelProperty(value = "创建用户id")
    private java.lang.String createUserId;
	/**修改用户id*/
	@Excel(name = "修改用户id", width = 15)
    @ApiModelProperty(value = "修改用户id")
    private java.lang.String updateUserId;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
	/**数据状态 -1 无效 1 有效*/
	@Excel(name = "数据状态 -1 无效 1 有效", width = 15)
    @ApiModelProperty(value = "数据状态 -1 无效 1 有效")
    private java.lang.Integer dataType;
}
