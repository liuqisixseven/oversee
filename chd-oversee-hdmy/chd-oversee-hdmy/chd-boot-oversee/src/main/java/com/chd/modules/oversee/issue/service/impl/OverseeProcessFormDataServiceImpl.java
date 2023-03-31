package com.chd.modules.oversee.issue.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.mapper.OverseeIssueFileMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSpecificMapper;
import com.chd.modules.oversee.issue.mapper.SpecificIssuesMapper;
import com.chd.modules.oversee.issue.service.IIssuesRectificationMeasureService;
import com.chd.modules.oversee.issue.service.IOverseeIssueAppendixService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.IOverseeProcessFormDataService;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OverseeProcessFormDataServiceImpl implements IOverseeProcessFormDataService {

    @Autowired
    private OverseeIssueMapper overseeIssueMapper;
    @Autowired
    SpecificIssuesMapper specificIssuesMapper;
    @Autowired
    private OverseeIssueSpecificMapper overseeIssueSpecificMapper;
    @Autowired
    private OverseeIssueFileMapper overseeIssueFileMapper;

    @Autowired
    private IOverseeIssueService overseeIssueService;

    @Autowired
    private IIssuesRectificationMeasureService issuesRectificationMeasureService;

    @Autowired
    private IOverseeIssueAppendixService overseeIssueAppendixService;

    @Autowired
    private IMyUserService myUserService;





    @Override
    public void receiveTaskFormData(OverseeIssue overseeIssue, WorkflowUserVo user, WorkflowTaskFormVo formData, WorkflowTaskVo task){
        String formId = formData.getFormId();

        if(null==user){
            user = new WorkflowUserVo("admin","admin");
        }

        //处理分配责任部门
        if (WorkflowConstants.TASK_FORMID.DEPTS_SELECT.equals(formId)) {
            handleAssignResponsibleOrg(formData,overseeIssue,user);
        }
        //流程发起者修改上报问题（流程驳回到发起者节点）
        if(WorkflowConstants.TASK_FORMID.PROCESS_OWNER_FORM.equals(formId)){
            handleModifyIssueContext(formData,overseeIssue,user);
        }

        if(WorkflowConstants.TASK_FORMID.SELECT_TASK_USER.equals(formId)){
            // 判断是不是指定责任单位牵头部门经办人
            String source = MapUtils.getString(formData.getForm(),"source");
            String userIds = MapUtils.getString(formData.getForm(),"userIds");
            if(WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE.equals(source)&&StringUtils.isNotEmpty(userIds)){
                if(null!=overseeIssue&&null!=overseeIssue.getId()){
                    OverseeIssue overseeIssueU = new OverseeIssue();
                    overseeIssueU.setId(overseeIssue.getId());
                    overseeIssueU.setResponsibleLeadDepartmentUserId(userIds);
                    overseeIssueU.setUpdateUserId(user.getId());
                    overseeIssueService.addOrUpdate(overseeIssueU);
                }
            }
        }
        //处理提交整改整改措施
       boolean handleImproveActionResult=handleSaveImproveAction(formData,overseeIssue,user);
        if(!handleImproveActionResult){//除了整改措施附件，其他都统一放在一张表集中保存
            //保存上传附件
            saveOverseeIssueFiles(overseeIssue,user,formData,task);
        }
    }


    /**
     * 处理分配责任部门
     */
    private boolean handleAssignResponsibleOrg(WorkflowTaskFormVo formData,OverseeIssue overseeIssue, WorkflowUserVo user){
        boolean result=false;
        String depts = MapUtils.getString(formData.getForm(), "depts");
        String responsibleMainOrgIds = MapUtils.getString(formData.getForm(), "responsibleMainOrgIds");
        String responsibleCoordinationOrgIds = MapUtils.getString(formData.getForm(), "responsibleCoordinationOrgIds");
        if(StringUtils.isEmpty(responsibleMainOrgIds)&&StringUtils.isNotEmpty(depts)){
            responsibleMainOrgIds = depts;
        }
        if (StringUtils.isNotBlank(responsibleMainOrgIds)) {
            OverseeIssue overseeIssueU = new OverseeIssue();
            overseeIssueU.setId(overseeIssue.getId());
            overseeIssueU.setResponsibleMainOrgIds(responsibleMainOrgIds);
            overseeIssueU.setResponsibleCoordinationOrgIds(responsibleCoordinationOrgIds);
            overseeIssueU.setUpdateUserId(user.getId());

//            if (StringUtils.isBlank(overseeIssue.getResponsibleOrgIds())) {
//                overseeIssue.setResponsibleOrgIds(depts);
//            } else {
//                overseeIssue.setResponsibleOrgIds(overseeIssue.getResponsibleOrgIds() + "," + depts);
//            }
//            overseeIssue.setUpdateUserId(user.getId());

            overseeIssueService.addOrUpdate(overseeIssueU);
            result=true;
        }
        return result;
    }

    /**
     * 流程发起者修改上报问题（流程驳回到发起者节点）
     * @param formData
     * @param overseeIssue
     * @param user
     */
    private boolean handleModifyIssueContext(WorkflowTaskFormVo formData,OverseeIssue overseeIssue,WorkflowUserVo user){
        boolean result=false;
        String content = MapUtils.getString(formData.getForm(), "text");
        if(StringUtils.isNotBlank(content)) {
            SpecificIssues specificIssues = new SpecificIssues();
            specificIssues.setSpecificIssuesContent(content);
            specificIssues.setUpdateTime(new Date());
            specificIssues.setUpdateUserId(user.getId());
            specificIssues.setId(overseeIssue.getSpecificIssuesId());
            specificIssuesMapper.updateById(specificIssues);
            result=true;
        }
        return result;
    }


    /**
     * 处理提交整改整改措施
     * @param formData
     * @param overseeIssue
     * @param user
     */
    private boolean handleSaveImproveAction(WorkflowTaskFormVo formData,OverseeIssue overseeIssue,WorkflowUserVo user){
        boolean result=false;
        String formId = formData.getFormId();
        String departId = (String) formData.getForm().get(WorkflowConstants.DEPART_ID_KEY);
        String updateUserId = (String) formData.getForm().get(WorkflowConstants.UPDATE_USERID_KEY);
        String taskId = formData.getTaskId();

        String formDataId = MapUtils.getString(formData.getForm(), "id");
        String content = MapUtils.getString(formData.getForm(), "text");
        if (StringUtils.isNotBlank(formId) && WorkflowConstants.TASK_FORMID.TEXT_AND_TIME.equals(formId)) {//导入整改措施
            List<Map<String, Object>> issuesRectificationMeasureList = (List<Map<String, Object>>) formData.getForm().get(WorkflowConstants.TEXT_AND_TIME_LIST_ITEM_NAME.ISSUES_RECTIFICATION_MEASURE_LIST);
            if(null!=issuesRectificationMeasureList&&issuesRectificationMeasureList.size()>0){
                List<IssuesRectificationMeasure> issuesRectificationMeasures = JSONArray.parseArray(JSONObject.toJSONString(issuesRectificationMeasureList), IssuesRectificationMeasure.class);
                if(null!=issuesRectificationMeasures&&issuesRectificationMeasures.size()>0){
//                    issuesRectificationMeasureService.remove(Wrappers.<IssuesRectificationMeasure>lambdaQuery().eq(IssuesRectificationMeasure::getIssueId, overseeIssue.getId()));
//                    删除当前问题id当前部门数据
                    if(StringUtils.isEmpty(departId)){
                        List<IssuesRectificationMeasure> issuesRectificationMeasureOrgs = issuesRectificationMeasures.stream().filter((issuesRectificationMeasureS) -> null != issuesRectificationMeasureS && StringUtils.isNotEmpty(issuesRectificationMeasureS.getOrgId())).collect(Collectors.toList());
                        if(null!=issuesRectificationMeasureOrgs&&issuesRectificationMeasureOrgs.size()>0){
                            departId = issuesRectificationMeasureOrgs.get(0).getOrgId();
                        }
                    }

                    if(StringUtils.isEmpty(departId)&&StringUtils.isNotEmpty(updateUserId)){
                        MyUser myUserById = myUserService.getById(updateUserId);
                        if(null!=myUserById&& org.apache.commons.lang3.StringUtils.isNotEmpty(myUserById.getOrgId())){
                            departId = myUserById.getOrgId();
                        }
                    }

                    if(StringUtils.isNotEmpty(departId)){
                        issuesRectificationMeasureService.update(null, new LambdaUpdateWrapper<IssuesRectificationMeasure>().eq(IssuesRectificationMeasure::getIssueId,overseeIssue.getId()).eq(IssuesRectificationMeasure::getOrgId,departId).eq(IssuesRectificationMeasure::getDataType,1).set(IssuesRectificationMeasure::getDataType,-1));
                    }
                    issuesRectificationMeasureService.addOrUpdateList(issuesRectificationMeasures,overseeIssue.getId(),departId,updateUserId,taskId,user.getName());
                }
            }
            String files = MapUtils.getString(formData.getForm(), "files");
            if(StringUtils.isNotEmpty(files)){
                List<OverseeIssueAppendix> overseeIssueAppendices = new ArrayList<>();
                try{
                    JSONArray filesJSONArray = JSONArray.parseArray(files);
                    if(null!=filesJSONArray&&filesJSONArray.size()>0){
                        for(int i=0; i< filesJSONArray.size(); i++){
                            String fileId = filesJSONArray.getString(i);
                            if(StringUtils.isNotEmpty(fileId)){
                                OverseeIssueAppendix overseeIssueAppendix = new OverseeIssueAppendix();
                                overseeIssueAppendix.setAppendixPath(fileId);
                                overseeIssueAppendices.add(overseeIssueAppendix);
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                if(CollectionUtils.isEmpty(overseeIssueAppendices)){
                    overseeIssueAppendices = JSONArray.parseArray(files, OverseeIssueAppendix.class);
                }

                //                  更新附件
                if(null!=overseeIssueAppendices&&overseeIssueAppendices.size()>0){
                    overseeIssueAppendixService.synchronizationOverseeIssueAppendixListFileName(overseeIssueAppendices);
                    for(OverseeIssueAppendix overseeIssueAppendix : overseeIssueAppendices){
                        overseeIssueAppendix.setUpdateUserId(updateUserId);
                        overseeIssueAppendix.setIssueId(overseeIssue.getId());
                        overseeIssueAppendix.setTaskId(taskId);
                    }
                }
                overseeIssueAppendixService.addOrUpdateList(overseeIssueAppendices,overseeIssue.getId(),OverseeIssueAppendix.RECTIFICATION_MEASURES_TYPE,updateUserId,departId);
            }
            result=true;
        }
        return result;
    }


    private boolean saveOverseeIssueFiles(OverseeIssue overseeIssue,WorkflowUserVo user, WorkflowTaskFormVo formData,WorkflowTaskVo task){
        boolean result=false;
        String taskId=task!=null?task.getTaskId():null;
        String files = MapUtils.getString(formData.getForm(), "files");
        if(StringUtils.isNotBlank(files)) {
            String fileIds=files.replaceAll(" ","");;
            if (StringUtils.isNotBlank(fileIds) && fileIds.startsWith("[") && fileIds.endsWith("]")) {
                fileIds = fileIds.substring(1, fileIds.length() - 1);
            }
            if(StringUtils.isNotBlank(fileIds)) {
                OverseeIssueFile issueFile = new OverseeIssueFile();
                issueFile.setUserId(user.getId());
                issueFile.setUpdateBy(user.getName());
                issueFile.setUpdateTime(new Date());
                issueFile.setCreateTime(issueFile.getUpdateTime());
                issueFile.setCreateBy(issueFile.getUpdateBy());
                issueFile.setIssueId(overseeIssue.getId());
                issueFile.setFiles(fileIds);
                issueFile.setTaskId(taskId);
                issueFile.setSpecificType(null);
                overseeIssueFileMapper.insert(issueFile);
                result = true;
            }
        }
        return result;
    }
}
