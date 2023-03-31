package com.chd.modules.workflow.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流用户
 */
@Data
public class WorkflowUserVo {
    private String id;
    private String name;
    private String taskKey;

    public WorkflowUserVo() {
    }

    public WorkflowUserVo(String userId) {
        this.id = userId;
    }
    public WorkflowUserVo(String userId, String userName) {
        this.id = userId;
        this.name = userName;
    }
    public WorkflowUserVo(String userId, String userName,String taskKey) {
        this.id = userId;
        this.name = userName;
        this.taskKey=taskKey;
    }
}
