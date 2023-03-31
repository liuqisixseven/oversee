package com.chd.modules.oversee.issue.entity;

import lombok.Data;

@Data
public class IssueAnalysisItemVo {
    private String name;
    private String subName;
    private Long value;
    //完成数量
    private Long completeCount;
    //未完成整改数量
    private Long undoneCount;
    //督办数量
    private Long superviseCount;


}
