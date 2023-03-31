package com.chd.modules.workflow.vo;

import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程变量
 */
@Data
public class WorkflowVariablesVo {

    private List<WorkflowProcessDepart> departList;
    private Map<String,Object> variables;

}
