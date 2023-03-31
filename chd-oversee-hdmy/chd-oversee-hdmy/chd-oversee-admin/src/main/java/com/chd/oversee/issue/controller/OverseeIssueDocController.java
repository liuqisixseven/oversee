package com.chd.oversee.issue.controller;

import com.chd.common.api.CommonAPI;
import com.chd.common.api.vo.Result;
import com.chd.common.constant.CommonConstant;
import com.chd.common.system.vo.DictModel;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.common.util.oConvertUtils;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.IOverseeWorkflowService;
import com.chd.modules.system.model.SysDepartTreeModel;
import com.chd.modules.system.service.ISysDepartService;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.vo.WorkflowProcessDetailVo;
import com.chd.modules.workflow.vo.WorkflowProcessVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import com.chd.oversee.issue.service.OverseeIssueDocServiceImpl;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.common.engine.impl.util.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Api(tags = "问题归档")
@RestController
@RequestMapping("/oversee/issue")
@Slf4j
public class OverseeIssueDocController {

    @Autowired
    private OverseeIssueDocServiceImpl overseeIssueService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    protected CommonAPI commonAPI;
    //    @Autowired
//    private WorkflowProcessUsersSerivce workflowProcessUsersSerivce;
//    @Autowired
//    private WorkflowProcessDepartService workflowProcessDepartService;
//    @Autowired
//    private WorkflowUserService workflowUserService;
//    @Autowired
//    private WorkflowVariablesService workflowVariablesService;
    @Autowired
    private ISysDepartService sysDepartService;

    @GetMapping("/issueToDoc")
    @ApiOperation(value = "oversee_issue-问题归档", notes = "oversee_issue-问题归档")
    @ApiImplicitParam(name = "paramsMap", paramType = "body", examples = @Example({
            @ExampleProperty(value = "{'issueIds':'问题id,List类型参数','docPath':'问题文档路径，String类型参数'}", mediaType = "application/json"),
    }))
    public void issueToDoc(String issueIds, HttpServletRequest req, HttpServletResponse response) throws Exception {
        response.setContentType("application/x-zip-compressed");
        File file = overseeIssueService.issueToDoc(issueIds,req,response);
        String filename = URLEncoder.encode(file.getName(), "UTF-8");
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);//浏览器上提示下载时默认的文件名
        OutputStream os = response.getOutputStream();
        FileInputStream fis =new FileInputStream(file);
        IOUtils.copy(fis,os);
        os.flush();
        fis.close();
        os.close();
        file.deleteOnExit();
    }


    @GetMapping(value = "/getIssueDepart")
    @ApiOperation(value = "获取流程节点责任配合部门列表", notes = "获取流程节点责任配合部门列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "processId", value = "流程实例id", dataType = "String", required = true),
            @ApiImplicitParam(name = "ids", value = "部门ids", dataType = "String", required = true)
    })
    public Result getIssueDepart(String processId, String ids) {
        List<WorkflowUserTaskVo> workflowUserTaskVos = workflowService.findNextTaskByProcessId(processId);
        String taskKey = workflowUserTaskVos.get(0).getTaskKey();
        List<DictModel> taskSetList = commonAPI.queryDictItemsByCode("work_flow_depart");
        List<String> taskKeyList = Lists.newArrayList();
        for (DictModel dictModel : taskSetList) {
            if (null != dictModel && StringUtil.isNotEmpty(dictModel.getValue())) {
                taskKeyList.add(dictModel.getValue());
            }
        }
        if (taskKeyList.contains(taskKey)) {
            Result.OK(null);
        }
        //responsibleOrgIds
//        WorkflowProcessDetailVo result=workflowService.processDetail(processId);
//        WorkflowProcessVo processVo= result.getProcess();
        Result<List<SysDepartTreeModel>> result = new Result<>();
        if (oConvertUtils.isNotEmpty(ids)) {
            List<SysDepartTreeModel> departList = sysDepartService.queryTreeList(ids);
            result.setResult(departList);
        } else {
            Result.OK(null);
        }
        result.setSuccess(true);
        return Result.OK(result);
    }
}
