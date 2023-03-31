package com.chd.modules.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.flowable.task.api.Task;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowTaskVo {
    private String processId;
    private String taskId;
    private String taskName;
    private String title;
    private String num;
    private String state;
    private String startUserName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date applyTime;
    private String bizId;
    private String bizUrl;
    private String processCategory;
    private String processDefKey;
    private String remark;
    private String taskKey;
    private String assignee;//任务的受理人，即执行人
    private String owner;//任务的委托人
    private List<String> candidateUser;//候选人
    private List<String> candidateGroup;//候选用户组
    private String formKey;//表单KEY
    private Map<String,Object> myTaskVariables;
    private String nextUserTask;
    private String previousTaskDataSrc;
    private String previousTaskUserNames;
    private String previousTaskUserEndTimes;
    private String previousTaskUserComments;
    private String processStage;
    private Map<String,Object> overseeIssueMap;



    public WorkflowTaskVo(){}
    public WorkflowTaskVo(Task task){
        setProcessId(task.getProcessInstanceId());
        setTaskId(task.getId());
        setTaskName(task.getName());
        setTaskKey(task.getTaskDefinitionKey());
        setAssignee(task.getAssignee());
        setApplyTime(task.getCreateTime());
        setRemark(task.getDescription());
        setFormKey(task.getFormKey());
        setOwner(task.getOwner());
        setAssignee(task.getAssignee());

    }
}
