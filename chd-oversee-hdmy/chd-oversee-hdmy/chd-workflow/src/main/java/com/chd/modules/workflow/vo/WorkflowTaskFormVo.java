package com.chd.modules.workflow.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class WorkflowTaskFormVo {
    private String taskId;//任务节点ID
    private List<Map<String,Object>> taskDataList;//任务节点ID
    private String processId;//流程实例ID
    private String comment;//留言
    private String approveType;//
    private String delegateUserId;//委托用户ID
    private Map<String,Object> form;
    private String formId;//
    private String url;//
    //值1督办提醒标志，在督办部分分管领导审核时前端传入
    private Integer superintendFlag=0;

    private String rejectOrgId;//驳回部门id
}
