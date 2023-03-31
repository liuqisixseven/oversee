package com.chd.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chd.common.system.base.entity.JeecgEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: oss云存储实体类
 *
 */
@Data
@TableName("oss_file_issue")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OssFileIssue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Excel(name = "id")
	private Long id;

	@Excel(name = "oss_file id")
	private String ossFileId;

	@Excel(name = "issueId")
	private Long issueId;

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
