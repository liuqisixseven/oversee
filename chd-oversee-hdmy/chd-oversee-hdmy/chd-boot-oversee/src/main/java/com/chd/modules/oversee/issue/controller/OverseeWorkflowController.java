package com.chd.modules.oversee.issue.controller;

import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueDetailVo;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.IOverseeWorkflowService;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.vo.WorkflowProcessDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
@Api(tags="问题启动详情")
@RestController
@RequestMapping("/oversee/flow")
@Slf4j
public class OverseeWorkflowController {

    @Autowired
    private IOverseeWorkflowService overseeWorkflowService;
    @Autowired
    private IOverseeIssueService overseeIssueService;

    @PostMapping("/launch")
    @ApiOperation(value="问题流程启动", notes="问题流程启动")
    public Result launch(Long issueId) {
        if(issueId==null){
            return Result.error("请求参数错误");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean result = overseeWorkflowService.overseeIssueLaunchProcess(issueId,sysUser);
        return result ? Result.OK("发起成功") : Result.error("发起失败");
    }

    @GetMapping("/issueDetail")
    public Result issueDetail(Long issueId,String bindResponsibleOrgIds){
        OverseeIssueDetailVo detailVo= overseeIssueService.getIssueDetailById(issueId,bindResponsibleOrgIds);
        return Result.OK(detailVo);
    }

    @GetMapping("/cancelNumlaunch")
    @ApiOperation(value="老问题销号流程启动", notes="老问题销号流程启动")
    public Result cancelNumlaunch(Long issueId) {
        if(issueId==null){
            return Result.error("请求参数错误");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean result = overseeWorkflowService.overseeOldIssueLaunchProcess(issueId,sysUser);
        return result ? Result.OK("发起成功") : Result.error("发起失败");
    }
}
