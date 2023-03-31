package com.chd.modules.workflow.controller;


import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.workflow.entity.WorkflowCandidateGroup;
import com.chd.modules.workflow.entity.WorkflowTaskFormTag;
import com.chd.modules.workflow.service.WorkflowCandidateGroupService;
import com.chd.modules.workflow.service.WorkflowManageService;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/workflow/mng")
@Slf4j
public class WorkflowManageController {
    @Autowired
    private WorkflowManageService workflowManageService;
    @Autowired
    private WorkflowCandidateGroupService workflowCandidateGroupService;

    @GetMapping("/candidateGroupList")
    public Result getCandidateGroupList(WorkflowCandidateGroup query){
        List<WorkflowCandidateGroup> list= workflowCandidateGroupService.candidateGroupList(query);
        return Result.OK(list);
    }

    @PostMapping("/saveCandidateGroup")
    public Result saveTaskRoleTag(@RequestBody WorkflowCandidateGroup candidateGroup){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        candidateGroup.setUpdateBy(sysUser.getUsername());
        int result=workflowCandidateGroupService.saveCandidateGroup(candidateGroup);
        return Result.OK(result);
    }

    @GetMapping("/candidateGroupDetail")
    public Result candidateGroupDetail(String id){
        WorkflowCandidateGroup result= workflowCandidateGroupService.findById(id);
        return Result.OK(result);
    }


    @GetMapping("/taskFormTag/list")
    public Result getTaskFormTagList(WorkflowTaskFormTag query){
        List<WorkflowTaskFormTag> list= workflowManageService.taskFormTagList(query);
        return Result.OK(list);
    }



    @PostMapping("/taskFormTag/save")
    public Result saveTaskFormTag(@RequestBody WorkflowTaskFormTag workflowTaskFormTag){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        workflowTaskFormTag.setUpdateBy(sysUser.getUsername());
        int result=workflowManageService.saveTaskFormTag(workflowTaskFormTag);
        return Result.OK(result);
    }



    @GetMapping("/taskFormTag/detail")
    public Result getTaskFormTagDetail(String code){
        WorkflowTaskFormTag result= workflowManageService.getTaskFormTag(code);
        return Result.OK(result);
    }


    @DeleteMapping("/taskFormTag/delete")
    public Result deleteTaskFormTag(String id){
        int result= workflowManageService.deleteTaskFormTag(id);
        return Result.OK(result);
    }

    /**
     * 获取审批用户
     * @param pageSize
     * @return
     */
    @GetMapping("/approveUsers")
    public Result approveUsers(Integer pageSize){
        if(pageSize==null){
            pageSize=10000;
        }
        List<WorkflowUserVo> userList=workflowManageService.approveUserList(1,pageSize);
        return Result.OK(userList);
    }


}
