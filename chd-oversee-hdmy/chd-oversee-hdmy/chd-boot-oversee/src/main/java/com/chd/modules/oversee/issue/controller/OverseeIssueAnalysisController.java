package com.chd.modules.oversee.issue.controller;

import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.issue.base.IssueBaseController;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.service.IOverseeIssueAnalysisService;
import com.chd.modules.oversee.issue.service.IOverseeIssueRoleService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.workflow.service.WorkflowUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/issue/analysis")
public class OverseeIssueAnalysisController extends IssueBaseController<OverseeIssue, IOverseeIssueService> {

    @Autowired
    private IMyOrgService myOrgService;

    @Autowired
    private IOverseeIssueService overseeIssueService;

    @Autowired
    private IOverseeIssueRoleService overseeIssueRoleService;

    @Autowired
    private IOverseeIssueAnalysisService overseeIssueAnalysisService;
    @Autowired
    private WorkflowUserService workflowUserService;

    @GetMapping("/info")
    public Result countTask(IssueAnalysisQueryVo query){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
            query.setUserId(sysUser.getId());
        }
        return  Result.OK( overseeIssueAnalysisService.issueInfo(query));
    }

    @GetMapping("/timelist")
    public Result timelist(IssueAnalysisQueryVo query){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
            query.setUserId(sysUser.getId());
        }
        return  Result.OK( overseeIssueAnalysisService.issueListByTime(query));
    }

    @GetMapping("/list")
    public Result list(IssueAnalysisQueryVo query){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
            query.setUserId(sysUser.getId());
        }
        return  Result.OK( overseeIssueAnalysisService.issueListByType(query));
    }

    @GetMapping("/overdueSituation")
    public Result overdueSituation(IssueAnalysisQueryVo query){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
            query.setUserId(sysUser.getId());
        }
        return  Result.OK( overseeIssueAnalysisService.overdueSituation(query));
    }

    @GetMapping("/newInfo")
    public Result newInfo(OverseeIssueQueryVo query){
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");
            Map<String, Object> overseeIssueQueryVoMapData = getOverseeIssueQueryVoMapData(query);
            Boolean isAll = (Boolean) overseeIssueQueryVoMapData.get("isAll");
            if(null==isAll)isAll = false;

            IssueAnalysisInfoVo issueAnalysisInfoVo = overseeIssueAnalysisService.newInfo(query);

            if(null!=issueAnalysisInfoVo){
                issueAnalysisInfoVo.setIsAll((isSystemRole()||isAll)?1:-1);
                MyOrg myOrgById = null;
                if(issueAnalysisInfoVo.getIsAll()!=1){
                    if(StringUtil.isNotEmpty(sysUser.getHdmyOrgId())){
                        myOrgById = myOrgService.getOrganizeCompany(sysUser.getHdmyOrgId());
                        if(null!=myOrgById&&StringUtil.isNotEmpty(myOrgById.getOrgId())&&myOrgById.getOrgId().equals(BaseConstant.HEADQUARTERS_ORG_ID)){
//                            华电煤业本部所有用户都可以看到所有部门的数据
                            issueAnalysisInfoVo.setIsAll(1);
                        }else{
                            issueAnalysisInfoVo.setParentOrganizeCompany(myOrgById);
                        }
                    }
                }

            }

            return Result.OK(issueAnalysisInfoVo);
        }catch (IllegalArgumentException e){
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }

    }


//    @GetMapping("/issueCount")
//    public Result issueCount(IssueAnalysisQueryVo query){
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        if(!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
//            query.setUserId(sysUser.getId());
//        }
//        return  Result.OK( overseeIssueAnalysisService.issueInfo(query));
//    }
}
