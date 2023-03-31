package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.util.UUIDGenerator;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.mapper.MyOrgMapper;
import com.chd.modules.oversee.hdmy.mapper.MyUserMapper;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueFlowOrg;
import com.chd.modules.oversee.issue.entity.OverseeIssueSpecific;
import com.chd.modules.oversee.issue.mapper.OverseeIssueFlowOrgMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSpecificMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueFlowOrgService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.service.WorkflowCandidateGroupService;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowProcessDepartService;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Service
@Slf4j
public class OverseeIssueFlowOrgServiceImpl implements IOverseeIssueFlowOrgService {


    @Autowired
    private MyOrgMapper myOrgMapper;
    @Autowired
    private MyUserMapper myUserMapper;
    @Autowired
    private OverseeIssueMapper overseeIssueMapper;
    @Autowired
    private OverseeIssueFlowOrgMapper overseeIssueFlowOrgMapper;
    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;
    @Autowired
    private WorkflowCandidateGroupService workflowCandidateGroupService;
    @Autowired
    private OverseeIssueSpecificMapper overseeIssueSpecificMapper;

    public static final String VARIABLEID_OWNER_DEPART_MANAGE="OWNER_DEPART_MANAGE";//本部牵头部门负责人
    public static final String VARIABLEID_OWNER_DEPART_SUPERVISOR="OWNER_DEPART_SUPERVISOR";//本部牵头部门分管领导
    public static final String VARIABLEID_RESPONSIBLE_DEPART_MANAGE="RESPONSIBLE_DEPART_MANAGE";//责任部门负责人
    public static final String VARIABLEID_RESPONSIBLE_DEPART_SUPERVISOR="RESPONSIBLE_DEPART_SUPERVISOR";//责任部门分管领导
    public static final String VARIABLEID_SUPERVISOR_DEPART_MANAGE="SUPERVISOR_DEPART_MANAGE";//督办部门负责人
    public static final String VARIABLEID_SUPERVISOR_DEPART_SUPERVISOR="SUPERVISOR_DEPART_SUPERVISOR";//督办部门分管领导


    @Override
    public List<WorkflowUserTaskVo> getBizVariableUser(List<WorkflowUserTaskVo> userTasks, String category, String bizId, String startUserId) {
        if(StringUtils.isNotBlank(bizId)) {
            OverseeIssue overseeIssue=overseeIssueMapper.selectById(Integer.valueOf(bizId));
            if(CollectionUtils.isNotEmpty( userTasks)){
                for(WorkflowUserTaskVo userTask:userTasks){
                    if(CollectionUtils.isNotEmpty(userTask.getVariableUser())){
                        Iterator<WorkflowUserVo> users=userTask.getVariableUser().iterator();
                        while (users.hasNext()){
                            WorkflowUserVo user=users.next();
                            List<WorkflowUserVo> orgUser=getOverseeIssueTaskOrgUser(overseeIssue,user.getId());
                            if(CollectionUtils.isNotEmpty( orgUser)){
                                userTask.getUsers().addAll(orgUser);
                                users.remove();
                            }

                        }

                    }
                }

            }
        }
        return userTasks;
    }


    @Override
    public synchronized OverseeIssueFlowOrg getOverseeIssueFlowOrg(String orgId,String variableId){
        QueryWrapper query=new QueryWrapper();
        query.eq("org_id",orgId);
        query.eq("flow_user_Id",variableId);
        OverseeIssueFlowOrg flowOrg= overseeIssueFlowOrgMapper.selectOne(query);
        if(flowOrg==null){
            QueryWrapper queryOrg= new QueryWrapper();
            queryOrg.eq("org_id", orgId);
            MyOrg myOrg = myOrgMapper.selectOne(queryOrg);
            if(myOrg!=null) {
                Date now = new Date();
                flowOrg = new OverseeIssueFlowOrg();
                flowOrg.setId(UUIDGenerator.generate());
                flowOrg.setOrgId(orgId);
                flowOrg.setFlowUserId(variableId);
                flowOrg.setName(myOrg.getOrgName());
                flowOrg.setCreateTime(now);
                flowOrg.setUpdateTime(now);
                overseeIssueFlowOrgMapper.insert(flowOrg);
            }
        }
        return flowOrg;
    }

    @Override
    public List<WorkflowUserVo> getOverseeIssueTaskOrgUser(OverseeIssue overseeIssue, String variableId){
        List<WorkflowUserVo> result=new ArrayList<>();
        String orgIds=getOverseeIssueOrgIds(overseeIssue,variableId);
        if(StringUtils.isNotBlank(orgIds)){
            result=convertTaskVariableUserByOrgIds(variableId,orgIds);
        }
        return result;
    }

    private void setDepartVariables(String source,String role,List<WorkflowDepart> workflowDeparts,Map<String, List<WorkflowDepart>> result){

        result.put(source+'_'+role,workflowDeparts);
    }
    @Override
    public Map<String, List<WorkflowDepart>> getBizDepartVariables(String category, String bizId, String startUserId) {
        Map<String,List<WorkflowDepart>> result=new HashMap<>();
        OverseeIssue overseeIssue=overseeIssueMapper.selectById(Integer.valueOf(bizId));
        if(overseeIssue!=null) {
            String[] sources=new String[]{WorkflowConstants.DEPART_SOURCE.INITIATOR,WorkflowConstants.DEPART_SOURCE.SUPERVISOR,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE};
            String[] departIds=new String[]{overseeIssue.getHeadquartersLeadDepartmentOrgId(),overseeIssue.getSupervisorOrgIds(),overseeIssue.getResponsibleLeadDepartmentOrgId()};
            String[] roles=new String[]{WorkflowConstants.DEPART_ROLE.MANAGER,WorkflowConstants.DEPART_ROLE.SUPERVISOR};
            for(int i=0,k=sources.length;i<k;i++) {
                String orgIds = departIds[i];
                String source=sources[i];
                List<WorkflowProcessDepart> processDepartList = workflowProcessDepartService.toProcessDepart(orgIds, source);
                for (String role : roles) {
                    List<WorkflowDepart> departList = workflowProcessDepartService.getDeparts(processDepartList, role);
                    result.put(source + '_' + role, departList);
                }
            }
        }
        return result;
    }

    @Override
    public String getOverseeIssueOrgIds(OverseeIssue overseeIssue,String variableId){
        String orgIds=null;
        if(VARIABLEID_OWNER_DEPART_MANAGE.equals(variableId) || VARIABLEID_OWNER_DEPART_SUPERVISOR.equals(variableId)){//本部牵头部门负责人\本部牵头分管领导
            orgIds=overseeIssue.getHeadquartersLeadDepartmentOrgId();
        }else if(VARIABLEID_RESPONSIBLE_DEPART_MANAGE.equals(variableId)||VARIABLEID_RESPONSIBLE_DEPART_SUPERVISOR.equals(variableId)){//责任部门负责人\责任部门分管领导
            orgIds=overseeIssue.getResponsibleLeadDepartmentOrgId();

        }else if(VARIABLEID_SUPERVISOR_DEPART_MANAGE.equals(variableId)||VARIABLEID_SUPERVISOR_DEPART_SUPERVISOR.equals(variableId)){//督办部门负责人\督办部门分管领导
            orgIds=overseeIssue.getResponsibleLeadDepartmentOrgId();
        }
        return orgIds;
    }

//    @Override
//    public void taskFormData2Biz(String bizId, WorkflowTaskVo task, WorkflowUserVo user, WorkflowTaskFormVo formData) {
//        try {
//            if (StringUtils.isNotBlank(bizId)) {
//                Long issueId = Long.valueOf(bizId);
//                OverseeIssue overseeIssue=overseeIssueMapper.selectById(issueId);
//                if (overseeIssue == null){
//                    return ;
//                }
//                String formId =formData.getFormId();
//                 if("deptsSelect".equals(formId)) {//分配责任部门,
//                     String depts=MapUtils.getString(formData.getForm(),"depts");
//                     if(StringUtils.isNotBlank(depts)){
//                         if(StringUtils.isBlank(overseeIssue.getResponsibleOrgIds())) {
//                             overseeIssue.setResponsibleOrgIds(depts);
//                         }else{
//                             overseeIssue.setResponsibleOrgIds(overseeIssue.getResponsibleOrgIds()+","+depts);
//                         }
//                         overseeIssueMapper.updateById(overseeIssue);
//                     }
//                 } else {
//                     String content = MapUtils.getString(formData.getForm(), "text");
//                     String formDataId = MapUtils.getString(formData.getForm(), "id");
//                     if ( StringUtils.isNotBlank(formDataId) && "IMPROVE_ACTION".equals(formDataId) && StringUtils.isNotBlank(content)) {
//                         OverseeIssueSpecific specific = new OverseeIssueSpecific();
//                         specific.setIssueId(issueId);
//                         specific.setSpecificType(OverseeConstants.SpecificType.IMPROVE_ACTION);
//                         specific.setContent(content);
//                         specific.setUserId(user.getId());
//                         specific.setUpdateBy(user.getName());
//                         specific.setUpdateTime(new Date());
//                         specific.setCreateTime(specific.getUpdateTime());
//                         specific.setCreateBy(specific.getUpdateBy());
//                         overseeIssueSpecificMapper.insert(specific);
//                     }
//                 }
//            }
//        }catch (Exception ex){
//            log.error("上报问题处理流程审批提交表单数据异常:bizId="+bizId,ex);
//        }
//    }

    private List<WorkflowUserVo> convertTaskVariableUserByOrgIds(String variableId, String orgIds){
        List<WorkflowUserVo> result=new ArrayList<>();
        if(StringUtils.isNotBlank(orgIds)) {
            String[] orgIdArray=orgIds.split(",");
            for(String orgId:orgIdArray) {
                if(StringUtils.isNotBlank(orgId)) {
                    List<WorkflowUserVo> users = convertTaskVariableUserByOrgId(variableId, orgId.trim());
                    if(CollectionUtils.isNotEmpty(users)){
                        result.addAll(users);
                    }
                }
            }
        }
        return result;
    }

    public List<WorkflowUserVo> convertTaskVariableUserByOrgId(String variableId, String orgId) {
        List<WorkflowUserVo> result = new ArrayList<>();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("org_id", orgId);
        MyOrg myOrg = myOrgMapper.selectOne(queryWrapper);
        if (myOrg == null) {
            return result;
        }
        MyUser myUser = null;
        if (VARIABLEID_OWNER_DEPART_MANAGE.equals(variableId) || VARIABLEID_RESPONSIBLE_DEPART_MANAGE.equals(variableId) || VARIABLEID_SUPERVISOR_DEPART_MANAGE.equals(variableId)){
            QueryWrapper queryUser = new QueryWrapper();
            queryUser.eq("user_id", myOrg.getManagerId());
            myUser = myUserMapper.selectOne(queryUser);
        }else if (VARIABLEID_OWNER_DEPART_SUPERVISOR.equals(variableId) || VARIABLEID_RESPONSIBLE_DEPART_SUPERVISOR.equals(variableId) || VARIABLEID_SUPERVISOR_DEPART_SUPERVISOR.equals(variableId)){
            QueryWrapper queryUser = new QueryWrapper();
            queryUser.eq("user_id", myOrg.getUpperSupervisorId());
            myUser = myUserMapper.selectOne(queryUser);
        }
        if (myUser != null) {
            WorkflowUserVo user = new WorkflowUserVo(myUser.getUserId(), myUser.getUserName());
            result.add(user);
        }

        return result;
    }
}
