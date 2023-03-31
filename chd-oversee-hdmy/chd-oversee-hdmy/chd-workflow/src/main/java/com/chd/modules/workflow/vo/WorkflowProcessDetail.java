package com.chd.modules.workflow.vo;

import lombok.Data;
import org.flowable.bpmn.model.UserTask;

import java.util.List;

/**
 * 流程详情
 */
@Data
public class WorkflowProcessDetail {

    private List<UserTask> userTaskList;//用户审批列表
    private WorkflowProcessVo process;//流程信息
}
