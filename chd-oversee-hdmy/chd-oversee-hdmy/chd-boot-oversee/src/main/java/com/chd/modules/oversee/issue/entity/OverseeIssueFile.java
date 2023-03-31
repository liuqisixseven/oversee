package com.chd.modules.oversee.issue.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OverseeIssueFile {
    private Long id;//id
    private Long issueId;//问题ID
    private String userId;//用户id
    private Integer specificType;//归属业务类型:0-问题上报，1-整改措施
    private String taskId;//任务id
    private String files;//附件
    private String createBy;//创建者
    private Date createTime;//创建时间
    private String updateBy;//更新者
    private Date updateTime;//更新时间
    private Integer dataType;//数据状态 -1 无效 1 有效
}
