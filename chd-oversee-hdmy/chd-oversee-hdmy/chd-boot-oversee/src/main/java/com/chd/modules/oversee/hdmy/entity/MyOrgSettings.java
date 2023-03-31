package com.chd.modules.oversee.hdmy.entity;

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

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Data
@TableName("my_org_settings")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="my_org_settings对象", description="my_org_settings")
public class MyOrgSettings implements Serializable {
    private static final long serialVersionUID = 1L;

	/**orgId*/
	@Excel(name = "orgId", width = 15)
    @ApiModelProperty(value = "orgId")
    @TableId(type = IdType.NONE)
    private String orgId;
	/**orgName 排序 越小越靠前*/
	@Excel(name = "sort", width = 15)
    @ApiModelProperty(value = "sort 排序 越小越靠前")
    private Integer sort;
	/**isShow 是否展示 -1 不展示 1 展示*/
	@Excel(name = "isShow 是否展示 -1 不展示 1 展示", width = 15)
    @ApiModelProperty(value = "isShow 是否展示 -1 不展示 1 展示")
    private Integer isShow;
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

    /**orgName*/
    @TableField(exist = false)
    @Excel(name = "orgName", width = 15)
    @ApiModelProperty(value = "orgName")
    private java.lang.String orgName;

    /**orgShortName*/
    @TableField(exist = false)
    @Excel(name = "orgShortName", width = 15)
    @ApiModelProperty(value = "orgShortName")
    private java.lang.String orgShortName;
}
