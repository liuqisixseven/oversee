package com.chd.modules.oversee.issue.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 上报问题内容
 * @author ljc
 */
@Data
public class OverseeIssueSpecific implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;//主键
    private Long issueId;//问题表主键
    private Integer issuesRectificationMeasureId;//主键
    private String userId;//用户id
    private String orgId;//部门id
    private List<String> orgIdList;//部门ids
    private Integer specificType;//类型
    private String content;//数据
    private Date expectCorrectTime;//预计整改完成时间
    private String files;//附件
    private String createBy;//创建人
    private Date createTime;//创建时间
    private String updateBy;//更新人
    private Date updateTime;//更新时间


}
