package com.chd.modules.workflow;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.issue.entity.OverseeIssueFlowOrg;
import com.chd.modules.oversee.issue.mapper.OverseeIssueFlowOrgMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueFlowOrgService;
import com.chd.modules.system.entity.SysRole;
import com.chd.modules.system.service.ISysRoleService;
import com.chd.modules.system.service.ISysUserDepartService;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.service.ISysUserRoleService;
import com.chd.modules.system.service.ISysUserService;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowUserService;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
// public class WorkflowUserService {
public class OverseeWorkflowUserService implements WorkflowUserService {

    @Autowired
	private ISysUserRoleService sysUserRoleService;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
	private ISysUserService sysUserService;
    @Autowired
    private OverseeIssueFlowOrgMapper overseeIssueFlowOrgMapper;
    @Autowired
   private IOverseeIssueFlowOrgService overseeIssueFlowOrgService;


    @Override
    public List<String> roleNameListByRoleId(List<String> roleId) {
       List<String> result=new ArrayList<>();
        List<SysRole> list= sysRoleService.listByIds(roleId);
        if(CollectionUtils.isNotEmpty(list)){
            for(SysRole role:list){
                result.add(role.getRoleName());
            }
        }
        return result;
    }

    @Override
    public List<WorkflowUserVo> userListByIds(List<String> userIds) {
       List<WorkflowUserVo> result=new ArrayList<>();
        List<SysUser> sysUserList= sysUserService.listByIds(userIds);
        if(CollectionUtils.isNotEmpty(sysUserList)){
            for(SysUser sysUser:sysUserList){
                result.add( new WorkflowUserVo(sysUser.getId(),sysUser.getRealname()));
            }
        }
        return result;
    }

    @Override
    public WorkflowUserVo getUserById(String userId) {
        SysUser sysUser= sysUserService.getById(userId);
        if(sysUser!=null){
           return  new WorkflowUserVo(sysUser.getId(),sysUser.getRealname());
        }
        return null;
    }

    @Override
    public boolean isWorkflowSuperUser(String userId) {
        return sysUserService.isSuperUser(userId);
    }

    @Override
    public List<WorkflowUserVo> allUserList(int pageNo, int pageSize) {
        List<WorkflowUserVo> result=new ArrayList<>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page,"USER_WORKFLOW",null);
        if(CollectionUtils.isNotEmpty(pageList.getRecords())){
            for(SysUser sysUser:pageList.getRecords()){
                result.add( new WorkflowUserVo(sysUser.getId(),sysUser.getRealname()));
            }
        }
        return result;
    }

    @Override
    public List<String> getTaskGroupIdsByUserId(String userId) {
        List<String> result=new ArrayList<>();
        List<OverseeIssueFlowOrg> orgList= overseeIssueFlowOrgMapper.orgListByUserId(userId);
        if(CollectionUtils.isNotEmpty(orgList)){
            for(OverseeIssueFlowOrg org:orgList){
                result.add(org.getId());
            }
        }
        return result;
    }

    @Override
    public List<WorkflowUserTaskVo> getBizVariableUser(List<WorkflowUserTaskVo> userTasks, String category,String bizId, String startUserId) {
        if(CollectionUtils.isNotEmpty( userTasks) && StringUtils.isNotBlank(startUserId)){
            WorkflowUserVo startUser=getUserById(startUserId);
            for(WorkflowUserTaskVo userTask:userTasks){
                if(CollectionUtils.isNotEmpty(userTask.getVariableUser())){
                    Iterator<WorkflowUserVo> users=userTask.getVariableUser().iterator();
                    while (users.hasNext()){
                        WorkflowUserVo user=users.next();
                        if(WorkflowConstants.FLOW_USER_OWNER.equals( user.getId())){
                            userTask.getUsers().add(startUser);
                            users.remove();
                        }
                    }

                }
            }

        }
        return overseeIssueFlowOrgService.getBizVariableUser(userTasks,category,bizId,startUserId);
    }

    @Override
    public Map<String, List<WorkflowUserVo>> getBizUserVariables(String category, String bizId, String startUserId) {
        return null;
    }

    @Override
    public Map<String, List<WorkflowDepart>> getBizDepartVariables(String category, String bizId, String startUserId) {
        return overseeIssueFlowOrgService.getBizDepartVariables(category, bizId, startUserId);
    }
}
