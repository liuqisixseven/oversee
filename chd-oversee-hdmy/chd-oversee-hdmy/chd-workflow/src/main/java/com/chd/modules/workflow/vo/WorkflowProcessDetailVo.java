package com.chd.modules.workflow.vo;

import com.chd.modules.workflow.entity.WorkflowComment;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowProcessDetailVo {
    private WorkflowProcessVo process;//流程
    private List<WorkflowUserTaskVo> userTasks;//审批用户节点
    private List<String> expressions;//表达式
    private List<WorkflowComment> comments;
    private List<WorkflowUserTaskVo> nextTasks;
}
