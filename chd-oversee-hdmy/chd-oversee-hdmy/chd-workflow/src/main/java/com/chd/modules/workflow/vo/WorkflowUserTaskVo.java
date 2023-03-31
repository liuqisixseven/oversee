package com.chd.modules.workflow.vo;

import com.chd.modules.workflow.entity.WorkflowDepart;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WorkflowUserTaskVo {
    private String name;
    private String taskKey;
    private String taskId;
    private String formKey;
    private List<WorkflowUserVo> users=new ArrayList<>();
    private List<WorkflowDepart> departs=new ArrayList<>();
    private List<WorkflowUserVo> variableUser=new ArrayList<>();
    private Integer userType;
    private String remark;//备注

}
