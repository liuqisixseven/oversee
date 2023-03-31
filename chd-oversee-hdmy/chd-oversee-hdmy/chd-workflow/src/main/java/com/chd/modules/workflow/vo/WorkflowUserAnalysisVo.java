package com.chd.modules.workflow.vo;

import lombok.Data;

@Data
public class WorkflowUserAnalysisVo {
    private Long todoTotal;//待办总量
    private Long doneTotal;//已办总量
}
