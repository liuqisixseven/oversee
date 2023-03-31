package com.chd.modules.oversee.issue.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("oversee_issue_flow_org")
public class OverseeIssueFlowOrg {
    private String id;//ID
    private String flowUserId;//角色编码
    private String orgId;//组织架构ID
    private String name;//节点名称
    private String remark;//备注
    private String createBy;//创建者
    private Date createTime;//创建时间
    private String updateBy;//更新者
    private Date updateTime;//更新时间
}
