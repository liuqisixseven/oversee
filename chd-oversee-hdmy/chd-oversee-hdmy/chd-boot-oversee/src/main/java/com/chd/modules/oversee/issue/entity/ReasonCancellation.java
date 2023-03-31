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
@TableName("reason_cancellation")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="reason_cancellation对象", description="reason_cancellation")
public class ReasonCancellation implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**任务id*/
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
	/**销号理由*/
	@Excel(name = "销号理由", width = 15)
    @ApiModelProperty(value = "销号理由")
    private String reason;
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
    private List<AccountabilityHandling> accountabilityHandlingList;

    @TableField(exist = false)
    private List<RectifyViolations> rectifyViolationsList;

    @TableField(exist = false)
    private List<RecoverFunds> recoverFundsList;

    @TableField(exist = false)
    private List<ImproveRegulations> improveRegulationsList;

    @TableField(exist = false)
    private BigDecimal recoveryIllegalDisciplinaryFundsSumBigDecimal;

    @TableField(exist = false)
    private BigDecimal recoverDamagesSumBigDecimal;

    @TableField(exist = false)
    private String departName;


}
