package com.chd.modules.oversee.issue.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: issues_rectification_measure
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Data
@TableName("issues_rectification_measure")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="issues_rectification_measure对象", description="issues_rectification_measure")
public class IssuesRectificationMeasure implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private java.lang.Integer id;
    /**任务id*/
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
    /**uuid*/
    @Excel(name = "uuid", width = 15)
    @ApiModelProperty(value = "uuid")
    private String uuid;
	/**问题ID*/
	@Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private java.lang.Long issueId;
	/**问题分配ID，如果一个问题对应多个分配，则此字段必填*/
	@Excel(name = "问题分配ID，如果一个问题对应多个分配，则此字段必填", width = 15)
    @ApiModelProperty(value = "问题分配ID，如果一个问题对应多个分配，则此字段必填")
    private java.lang.Integer issuesAllocationId;
	/**整改措施*/
	@Excel(name = "整改措施", width = 15)
    @ApiModelProperty(value = "整改措施")
    private java.lang.String rectificationMeasureContent;
	/**整改时限*/
	@Excel(name = "整改时限", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "整改时限")
    private java.util.Date rectificationTimeLimit;
    /**责任单位的责任部门id*/
    @Excel(name = "责任单位的责任部门id", width = 15)
    @ApiModelProperty(value = "责任单位的责任部门id")
    private String orgId;
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
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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

    @TableField(exist = false)
    private List<OverseeIssueAppendix> files;

    @TableField(exist = false)
    private String updateBy;//更新人

    @TableField(exist = false)
    private String filesSrc;//附件;//更新人

//    部门名称
    @TableField(exist = false)
    private String departName;



}
