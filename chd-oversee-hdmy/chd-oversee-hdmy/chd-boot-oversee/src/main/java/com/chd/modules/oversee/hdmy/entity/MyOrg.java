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
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Data
@TableName("my_org")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="my_org对象", description="my_org")
public class MyOrg implements Serializable {
    private static final long serialVersionUID = 1L;

	/**orgId*/
	@Excel(name = "orgId", width = 15)
    @ApiModelProperty(value = "orgId")
    @TableId(type = IdType.NONE)
    private java.lang.String orgId;
	/**orgName*/
	@Excel(name = "orgName", width = 15)
    @ApiModelProperty(value = "orgName")
    private java.lang.String orgName;
	/**orgShortName*/
	@Excel(name = "orgShortName", width = 15)
    @ApiModelProperty(value = "orgShortName")
    private java.lang.String orgShortName;
	/**zzjs*/
	@Excel(name = "zzjs", width = 15)
    @ApiModelProperty(value = "zzjs")
    private java.lang.String zzjs;
	/**jtCode*/
	@Excel(name = "jtCode", width = 15)
    @ApiModelProperty(value = "jtCode")
    private java.lang.String jtCode;
	/**jtCode2*/
	@Excel(name = "jtCode2", width = 15)
    @ApiModelProperty(value = "jtCode2")
    private java.lang.String jtCode2;
	/**managerId*/
	@Excel(name = "managerId", width = 15)
    @ApiModelProperty(value = "managerId")
    private java.lang.String managerId;
	/**areaCode*/
	@Excel(name = "areaCode", width = 15)
    @ApiModelProperty(value = "areaCode")
    private java.lang.String areaCode;
	/**znCode*/
	@Excel(name = "znCode", width = 15)
    @ApiModelProperty(value = "znCode")
    private java.lang.String znCode;
	/**streetAddress*/
	@Excel(name = "streetAddress", width = 15)
    @ApiModelProperty(value = "streetAddress")
    private java.lang.String streetAddress;
	/**postCode*/
	@Excel(name = "postCode", width = 15)
    @ApiModelProperty(value = "postCode")
    private java.lang.String postCode;
	/**国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济*/
	@Excel(name = "国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济", width = 15)
    @ApiModelProperty(value = "国有经济、集体经济、私营经济、有限责任公司、联营经济、股份合作、外商投资、港澳台投资、其他经济")
    private java.lang.String jjType;
	/**农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业*/
	@Excel(name = "农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业", width = 15)
    @ApiModelProperty(value = "农、林、牧、渔业，采掘业，制造业，电力、煤气及水的生成和供应业，建筑业，地址勘查业、水利管理业，交通运输仓储业及邮电通信业，批发和零售贸易、餐饮业，金融保险业，房地产业，社会服务业，卫生、体育和社会福利业，教育文化艺术及广电业，科学研究和综合技术服务业，国家机关政党机关和社会团体，其他行业")
    private java.lang.String hyType;
	/**createTime*/
    @ApiModelProperty(value = "createTime")
    private java.lang.String createTime;
	/**area*/
	@Excel(name = "area", width = 15)
    @ApiModelProperty(value = "area")
    private java.math.BigDecimal area;
	/**employersNumber*/
	@Excel(name = "employersNumber", width = 15)
    @ApiModelProperty(value = "employersNumber")
    private java.math.BigDecimal employersNumber;
	/**gdValue*/
	@Excel(name = "gdValue", width = 15)
    @ApiModelProperty(value = "gdValue")
    private java.math.BigDecimal gdValue;
	/**anIncome*/
	@Excel(name = "anIncome", width = 15)
    @ApiModelProperty(value = "anIncome")
    private java.math.BigDecimal anIncome;
	/**anProfit*/
	@Excel(name = "anProfit", width = 15)
    @ApiModelProperty(value = "anProfit")
    private java.math.BigDecimal anProfit;
	/**mainProducts*/
	@Excel(name = "mainProducts", width = 15)
    @ApiModelProperty(value = "mainProducts")
    private java.lang.String mainProducts;
	/**生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业*/
	@Excel(name = "生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业", width = 15)
    @ApiModelProperty(value = "生产型企业，经营型企业，基建型企业，发展型企业，电煤供应企业")
    private java.lang.String constructionType;
	/**煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务*/
	@Excel(name = "煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务", width = 15)
    @ApiModelProperty(value = "煤炭业务，发电 业务，化工业务，港口业务，航运业务，营销业务")
    private java.lang.String businessType;
	/**parentOrgId*/
	@Excel(name = "parentOrgId", width = 15)
    @ApiModelProperty(value = "parentOrgId")
    private java.lang.String parentOrgId;
	/**标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推*/
	@Excel(name = "标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推", width = 15)
    @ApiModelProperty(value = "标识组织节点所在的级别，如一级为公司，二级为部门，三级为处室等，依此类推")
    private java.math.BigDecimal orgLevel;
	/**upperSupervisorId*/
	@Excel(name = "upperSupervisorId", width = 15)
    @ApiModelProperty(value = "upperSupervisorId")
    private java.lang.String upperSupervisorId;
	/**upperSupervisorName*/
	@Excel(name = "upperSupervisorName", width = 15)
    @ApiModelProperty(value = "upperSupervisorName")
    private java.lang.String upperSupervisorName;
	/**displayOrder*/
	@Excel(name = "displayOrder", width = 15)
    @ApiModelProperty(value = "displayOrder")
    private java.lang.String displayOrder;
	/**description*/
	@Excel(name = "description", width = 15)
    @ApiModelProperty(value = "description")
    private java.lang.String description;
	/**1－增加，2－删除，3－修改*/
	@Excel(name = "1－增加，2－删除，3－修改", width = 15)
    @ApiModelProperty(value = "1－增加，2－删除，3－修改")
    private java.math.BigDecimal operationCode;
	/**同步时间(时间格式：yyyy-MM-dd HH:mm:ss*/
	@Excel(name = "同步时间(时间格式：yyyy-MM-dd HH:mm:ss", width = 15)
    @ApiModelProperty(value = "同步时间(时间格式：yyyy-MM-dd HH:mm:ss")
    private java.lang.String synTime;
	/**1－已处理，0－未处理*/
	@Excel(name = "1－已处理，0－未处理", width = 15)
    @ApiModelProperty(value = "1－已处理，0－未处理")
    private java.math.BigDecimal synFlag;
	/**默认值0，当一次处理成功则仍然为0；处理失败则加1*/
	@Excel(name = "默认值0，当一次处理成功则仍然为0；处理失败则加1", width = 15)
    @ApiModelProperty(value = "默认值0，当一次处理成功则仍然为0；处理失败则加1")
    private java.math.BigDecimal retryTimes;
	/**gkOrgId*/
	@Excel(name = "gkOrgId", width = 15)
    @ApiModelProperty(value = "gkOrgId")
    private java.lang.String gkOrgId;
	/**jsm*/
	@Excel(name = "jsm", width = 15)
    @ApiModelProperty(value = "jsm")
    private java.lang.String jsm;
    /**path 组织路径 ,号分隔*/
    @Excel(name = "path", width = 15)
    @ApiModelProperty(value = "path")
    private java.lang.String path;


    /**orgName 排序 越小越靠前*/
    @TableField(exist = false)
    @Excel(name = "sort", width = 15)
    @ApiModelProperty(value = "sort 排序 越小越靠前")
    private Integer sort;
}
