package com.chd.modules.oversee.hdmy.entity;

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
 * @Description: my_user
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Data
@TableName("my_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="my_user对象", description="my_user")
public class MyUser implements Serializable {
    private static final long serialVersionUID = 1L;

	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    @TableId(type = IdType.NONE)
    private java.lang.String userId;
	/**userName*/
	@Excel(name = "userName", width = 15)
    @ApiModelProperty(value = "userName")
    private java.lang.String userName;
	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
    private java.lang.String password;
	/**promptQuestion*/
	@Excel(name = "promptQuestion", width = 15)
    @ApiModelProperty(value = "promptQuestion")
    private java.lang.String promptQuestion;
	/**promptAnswer*/
	@Excel(name = "promptAnswer", width = 15)
    @ApiModelProperty(value = "promptAnswer")
    private java.lang.String promptAnswer;
	/**sex*/
	@Excel(name = "sex", width = 15)
    @ApiModelProperty(value = "sex")
    private java.lang.String sex;
	/**employerNumber*/
	@Excel(name = "employerNumber", width = 15)
    @ApiModelProperty(value = "employerNumber")
    private java.lang.String employerNumber;
	/**telNumber*/
	@Excel(name = "telNumber", width = 15)
    @ApiModelProperty(value = "telNumber")
    private java.lang.String telNumber;
	/**mobile*/
	@Excel(name = "mobile", width = 15)
    @ApiModelProperty(value = "mobile")
    private java.lang.String mobile;
	/**fasNumber*/
	@Excel(name = "fasNumber", width = 15)
    @ApiModelProperty(value = "fasNumber")
    private java.lang.String fasNumber;
	/**mail*/
	@Excel(name = "mail", width = 15)
    @ApiModelProperty(value = "mail")
    private java.lang.String mail;
	/**titile*/
	@Excel(name = "titile", width = 15)
    @ApiModelProperty(value = "titile")
    private java.lang.String titile;
	/**1－可用，0－不可用*/
	@Excel(name = "1－可用，0－不可用", width = 15)
    @ApiModelProperty(value = "1－可用，0－不可用")
    private java.lang.Integer enable;
	/**createTime*/
    @ApiModelProperty(value = "createTime")
    private java.lang.String createTime;
	/**orgId*/
	@Excel(name = "orgId", width = 15)
    @ApiModelProperty(value = "orgId")
    private java.lang.String orgId;
	/**1-正职，2-副职，3-员工*/
	@Excel(name = "1-正职，2-副职，3-员工", width = 15)
    @ApiModelProperty(value = "1-正职，2-副职，3-员工")
    private java.lang.String orgDuty;
	/**员工在部门的排序（3位编码，排序最优先为000）*/
	@Excel(name = "员工在部门的排序（3位编码，排序最优先为000）", width = 15)
    @ApiModelProperty(value = "员工在部门的排序（3位编码，排序最优先为000）")
    private java.lang.String displayOrder;
	/**用户属于其他部门ID，如果存在多个部门，以“，”分隔*/
	@Excel(name = "用户属于其他部门ID，如果存在多个部门，以“，”分隔", width = 15)
    @ApiModelProperty(value = "用户属于其他部门ID，如果存在多个部门，以“，”分隔")
    private java.lang.String otherOrgId;
	/**1－增加，2－删除，3－修改，4—修改密码*/
	@Excel(name = "1－增加，2－删除，3－修改，4—修改密码", width = 15)
    @ApiModelProperty(value = "1－增加，2－删除，3－修改，4—修改密码")
    private java.lang.Integer operationCode;
	/**格式为YYYY-MM-DD hh:mm:ss*/
	@Excel(name = "格式为YYYY-MM-DD hh:mm:ss", width = 15)
    @ApiModelProperty(value = "格式为YYYY-MM-DD hh:mm:ss")
    private java.lang.String synTime;
	/**1-已处理，0-未处理*/
	@Excel(name = "1-已处理，0-未处理", width = 15)
    @ApiModelProperty(value = "1-已处理，0-未处理")
    private java.lang.Integer synFlag;
	/**默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1*/
	@Excel(name = "默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1", width = 15)
    @ApiModelProperty(value = "默认值为0，当一次处理成功则仍然为0；当处理失败时累计加1")
    private java.lang.Integer retryTime;

//    /**orgName*/
//    @TableField(exist = false)
//    @Excel(name = "orgName", width = 15)
//    @ApiModelProperty(value = "orgName")
//    private java.lang.String orgName;
//
//
//    /**orgShortName*/
//    @TableField(exist = false)
//    @Excel(name = "orgShortName", width = 15)
//    @ApiModelProperty(value = "orgShortName")
//    private java.lang.String orgShortName;
//
//    /**zzjs*/
//    @TableField(exist = false)
//    @Excel(name = "zzjs", width = 15)
//    @ApiModelProperty(value = "zzjs")
//    private java.lang.String zzjs;




}
