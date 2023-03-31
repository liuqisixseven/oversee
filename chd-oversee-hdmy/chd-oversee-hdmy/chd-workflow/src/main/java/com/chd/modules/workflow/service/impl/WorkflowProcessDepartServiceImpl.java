package com.chd.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.StringUtil;
import com.chd.common.util.UUIDGenerator;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.mapper.WorkflowDepartMapper;
import com.chd.modules.workflow.mapper.WorkflowProcessDepartMapper;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowProcessDepartService;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
public class WorkflowProcessDepartServiceImpl implements WorkflowProcessDepartService {

    @Autowired
    private WorkflowProcessDepartMapper workflowProcessDepartMapper;
    @Autowired
    private WorkflowDepartMapper workflowDepartMapper;

    @Override
    public int batchAddProcessDepart(List<WorkflowProcessDepart> processDeparts, String processId) {
        int result=0;
        if(CollectionUtils.isNotEmpty(processDeparts)){
            Date now=new Date();
            for(WorkflowProcessDepart depart:processDeparts){
                if(StringUtils.isBlank(depart.getId())) {
                    depart.setId(UUIDGenerator.generate());
                }
                depart.setUpdateTime(now);
                depart.setProcessId(processId);
                depart.setCreateTime(depart.getUpdateTime());
                result+=workflowProcessDepartMapper.insert(depart);
            }
        }
        return result;
    }

    @Override
    public List<WorkflowProcessDepart> toProcessDepart(String departIds, String source) {
        List<WorkflowProcessDepart> result=new ArrayList<>();
        if(StringUtils.isNotBlank(departIds)){
            String[] departIdArray=departIds.split(",");
            for(String departId:departIdArray){
                WorkflowProcessDepart depart=new WorkflowProcessDepart();
                depart.setSource(source);
                depart.setDepartId(departId);
                result.add(depart);
            }
        }
        return result;
    }

    @Override
    public Map<String, List<String>> getProcessDepartVariableMap(List<WorkflowProcessDepart> processDeparts) {
        Map<String,List<String>> result=new HashMap<>();
        String[] roles=new String[]{WorkflowConstants.DEPART_ROLE.MANAGER,WorkflowConstants.DEPART_ROLE.SUPERVISOR};
        if(CollectionUtils.isNotEmpty(processDeparts)){
            for(WorkflowProcessDepart depart:processDeparts){
                String source=depart.getSource();
                for(String role :roles) {
                    String key=source+"_"+role;
                    List<String> variableValue= result.get(key);
                    if(variableValue==null){
                        variableValue=new ArrayList<>();
                        result.put(key,variableValue);
                    }
                    WorkflowDepart workflowDepart= generateDepart(depart.getDepartId(), role);
                    if(!variableValue.contains(workflowDepart.getId())) {
                        variableValue.add(workflowDepart.getId());
                    }
                }

            }
        }
        return result;
    }

    @Override
    public Map<String, List<WorkflowDepart>> getProcessDepartVariable(List<WorkflowProcessDepart> processDeparts) {
        Map<String,List<WorkflowDepart>> result=new HashMap<>();
        String[] roles=new String[]{WorkflowConstants.DEPART_ROLE.MANAGER,WorkflowConstants.DEPART_ROLE.SUPERVISOR};
        if(CollectionUtils.isNotEmpty(processDeparts)){
            for(WorkflowProcessDepart depart:processDeparts){
                String source=depart.getSource();
                for(String role :roles) {
                    String key=source+"_"+role;
                    List<WorkflowDepart> variableValue= result.get(key);
                    if(variableValue==null){
                        variableValue=new ArrayList<>();
                        result.put(key,variableValue);
                    }
                    WorkflowDepart workflowDepart= generateDepart(depart.getDepartId(), role);
                    if(!variableValue.stream().anyMatch((workflowDepartVariableValue)-> null!=workflowDepartVariableValue&&StringUtil.isNotEmpty(workflowDepartVariableValue.getId())&&workflowDepartVariableValue.getId().equals(workflowDepart.getId()))) {
                        List<WorkflowUserVo> users= getDepartUsers(depart.getDepartId(), role);
                         workflowDepart.setUsers(users);
                        variableValue.add(workflowDepart);
                    }
                }

            }
        }
        return result;
    }

    @Override
    public List<WorkflowProcessDepart> getProcessDepartListByProcessId(String processId) {
        QueryWrapper query=new QueryWrapper();
        query.eq("process_id",processId);
        List<WorkflowProcessDepart> result= workflowProcessDepartMapper.selectList(query);
        return result;
    }

    @Override
    public List<WorkflowDepart> processDepartList(String processId, String source, String role) {
        List<WorkflowDepart> result=new ArrayList<>();
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("process_id",processId);
        queryWrapper.eq("source",source);
        List<WorkflowProcessDepart> processDeparts= workflowProcessDepartMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(processDeparts)){
            for(WorkflowProcessDepart processDepart:processDeparts) {
                if(StringUtils.isBlank(role)){
                    String[] roles=new String[]{WorkflowConstants.DEPART_ROLE.MANAGER,WorkflowConstants.DEPART_ROLE.SUPERVISOR};
                    for(String role1:roles){
                        WorkflowDepart depart=generateDepart(processDepart.getDepartId(),role1);
                        if(depart!=null) {
                            result.add(depart);
                        }
                    }
                }else {
                    WorkflowDepart depart = generateDepart(processDepart.getDepartId(), role);
                    if (depart != null) {
                        result.add(depart);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<WorkflowDepart> userProcessDepartList(String processId, String source, String userId) {
        List<WorkflowDepart> result=new ArrayList<>();
        List<WorkflowDepart> departList= processDepartList(processId,source,null);
        if(CollectionUtils.isNotEmpty(departList)) {
            for (WorkflowDepart depart : departList) {
                List<WorkflowUserVo> manageUserList= getDepartUsers(depart.getDepartId(),WorkflowConstants.DEPART_ROLE.MANAGER);
                if(CollectionUtils.isNotEmpty(manageUserList)) {
                    for (WorkflowUserVo user : manageUserList) {
                        if (user.getId().equals(userId)) {
                            result.add(depart);
                        }
                    }
                }
                List<WorkflowUserVo> supervisorUserList=getDepartUsers(depart.getDepartId(),WorkflowConstants.DEPART_ROLE.SUPERVISOR);
                if(CollectionUtils.isNotEmpty(supervisorUserList)) {
                    for (WorkflowUserVo user : supervisorUserList) {
                        if (user.getId().equals(userId)) {
                            result.add(depart);
                        }
                    }
                }
            }
        }
        return result;
    }

    private Integer insertDepart(WorkflowDepart workflowDepart) {
        Date now=new Date();
        workflowDepart.setUpdateTime(now);
        workflowDepart.setCreateTime(workflowDepart.getUpdateTime());
        workflowDepart.setId(UUIDGenerator.generate());
        WorkflowDepart org= workflowDepartMapper.findOrgById(workflowDepart.getDepartId());
        Asserts.notNull(org,"部门ID无效");
        workflowDepart.setDepartName(org.getDepartName());
        return workflowDepartMapper.insertDepart(workflowDepart);
    }


    public WorkflowDepart getDepart(String departId, String role) {
        WorkflowDepart query=new WorkflowDepart();
        query.setDepartId(departId);
        query.setRole(role);
        return workflowDepartMapper.findDepart(query);
    }

    @Override
    public synchronized WorkflowDepart generateDepart(String departId, String role) {
        Asserts.notBlank(departId,"部门ID不能为空");
        Asserts.notBlank(role,"角色不能为空");
        WorkflowDepart depart=getDepart(departId, role);
        if(depart==null){
            depart=new WorkflowDepart();
            depart.setDepartId(departId);
            depart.setRole(role);
            insertDepart(depart);
        }
        return depart;
    }

    @Override
    public List<WorkflowDepart> getDeparts(List<WorkflowProcessDepart> processDeparts,String role) {
        List<WorkflowDepart> departList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(processDeparts)){
            for(WorkflowProcessDepart processDepart:processDeparts){
                WorkflowDepart depart= generateDepart(processDepart.getDepartId(),role);
                if(depart!=null) {
                    List<WorkflowUserVo> departUsers=getDepartUsers(depart.getDepartId(), depart.getRole());
                    depart.setUsers(departUsers);
                    departList.add(depart);
                }
            }
        }
        return departList;
    }

    @Override
    public List<WorkflowUserVo> getDepartUsers(String departId,String role) {
        List<WorkflowUserVo> result=new ArrayList<>();
        if(StringUtils.isNotBlank(role)){
            if(WorkflowConstants.DEPART_ROLE.MANAGER.equals(role)){
               List<WorkflowUserVo> users=workflowDepartMapper.manageUsersByDepartId(departId);
                if(CollectionUtils.isNotEmpty(users)){
                    for(WorkflowUserVo user:users){
                        if(user!=null){
                            result.add(user);
                        }
                    }
                }
            }else if(WorkflowConstants.DEPART_ROLE.SUPERVISOR.equals(role)){
                List<WorkflowUserVo> users=workflowDepartMapper.supervisorUsersByDepartId(departId);
                if(CollectionUtils.isNotEmpty(users)){
                    for(WorkflowUserVo user:users){
                        if(user!=null){
                            result.add(user);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<String> userDepartVariables(String userId) {
        return workflowDepartMapper.userDepartVariablesValue(userId);
    }

    @Override
    public  WorkflowDepart getDepartById(String id) {
        WorkflowDepart query=new WorkflowDepart();
        query.setId(id);
        return workflowDepartMapper.findDepart(query);
    }

    @Override
    public List<WorkflowProcessDepart> selectList(Wrapper<WorkflowProcessDepart> queryWrapper) {
        return workflowProcessDepartMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public Integer updateWorkflowProcessDepartNewDepartByOldDepart(String processId, String source, String oldDepartId, String newDepartId,String updateUserId) {
        int updateCount = 0;

        Assert.isTrue((StringUtil.isNotEmpty(processId)), "请传递processId");
        Assert.isTrue((StringUtil.isNotEmpty(source)), "请传递source");
        Assert.isTrue((StringUtil.isNotEmpty(oldDepartId)), "请传递oldDepartId");
        Assert.isTrue((StringUtil.isNotEmpty(newDepartId)), "请传递newDepartId");
        List<WorkflowProcessDepart> workflowProcessDeparts = workflowProcessDepartMapper.selectList(Wrappers.<WorkflowProcessDepart>lambdaQuery().eq(WorkflowProcessDepart::getProcessId, processId).eq(WorkflowProcessDepart::getSource, source));
        if(CollectionUtils.isNotEmpty(workflowProcessDeparts)){
            for(WorkflowProcessDepart workflowProcessDepart : workflowProcessDeparts){
                workflowProcessDepartMapper.deleteById(workflowProcessDepart.getId());
            }
        }

        updateCount += workflowProcessDepartMapper.insert(initWorkflowProcessDepart(processId,source,WorkflowConstants.DEPART_ROLE.SUPERVISOR,newDepartId,updateUserId));
        updateCount += workflowProcessDepartMapper.insert(initWorkflowProcessDepart(processId,source,WorkflowConstants.DEPART_ROLE.MANAGER,newDepartId,updateUserId));

        return updateCount;
    }

    private WorkflowProcessDepart initWorkflowProcessDepart(String processId, String source, String role, String departId,String updateUserId){
        WorkflowProcessDepart workflowProcessDepart = new WorkflowProcessDepart();
        workflowProcessDepart.setProcessId(processId);
        workflowProcessDepart.setDepartId(departId);
        workflowProcessDepart.setSource(source);
        workflowProcessDepart.setRole(role);
        workflowProcessDepart.setCreateTime(new Date());
        workflowProcessDepart.setUpdateTime(new Date());
        workflowProcessDepart.setCreateBy(updateUserId);
        workflowProcessDepart.setUpdateBy(updateUserId);
        return workflowProcessDepart;
    }

    @Override
    public Integer updateWorkflowProcessDepart(WorkflowProcessDepart workflowProcessDepart) {
        if(null!=workflowProcessDepart&&null!=workflowProcessDepart.getId()){
            return workflowProcessDepartMapper.updateById(workflowProcessDepart);
        }

        return null;
    }


}
