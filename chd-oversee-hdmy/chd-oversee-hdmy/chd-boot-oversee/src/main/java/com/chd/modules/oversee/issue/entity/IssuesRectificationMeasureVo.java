package com.chd.modules.oversee.issue.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.chd.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 巡查问题详情
 */
@Data
public class IssuesRectificationMeasureVo {


    @Excel(name = "问题整改措施")
    private String improveAction;//整改措施
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "问题整改计划完成时间",dateFormat = "yyyy-MM-dd")
    private Date expectTime;//预计完成时间

}
