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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Data
@TableName("oversee_issue_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oversee_issue_role对象", description="oversee_issue_role")
public class OverseeIssueRole implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int ROLE_TYPE_USER = 1;
    public static final int ROLE_TYPE_DEPART = 2;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**问题ID*/
    @Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private Long issueId;
	/**角色对应数据id*/
	@Excel(name = "角色对应数据id", width = 15)
    @ApiModelProperty(value = "角色对应数据id")
    private String dataId;
    /**角色类型 1 用户 2 部门*/
    @Excel(name = "角色类型 1 用户 2 部门", width = 15)
    @ApiModelProperty(value = "角色类型 1 用户 2 部门")
    private Integer roleType;
	/**来源 暂不使用*/
	@Excel(name = "来源 暂不使用", width = 15)
    @ApiModelProperty(value = "来源 暂不使用")
    private String source;
    /**角色 暂不使用 :EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员*/
//    @Excel(name = "角色 LedHeadquarters 本部牵头  Supervised_Headquarters 本部督办 ResponsibleLeader 责任牵头部门  responsibility责任单位责任部门", width = 15)
    @Excel(name = "EXECUTOR-经办人,SECRETARY-书记,SPECIALIST-业务员", width = 15)
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


}
