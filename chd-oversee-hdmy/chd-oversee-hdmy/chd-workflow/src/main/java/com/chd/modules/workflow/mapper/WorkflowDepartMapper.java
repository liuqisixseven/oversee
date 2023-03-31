package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkflowDepartMapper  extends BaseMapper<WorkflowDepart> {

    /**
     * 获取部门的负责人用户
     * @param departId
     * @return
     */
    List<WorkflowUserVo> manageUsersByDepartIds(List<String> departId);
    List<WorkflowUserVo> manageUsersByDepartId(String departId);

    /**
     * 获取部门的分管领导用户
     * @param departId
     * @return
     */
    List<WorkflowUserVo> supervisorUsersByDepartIds(List<String> departId);
    List<WorkflowUserVo> supervisorUsersByDepartId(String departId);

    List<String> userDepartVariablesValue(String userId);
    List<WorkflowDepart> departListByIds(List<String> ids);



    Integer insertDepart(WorkflowDepart depart);

    WorkflowDepart findDepart(WorkflowDepart depart);

    WorkflowDepart findOrgById(String departId);
    List<WorkflowDepart> findHasUserDepartByIds(String departIds);

    List<WorkflowDepart> getWorkflowDepartList(@Param("map") Map<String,Object> map);


}
