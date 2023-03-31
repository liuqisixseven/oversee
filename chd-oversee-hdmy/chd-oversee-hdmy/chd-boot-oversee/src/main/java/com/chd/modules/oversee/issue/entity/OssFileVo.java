package com.chd.modules.oversee.issue.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chd.common.system.base.entity.JeecgEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: oss云存储实体类
 *
 */
@Data
@TableName("oss_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OssFileVo extends JeecgEntity {

	private static final long serialVersionUID = 1L;

	@Excel(name = "文件名称")
	private String fileName;

	@Excel(name = "文件地址")
	private String url;

	@Excel(name = "相对路径")
	private String relativePath;

	@Excel(name = "关联问题id列表，以逗号分隔")
	private String issueIds;
	@Excel(name = "数据类型，1表示问题归档类型")
	private Integer type;
	@Excel(name = "数据所属公司")
	private String responsibleUnitOrgName;

	/**责任单位id*/
	@Excel(name = "责任单位id", width = 15)
	@ApiModelProperty(value = "责任单位id")
	private String responsibleUnitOrgId;
}
