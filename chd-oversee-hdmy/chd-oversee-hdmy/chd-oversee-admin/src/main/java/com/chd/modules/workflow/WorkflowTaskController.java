package com.chd.modules.workflow;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.common.api.vo.Result;
import com.chd.common.exception.AssertBizException;
import com.chd.common.exception.BizException;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.RequestUtils;
import com.chd.common.util.StringUtil;
import com.chd.common.util.poi.ExcelUtils;
import com.chd.modules.oversee.issue.entity.OverseeIssueDetailVo;
import com.chd.modules.oversee.issue.entity.OverseeIssueQueryVo;
import com.chd.modules.workflow.service.WorkflowService;
import com.chd.modules.workflow.service.WorkflowTaskService;
import com.chd.modules.workflow.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow/task")
@Slf4j
@Api(tags="华电煤业工作流程任务管理")
public class WorkflowTaskController {


    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private WorkflowTaskService workflowTaskService;

    private static String saveFormData = "/workflow/task/saveFormData";
    private static String tasksReject = "/workflow/task/tasksReject";
    private static String delegateBackNode = "/workflow/task/delegateBackNode";
    private static String tasksDelegate = "/workflow/task/tasksDelegate";
    private static String tasksApprove = "/workflow/task/tasksApprove";

    /**
     * 通过
     * @param form
     * @return
     */
    @PostMapping("/tasksApprove")
    @ApiOperation(value="任务审批通过", notes="任务审批")
    public Result tasksApprove( @RequestBody WorkflowTaskFormVo form){
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            WorkflowUserVo taskUser=new WorkflowUserVo();
            taskUser.setName(sysUser.getRealname());
            taskUser.setId(sysUser.getId());
            boolean result = workflowTaskService.tasksApprove(form, taskUser);
            return result ? Result.OK("提交成功") : Result.error("提交失败");
        }catch (BizException ex){
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 拒绝
     * @param form
     * @return
     */
    @PostMapping("/tasksReject")
    @ApiOperation(value="任务审拒绝", notes="任务审批")
    public Result tasksReject( @RequestBody WorkflowTaskFormVo form){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean result=workflowTaskService.tasksReject(form,sysUser.getId());
        return result?Result.OK("操作成功"):Result.error("操作失败");
    }

    @PostMapping("/tasksRejectBatch")
    @ApiOperation(value="任务审批批量拒绝", notes="任务审批")
    public Result tasksRejectBatch( @RequestBody WorkflowTaskFormVo form){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean result=workflowTaskService.tasksReject(form,sysUser.getId());
        return result?Result.OK("操作成功"):Result.error("操作失败");
    }

    /**
     * 委派
     * @param form
     * @return
     */
    @PostMapping("/tasksDelegate")
    @ApiOperation(value="任务委派", notes="任务委派")
    public Result tasksDelegate( @RequestBody WorkflowTaskFormVo form){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkflowUserVo taskUser=new WorkflowUserVo();
        taskUser.setName(sysUser.getRealname());
        taskUser.setId(sysUser.getId());
        boolean result=workflowTaskService.tasksDelegate(form,taskUser);
        return result?Result.OK("操作成功"):Result.error("操作失败");
    }

    /**
     * 指派后面节点为（按节点ID命名包含有"BACKPOINT-"顺序指派)
     * @param form
     * @return
     */
    @PostMapping("/delegateBackNode")
    @ApiOperation(value="任务委派后面节点", notes="任务委派")
    public Result delegateBackNode( @RequestBody WorkflowTaskFormVo form){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkflowUserVo taskUser=new WorkflowUserVo();
        taskUser.setName(sysUser.getRealname());
        taskUser.setId(sysUser.getId());
        boolean result=workflowTaskService.tasksDelegateBackNodes(form,taskUser);
        return result?Result.OK("操作成功"):Result.error("操作失败");
    }

    /**
     * 返回发起者
     * @param form
     * @return
     */
    @PostMapping("/backStart")
    @ApiOperation(value="任务打回开始节点", notes="任务委派")
    public Result backStart( @RequestBody WorkflowTaskFormVo form){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkflowUserVo taskUser=new WorkflowUserVo();
        taskUser.setName(sysUser.getRealname());
        taskUser.setId(sysUser.getId());
        boolean result=workflowTaskService.tasksBackStart(form,taskUser);
        return result?Result.OK("操作成功"):Result.error("操作失败");
    }

    @GetMapping("/detail")
    @ApiOperation(value="任务详情", notes="任务委派")
    public Result taskDetail(String taskId){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkflowTaskVo taskVo=workflowService.findTaskById(taskId,sysUser);
        return  Result.OK(taskVo);
    }

    /**
     * 待办列表
     * @param query
     * @return
     */
    @GetMapping("/todolist")
    @ApiOperation(value="我的代办", notes="任务委派")
    public Result getTodoList(WorkflowQueryVo query, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> parameterMap = RequestUtils.conversionObjectMap(req.getParameterMap());
        IPage<WorkflowTaskVo> result= workflowTaskService.todoTaskList(query,sysUser.getId(),parameterMap);
        return Result.OK(result);
    }

    @GetMapping(value = "/exportExcel")
    public Result<?> exportExcel(WorkflowQueryVo query, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> parameterMap = RequestUtils.conversionObjectMap(req.getParameterMap());
        IPage<WorkflowTaskVo> result= workflowTaskService.todoTaskList(query,sysUser.getId(),parameterMap);
        List<WorkflowTaskExcelVo> workflowTaskExcelVoList  = new ArrayList<WorkflowTaskExcelVo>();
        if(null!=result&&null!=result.getRecords()&&result.getRecords().size()>0){
            for(WorkflowTaskVo workflowTaskVo : result.getRecords()){
                WorkflowTaskExcelVo workflowTaskExcelVo = new WorkflowTaskExcelVo(workflowTaskVo);
                workflowTaskExcelVoList.add(workflowTaskExcelVo);
            }
        }
        ExcelUtils<WorkflowTaskExcelVo> util = new ExcelUtils<>(WorkflowTaskExcelVo.class);
        return util.exportExcel(workflowTaskExcelVoList, "待办数据");
    }

    @PostMapping(value = "/exportExcelNew")
    public Result<?> exportExcelNew(@RequestBody WorkflowQueryVo query, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> parameterMap = RequestUtils.conversionObjectMap(req.getParameterMap());
        IPage<WorkflowTaskVo> result= workflowTaskService.todoTaskList(query,sysUser.getId(),parameterMap);
        List<WorkflowTaskExcelVo> workflowTaskExcelVoList  = new ArrayList<WorkflowTaskExcelVo>();
        if(null!=result&&null!=result.getRecords()&&result.getRecords().size()>0){
            for(WorkflowTaskVo workflowTaskVo : result.getRecords()){
                WorkflowTaskExcelVo workflowTaskExcelVo = new WorkflowTaskExcelVo(workflowTaskVo);
                workflowTaskExcelVoList.add(workflowTaskExcelVo);
            }
        }
        ExcelUtils<WorkflowTaskExcelVo> util = new ExcelUtils<>(WorkflowTaskExcelVo.class);
        return util.exportExcel(workflowTaskExcelVoList, "待办数据");
    }


    /**
     * 暂存表单信息
     * @param form
     * @return
     */
    @PostMapping("/saveFormData")
    @ApiOperation(value="任务暂存", notes="任务委派")
    public Result saveFormData( @RequestBody WorkflowTaskFormVo form){
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            WorkflowUserVo taskUser=new WorkflowUserVo();
            taskUser.setName(sysUser.getRealname());
            taskUser.setId(sysUser.getId());
            boolean result = workflowTaskService.saveTaskFormData(form, taskUser);
            return result ? Result.OK("提交成功") : Result.error("提交失败");
        }catch (BizException ex){
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("/taskBatch")
    @ApiOperation(value="任务批量处理", notes="任务委派")
    public Result taskBatch( @RequestBody WorkflowTaskFormVo form){
        try {
            Assert.notNull(form,"请传递数据");
            Assert.notNull(form.getTaskDataList(),"请传递数据");
            AssertBizException.isTrue((null!=form.getTaskDataList()&&form.getTaskDataList().size()>0),"请传递批量数据");
            AssertBizException.isTrue((StringUtil.isNotEmpty(form.getUrl())),"请传递批量处理url");
            String url = form.getUrl();
            int count = 0;
            for(Map<String,Object> taskData : form.getTaskDataList()){
                try{
                    if(null!=taskData){
                        String taskId = (String) taskData.get("taskId");
                        String processId = (String) taskData.get("processId");

                        if(StringUtil.isNotEmpty(taskId)&&StringUtil.isNotEmpty(processId)){
                            WorkflowTaskFormVo workflowTaskFormVo = new WorkflowTaskFormVo();
                            BeanUtils.copyProperties(form,workflowTaskFormVo);
                            workflowTaskFormVo.setTaskId(taskId);
                            workflowTaskFormVo.setProcessId(processId);
                            if(saveFormData.equals(url)){
                                saveFormData(workflowTaskFormVo);
                            }else if(tasksReject.equals(url)){
                                tasksReject(workflowTaskFormVo);
                            }else if(delegateBackNode.equals(url)){
                                delegateBackNode(workflowTaskFormVo);
                            }else if(tasksDelegate.equals(url)){
                                tasksDelegate(workflowTaskFormVo);
                            }else if(tasksApprove.equals(url)){
                                tasksApprove(workflowTaskFormVo);
                            }else{
                                AssertBizException.isTrue((false),"当前节点未找到批处理方法");
                            }

                            count ++;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return count > 0 ? Result.OK("批量处理成功"+count+"个任务") : Result.error("提交失败,批量处理成功"+count+"个任务");
        }catch (BizException ex){
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("/saveFormDataBatch")
    @ApiOperation(value="任务批量保存", notes="任务委派")
    public Result saveFormDataBatch( @RequestBody WorkflowTaskFormVo form){
        try {
            Assert.notNull(form,"请传递数据");
            Assert.notNull(form.getTaskDataList(),"请传递数据");
            AssertBizException.isTrue((null!=form.getTaskDataList()&&form.getTaskDataList().size()>0),"请传递批量数据");
            int count = 0;
            for(Map<String,Object> taskData : form.getTaskDataList()){
                try{
                    if(null!=taskData){
                        String taskId = (String) taskData.get("taskId");
                        String processId = (String) taskData.get("processId");
                        if(StringUtil.isNotEmpty(taskId)&&StringUtil.isNotEmpty(processId)){
                            WorkflowTaskFormVo workflowTaskFormVo = new WorkflowTaskFormVo();
                            BeanUtils.copyProperties(form,workflowTaskFormVo);
                            workflowTaskFormVo.setTaskId(taskId);
                            workflowTaskFormVo.setProcessId(processId);
                            saveFormData(workflowTaskFormVo);
                            count ++;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return count > 0 ? Result.OK("提交成功") : Result.error("提交失败");
        }catch (BizException ex){
            return Result.error(ex.getMessage());
        }
    }

}
