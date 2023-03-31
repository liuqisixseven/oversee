package com.chd.modules.oversee.issue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chd.common.util.JsToolUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @Description: accountability_handling
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Data
@TableName("accountability_handling")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="accountability_handling对象", description="accountability_handling")
public class AccountabilityHandling implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
    /**任务id*/
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
    /**uuid*/
    @Excel(name = "uuid", width = 15)
    @ApiModelProperty(value = "uuid")
    private String uuid;
	/**形态大类*/
	@Excel(name = "形态大类", width = 15)
    @ApiModelProperty(value = "形态大类")
    private Integer morphologicalCategoriesId;
	/**形态小类*/
	@Excel(name = "形态小类", width = 15)
    @ApiModelProperty(value = "形态小类")
    private Integer morphologicalSubclassId;
	/**问责日期*/
	@Excel(name = "问责日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "问责日期")
    private Date accountabilityDate;
	/**问责主体*/
	@Excel(name = "问责主体", width = 15)
    @ApiModelProperty(value = "问责主体")
    private String accountableSubject;
	/**是否组织处理 -1 不是 1是*/
	@Excel(name = "是否组织处理 -1 不是 1是", width = 15)
    @ApiModelProperty(value = "是否组织处理 -1 不是 1是")
    private Integer tissueProcessing;
	/**是否立案 -1 不是 1是*/
	@Excel(name = "是否立案 -1 不是 1是", width = 15)
    @ApiModelProperty(value = "是否立案 -1 不是 1是")
    private Integer fileACase;
	/**是否移交司法 -1 不是 1是*/
	@Excel(name = "是否移交司法 -1 不是 1是", width = 15)
    @ApiModelProperty(value = "是否移交司法 -1 不是 1是")
    private Integer transferOfJustice;
	/**问题ID*/
	@Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private Long issueId;
	/**责任单位的责任部门id*/
	@Excel(name = "责任单位的责任部门id", width = 15)
    @ApiModelProperty(value = "责任单位的责任部门id")
    private String orgId;
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
    private List<OverseeIssueAppendix> files;

    @TableField(exist = false)
    private String jobTitle;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String morphologicalCategoriesName;

    @TableField(exist = false)
    private String morphologicalSubclassName;



}
