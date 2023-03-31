package com.chd.modules.oversee.issue.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.PageVo;
import com.chd.common.core.text.ExcelHeader;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class OverseeIssueQueryVo<T> extends PageVo {

    private Long id;//主键
    private String num;//问题编号
    private String title;//标题
    private String subtitle;//副标题
    private Long specificIssuesId;//具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段
    private Integer submitState;//提交状态 0 草稿 1提交
    private List<Integer> submitStateList;//提交状态 0 草稿 1提交
    private Integer source;//来源 0 巡视 1 巡查 2 专项检查
    private Integer isCompanyLeadershipReview;//公司领导审核 -1 不需要 1 需要
    private String headquartersLeadDepartmentOrgId;//本部牵头部门id
    private String headquartersLeadDepartmentManagerUserId;//本部牵头部门经办人user_id
    private String responsibleUnitOrgId;//责任单位id
    private String responsibleOrgId;//责任单位责任部门id
    private String responsibleLeadDepartmentOrgId;//责任单位对应的牵头部门id
    private Integer isSupervise;//是否需要督办 -1 不需要 1 需要
    private String supervisorOrgIds;//督办部门ids
    private String supervisorManagerUserId;//督办部门经办人user_id
    private Date reportTime;//上报时间
    private String reportUserId;//上报user_id
    private Date checkTime;//检查时间
    private String approvalBody;//批准主体
    private Long issueCategoryId;//问题大类ID
    private Long issueSubcategoryId;//问题小类ID
    private Integer severity;//严重程度 0 普通 1 重要 2严重
    private Integer isSign;//是否需要会签 -1 不需要 1 需要
    private String createUserId;//创建用户id
    private String selectUserId;//创建用户id
    private String updateUserId;//修改用户id
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Integer dataType;//数据状态 -1 无效 1 有效
    private Date completedTime;//完成时间
    private Date expectTime;//预计完成时间
    private Integer completedTimeout;//是否超期

    private String headquartersLeadDepartmentOrg;//本部牵头部门
    private String responsibleUnitOrg;//责任单位
    private String responsibleLeadDepartmentOrg;//责任单位对应的牵头部门
    private String supervisorOrg;//督办部门
    private String specificIssuesContent;//问题详情
    private String issueCategory;//问题大类
    private String issueSubcategory;//问题小类
    private String reportUser;

    private Date startTime;
    private Date endTime;
    private String supervisorOrgId;
    private String selectOrgIds;
    private String selectOrgId;
    private Boolean exportAll=false;

    private List<String> checkTimeSection;
    private String checkTimeSectionSrc;
    private List<String> completedTimeSection;

    private String checkTimeGt;
    private String checkTimeLt;

    private String completedTimeGt;
    private String completedTimeLt;

    private Date expectTimeGt;
    private Date expectTimeLt;

    private Integer selectType;
    private String selectTypes;


    private String loginId;//标题

    private String ids;//主键

    private List<Long> idArray;//主键

    private Integer orderByType;

    private java.util.Date updateTimeGt;

    private java.util.Date updateTimeLt;

    private Boolean isSelect;

    private List<ExcelHeader> columnAlls;

    private String columnAllsSrc;




}
