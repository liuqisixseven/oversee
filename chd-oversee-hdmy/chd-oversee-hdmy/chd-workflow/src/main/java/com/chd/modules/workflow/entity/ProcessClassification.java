package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("process_classification")
public class ProcessClassification {

    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;
    /**流程绑定名称*/
    @Excel(name = "流程绑定名称", width = 15)
    @ApiModelProperty(value = "流程绑定名称")
    private java.lang.String name;
    /**流程定义值*/
    @Excel(name = "流程分类值", width = 15)
    @ApiModelProperty(value = "流程分类值")
    private java.lang.String value;

    /**流程类别 默认是问题 0问题上报流程*/
    @Excel(name = "流程类别 默认是问题 0问题上报流程", width = 15)
    @ApiModelProperty(value = "流程类别 默认是问题 0问题上报流程")
    private java.lang.Integer type;
    /**排序 越小越靠前*/
    @Excel(name = "排序 越小越靠前", width = 15)
    @ApiModelProperty(value = "排序 越小越靠前")
    private java.lang.Integer sort;
    /**子类别 问题上报流程默认绑定来源*/
    @Excel(name = "子类别 问题上报流程默认绑定来源", width = 15)
    @ApiModelProperty(value = "子类别 问题上报流程默认绑定来源")
    private java.lang.String subcategory;



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


    @TableField(exist = false)
    private String createUserRealName;
    @TableField(exist = false)
    private String updateUserRealName;

}
