package com.chd.modules.oversee.issue.controller;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.constant.CommonConstant;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.core.text.ExcelHeader;
import com.chd.common.kafka.service.KafkaProducerService;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.system.vo.SysDepartModel;
import com.chd.common.system.vo.SysUserCacheInfo;
import com.chd.common.util.HTMLUtils;
import com.chd.common.util.poi.ExcelUtils;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.issue.base.IssueBaseController;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.service.*;
import com.chd.modules.workflow.service.WorkflowUserService;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.chd.common.aspect.annotation.AutoLog;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date: 2022-08-03
 * @Version: V1.0
 */
@Api(tags = "问题列表")
@RestController
@RequestMapping("/issue/overseeIssue")
@Slf4j
public class OverseeIssueController extends IssueBaseController<OverseeIssue, IOverseeIssueService> {

    @Autowired
    private IOverseeIssueService overseeIssueService;

    @Autowired
    private IOverseeIssueCategoryService overseeIssueCategoryService;

    @Autowired
    private IOverseeIssueSubcategoryService overseeIssueSubcategoryService;


    @Autowired
    private WorkflowUserService workflowUserService;

    @Autowired
    private IOverseeWorkflowService overseeWorkflowService;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IMyOrgService myOrgService;

    @Autowired
    private IMyUserService myUserService;


    @Autowired
    private IIssuesSupervisorService issuesSupervisorService;

    @Autowired
    IAccountabilityHandlingService accountabilityHandlingService;

    @Autowired
    private IImproveRegulationsService improveRegulationsService;

    @Autowired
    private IRecoverFundsService recoverFundsService;

    @Autowired
    private IRectifyViolationsService rectifyViolationsService;

    @Autowired
    private IReasonCancellationService reasonCancellationService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private IOverseeIssueSpecificService overseeIssueSpecificService;

    @Autowired
    private IOverseeIssueRoleService overseeIssueRoleService;

    /**
     * 分页列表查询
     *
     * @param overseeIssue
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "oversee_issue-分页列表查询")
    @ApiOperation(value = "oversee_issue-分页列表查询", notes = "oversee_issue-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OverseeIssue>> queryPageList(OverseeIssue overseeIssue,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        try {
            Map<String, Object> selectMap = getSelectMap(req, "num", "title", "subtitle");
            Page<OverseeIssue> page = new Page<OverseeIssue>(pageNo, pageSize);
            IPage<OverseeIssue> pageList = overseeIssueService.selectOverseeIssuePageVo(page, selectMap);
            return Result.OK(pageList);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    /**
     * 添加
     *
     * @param overseeIssue
     * @return
     */
    @AutoLog(value = "oversee_issue-添加")
    @ApiOperation(value = "oversee_issue-添加", notes = "oversee_issue-添加")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:add")
    @PostMapping(value = "/add")
    public Result<Map<String, Object>> add(@RequestBody OverseeIssue overseeIssue) {
        return addOrUpdate(overseeIssue);
    }

    private Result<Map<String, Object>> addOrUpdate(OverseeIssue overseeIssue) {
        try {
            Result<Map<String, Object>> result = Result.OK("保存成功！");
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
//			 TODO 此处只保存 不提交
            if (null != overseeIssue.getSubmitState() && overseeIssue.getSubmitState() == 1) {
                overseeIssue.setSubmitState(null);
            }
            overseeIssue.setUpdateUserId(sysUser.getId());
            Assert.isTrue((overseeIssueService.addOrUpdate(overseeIssue, true) > 0), "编辑问题异常");
            Map<String, Object> data = Maps.newHashMap();
            data.put("overseeIssue", overseeIssue);
            result.setResult(data);
            return result;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    /**
     * 编辑
     *
     * @param overseeIssue
     * @return
     */
    @AutoLog(value = "oversee_issue-编辑")
    @ApiOperation(value = "oversee_issue-编辑", notes = "oversee_issue-编辑")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<Map<String, Object>> edit(@RequestBody OverseeIssue overseeIssue) {
        return addOrUpdate(overseeIssue);
    }

    /**
     * 不再发起整改
     *
     * @param overseeIssue
     * @return
     */
    @AutoLog(value = "oversee_issue-不再发起整改")
    @ApiOperation(value = "oversee_issue-不再发起整改", notes = "oversee_issue-不再发起整改")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:edit")
    @RequestMapping(value = "/noRectification", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<Map<String, Object>> noRectification(@RequestBody OverseeIssue overseeIssue) {
        try {
            Result<Map<String, Object>> result = Result.OK("保存成功！");
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
            Assert.isTrue((null != overseeIssue), "请传递问题数据");
            Assert.isTrue((null != overseeIssue.getId() && overseeIssue.getId().longValue() > 0), "请传递问题数据");
            OverseeIssue redisCacheOverseeIssue = overseeIssueService.getRedisCacheOverseeIssue(overseeIssue.getId());
            Assert.isTrue((null != redisCacheOverseeIssue), "不存在当前问题");
            Assert.isTrue((null != redisCacheOverseeIssue.getSubmitState() && redisCacheOverseeIssue.getSubmitState().intValue() != -1), "当前问题已结束");
            Assert.isTrue((sysUser.getId().equals(redisCacheOverseeIssue.getReportUserId())), "只有发起人才能申请不再纳入整改");

            overseeWorkflowService.overseeIssueLaunchProcess(redisCacheOverseeIssue.getId(), sysUser, -1);
            return result;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }


    /**
     * 发起督办
     *
     * @param overseeIssue
     * @return
     */
    @AutoLog(value = "oversee_issue-发起督办")
    @ApiOperation(value = "oversee_issue-发起督办", notes = "oversee_issue-发起督办")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:edit")
    @RequestMapping(value = "/initiateSupervision", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<Map<String, Object>> initiateSupervision(@RequestBody OverseeIssue overseeIssue) {
        try {
            Result<Map<String, Object>> result = Result.OK("保存成功！");
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");

            List<String> myOrgList = null;
            if (StringUtil.isNotEmpty(sysUser.getHdmyUserId())) {
                List<MyOrg> myOrgs = myOrgService.authorizeOrgList(sysUser.getHdmyUserId());
                if (null != myOrgs && myOrgs.size() > 0) {
                    myOrgList = myOrgs.stream().map((myOrg) -> myOrg.getOrgId()).collect(Collectors.toList());
                }
            }

            Assert.isTrue((null != overseeIssue), "请传递问题数据");
            Assert.isTrue((null != overseeIssue.getId() && overseeIssue.getId().longValue() > 0), "请传递问题数据");

            OverseeIssue redisCacheOverseeIssue = overseeIssueService.getRedisCacheOverseeIssue(overseeIssue.getId());
            Assert.isTrue((null != redisCacheOverseeIssue), "不存在当前问题");
//			 Assert.isTrue((null!=redisCacheOverseeIssue.getIsSupervise()&&redisCacheOverseeIssue.getIsSupervise().intValue()==1),"当前问题未设置督办信息");
            Assert.isTrue((null != redisCacheOverseeIssue.getSubmitState() && redisCacheOverseeIssue.getSubmitState().intValue() != -1), "当前问题已结束");


//			权限限制 本部牵头部门 本部督办部门 责任单位牵头部门有权发起审核
            Assert.isTrue((isSystemRole() || overseeIssueRoleService.isAuthorizeSupervise(overseeIssue.getId(), CollectionUtils.isNotEmpty(myOrgList) ? myOrgList.stream().collect(Collectors.joining(",")) : null, sysUser.getId()) == 1), "当前部门无权发起审核");
//			 Assert.isTrue((isInitiateSupervisionAuthority( myOrgList,redisCacheOverseeIssue.getHeadquartersLeadDepartmentOrgId(),redisCacheOverseeIssue.getSupervisorOrgIds(),redisCacheOverseeIssue.getResponsibleLeadDepartmentOrgId()).intValue()==1),"当前部门无权发起审核");

            Map<String, Object> superviseMapData = overseeIssue.getSuperviseMapData();
            Assert.isTrue((null != superviseMapData && null != superviseMapData.get(IssuesSupervisor.reasonKey) && StringUtils.isNotEmpty((String) superviseMapData.get(IssuesSupervisor.reasonKey))), "清输入督办理由");

            IssuesSupervisor issuesSupervisor = new IssuesSupervisor();
            issuesSupervisor.setIssueId(overseeIssue.getId());
            issuesSupervisor.setIssuesProcessType(2);
            issuesSupervisor.setReason((String) superviseMapData.get(IssuesSupervisor.reasonKey));
            if (null != superviseMapData.get(IssuesSupervisor.appendixListKey))
                issuesSupervisor.setAppendixList((List<OverseeIssueAppendix>) superviseMapData.get(IssuesSupervisor.appendixListKey));
            SysUserCacheInfo sysUserById = commonAPI.getSysUserById(sysUser.getId());
            Assert.isTrue((null != sysUserById), "未找到当前用户");
            Assert.isTrue((StringUtils.isNotEmpty(sysUserById.getHdmyOrgId())), "未找到当前用户对应部门");
            issuesSupervisor.setSupervisorOrgId(sysUserById.getHdmyOrgId());
            List<MyOrg> list = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getOrgId, sysUserById.getHdmyOrgId()));
            Assert.isTrue((null != list && list.size() > 0), "未找到当前用户对应部门数据");
            MyOrg myOrg = list.get(0);
            Assert.isTrue((StringUtils.isNotEmpty(myOrg.getManagerId())), "未找到当前部门负责人");
            Assert.isTrue((StringUtils.isNotEmpty(myOrg.getUpperSupervisorId())), "未找到当前部门分管领导");
            issuesSupervisor.setShowType(-1);
            issuesSupervisor.setManageUserId(myOrg.getManagerId());
            issuesSupervisor.setSupervisorUserId(myOrg.getUpperSupervisorId());
            issuesSupervisor.setUserId(sysUser.getId());
            issuesSupervisor.setUpdateUserId(sysUser.getId());
            Assert.isTrue((issuesSupervisorService.addOrUpdate(issuesSupervisor) > 0), "保存督办信息异常");


            Map<String, Object> map = Maps.newHashMap();
            map.put("issueId", redisCacheOverseeIssue.getId());
            map.put("loginUser", sysUser);
            map.put("issueLaunchType", 2);
            map.put("issuesSupervisor", issuesSupervisor);

            overseeWorkflowService.overseeIssueLaunchProcess(map);
            return result;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    /**
     * 销号数据查询
     *
     * @param overseeIssue
     * @param req
     * @return
     */
    //@AutoLog(value = "oversee_issue-分页列表查询")
    @ApiOperation(value = "oversee_issue-销号数据查询", notes = "oversee_issue-销号数据查询")
    @GetMapping(value = "/getCancelANumber")
    public Result<Map<String, Object>> getCancelANumber(OverseeIssue overseeIssue, HttpServletRequest req) {
        try {
            Assert.isTrue((null != overseeIssue && null != overseeIssue.getId() && overseeIssue.getId().longValue() > 0L), "请传递问题id");
            Map<String, Object> selectMap = Maps.newHashMap();
            selectMap.put("overseeIssueId", overseeIssue.getId());
            return Result.OK(reasonCancellationService.getCancelANumber(selectMap));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }


    /**
     * 销号数据查询
     *
     * @param overseeIssue
     * @param req
     * @return
     */
    //@AutoLog(value = "oversee_issue-分页列表查询")
    @ApiOperation(value = "oversee_issue-销号数据查询", notes = "oversee_issue-销号数据查询")
    @GetMapping(value = "/getCancelANumberCount")
    public Result<Map<String, Object>> getCancelANumberCount(OverseeIssue overseeIssue, HttpServletRequest req) {
        try {
            Map<String, Object> map = Maps.newHashMap();
            Map<String, Object> selectMap = getSelectMap(req, "num", "title", "subtitle");
            map.put("accountabilityHandlingCount", accountabilityHandlingService.selectCount(selectMap));
            map.put("rectifyViolationsCount", rectifyViolationsService.selectCount(selectMap));
            map.put("improveRegulationsCount", improveRegulationsService.selectCount(selectMap));
            map.put("recoveryIllegalDisciplinaryFundsNumberSum", recoverFundsService.selectRecoveryIllegalDisciplinaryFundsNumberSum(selectMap));
            map.put("recoverDamagesNumberSum", recoverFundsService.selectRecoverDamagesNumberSum(selectMap));
            return Result.OK(map);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    /**
     * 销号数据导出
     *
     * @param overseeIssue
     * @param req
     * @return
     */
    //@AutoLog(value = "oversee_issue-分页列表查询")
    @ApiOperation(value = "oversee_issue-销号数据导出", notes = "oversee_issue-销号数据导出")
    @GetMapping(value = "/getCancelANumberWordExport")
    public void getCancelANumberWordExport(OverseeIssue overseeIssue, HttpServletRequest req, HttpServletResponse response) throws IOException {
        XWPFTemplate template= overseeIssueService.getCancelANumberWordExport(overseeIssue);
        ServletOutputStream out = response.getOutputStream();
        template.write(out);
        out.flush();
    }



    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "oversee_issue-通过id删除")
    @ApiOperation(value = "oversee_issue-通过id删除", notes = "oversee_issue-通过id删除")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) Long id) {
//		overseeIssueService.removeById(id);
        try {
            Assert.notNull(id, "参数错误");
            Integer deleteCount = overseeIssueService.deleteByOverseeIssueId(id);
            Assert.isTrue((deleteCount > 0), "删除异常");
            return Result.OK("删除成功!");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }

    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "oversee_issue-批量删除")
    @ApiOperation(value = "oversee_issue-批量删除", notes = "oversee_issue-批量删除")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
//		this.overseeIssueService.removeByIds(Arrays.asList(ids.split(",")));
        try {
            Assert.isTrue((StringUtil.isNotEmpty(ids)), "请传递数据ids");
            List<String> idArray = Arrays.asList(ids.split(","));
            if (null != idArray && idArray.size() > 0) {
                for (String id : idArray) {
                    if (StringUtils.isNotBlank(id)) {
                        Result<String> deleteResult = delete(Long.valueOf(id));
                        Assert.isTrue((null != deleteResult && null != deleteResult.getCode() && deleteResult.getCode() == CommonConstant.SC_OK_200), "删除异常");
                    }
                }
            }
            return Result.OK("批量删除成功!");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "oversee_issue-通过id查询")
    @ApiOperation(value = "oversee_issue-通过id查询", notes = "oversee_issue-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OverseeIssue> queryById(@RequestParam(name = "id", required = true) Long id) {
        try {
            Assert.notNull(id, "请传递查询数据");
            OverseeIssue overseeIssue = overseeIssueService.getRedisCacheOverseeIssue(id);
            if(null!=overseeIssue){
                if(StringUtil.isEmpty(overseeIssue.getResponsibleMainOrgIds())&&StringUtil.isNotEmpty(overseeIssue.getResponsibleOrgIds())){
                    overseeIssue.setResponsibleMainOrgIds(overseeIssue.getResponsibleOrgIds());
                }
                // 督办部门数据
                Map<String, Object> selectMap = Maps.newHashMap();
                selectMap.put("issueId",id);
                selectMap.put("issuesProcessType",IssuesSupervisor.issuesProcessMainType);
                overseeIssue.setIssuesSupervisorList(issuesSupervisorService.selectIssuesSupervisorList(selectMap));

//                if(CollectionUtils.isNotEmpty(overseeIssue.getIssuesSupervisorList())){
//                    overseeIssue.setIssuesSupervisorList(overseeIssue.getIssuesSupervisorList().stream().filter((issuesSupervisor)->null!=issuesSupervisor&&null!=issuesSupervisor.getIssuesProcessType()&&issuesSupervisor.getIssuesProcessType().intValue()==1).collect(Collectors.toList()));
//                }
            }else{
                return Result.error("未找到对应数据");
            }
            setOverseeIssueDetailsName(overseeIssue);
            return Result.OK(overseeIssue);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }

    }


    /**
     * 发起督办
     *
     * @param paramMap
     * @return
     */
    @AutoLog(value = "oversee_issue-批量提交")
    @ApiOperation(value = "oversee_issue-批量提交", notes = "oversee_issue-批量提交")
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:edit")
    @RequestMapping(value = "/batchSubmission", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<Map<String, Object>> batchSubmission(@RequestBody Map<String, Object> paramMap) {
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != paramMap && paramMap.size() > 0), "请传递批量审核参数");

            Map<String, Object> map = Maps.newHashMap();
            int launchCount = 0;

            List<Integer> idList = (List<Integer>) paramMap.get("ids");
            Assert.isTrue((CollectionUtils.isNotEmpty(idList)), "请传递批量审核参数ids");
            for (Integer overseeIssueId : idList) {
                try {
                    if (null != overseeIssueId && overseeIssueId.intValue() > 0) {
                        OverseeIssue redisCacheOverseeIssue = overseeIssueService.getRedisCacheOverseeIssue(Long.parseLong(overseeIssueId.toString()));
                        if (null != redisCacheOverseeIssue) {
                            if (null != redisCacheOverseeIssue.getSubmitState() && redisCacheOverseeIssue.getSubmitState().longValue() == 0L) {
                                boolean isLaunch = overseeWorkflowService.overseeIssueLaunchProcess(redisCacheOverseeIssue.getId(), sysUser);
                                if (isLaunch) {
                                    launchCount++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            map.put("launchCount", launchCount);
            Result<Map<String, Object>> result = Result.OK("提交成功，共提交" + launchCount + "个问题");
            result.setResult(map);
            return result;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }


    private void setOverseeIssueDetailsName(OverseeIssue overseeIssue) {
        Assert.isTrue((null != overseeIssue), "请传递数据");
//		TODO 之后通过缓存读取分类数据
        if (null != overseeIssue.getIssueCategoryId() && overseeIssue.getIssueCategoryId().intValue() > 0) {
            OverseeIssueCategory overseeIssueCategoryById = overseeIssueCategoryService.getById(overseeIssue.getIssueCategoryId());
            if (null != overseeIssueCategoryById && null != overseeIssueCategoryById.getId() && overseeIssueCategoryById.getId().intValue() > 0) {
                overseeIssue.setIssueCategoryName(overseeIssueCategoryById.getName());
            }
        }

        if (null != overseeIssue.getIssueSubcategoryId() && overseeIssue.getIssueSubcategoryId().intValue() > 0) {
            OverseeIssueSubcategory overseeIssueSubcategoryById = overseeIssueSubcategoryService.getById(overseeIssue.getIssueSubcategoryId());
            if (null != overseeIssueSubcategoryById && null != overseeIssueSubcategoryById.getId() && overseeIssueSubcategoryById.getId().intValue() > 0) {
                overseeIssue.setIssueSubcategoryName(overseeIssueSubcategoryById.getName());
            }
        }

//		TODO 之后通过缓存查询部门用户等信息
        if (StringUtil.isNotEmpty(overseeIssue.getHeadquartersLeadDepartmentOrgId())) {
            SysDepartModel sysDepartById = commonAPI.getSysDepartById(overseeIssue.getHeadquartersLeadDepartmentOrgId());
            if (null != sysDepartById && StringUtil.isNotEmpty(sysDepartById.getDepartName())) {
                overseeIssue.setHeadquartersLeadDepartmentOrgName(sysDepartById.getDepartName());
            }
        }

        if (StringUtil.isNotEmpty(overseeIssue.getResponsibleUnitOrgId())) {
            SysDepartModel sysDepartById = commonAPI.getSysDepartById(overseeIssue.getResponsibleUnitOrgId());
            if (null != sysDepartById && StringUtil.isNotEmpty(sysDepartById.getDepartName())) {
                overseeIssue.setResponsibleUnitOrgName(sysDepartById.getDepartName());
            }
        }

        if (StringUtil.isNotEmpty(overseeIssue.getResponsibleLeadDepartmentOrgId())) {
            SysDepartModel sysDepartById = commonAPI.getSysDepartById(overseeIssue.getResponsibleLeadDepartmentOrgId());
            if (null != sysDepartById && StringUtil.isNotEmpty(sysDepartById.getDepartName())) {
                overseeIssue.setResponsibleLeadDepartmentOrgName(sysDepartById.getDepartName());
            }
        }


        if (StringUtil.isNotEmpty(overseeIssue.getHeadquartersLeadDepartmentManagerUserId())) {
            SysUserCacheInfo sysUserById = commonAPI.getSysUserById(overseeIssue.getHeadquartersLeadDepartmentManagerUserId());
            if (null != sysUserById && StringUtil.isNotEmpty(sysUserById.getSysUserName())) {
                overseeIssue.setHeadquartersLeadDepartmentManagerUserName(sysUserById.getSysUserName());
            }
        }

    }


    /**
     * 导出excel
     *
     * @param request
     * @param overseeIssue
     */
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OverseeIssue overseeIssue) {
        return super.exportXls(request, overseeIssue, OverseeIssue.class, "oversee_issue");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("oversee_issue:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, OverseeIssue.class);
    }


    @GetMapping(value = "/detailList")
    public Result detailList(OverseeIssueQueryVo query) {
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");
            Map<String, Object> overseeIssueQueryVoMapData = getOverseeIssueQueryVoMapData(query);
            List<String> myOrgList = (List<String>) overseeIssueQueryVoMapData.get("myOrgList");

            IPage<OverseeIssueDetailVo> pageList = overseeIssueService.queryIssueDetailPage(query);
            if (null != pageList && CollectionUtils.isNotEmpty(pageList.getRecords())) {
                for (OverseeIssueDetailVo overseeIssueDetailVo : pageList.getRecords()) {
                    if (null != overseeIssueDetailVo) {
                        if (sysUser.getId().equals(overseeIssueDetailVo.getReportUserId())) {
                            overseeIssueDetailVo.setIsSponsor(1);
                        }
                        overseeIssueDetailVo.setInitiateSupervisionAuthority(isSystemRole() ? 1 : overseeIssueRoleService.isAuthorizeSupervise(overseeIssueDetailVo.getId(), CollectionUtils.isNotEmpty(myOrgList) ? myOrgList.stream().collect(Collectors.joining(",")) : null, sysUser.getId()));
                    }
                }
                overseeIssueService.getExamineAndVerifyData(pageList.getRecords(),false);
            }

            return Result.OKPage(pageList);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    @PostMapping("/updateDepartment")
    public Result updateDepartment(@Valid @RequestBody Map<String,Object> requestMap) {
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");
            Assert.isTrue((null != requestMap && requestMap.size() > 0), "请传递数据");
            Integer updateCount = null;
            if(null!=requestMap){
                updateCount = overseeIssueService.updateDepartmentByMap(requestMap,sysUser);
            }
            return Result.OK("修改完成" + (null!=updateCount?(" : 共修改"+updateCount+"条问题"):""));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }

    }



    @GetMapping("/getOssFileList")
    public Result<IPage<OssFileVo>> getOssFileList(OverseeIssueQueryVo query,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");
        Map<String, Object> overseeIssueQueryVoMapData = getOverseeIssueQueryVoMapData(query);
        IPage<OssFileVo> ossFileVoIPage = overseeIssueService.queryOssFilePage(query);
        return Result.OKPage(ossFileVoIPage);
    }

    private Integer isInitiateSupervisionAuthority(List<String> myOrgList, String... orgIdss) {
//		if(StringUtil.isNotEmpty(userId)){
//			myOrgService.
//		}

        if (null != myOrgList && myOrgList.size() > 0) {
            System.out.println("myOrgList : " + JSONObject.toJSONString(myOrgList));
            if (null != orgIdss && orgIdss.length > 0) {
                System.out.println("orgIdss : " + JSONObject.toJSONString(orgIdss));
                for (String orgIds : orgIdss) {
                    if (StringUtil.isNotEmpty(orgIds)) {
                        String[] orgIdArray = orgIds.split(",");
                        if (null != orgIdArray && orgIdArray.length > 0) {
                            for (String orgId : orgIdArray) {
                                if (StringUtil.isNotEmpty(orgId)) {
                                    if (myOrgList.contains(orgId.trim())) {
                                        return 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }


    @RequestMapping(value = "/importOfflineExcel", method = RequestMethod.POST)
    public Result<?> importOfflineExcel(MultipartFile file) {
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            ExcelUtils<OverseeIssueDetailVo> util = new ExcelUtils<>(OverseeIssueDetailVo.class);
            List<OverseeIssueDetailVo> list = util.importExcel(file.getInputStream());
            Map<String, Object> map = overseeIssueService.importOfflineIssueData(list, sysUser);
            String resultMessage = "数据导入";
            if (null != map) {
                resultMessage = "";
                Integer resultCount = (Integer) map.get("resultCount");
                if (null != resultCount) {
                    resultMessage = "成功导入" + resultCount + "条数据";
                }
                String errorRow = (String) map.get("errorRow");
                if (StringUtil.isNotEmpty(errorRow)) {
                    resultMessage += "; <br> 错误数据行数据 : " + errorRow;
                }
            }
            return Result.OK(resultMessage);
        } catch (Exception ex) {
            log.error("导入线下Excel文件异常", ex);
            return Result.error("导入失败");
        }
    }


    @GetMapping(value = "/importTemplate")
    public Result<?> importTemplate(OverseeIssueQueryVo query) {
        ExcelUtils<OverseeIssueDetailVo> util = new ExcelUtils<>(OverseeIssueDetailVo.class);
        return util.importTemplateExcel("问题数据导入模板");
    }


    @GetMapping(value = "/exportExcel")
    public Result<?> exportExcel(OverseeIssueQueryVo query) {
        IPage<OverseeIssueDetailVo> pageList = getExportExcelDataPage(query);
        ExcelUtils<OverseeIssueDetailVo> util = new ExcelUtils<>(OverseeIssueDetailVo.class);
        return util.exportExcel(pageList.getRecords(), "上报问题数据");
    }

    @PostMapping(value = "/exportExcelNew")
    public Result<?> exportExcelNew(@RequestBody OverseeIssueQueryVo query) {
        IPage<OverseeIssueDetailVo> pageList = getExportExcelDataPage(query);
        List<ExcelHeader> columnAlls = query.getColumnAlls();
        if(query.getIsSelect()!=null&&query.getIsSelect()){
            if(CollectionUtils.isNotEmpty(columnAlls)){
                columnAlls = columnAlls.stream().filter((excelHeader)->null!=excelHeader&&StringUtil.isNotEmpty(excelHeader.getName())&&null!=excelHeader.getChecked()&&excelHeader.getChecked()).collect(Collectors.toList());
            }
        }
        ExcelUtils<OverseeIssueDetailVo> util = new ExcelUtils<>(OverseeIssueDetailVo.class);
        return util.exportExcel(pageList.getRecords(), "上报问题数据", columnAlls);
//        List<ExcelHeader> exportHeader = new ArrayList<ExcelHeader>();
    }


    @GetMapping(value = "/getExportExcelData")
    public Result getExportExcelData(OverseeIssueQueryVo query) {
        try {
            IPage<OverseeIssueDetailVo> pageList = getExportExcelDataPage(query,false);
            return Result.OKPage(pageList);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    private IPage<OverseeIssueDetailVo> getExportExcelDataPage(OverseeIssueQueryVo query){
        return getExportExcelDataPage(query,true);
    }
    private IPage<OverseeIssueDetailVo> getExportExcelDataPage(OverseeIssueQueryVo query,Boolean isTtmlToText){

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
            query.setCreateUserId(sysUser.getId());
        }
        if (StringUtil.isNotEmpty(query.getIds())) {
            String[] idArray = query.getIds().split(",");
            if(null!=idArray&&idArray.length>0){
                query.setIdArray(Arrays.asList(idArray));
            }
        }
        if (query.getExportAll()) {
            query.setPageNo(1L);
            query.setPageSize(Long.MAX_VALUE);
        }

        IPage<OverseeIssueDetailVo> pageList = overseeIssueService.queryIssueDetailPage(query);
        if(null!=pageList){
            overseeIssueService.getExamineAndVerifyData(pageList.getRecords(),isTtmlToText);
        }

        return pageList;
    }

    private void getExamineAndVerifyData(List<OverseeIssueDetailVo> OverseeIssueDetails,Boolean isTtmlToText){
        isTtmlToText = null != isTtmlToText ? isTtmlToText : true;
        String newlineCharacter = isTtmlToText ? String.valueOf((char)10) : "<br>";
        if (CollectionUtils.isNotEmpty(OverseeIssueDetails)) {
            for (OverseeIssueDetailVo detail : OverseeIssueDetails) {
                if (null != detail && null != detail.getId()) {
                    OverseeIssueSpecific overseeIssueSpecific = new OverseeIssueSpecific();
                    overseeIssueSpecific.setIssueId(detail.getId());
                    List<OverseeIssueSpecific> overseeIssueSpecifics = overseeIssueSpecificService.queryList(overseeIssueSpecific);
                    if(StringUtils.isNotEmpty(detail.getResponsibleMainOrgNames()))detail.setResponsibleMainOrgNames(detail.getResponsibleMainOrgNames().replaceAll(",",";"));
                    if(StringUtils.isNotEmpty(detail.getResponsibleCoordinationOrgNames()))detail.setResponsibleCoordinationOrgNames(detail.getResponsibleCoordinationOrgNames().replaceAll(",",";"));
                    if(StringUtils.isNotEmpty(detail.getSupervisorMainOrgNames()))detail.setSupervisorMainOrgNames(detail.getSupervisorMainOrgNames().replaceAll(",",";"));
                    if(StringUtils.isNotEmpty(detail.getSupervisorCoordinationOrgNames()))detail.setSupervisorCoordinationOrgNames(detail.getSupervisorCoordinationOrgNames().replaceAll(",",";"));
                    if (CollectionUtils.isNotEmpty(overseeIssueSpecifics)) {
                        StringBuilder improveAction = new StringBuilder();
                        for (OverseeIssueSpecific specific : overseeIssueSpecifics) {
                            if (specific.getSpecificType() != null && OverseeConstants.SpecificType.IMPROVE_ACTION == specific.getSpecificType().intValue()) {
                                String content = specific.getContent();
                                if(StringUtil.isNotEmpty(content)){
                                    content = content.replaceAll("<br>",newlineCharacter);
                                    improveAction.append(getContentIsTtmlToText(content,isTtmlToText,newlineCharacter));
                                }

                            }
                        }
                        String improveActionSrc = improveAction.toString();
                        if(StringUtil.isNotEmpty(improveActionSrc))improveActionSrc = improveActionSrc.replace("<br>",newlineCharacter);
                        detail.setImproveAction(improveActionSrc);
                    }

                    List<ReasonCancellation> reasonCancellationList = reasonCancellationService.list(Wrappers.<ReasonCancellation>lambdaQuery().eq(ReasonCancellation::getIssueId, detail.getId()).eq(ReasonCancellation::getDataType, 1));
                    if (CollectionUtils.isNotEmpty(reasonCancellationList)) {
                        StringBuilder rectification = new StringBuilder();
                        for (ReasonCancellation reasonCancellation : reasonCancellationList) {
                            if (null != reasonCancellation && StringUtil.isNotEmpty(reasonCancellation.getReason())) {
                                String replace = reasonCancellation.getReason().replace("<br>", newlineCharacter);
                                rectification.append(getContentIsTtmlToText(replace,isTtmlToText,newlineCharacter));
                            }
                        }
                        String rectificationSrc = rectification.toString();
                        if(StringUtil.isNotEmpty(rectificationSrc))rectificationSrc = rectificationSrc.replace("<br>",newlineCharacter);
                        detail.setRectification(rectificationSrc);
                    }

                }
//				 if(CollectionUtils.isNotEmpty(detail.getSpecificList())){
//					 StringBuilder improveAction=new StringBuilder();
//					 for(OverseeIssueSpecific specific:detail.getSpecificList()){
//						 if(specific.getSpecificType()!=null &&OverseeConstants.SpecificType.IMPROVE_ACTION== specific.getSpecificType().intValue()){
//							 improveAction.append(specific.getContent());
//						 }
//					 }
//					 detail.setImproveAction(improveAction.toString());
//				 }

            }
        }
    }

    private String getContentIsTtmlToText(String content,Boolean isTtmlToText,String newlineCharacter){
        isTtmlToText = null != isTtmlToText ? isTtmlToText : true;
        String newContent = content;
        if(StringUtil.isNotEmpty(content)){
            if(isTtmlToText){
                newContent = HTMLUtils.getInnerText(content) ;
                if(StringUtil.isNotEmpty(newContent)){
                    newContent += newlineCharacter;
                }
            }
        }
        return newContent;
    }

    @RequestMapping(value = "/importImproveActionExcel", method = RequestMethod.POST)
    public Result<?> importImproveActionExcel(MultipartFile file, Long issueId) {
        try {
            int successNum = 0;
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            ExcelUtils<OverseeIssueDetailVo> util = new ExcelUtils<>(OverseeIssueDetailVo.class);
            List<OverseeIssueDetailVo> list = util.importExcel(file.getInputStream());
            if (CollectionUtils.isNotEmpty(list)) {
                Date now = new Date();
                for (OverseeIssueDetailVo item : list) {
                    try {
                        if (StringUtils.isNotBlank(item.getImproveAction())) {
                            OverseeIssueSpecific specific = new OverseeIssueSpecific();
                            specific.setContent(item.getImproveAction());
                            specific.setExpectCorrectTime(item.getExpectTime());
                            specific.setUserId(sysUser.getId());
                            specific.setUpdateBy(sysUser.getRealname());
                            specific.setIssueId(issueId);
                            specific.setUpdateTime(now);
                            specific.setSpecificType(OverseeConstants.SpecificType.IMPROVE_ACTION);
                            overseeIssueService.saveIssueSpecific(specific);
                            successNum += 1;
                        }
                    } catch (Exception ex) {
                        log.error("导入整改数据异常", ex);
                    }
                }
            }
//			 int result= overseeIssueService.importOfflineIssueData(list,sysUser);
//			 int result=0;
            return Result.OK("成功导入" + successNum + "条数据");
        } catch (Exception ex) {
            log.error("导入线下Excel文件异常", ex);
            return Result.error("导入失败");
        }
    }

    @RequestMapping(value = "/importImproveActionExcelReturnData", method = RequestMethod.POST)
    public Result<?> importImproveActionExcelReturnData(MultipartFile file, Long issueId) {
        try {
            Result<Map<String, Object>> result = Result.OK("保存成功！");
            Map<String, Object> map = Maps.newHashMap();
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            ExcelUtils<OverseeIssueDetailVo> util = new ExcelUtils<>(OverseeIssueDetailVo.class);
            List<OverseeIssueDetailVo> list = util.importExcel(file.getInputStream());
            map.put("overseeIssueSpecificList",list);
            result.setResult(map);
            return result;
        } catch (Exception ex) {
            log.error("导入线下Excel文件异常", ex);
            return Result.error("导入失败");
        }
    }


    @GetMapping(value = "/importIssuesRectificationMeasureTemplate")
    public Result<?> importIssuesRectificationMeasureTemplate(OverseeIssueQueryVo query) {
        ExcelUtils<IssuesRectificationMeasureVo> util = new ExcelUtils<>(IssuesRectificationMeasureVo.class);
        return util.importTemplateExcel("问题数据导入模板");
    }

}
