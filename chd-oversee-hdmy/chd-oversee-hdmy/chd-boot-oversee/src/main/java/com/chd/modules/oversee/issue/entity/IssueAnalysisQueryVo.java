package com.chd.modules.oversee.issue.entity;

import lombok.Data;

import java.util.Date;

/**
 * 统计查询
 */
@Data
public class IssueAnalysisQueryVo {
    private Date startTime;
    private Date endTime;
    private String userId;
    private Integer dateType=1;//1-日，2-月，3-年
    private String analysisName;//统计项
    private Integer state;//统计状态：空值为所有上报的记录（除未上报之外的所有数据）；100全部（包含未上报),0未上报，1上报未完成，2上报已完成

    private Date expectTimeGt; //大于预计完成时间
    private Date expectTimeLt; // 小于预计完成时间
}
