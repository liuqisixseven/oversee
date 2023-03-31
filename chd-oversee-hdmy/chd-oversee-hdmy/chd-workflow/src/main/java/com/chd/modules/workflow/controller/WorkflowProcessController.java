package com.chd.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workflow/process")
@Slf4j
public class WorkflowProcessController {

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private WorkflowImageService workflowImageService;
    @Autowired
    private WorkflowProcessDefinitionService workflowProcessDefinitionService;
    @Autowired
    private WorkflowUserService workflowUserService;
    @Autowired
    private WorkflowVariablesService workflowVariablesService;
    @Autowired
    private WorkflowProcessUsersSerivce workflowProcessUsersSerivce;
    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;

    @Autowired
    private WorkflowDepartService workflowDepartService;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    protected HistoryService historyService;

    @PostMapping("/launch")
    public Result launch(@Valid @RequestBody WorkflowProcess process) {
        Date now=new Date();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        process.setStartUserId(sysUser.getId());
        if(StringUtils.isBlank( process.getProcessCategory())){//不传流程类型时，给默认值
            process.setProcessCategory("ISSUES");
        }

        WorkflowProcess result = workflowService.launchProcess(process);
        return result!=null ? Result.OK("发起成功") : Result.error("发起失败");
    }

    @GetMapping("/myprocess")
    public Result myprocess(WorkflowQueryVo query) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        IPage<WorkflowProcess> result= workflowService.involvedUserProcessList(query,sysUser.getId());
        return Result.OK(result);
    }


    /**
     * 流程实例图
     * @param processInstanceId
     * @param response
     */
    @GetMapping(value = "/proccessImage/{processInstanceId}")
    public void proccessImage(@PathVariable String processInstanceId, HttpServletResponse response) {
        try {
            byte[] b = workflowImageService.proccessImage(processInstanceId);
            response.setHeader("Content-type", "image/png;charset=UTF-8");
            response.getOutputStream().write(b);
        } catch (Exception e) {
            log.error("生成流程图失败" + e);
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/detail")
    public Result getCommentByProcessInstanceId(String processId){
        WorkflowProcessDetailVo result=workflowService.processDetail(processId);
        WorkflowProcessVo processVo= result.getProcess();
        if(CollectionUtils.isNotEmpty( result.getUserTasks())) {
            List<WorkflowUserTaskVo> userTasks = result.getUserTasks();
//            userTasks=workflowUserService.getBizVariableUser(userTasks,processVo.getCategory(),processVo.getBizId(),processVo.getStartUserId());
            Map<String,Object> taskVariables=new HashMap<>();
            taskVariables.put(WorkflowConstants.FLOW_USER_OWNER,new WorkflowUserVo(processVo.getStartUserId(),processVo.getStartUserName()));
            Map<String,List<WorkflowDepart>> bizVariableDepart= workflowUserService.getBizDepartVariables(processVo.getCategory(),processVo.getBizId(),processVo.getStartUserId());
            if(MapUtils.isNotEmpty(bizVariableDepart)) {
                taskVariables.putAll(bizVariableDepart);
            }
            Map<String,List<WorkflowUserVo>> processUserList=workflowProcessUsersSerivce.getProcessUsersVariables(processId,null,null);
            if(MapUtils.isNotEmpty(processUserList)){
                taskVariables.putAll(processUserList);
            }
           List<WorkflowProcessDepart> processDeparts=  workflowProcessDepartService.getProcessDepartListByProcessId(processId);
            if(CollectionUtils.isNotEmpty(processDeparts)) {
                Map<String, List<WorkflowDepart>> departList = workflowProcessDepartService.getProcessDepartVariable(processDeparts);
                if(MapUtils.isNotEmpty(departList)){
                    taskVariables.putAll(departList);
                }
            }
            userTasks=workflowVariablesService.setUserTaskListVariables(userTasks,taskVariables);
            result.setUserTasks(userTasks);
            result.setNextTasks(workflowService.findNextTaskByProcessId(processId));
        }
        return Result.OK(result);
    }

//    @GetMapping(value = "/detail/{bizId}")
//    public Result getBizProcessDetail(@PathVariable String bizId){
//        WorkflowProcessDetailVo result=workflowService.processDetailByBizId(bizId);
//        return Result.OK(result);
//    }

    /**
     * 流程实例图
     * @param processDefId
     * @param response
     */
    @GetMapping(value = "/proccessDefImage/{processDefId}")
    public ResponseEntity<byte[]>  proccessDefImage(@PathVariable String processDefId, HttpServletResponse response) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", MediaType.IMAGE_PNG_VALUE);
        return new ResponseEntity<>(workflowProcessDefinitionService.getProcessDefImage(processDefId), responseHeaders, HttpStatus.OK);
    }






}
