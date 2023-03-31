package com.chd.modules.oversee.issue.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Data
@TableName("oversee_issue")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oversee_issue对象", description="oversee_issue")
public class OverseeIssue implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;
	/**问题编号*/
	@Excel(name = "问题编号", width = 15)
    @ApiModelProperty(value = "问题编号")
    private java.lang.String num;
	/**标题*/
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String title;
	/**副标题*/
	@Excel(name = "副标题", width = 15)
    @ApiModelProperty(value = "副标题")
    private java.lang.String subtitle;
	/**具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段*/
//	@Excel(name = "具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段", width = 15)
    @ApiModelProperty(value = "具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段")
    private java.lang.Integer specificIssuesId;
    @Excel(name = "具体问题", width = 15)
    @TableField(exist = false)
    /**具体问题内容*/
    private java.lang.String specificIssuesContent;
	/**提交状态 0 草稿 1提交*/
	@Excel(name = "提交状态", width = 15)
    @ApiModelProperty(value = "提交状态 0 草稿 1提交")
    private java.lang.Integer submitState;
	/**来源 1 巡视 2 巡查 3 专项检查*/
	@Excel(name = "来源 1 巡视 2 巡查 3 专项检查", width = 15)
    @ApiModelProperty(value = "来源 1 巡视 2 巡查 3 专项检查")
    private java.lang.Integer source;
    /**公司领导审核 -1 不需要 1 需要*/
	@Excel(name = "公司领导审核 -1 不需要 1 需要", width = 15)
    @ApiModelProperty(value = "公司领导审核 -1 不需要 1 需要")
    private java.lang.Integer isCompanyLeadershipReview;
	/**本部牵头部门id*/
	@Excel(name = "本部牵头部门id", width = 15)
    @ApiModelProperty(value = "本部牵头部门id")
    private java.lang.String headquartersLeadDepartmentOrgId;
	/**本部牵头部门经办人user_id*/
	@Excel(name = "本部牵头部门经办人user_id", width = 15)
    @ApiModelProperty(value = "本部牵头部门经办人user_id")
    private java.lang.String headquartersLeadDepartmentManagerUserId;
	/**责任单位id*/
	@Excel(name = "责任单位id", width = 15)
    @ApiModelProperty(value = "责任单位id")
    private java.lang.String responsibleUnitOrgId;
    /**责任单位对应的牵头部门id*/
	@Excel(name = "责任单位对应的牵头部门id", width = 15)
    @ApiModelProperty(value = "责任单位对应的牵头部门id")
    private java.lang.String responsibleLeadDepartmentOrgId;
    /**责任单位对应的牵头部门经办人id*/
    @Excel(name = "责任单位对应的牵头部门经办人id", width = 15)
    @ApiModelProperty(value = "责任单位对应的牵头部门经办人id")
    private java.lang.String responsibleLeadDepartmentUserId;
	/**督办部门ids*/
	@Excel(name = "督办部门ids", width = 15)
    @ApiModelProperty(value = "督办部门ids")
    private java.lang.String supervisorOrgIds;
	/**督办部门经办人user_id*/
	@Excel(name = "督办部门经办人user_id", width = 15)
    @ApiModelProperty(value = "督办部门经办人user_id")
    private java.lang.String supervisorManagerUserId;
	/**上报时间*/
	@Excel(name = "上报时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "上报时间")
    private java.util.Date reportTime;
	/**上报user_id*/
	@Excel(name = "上报user_id", width = 15)
    @ApiModelProperty(value = "上报user_id")
    private java.lang.String reportUserId;
	/**检查时间*/
	@Excel(name = "检查时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "检查时间")
    private java.util.Date checkTime;
	/**批准主体*/
	@Excel(name = "批准主体", width = 15)
    @ApiModelProperty(value = "批准主体")
    private java.lang.String approvalBody;
	/**问题大类ID*/
	@Excel(name = "问题大类ID", width = 15)
    @ApiModelProperty(value = "问题大类ID")
    private java.lang.Integer issueCategoryId;

	/**问题小类ID*/
	@Excel(name = "问题小类ID", width = 15)
    @ApiModelProperty(value = "问题小类ID")
    private java.lang.Integer issueSubcategoryId;
	/**严重程度 0 普通 1 重要 2严重*/
	@Excel(name = "严重程度 0 普通 1 重要 2严重", width = 15)
    @ApiModelProperty(value = "严重程度 0 普通 1 重要 2严重")
    private java.lang.Integer severity;
	/**是否需要督办 -1 不需要 1 需要*/
	@Excel(name = "是否需要督办 -1 不需要 1 需要", width = 15)
    @ApiModelProperty(value = "是否需要督办 -1 不需要 1 需要")
    private java.lang.Integer isSupervise;
	/**是否需要会签 -1 不需要 1 需要*/
	@Excel(name = "是否需要会签 -1 不需要 1 需要", width = 15)
    @ApiModelProperty(value = "是否需要会签 -1 不需要 1 需要")
    private java.lang.Integer isSign;
    /** 完成时间 **/


    @ApiModelProperty(value = "完成时间")
    private java.util.Date completedTime;
    /** 预计完成时间 **/
    private Date expectTime;
    /** 备注 **/
    private String remark;

    /** 责任部门IDs **/
    private String responsibleOrgIds;

    /** 主要责任部门IDs **/
    @TableField(exist = false)
    private String responsibleMainOrgIds;

    /** 配合责任部门IDs **/
    @TableField(exist = false)
    private String responsibleCoordinationOrgIds;

    @ApiModelProperty(value = "关联问题ids")
    private String relatedIssueIds;

    @ApiModelProperty(value = "流程实例id")
    private java.lang.String processId;

    @ApiModelProperty(value = "流程定义id")
    private java.lang.String processDefId;

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

    @ApiModelProperty(value = "问题归档状态 0 未归档 1 已归档")
    private java.lang.Integer isDocument;
    @TableField(exist = false)
    private String createUserRealName;
    @TableField(exist = false)
    private String updateUserRealName;
    @TableField(exist = false)
    private String issueCategoryName;
    @TableField(exist = false)
    private String issueSubcategoryName;
    @TableField(exist = false)
    private java.lang.String headquartersLeadDepartmentOrgName;
    @TableField(exist = false)
    private java.lang.String responsibleLeadDepartmentOrgName;
    @TableField(exist = false)
    private java.lang.String headquartersLeadDepartmentManagerUserName;
    @TableField(exist = false)
    private java.lang.String responsibleUnitOrgName;
//    责任单位责任部门
    @TableField(exist = false)
    private java.lang.String responsibleUnitResponsibleDepartmentOrgName;
    @TableField(exist = false)
    private List<OverseeIssueAppendix> overseeIssueAppendixList;
    @TableField(exist = false)
    private java.lang.String processClassificationValue;
    @TableField(exist = false)
    private Map<String,Object> superviseMapData;
    @TableField(exist = false)
    private List<IssuesAllocation> issuesAllocations;
    @TableField(exist = false)
    private Map<String, Object> cancelANumberMap;





    @TableField(exist = false)
    private List<Map<String,Object>> relatedIssueDataList;

    @TableField(exist = false)
    private List<IssuesSupervisor> issuesSupervisorList; //督办部门数据





}
