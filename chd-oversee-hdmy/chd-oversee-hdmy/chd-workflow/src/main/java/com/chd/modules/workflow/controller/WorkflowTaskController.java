//package com.chd.modules.workflow.controller;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.chd.common.api.vo.Result;
//import com.chd.common.exception.AssertBizException;
//import com.chd.common.exception.BizException;
//import com.chd.common.system.vo.LoginUser;
//import com.chd.common.util.RequestUtils;
//import com.chd.common.util.StringUtil;
//import com.chd.modules.workflow.service.WorkflowImageService;
//import com.chd.modules.workflow.service.WorkflowManageService;
//import com.chd.modules.workflow.service.WorkflowService;
//import com.chd.modules.workflow.service.WorkflowTaskService;
//import com.chd.modules.workflow.vo.WorkflowQueryVo;
//import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
//import com.chd.modules.workflow.vo.WorkflowTaskVo;
//import com.chd.modules.workflow.vo.WorkflowUserVo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.SecurityUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.Assert;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/workflow/task")
//@Slf4j
//public class WorkflowTaskController {
//
//
//    @Autowired
//    private WorkflowService workflowService;
//    @Autowired
//    private WorkflowTaskService workflowTaskService;
//
//    private static String saveFormData = "/workflow/task/saveFormData";
//    private static String tasksReject = "/workflow/task/tasksReject";
//    private static String delegateBackNode = "/workflow/task/delegateBackNode";
//    private static String tasksDelegate = "/workflow/task/tasksDelegate";
//    private static String tasksApprove = "/workflow/task/tasksApprove";
//
//
//
//
//    /**
//     * 通过
//     * @param form
//     * @return
//     */
//    @PostMapping("/tasksApprove")
//    public Result tasksApprove( @RequestBody WorkflowTaskFormVo form){
//        try {
//            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//            WorkflowUserVo taskUser=new WorkflowUserVo();
//            taskUser.setName(sysUser.getRealname());
//            taskUser.setId(sysUser.getId());
//            boolean result = workflowTaskService.tasksApprove(form, taskUser);
//            return result ? Result.OK("提交成功") : Result.error("提交失败");
//        }catch (BizException ex){
//            return Result.error(ex.getMessage());
//        }
//    }
//
//    /**
//     * 拒绝
//     * @param form
//     * @return
//     */
//    @PostMapping("/tasksReject")
//    public Result tasksReject( @RequestBody WorkflowTaskFormVo form){
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        boolean result=workflowTaskService.tasksReject(form,sysUser.getId());
//        return result?Result.OK("操作成功"):Result.error("操作失败");
//    }
//
//    @PostMapping("/tasksRejectBatch")
//    public Result tasksRejectBatch( @RequestBody WorkflowTaskFormVo form){
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        boolean result=workflowTaskService.tasksReject(form,sysUser.getId());
//        return result?Result.OK("操作成功"):Result.error("操作失败");
//    }
//
//    /**
//     * 委派
//     * @param form
//     * @return
//     */
//    @PostMapping("/tasksDelegate")
//    public Result tasksDelegate( @RequestBody WorkflowTaskFormVo form){
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        WorkflowUserVo taskUser=new WorkflowUserVo();
//        taskUser.setName(sysUser.getRealname());
//        taskUser.setId(sysUser.getId());
//        boolean result=workflowTaskService.tasksDelegate(form,taskUser);
//        return result?Result.OK("操作成功"):Result.error("操作失败");
//    }
//
//    /**
//     * 指派后面节点为（按节点ID命名包含有"BACKPOINT-"顺序指派)
//     * @param form
//     * @return
//     */
//    @PostMapping("/delegateBackNode")
//    public Result delegateBackNode( @RequestBody WorkflowTaskFormVo form){
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        WorkflowUserVo taskUser=new WorkflowUserVo();
//        taskUser.setName(sysUser.getRealname());
//        taskUser.setId(sysUser.getId());
//        boolean result=workflowTaskService.tasksDelegateBackNodes(form,taskUser);
//        return result?Result.OK("操作成功"):Result.error("操作失败");
//    }
//
//    /**
//     * 返回发起者
//     * @param form
//     * @return
//     */
//    @PostMapping("/backStart")
//    public Result backStart( @RequestBody WorkflowTaskFormVo form){
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        WorkflowUserVo taskUser=new WorkflowUserVo();
//        taskUser.setName(sysUser.getRealname());
//        taskUser.setId(sysUser.getId());
//        boolean result=workflowTaskService.tasksBackStart(form,taskUser);
//        return result?Result.OK("操作成功"):Result.error("操作失败");
//    }
//
//    @GetMapping("/detail")
//    public Result taskDetail(String taskId){
//        WorkflowTaskVo taskVo=workflowService.findTaskById(taskId);
//        return  Result.OK(taskVo);
//    }
//
//    /**
//     * 待办列表
//     * @param query
//     * @return
//     */
//    @GetMapping("/todolist")
//    public Result getTodoList(WorkflowQueryVo query, HttpServletRequest req) {
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        Map<String, Object> parameterMap = RequestUtils.conversionObjectMap(req.getParameterMap());
//        IPage<WorkflowTaskVo> result= workflowTaskService.todoTaskList(query,sysUser.getId(),parameterMap);
//        return Result.OK(result);
//    }
//
//    /**
//     * 暂存表单信息
//     * @param form
//     * @return
//     */
//        @PostMapping("/saveFormData")
//    public Result saveFormData( @RequestBody WorkflowTaskFormVo form){
//        try {
//            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//            WorkflowUserVo taskUser=new WorkflowUserVo();
//            taskUser.setName(sysUser.getRealname());
//            taskUser.setId(sysUser.getId());
//            boolean result = workflowTaskService.saveTaskFormData(form, taskUser);
//            return result ? Result.OK("提交成功") : Result.error("提交失败");
//        }catch (BizException ex){
//            return Result.error(ex.getMessage());
//        }
//    }
//
//    @PostMapping("/taskBatch")
//    public Result taskBatch( @RequestBody WorkflowTaskFormVo form){
//        try {
//            Assert.notNull(form,"请传递数据");
//            Assert.notNull(form.getTaskDataList(),"请传递数据");
//            AssertBizException.isTrue((null!=form.getTaskDataList()&&form.getTaskDataList().size()>0),"请传递批量数据");
//            AssertBizException.isTrue((StringUtil.isNotEmpty(form.getUrl())),"请传递批量处理url");
//            String url = form.getUrl();
//            int count = 0;
//            for(Map<String,Object> taskData : form.getTaskDataList()){
//                try{
//                    if(null!=taskData){
//                        String taskId = (String) taskData.get("taskId");
//                        String processId = (String) taskData.get("processId");
//
//                        if(StringUtil.isNotEmpty(taskId)&&StringUtil.isNotEmpty(processId)){
//                            WorkflowTaskFormVo workflowTaskFormVo = new WorkflowTaskFormVo();
//                            BeanUtils.copyProperties(form,workflowTaskFormVo);
//                            workflowTaskFormVo.setTaskId(taskId);
//                            workflowTaskFormVo.setProcessId(processId);
//                            if(saveFormData.equals(url)){
//                                saveFormData(workflowTaskFormVo);
//                            }else if(tasksReject.equals(url)){
//                                tasksReject(workflowTaskFormVo);
//                            }else if(delegateBackNode.equals(url)){
//                                delegateBackNode(workflowTaskFormVo);
//                            }else if(tasksDelegate.equals(url)){
//                                tasksDelegate(workflowTaskFormVo);
//                            }else if(tasksApprove.equals(url)){
//                                tasksApprove(workflowTaskFormVo);
//                            }else{
//                                AssertBizException.isTrue((false),"当前节点未找到批处理方法");
//                            }
//
//                            count ++;
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            return count > 0 ? Result.OK("批量处理成功"+count+"个任务") : Result.error("提交失败,批量处理成功"+count+"个任务");
//        }catch (BizException ex){
//            return Result.error(ex.getMessage());
//        }
//    }
//
//    @PostMapping("/saveFormDataBatch")
//    public Result saveFormDataBatch( @RequestBody WorkflowTaskFormVo form){
//        try {
//            Assert.notNull(form,"请传递数据");
//            Assert.notNull(form.getTaskDataList(),"请传递数据");
//            AssertBizException.isTrue((null!=form.getTaskDataList()&&form.getTaskDataList().size()>0),"请传递批量数据");
//            int count = 0;
//            for(Map<String,Object> taskData : form.getTaskDataList()){
//                try{
//                    if(null!=taskData){
//                        String taskId = (String) taskData.get("taskId");
//                        String processId = (String) taskData.get("processId");
//                        if(StringUtil.isNotEmpty(taskId)&&StringUtil.isNotEmpty(processId)){
//                            WorkflowTaskFormVo workflowTaskFormVo = new WorkflowTaskFormVo();
//                            BeanUtils.copyProperties(form,workflowTaskFormVo);
//                            workflowTaskFormVo.setTaskId(taskId);
//                            workflowTaskFormVo.setProcessId(processId);
//                            saveFormData(workflowTaskFormVo);
//                            count ++;
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            return count > 0 ? Result.OK("提交成功") : Result.error("提交失败");
//        }catch (BizException ex){
//            return Result.error(ex.getMessage());
//        }
//    }
//
//}
