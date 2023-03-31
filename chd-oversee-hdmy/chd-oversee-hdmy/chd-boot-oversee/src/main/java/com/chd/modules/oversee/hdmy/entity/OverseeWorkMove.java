package com.chd.modules.oversee.hdmy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Data
@TableName("oversee_work_move")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OverseeWorkMove对象", description="OverseeWorkMove")
public class OverseeWorkMove implements Serializable {
    private static final long serialVersionUID = 1L;
	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;
	/**提交状态 0 草稿 1提交*/
    @ApiModelProperty(value = "状态1有效")
    private Integer status;
	/**督办部门经办人user_id*/
    @ApiModelProperty(value = "工作移交人user_id")
    private String fromUserId;
	/**上报user_id*/
    @ApiModelProperty(value = "工作被移交人user_id")
    private String toUserId;
	/**创建用户id*/
    @ApiModelProperty(value = "创建用户id")
    private String createUserId;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
