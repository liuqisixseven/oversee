package com.chd.modules.oversee.issue.entity;

import com.chd.modules.oversee.hdmy.entity.MyOrg;
import lombok.Data;

import java.util.List;

@Data
public class IssueAnalysisInfoVo {

    private Long total;//总数量
    private Long completed;//已完成数量
    private List<IssueAnalysisItemVo> source;//按来源
    private List<IssueAnalysisItemVo> category;//按大类
    private List<IssueAnalysisItemVo> issueSubcategoryList;//按小类
    private List<IssueAnalysisItemVo> fullCategory;//按大小类
    private List<IssueAnalysisItemVo> checkTime;//按巡视巡察时间
    private List<IssueAnalysisItemVo> completedTime;//按完成时间
    private List<IssueAnalysisItemVo> createTime;//按创建时间
    private List<IssueAnalysisItemVo> reportTime;//按上报时间

    private List<IssueAnalysisItemVo> completeList;
    private List<IssueAnalysisItemVo> allList;
    private List<IssueAnalysisItemVo> undoneList;


    private List<IssueAnalysisItemVo> submitStateList;



    private List<IssueAnalysisItemVo> superviseList;

    private List<IssueAnalysisItemVo> remainingProblems; //剩余问题

    private List<IssueAnalysisItemVo> overdueSituationList; //剩余问题

    private int isAll;
    private MyOrg parentOrganizeCompany;

}
