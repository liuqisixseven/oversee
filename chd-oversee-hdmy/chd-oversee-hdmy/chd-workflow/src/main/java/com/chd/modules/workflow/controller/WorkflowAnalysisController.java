package com.chd.modules.workflow.controller;

import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.workflow.service.WorkflowTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程分析统计
 */
@RestController
@RequestMapping("/workflow/analysis")
public class WorkflowAnalysisController {

    @Autowired
    private WorkflowTaskService workflowTaskService;

    /**
     * 我的任务数量统计
     * @return
     */
    @GetMapping("/countTask")
    public Result countTask(){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       return  Result.OK( workflowTaskService.userProcessAnalysis(sysUser.getId()));
    }
}
