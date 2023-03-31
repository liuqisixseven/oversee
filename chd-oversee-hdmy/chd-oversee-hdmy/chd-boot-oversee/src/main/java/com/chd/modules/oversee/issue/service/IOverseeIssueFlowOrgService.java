package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueFlowOrg;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

public interface IOverseeIssueFlowOrgService {

    /**
     * 获取记录用户变量
     * @param userTasks
     * @param category
     * @param bizId
     * @param startUserId
     * @return
     */
    List<WorkflowUserTaskVo> getBizVariableUser(List<WorkflowUserTaskVo> userTasks, String category, String bizId, String startUserId);

    List<WorkflowUserVo> getOverseeIssueTaskOrgUser(OverseeIssue overseeIssue, String variableId);


    Map<String, List<WorkflowDepart>> getBizDepartVariables(String category, String bizId, String startUserId);

    /**
     * 获取流程变量所指的机构记录
     * @param orgId
     * @param variableId
     * @return
     */
    OverseeIssueFlowOrg getOverseeIssueFlowOrg(String orgId, String variableId);

    /**
     * 获取问题上报变量所指的机构
     * @param overseeIssue
     * @param variableId
     * @return
     */
    String getOverseeIssueOrgIds(OverseeIssue overseeIssue,String variableId);

//    void taskFormData2Biz(String bizId, WorkflowTaskVo task, WorkflowUserVo user, WorkflowTaskFormVo formData);
}
