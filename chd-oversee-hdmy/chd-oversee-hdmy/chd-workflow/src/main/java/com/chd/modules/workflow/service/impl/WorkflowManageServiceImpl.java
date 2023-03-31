package com.chd.modules.workflow.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chd.common.util.StringUtil;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowTaskFormTag;
import com.chd.modules.workflow.mapper.WorkflowDepartMapper;
import com.chd.modules.workflow.mapper.WorkflowTaskFormTagMapper;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 流程管理
 */
@Service
public class WorkflowManageServiceImpl implements WorkflowManageService {
    @Autowired
    private WorkflowTaskFormTagMapper workflowTaskFormTagMapper;
    @Autowired
    private WorkflowUserService workflowUserService;
    @Autowired
    private WorkflowDepartMapper workflowDepartMapper;
    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;

    @Autowired
    private HistoryService historyService;
    @Autowired
    private WorkflowVariablesService workflowVariablesService;

    @Override
    public List<WorkflowTaskFormTag> taskFormTagList(WorkflowTaskFormTag query){
        LambdaQueryWrapper<WorkflowTaskFormTag> queryWrapper=new LambdaQueryWrapper<WorkflowTaskFormTag>();
        if(StringUtils.isNotBlank(query.getCode())) {
            queryWrapper.eq(WorkflowTaskFormTag::getCode, query.getCode());
        }
        if(StringUtils.isNotBlank(query.getName())){
            queryWrapper.like(WorkflowTaskFormTag::getName,query.getName());
        }
        return workflowTaskFormTagMapper.selectList(queryWrapper);
    }

    @Override
    public  WorkflowTaskFormTag getTaskFormTag(String code){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("code",code);
        return workflowTaskFormTagMapper.selectOne(queryWrapper);
    }

    @Override
    public int saveTaskFormTag(WorkflowTaskFormTag taskFormTag) {
        Asserts.notEmpty(taskFormTag.getCode(),"编码不能为空");
        Asserts.notEmpty(taskFormTag.getName(),"编码不能为空");
        Date now=new Date();
        WorkflowTaskFormTag dbTaskFormTag= getTaskFormTag(taskFormTag.getCode());
        if(dbTaskFormTag==null){
            taskFormTag.setUpdateTime(now);
            taskFormTag.setCreateBy(taskFormTag.getUpdateBy());
            taskFormTag.setCreateTime(taskFormTag.getUpdateTime());
            return workflowTaskFormTagMapper.insert(taskFormTag);
        }else{
            dbTaskFormTag.setUpdateBy(taskFormTag.getUpdateBy());
            dbTaskFormTag.setUpdateTime(now);
            return workflowTaskFormTagMapper.updateById(dbTaskFormTag);
        }

    }

    @Override
    public int deleteTaskFormTag(String code){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("code",code);
        return workflowTaskFormTagMapper.delete(queryWrapper);
    }

    @Override
    public List<WorkflowUserVo> approveUserList(int pageNo, int pageSize){
        List<WorkflowUserVo> result=null;
        List<WorkflowUserVo> userlist= workflowUserService.allUserList(pageNo,pageSize);
        if(CollectionUtils.isNotEmpty(userlist)){
            result=userlist;
        }else{
            result=new ArrayList<>();
        }
//        List<WorkflowTaskRoleTag> taskRoleTagList= taskRoleTagList(new WorkflowTaskRoleTag());
//        if(CollectionUtils.isNotEmpty(taskRoleTagList)){
//            for(WorkflowTaskRoleTag roleTag:taskRoleTagList){
//                result.add(0,new WorkflowUserVo(roleTag.getCode(),roleTag.getName()));
//            }
//        }
        return result;
    }


    @Override
    public List<WorkflowUserVo> orgUserListByIds(List<String> ids) {
        return null;
    }


    public List<WorkflowUserTaskVo> convertUserTask(List<UserTask> userTasks) {
        return convertUserTask(userTasks,null);
    }
    @Override
    public List<WorkflowUserTaskVo> convertUserTask(List<UserTask> userTasks,String processId) {
        List<WorkflowUserTaskVo> result=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userTasks)){
            List<String> taskRoleList=new ArrayList<>();
            List<String> userIdList=new ArrayList<>();
            for(UserTask userTask:userTasks){
                WorkflowUserTaskVo user=new WorkflowUserTaskVo();
                user.setName(userTask.getName());
                user.setFormKey(userTask.getFormKey());
                user.setTaskKey(userTask.getId());
                user.setTaskId(userTask.getId());
                if(StringUtils.isNotBlank(userTask.getAssignee())){//任务的受理人，即执行人
                    user.setUserType(1);
                    mountUserTask(user,taskRoleList,userIdList,userTask.getAssignee());
                }else if(StringUtils.isNotBlank(userTask.getOwner())){
                    user.setUserType(2);
                    mountUserTask(user,taskRoleList,userIdList,userTask.getOwner());
                }else if(CollectionUtils.isNotEmpty(userTask.getCandidateUsers())){
                    user.setUserType(3);
                    for(String candidateUser:userTask.getCandidateUsers()){
                        mountUserTask(user,taskRoleList,userIdList,candidateUser);
                    }
                }else if(CollectionUtils.isNotEmpty(userTask.getCandidateGroups())){
                    user.setUserType(4);
                    for(String candidateUser:userTask.getCandidateGroups()){
                        mountUserTask(user,taskRoleList,userIdList,candidateUser);
                    }
                }
                result.add(user);
            }

            Map<String,WorkflowUserVo> userMap=new HashMap<>();
            if(CollectionUtils.isNotEmpty( userIdList)){
                List<WorkflowUserVo> users= workflowUserService.userListByIds(userIdList);
                if(CollectionUtils.isNotEmpty(users)) {
                    for(WorkflowUserVo user:users){
                        userMap.put(user.getId(),user);
                    }

                }
            }
            Map<String,WorkflowDepart> tagMap=new HashMap<>();
            if(CollectionUtils.isNotEmpty(taskRoleList)){

                List<WorkflowDepart> roleTags= workflowDepartMapper.departListByIds(taskRoleList);
                if(CollectionUtils.isNotEmpty(roleTags)){
                    for(WorkflowDepart tag:roleTags){
                        tagMap.put(tag.getId(),tag);
                    }
                }
            }
            for (WorkflowUserTaskVo task : result) {
                if (CollectionUtils.isNotEmpty(task.getUsers())) {
                    for (WorkflowUserVo user : task.getUsers()) {
                        WorkflowUserVo myUser= userMap.get(user.getId());
                        if(myUser!=null) {
                            user.setName(myUser.getName());
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(task.getVariableUser())){
                    for(WorkflowUserVo user:task.getVariableUser()){
                        String id = user.getId();
                        if(null!=id&&id.trim().length()>0){
                            WorkflowDepart myTag= tagMap.get(user.getId());
                            if(myTag!=null) {
                                user.setName(myTag.getDepartName());
                                user.setId(user.getId());
                            }



//                          处理部门分配等用户显示
                            if(StringUtil.isNotEmpty(processId)&&(id.startsWith(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY)||id.startsWith(WorkflowConstants.SUPERVISOR_KEY))){
                                Map<String,Object> myTaskVariables=new HashMap<>();
                                HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(processId).singleResult();
                                if(processInstance!=null){
                                    Map<String,Object> processVariables=processInstance.getProcessVariables();
                                    if(MapUtils.isNotEmpty( processVariables)){
                                        myTaskVariables=processVariables;
                                    }
                                }

                                List<Map<String,Object>> dataList = null;
                                String prefixKey = "";
                                if(id.startsWith(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY)){
                                    dataList = (List<Map<String, Object>>) myTaskVariables.get(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY);
                                    prefixKey = WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY + ".";
                                }else if(id.startsWith(WorkflowConstants.SUPERVISOR_KEY)){
                                    dataList = (List<Map<String, Object>>) myTaskVariables.get(WorkflowConstants.SUPERVISOR_LIST_KEY);
                                    prefixKey = WorkflowConstants.SUPERVISOR_KEY + ".";
                                }

                                if(null!=dataList&&dataList.size()>0){
                                    String userIds= "";
                                    if(id.equals(prefixKey + WorkflowConstants.MANAGE_USER_ID_KEY)){
//                                        String workflowDepartIds = dataList.stream().filter((data)->null!=data&&null!=data.get(WorkflowConstants.manageUserIdKey)&&  StringUtil.isNotEmpty(((List<String>) data.get(WorkflowConstants.manageUserIdKey)).stream().collect(Collectors.joining(",")))).map((data)->((List<String>) data.get(WorkflowConstants.manageUserIdKey)).stream().collect(Collectors.joining(","))).collect(Collectors.joining(","));
                                        userIds = dataList.stream().filter((data) -> null != data && null != data.get(WorkflowConstants.DEPART_ID_KEY) && StringUtil.isNotEmpty((String) data.get(WorkflowConstants.DEPART_ID_KEY))).map((data) -> workflowProcessDepartService.getDepartUsers(((String) data.get(WorkflowConstants.DEPART_ID_KEY)), WorkflowConstants.DEPART_ROLE.MANAGER)).filter((workflowUserVos) -> null != workflowUserVos && workflowUserVos.size() > 0).map((workflowUserVos) -> workflowUserVos.stream().map((workflowUserVo) -> workflowUserVo.getId()).collect(Collectors.joining(","))).collect(Collectors.joining(","));
                                    }else if(id.equals(prefixKey + WorkflowConstants.USER_ID_KEY)){
                                        userIds = dataList.stream().filter((data)->null!=data&&null!=data.get(WorkflowConstants.USER_ID_KEY)&& StringUtil.isNotEmpty(((List<String>) data.get(WorkflowConstants.USER_ID_KEY)).stream().collect(Collectors.joining(",")))).map((data)->((List<String>) data.get(WorkflowConstants.USER_ID_KEY)).stream().collect(Collectors.joining(","))).collect(Collectors.joining(","));
                                    }else if(id.equals(prefixKey + WorkflowConstants.SUPERVISOR_USER_ID_KEY)){
//                                        String workflowDepartIds = dataList.stream().filter((data)->null!=data&&null!=data.get(WorkflowConstants.supervisorUserIdKey)&& StringUtil.isNotEmpty(((List<String>) data.get(WorkflowConstants.supervisorUserIdKey)).stream().collect(Collectors.joining(",")))).map((data)->((List<String>) data.get(WorkflowConstants.supervisorUserIdKey)).stream().collect(Collectors.joining(","))).collect(Collectors.joining(","));
                                        userIds = dataList.stream().filter((data) -> null != data && null != data.get(WorkflowConstants.DEPART_ID_KEY) && StringUtil.isNotEmpty((String) data.get(WorkflowConstants.DEPART_ID_KEY))).map((data) -> workflowProcessDepartService.getDepartUsers(((String) data.get(WorkflowConstants.DEPART_ID_KEY)), WorkflowConstants.DEPART_ROLE.SUPERVISOR)).filter((workflowUserVos) -> null != workflowUserVos && workflowUserVos.size() > 0).map((workflowUserVos) -> workflowUserVos.stream().map((workflowUserVo) -> workflowUserVo.getId()).collect(Collectors.joining(","))).collect(Collectors.joining(","));
                                    }
                                    if(StringUtil.isNotEmpty(userIds)){
                                        List<WorkflowUserVo> workflowUserVos = workflowUserService.userListByIds(Arrays.asList(userIds.split(",")));
                                        if(null!=workflowUserVos&&workflowUserVos.size()>0){
                                            String names = workflowUserVos.stream().filter((workflowUser) -> null != workflowUser && StringUtil.isNotEmpty(workflowUser.getName())).map((workflowUser) -> workflowUser.getName()).collect(Collectors.joining(","));
                                            if(StringUtil.isNotEmpty(names)){
                                                user.setName(names);
                                                user.setId(user.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }


    @Override
    public List<WorkflowUserTaskVo> convertTask(List<WorkflowTaskVo> userTasks) {
        List<WorkflowUserTaskVo> result=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userTasks)){
            List<String> departIdList=new ArrayList<>();
            List<String> userIdList=new ArrayList<>();
            for(WorkflowTaskVo userTask:userTasks){
                WorkflowUserTaskVo user=new WorkflowUserTaskVo();
                user.setName(userTask.getTaskName());
                user.setFormKey(userTask.getFormKey());
                user.setTaskKey(userTask.getTaskKey());
                user.setTaskId(userTask.getTaskId());
                if(StringUtils.isNotBlank(userTask.getAssignee())){//任务的受理人，即执行人
                    user.setUserType(1);
                    userIdList.add(userTask.getAssignee());
                    WorkflowUserVo assignee= workflowUserService.getUserById(userTask.getAssignee());
                    if(assignee!=null) {
                        user.getUsers().add(assignee);
                    }
                }else  if(StringUtils.isNotBlank(userTask.getOwner())){
                    user.setUserType(2);
                    userIdList.add(userTask.getOwner());
                    WorkflowUserVo owner= workflowUserService.getUserById(userTask.getOwner());
                    if(owner!=null) {
                        user.getUsers().add(owner);
                    }
                }else if(CollectionUtils.isNotEmpty(userTask.getCandidateUser())){
                    user.setUserType(3);
                    for(String candidateUser:userTask.getCandidateUser()){
                        userIdList.add(candidateUser);
                        WorkflowUserVo candidate= workflowUserService.getUserById(candidateUser);
                        if(candidate!=null) {
                            user.getUsers().add(candidate);
                        }
                    }
                }else if(CollectionUtils.isNotEmpty(userTask.getCandidateGroup())){
                    user.setUserType(4);
                    for(String candidateUser:userTask.getCandidateGroup()){
                        WorkflowDepart depart=workflowProcessDepartService.getDepartById(candidateUser);
                        if(depart!=null) {
                            List<WorkflowUserVo> departUsers = workflowProcessDepartService.getDepartUsers(depart.getDepartId(), depart.getRole());
                            depart.setUsers(departUsers);
                            user.getDeparts().add(depart);
                        }
                    }
                }else{
                    user.setRemark("当前审批节点没有审批人（或审批部门用户）");
                }
                result.add(user);
            }

            Map<String,WorkflowUserVo> userMap=new HashMap<>();
            if(CollectionUtils.isNotEmpty( userIdList)){
                List<WorkflowUserVo> users= workflowUserService.userListByIds(userIdList);
                if(CollectionUtils.isNotEmpty(users)) {
                    for(WorkflowUserVo user:users){
                        userMap.put(user.getId(),user);
                    }

                }
            }

            for (WorkflowUserTaskVo task : result) {
                if (CollectionUtils.isNotEmpty(task.getUsers())) {
                    for (WorkflowUserVo user : task.getUsers()) {
                        WorkflowUserVo myUser= userMap.get(user.getId());
                        if(myUser!=null) {
                            user.setName(myUser.getName());
                        }
                    }
                }
            }
        }
        return result;
    }

    private void mountUserTask(WorkflowUserTaskVo userTaskVo, List<String> taskRoleList, List<String> userIdList, String userText){

        if(userText.trim().startsWith("${")&& userText.endsWith("}")){
            String code=userText.substring(2,userText.length()-1);
            taskRoleList.add(code);
            userTaskVo.getVariableUser().add(new WorkflowUserVo(code));
        }else{
            userIdList.add(userText);
            userTaskVo.getUsers().add(new WorkflowUserVo(userText));
        }

    }

}
