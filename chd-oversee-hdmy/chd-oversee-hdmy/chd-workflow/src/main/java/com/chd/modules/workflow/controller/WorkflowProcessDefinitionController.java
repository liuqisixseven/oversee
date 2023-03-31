package com.chd.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.SpringContextUtils;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow/processDef")
@Slf4j
public class WorkflowProcessDefinitionController {

    @Autowired
    private WorkflowProcessDefinitionService workflowProcessDefinitionService;
    @Autowired
    private WorkflowManageService workflowManageService;
    @Autowired
    private WorkflowUserService workflowUserService;

    @GetMapping("/list")
    public Result processDefinitionList(WorkflowProcessVo query) {
        IPage<WorkflowProcessVo> result= workflowProcessDefinitionService.processDefinitionList(query);
        return Result.OK(result);
    }

    @PostMapping("/suspensionState/{id}")
    public Result changeProcessStatus(@PathVariable String id, @RequestBody WorkflowProcessVo processVo) {
        if(StringUtils.isNotBlank(id) && processVo!=null){
            String msg="操作成功";
            if(WorkflowProcessVo.SuspensionState.active.equals(processVo.getSuspensionState())){
                msg="激活成功";
            }else if(WorkflowProcessVo.SuspensionState.suspended.equals(processVo.getSuspensionState())){
                msg="成功挂起";
            }else{
                return Result.error("请求参数错误");
            }
            workflowProcessDefinitionService.suspendOrActivateProcessDefinition(id, processVo.getSuspensionState());
            return Result.OK(msg);
        }else{
            return Result.error("请求参数错误");
        }
    }

    @GetMapping("/detail")
    public Result processDetail(String category,String bizId) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkflowProcessDetailVo result =null;
        ProcessDefinition processDefinition = workflowProcessDefinitionService.lastVersionProcessDefByCategory(category);
        if (processDefinition != null) {

            Map<String,Object> processVariables=null;
            WorkflowBusinessProcessService bizProcess= SpringContextUtils.getBean(WorkflowBusinessProcessService.class);
            if(bizProcess!=null){
                processVariables=bizProcess.getProcessVariables(category,bizId,new WorkflowUserVo(sysUser.getId(),sysUser.getRealname()));
            }

            Map<String,List<WorkflowDepart>> bizVariableDepart= workflowUserService.getBizDepartVariables(category,bizId,sysUser.getId());
            WorkflowProcessDetail processDefDetail = workflowProcessDefinitionService.getProcessDefDetail(processDefinition,processVariables);
            result=workflowProcessDefinitionService.getProcessDefDetailView(processDefDetail,bizVariableDepart);
                if(CollectionUtils.isNotEmpty( result.getUserTasks())){
                   List<WorkflowUserTaskVo> userTasks=result.getUserTasks();
                    //过滤上报都自己
                    if (CollectionUtils.isNotEmpty(userTasks)) {
                        for (WorkflowUserTaskVo userTask : userTasks) {
                            if (CollectionUtils.isNotEmpty(userTask.getVariableUser())) {
                                Iterator<WorkflowUserVo> users = userTask.getVariableUser().iterator();
                                while (users.hasNext()) {
                                    WorkflowUserVo user = users.next();
                                    if (WorkflowConstants.FLOW_USER_OWNER.equals(user.getId())) {
                                        userTask.getUsers().add(new WorkflowUserVo(sysUser.getId(), sysUser.getRealname()));
                                        users.remove();
                                    }
                                }

                            }
                        }
                        result.setUserTasks(userTasks);
                    }
                }
            }
        return Result.OK(result);
    }

//    @GetMapping("/detail")
//    public Result processDetail(String category,String bizId) {
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        WorkflowProcessDetailVo result =null;
//        ProcessDefinition processDefinition = workflowProcessDefinitionService.lastVersionProcessDefByCategory(category);
//        if (processDefinition != null) {
//
//            WorkflowProcessDetail processDefDetail = workflowProcessDefinitionService.getProcessDefDetail(processDefinition);
//            result = workflowProcessDefinitionService.getProcessDefDetailView(processDefDetail);
//            if(CollectionUtils.isNotEmpty( result.getUserTasks())){
//                List<WorkflowUserTaskVo> userTasks=result.getUserTasks();
//                //过滤上报都自己
//                if (CollectionUtils.isNotEmpty(userTasks)) {
//                    for (WorkflowUserTaskVo userTask : userTasks) {
//                        if (CollectionUtils.isNotEmpty(userTask.getVariableUser())) {
//                            Iterator<WorkflowUserVo> users = userTask.getVariableUser().iterator();
//                            while (users.hasNext()) {
//                                WorkflowUserVo user = users.next();
//                                if (WorkflowConstants.FLOW_USER_OWNER.equals(user.getId())) {
//                                    userTask.getUsers().add(new WorkflowUserVo(sysUser.getId(), sysUser.getRealname()));
//                                    users.remove();
//                                }
//                            }
//
//                        }
//                    }
//                    userTasks = workflowUserService.getBizVariableUser(userTasks, category, bizId, sysUser.getId());
//                    result.setUserTasks(userTasks);
//                }
//            }
//        }
//        return Result.OK(result);
//    }

    @GetMapping(value = "/image/{category}")
    public void image(@PathVariable String category, HttpServletResponse response) {
        try {
            ProcessDefinition processDefinition=workflowProcessDefinitionService.lastVersionProcessDefByCategory(category);
            if(processDefinition!=null){
                byte[] b = workflowProcessDefinitionService.getProcessDefImage(processDefinition.getId());
                response.setHeader("Content-type", "image/png;charset=UTF-8");
                response.getOutputStream().write(b);
            }
        } catch (Exception e) {
            log.error("生成流程图失败" + e);

        }
    }
}
