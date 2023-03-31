package com.chd.modules.workflow.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.entity.WorkflowProcessDepart;
import com.chd.modules.workflow.entity.WorkflowProcessRoleGroup;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 流程执行部门
 */
public interface WorkflowProcessDepartService {


    /**
     * 用流程processId添加流程部门
     * @param processDeparts
     * @param processId
     * @return
     */
    int batchAddProcessDepart(List<WorkflowProcessDepart> processDeparts,String processId);

    /**
     * 部门ID转为流程部门
     * @param departIds
     * @param source
     * @return
     */
    List<WorkflowProcessDepart> toProcessDepart(String departIds,String source);

    /**
     * 获取流程部门的流程用户组变量值MAP
     * @param processDeparts
     * @return
     */
    Map<String,List<String>> getProcessDepartVariableMap(List<WorkflowProcessDepart> processDeparts);
    Map<String,List<WorkflowDepart>> getProcessDepartVariable(List<WorkflowProcessDepart> processDeparts);



    List<WorkflowProcessDepart> getProcessDepartListByProcessId(String processId);

    /**
     * 获取流程部门
     * @param processId 流程ID
     * @param source 部门来源
     * @param role 流程部门编码
     * @return
     */
    List<WorkflowDepart> processDepartList(String processId,String source,String role);

    /**
     * 获取用户流程里设置的所属部门
     * @param processId
     * @param source
     * @param userId
     * @return
     */
    List<WorkflowDepart> userProcessDepartList(String processId,String source,String userId);


    /**
     * 生成流程部门
     * @param departId
     * @param role
     * @return
     */
    WorkflowDepart generateDepart(String departId,String role);

    /**
     * 根据角色选取流程部门
     * @param processDeparts
     * @param role
     * @return
     */
    List<WorkflowDepart> getDeparts(List<WorkflowProcessDepart>  processDeparts,String role);


    List<WorkflowUserVo> getDepartUsers(String departId,String role);

    /**
     * 用户的部门流程任务变更
     * @param userId
     * @return
     */
    List<String> userDepartVariables(String userId);

    WorkflowDepart getDepartById(String id);

    Integer updateWorkflowProcessDepart(WorkflowProcessDepart workflowProcessDepart);

    List<WorkflowProcessDepart> selectList(Wrapper<WorkflowProcessDepart> queryWrapper);

    Integer updateWorkflowProcessDepartNewDepartByOldDepart(String processId, String source, String oldDepartId, String newDepartId,String updateUserId);
}
