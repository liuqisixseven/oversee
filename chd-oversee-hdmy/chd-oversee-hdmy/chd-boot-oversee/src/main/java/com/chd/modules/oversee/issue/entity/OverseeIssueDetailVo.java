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
public class OverseeIssueDetailVo {
    private Long id;//主键
    @Excel(name="问题编号")
    private String num;//问题编号
    @Excel(name="问题标题")
    private String title;//标题
//    @Excel(name="问题副标题",width=50)
    @Excel(name="问题副标题")
    private String subtitle;//副标题
    @Excel(name = "问题描述")
    private String specificIssuesContent;//问题详情
    @Excel(name="问题来源",dictType = "issue_source")
    private Integer source;//来源 1 巡视 2 巡查 3 专项检查
    private String sourceSrc;//来源 1 巡视 2 巡查 3 专项检查
    @Excel(name = "问题分类（一级）")
    private String issueCategory;//问题大类
    @Excel(name = "问题分类（二级）")
    private String issueSubcategory;//问题小类
    @Excel(name="是否需要本部领导审核",readConverterExp = "-1=否,1=是")
    private Integer isCompanyLeadershipReview;//公司领导审核 -1 不需要 1 需要
    @Excel(name="是否需要本部督办",readConverterExp = "-1=否,1=是")
    private Integer isSupervise;//是否需要督办 -1 不需要 1 需要
    @Excel(name="是否需要会签",readConverterExp = "-1=否,1=是")
    private Integer isSign;//是否需要会签 -1 不需要 1 需要
    @Excel(name = "本部牵头部门")
    private String headquartersLeadDepartmentOrg;//本部牵头部门
    @Excel(name = "上报时间",dateFormat = "yyyy-MM-dd")
    private Date reportTime;//上报时间
    @Excel(name = "检查时间",dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date checkTime;//检查时间
    @Excel(name = "批准主体")
    private String approvalBody;//批准主体
    @Excel(name = "问题督办等级",dictType = "issue_severity")
    private Integer severity;//严重程度 0 普通 1 重要 2严重
    private String severitySrc;//严重程度 0 普通 1 重要 2严重
//    @Excel(name = "本部督办部门")
    private String supervisorOrg;//督办部门
    @Excel(name="本部督办部门经办人")
    private String supervisorUserName;//督办部门经办人user_id
    @Excel(name = "责任单位名称")
    private String responsibleUnitOrg;//责任单位
    @Excel(name = "责任单位牵头部门")
    private String responsibleLeadDepartmentOrg;//责任单位对应的牵头部门
//    @Excel(name = "责任单位责任部门")
    private String responsibleDeparts;//责任部门
    @Excel(name = "责任单位责任部门业务人员")
    private String responsibleOrgUserName;//责任单位责任部门业务人员
    @Excel(name = "问题整改措施")
    private String improveAction;//整改措施
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "问题整改计划完成时间",dateFormat = "yyyy-MM-dd")
    private Date expectTime;//预计完成时间
    @Excel(name="整改状态",dictType = "issue_submit_state")
    private Integer submitState;//提交状态 0 草稿 1提交
    @Excel(name = "整改情况说明")
    private String rectification;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "问题整改实际完成时间",dateFormat = "yyyy-MM-dd")
    private Date completedTime;//完成时间
    @Excel(name = "备注")
    private String remark;//备注


    private Long specificIssuesId;//具体问题id 优化具体问题大字段影响搜索 后续可能一个问题对应多个具体问题 则不使用此字段



    private String headquartersLeadDepartmentOrgId;//本部牵头部门id
    private String headquartersLeadDepartmentManagerUserId;//本部牵头部门经办人user_id
    private String responsibleUnitOrgId;//责任单位id
    private String responsibleLeadDepartmentOrgId;//责任单位对应的牵头部门id

    private String supervisorOrgIds;//督办部门ids
    private String supervisorManagerUserId;//督办部门负责人user_id


    private String reportUserId;//上报user_id

//    发起人 1 是
    private Integer isSponsor;

    //    发起督办权限 1 是
    private Integer initiateSupervisionAuthority;


    private Long issueCategoryId;//问题大类ID
    private Long issueSubcategoryId;//问题小类ID


    private String createUserId;//创建用户id
    private String updateUserId;//修改用户id
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//更新时间
    private Integer dataType;//数据状态 -1 无效 1 有效


    private Integer completedTimeout;//是否超期

    private String responsibleOrgIds;//责任部门ID








    private String reportUser;
    private String supervisorOrgId;
    private String workflowProcessId;



    /**
     * 问题细节
     */
    List<OverseeIssueSpecific> specificList;

    @TableField(exist = false)
    private List<OverseeIssueAppendix> overseeIssueAppendixList;

//    督办申请信息
    @TableField(exist = false)
    private Map<String,Object> superviseMapData;

    private List<IssuesSupervisor> issuesSupervisorList;
    /**
     * 问题附件列表
     */
    private List<OverseeIssueFile> fileList;

    private Map<String, Object> cancelANumberMap;

    @TableField(exist = false)
    private String issuesRectificationMeasureAppendixIds;

    @TableField(exist = false)
    private List<Map<String,Object>> relatedIssueDataList;

    @TableField(exist = false)
    private List<IssuesRectificationMeasure> issuesRectificationMeasureList;

    /** 主要责任部门IDs **/
    @TableField(exist = false)
    @Excel(name = "整改责任部门")
    private String responsibleMainOrgNames;

    /** 配合责任部门IDs **/
    @TableField(exist = false)
    @Excel(name = "配合整改部门")
    private String responsibleCoordinationOrgNames;

    @TableField(exist = false)
    @Excel(name = "本部督办部门")
    private String supervisorMainOrgNames;//督办主部门
    @TableField(exist = false)
    @Excel(name = "配合督办部门")
    private String supervisorCoordinationOrgNames;//督办主部门

    private String submitStateSrc;

    private String completedTimeoutSrc;


}
