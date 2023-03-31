package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;

public interface IOverseeProcessFormDataService {

    /**
     * 接收审批表单数据
     * @param overseeIssue
     * @param user
     * @param formData
     * @param task
     */
     void receiveTaskFormData(OverseeIssue overseeIssue, WorkflowUserVo user, WorkflowTaskFormVo formData, WorkflowTaskVo task);
}
