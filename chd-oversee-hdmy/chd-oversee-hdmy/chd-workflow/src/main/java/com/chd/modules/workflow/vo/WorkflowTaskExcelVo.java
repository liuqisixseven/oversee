package com.chd.modules.workflow.vo;

import com.chd.common.annotation.Excel;
import com.chd.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.flowable.task.api.Task;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowTaskExcelVo {


    @Excel(name="责任单位")
    private String responsibleUnitOrgName;

    @Excel(name="问题来源",dictType = "issue_source")
    private Integer source;

    @Excel(name="检查时间")
    private String checkTime;

    @Excel(name="问题编号")
    private String num;

    @Excel(name="问题描述")
    private String title;

    @Excel(name="办理阶段")
    private String processStage;

    @Excel(name="办理环节")
    private String taskName;

    @Excel(name="上一步处理人")
    private String previousTaskUserNames;

    @Excel(name="接收时间")
    private String previousTaskUserEndTimes;


    public WorkflowTaskExcelVo(){

    }

    public WorkflowTaskExcelVo(Task task){
        setTaskName(task.getName());
    }

    public WorkflowTaskExcelVo(WorkflowTaskVo task){

        Map<String, Object> overseeIssueMap = task.getOverseeIssueMap();
        if(null!=overseeIssueMap&&overseeIssueMap.size()>0){
            setResponsibleUnitOrgName((String) overseeIssueMap.get("responsibleUnitOrgName"));
            setSource((Integer) overseeIssueMap.get("source"));
            Long checkTime1 = (Long) overseeIssueMap.get("checkTime");
            if(null!=checkTime1&&checkTime1.longValue()>0){
                setCheckTime(DateUtils.date2Str(new Date(checkTime1),DateUtils.yyyyMMdd.get()));
            }
        }

        setNum(task.getNum());
        setTitle(task.getTitle());
        setProcessStage(task.getProcessStage());
        setTaskName(task.getTaskName());
        setPreviousTaskUserNames(task.getPreviousTaskUserNames());
        setPreviousTaskUserEndTimes(task.getPreviousTaskUserEndTimes());
    }



}
