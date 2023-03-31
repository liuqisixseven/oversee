package com.chd.modules.workflow.vo;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowTaskDetailVo {
    private String nodeId;
    private String nodeName;
    private String formKey;
    private String status;
    private String userId;
    private String userName;
    private List<String> groups;
    private String startTime;
}
