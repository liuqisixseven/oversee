package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_deployment")
public class WorkflowDeployment {
    private String id;
    private String category;
    private byte[] bpmnXml;
    private Date createTime;
}
