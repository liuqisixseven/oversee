package com.chd.modules.workflow.service.impl;

import com.chd.common.exception.BizException;
import com.chd.common.util.UUIDGenerator;
import com.chd.modules.workflow.entity.WorkflowProcessUsers;
import com.chd.modules.workflow.mapper.WorkflowProcessUsersMapper;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowProcessUsersSerivce;
import com.chd.modules.workflow.service.WorkflowUserService;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkflowProcessUsersSerivceImpl implements WorkflowProcessUsersSerivce {

    @Autowired
    private WorkflowProcessUsersMapper workflowProcessUsersMapper;
    @Autowired
    private WorkflowUserService workflowUserService;

    @Override
    public int saveProcessUsersForm(Map<String,Object> form, String departId, String processId, WorkflowUserVo user) {
        int result=0;
        String role= MapUtils.getString(form,"role");
        String source=MapUtils.getString(form,"source");
        String userIds=MapUtils.getString(form,"userIds");
        if(StringUtils.isBlank(userIds)){
            throw new BizException("请指定审批用户");
        }
        if(StringUtils.isBlank(source)){
            throw new BizException("请指定部门");
        }
        if(StringUtils.isBlank(role)){
            throw new BizException("请指定用户角色");
        }
        String[] userIdArray=userIds.split(",");
        for(String userId:userIdArray) {
            if(StringUtils.isNotBlank(userId)) {
                WorkflowProcessUsers processUsers = new WorkflowProcessUsers();
                processUsers.setId(UUIDGenerator.generate());
                processUsers.setDepartId(departId);
                processUsers.setProcessId(processId);
                processUsers.setRole(role);
                processUsers.setUserId(userId);
                processUsers.setSource(source);
                processUsers.setUpdateBy(user.getName());
                processUsers.setCreateTime(new Date());
                processUsers.setCreateBy(processUsers.getUpdateBy());
                processUsers.setUpdateTime(processUsers.getCreateTime());
                Integer res=workflowProcessUsersMapper.insert(processUsers);
                result+=(res==null?0:res.intValue());
            }
        }
        return result;
    }

    @Override
    public Map<String, List<String>> getProcessUsersVariableMap(String processId, String subProcessId, String departId) {
        Map<String,List<String>> result=new HashMap<>();
        WorkflowProcessUsers queryUsers=new WorkflowProcessUsers();
        queryUsers.setProcessId(processId);
        queryUsers.setDepartId(departId);
        queryUsers.setSubProcessId(subProcessId);
        List<WorkflowProcessUsers> processUsersList= workflowProcessUsersMapper.getUserListByProcessId(queryUsers);
        if(CollectionUtils.isNotEmpty(processUsersList)){
            String[] sources=new String[]{WorkflowConstants.DEPART_SOURCE.INITIATOR,WorkflowConstants.DEPART_SOURCE.SUPERVISOR,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE};
            String[] roles=new String[]{WorkflowConstants.DEPART_ROLE.EXECUTOR,WorkflowConstants.DEPART_ROLE.SPECIALIST,WorkflowConstants.DEPART_ROLE.SECRETARY};
            for(String source:sources){
                for(String role:roles){
                    String key=source+"_"+role;
                    List<String> userIds=new ArrayList<>();
                    for(WorkflowProcessUsers user:processUsersList){
                        if(role.equals(user.getRole()) && source.equals(user.getSource())){
                            userIds.add(user.getUserId());
                        }
                    }
                    result.put(key,userIds);
                }
            }
        }
        return result;
    }


    @Override
    public Map<String, List<WorkflowUserVo>> getProcessUsersVariables(String processId,String subProcessId,String departId) {
        Map<String,List<WorkflowUserVo>> result=new HashMap<>();
        WorkflowProcessUsers queryUsers=new WorkflowProcessUsers();
        queryUsers.setProcessId(processId);
        queryUsers.setDepartId(departId);
        queryUsers.setSubProcessId(subProcessId);
        List<WorkflowProcessUsers> processUsersList= workflowProcessUsersMapper.getUserListByProcessId(queryUsers);
        if(CollectionUtils.isNotEmpty(processUsersList)){
            String[] sources=new String[]{WorkflowConstants.DEPART_SOURCE.INITIATOR,WorkflowConstants.DEPART_SOURCE.SUPERVISOR,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE,WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE,WorkflowConstants.DEPART_SOURCE.LAUNCH};
            String[] roles=new String[]{WorkflowConstants.DEPART_ROLE.EXECUTOR,WorkflowConstants.DEPART_ROLE.SPECIALIST,WorkflowConstants.DEPART_ROLE.SECRETARY};
            for(String source:sources){
                for(String role:roles){
                    String key=source+"_"+role;
                    List<WorkflowUserVo> userIds=new ArrayList<>();
                    for(WorkflowProcessUsers user:processUsersList){
                        if(role.equals(user.getRole()) && source.equals(user.getSource())){
                            WorkflowUserVo userVo= workflowUserService.getUserById(user.getUserId());
                            userIds.add(userVo);
                        }
                    }
                    result.put(key,userIds);
                }
            }
        }
        return result;
    }
}
