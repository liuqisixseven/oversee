package com.chd.modules.oversee.issue.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
 * @Description: oversee_issue_appendix
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Data
@TableName("oversee_issue_appendix")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oversee_issue_appendix对象", description="oversee_issue_appendix")
public class OverseeIssueAppendix implements Serializable {
    private static final long serialVersionUID = 1L;

    public static int PROBLEM_ENTRY_TYPE = 1;
    public static int ISSUE_ASSIGNMENT_TYPE = 2;
    public static int RECTIFICATION_MEASURES_TYPE = 3;
    public static int PROBLEM_SUPERVISION_2_TYPE = 4;
    public static int CANCEL_A_NUMBER_TYPE = 5;



	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private java.lang.Integer id;
	/**附件路径地址*/
	@Excel(name = "附件路径地址", width = 15)
    @ApiModelProperty(value = "附件路径地址")
    private java.lang.String appendixPath;
	/**附件类型 1 问题录入 2问题分配 3整改措施 4问题督办(issuesProcessType=2)*/
	@Excel(name = "附件类型 1 问题录入 2问题分配 3整改措施 4问题督办(issuesProcessType=2) 5销号-问责处理", width = 15)
    @ApiModelProperty(value = "附件类型 1 问题录入 2问题分配 3整改措施 4问题督办(issuesProcessType=2)")
    private java.lang.Integer type;
	/**文件类型 0 无 1图片 2pdf 此字段不一定启用*/
	@Excel(name = "文件类型 0 无 1图片 2pdf 此字段不一定启用", width = 15)
    @ApiModelProperty(value = "文件类型 0 无 1图片 2pdf 此字段不一定启用")
    private java.lang.Integer fileType;
	/**问题ID*/
	@Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private java.lang.Long issueId;
    /**数据id，根据type录入不同值 type=5时录入部门id*/
	@Excel(name = "数据id，根据type录入不同值 type=5时录入部门id", width = 15)
    @ApiModelProperty(value = "数据id，根据type录入不同值 type=5时录入部门id")
    private java.lang.String dataId;
    /**任务id*/
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
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

    @Excel(name = "文件名称")
    @TableField(exist = false)
    private String fileName;

    @Excel(name = "文件地址")
    @TableField(exist = false)
    private String url;

    @Excel(name = "相对路径")
    @TableField(exist = false)
    private String relativePath;
}
