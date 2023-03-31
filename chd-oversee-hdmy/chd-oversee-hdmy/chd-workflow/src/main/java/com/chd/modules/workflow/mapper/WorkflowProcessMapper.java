package com.chd.modules.workflow.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.workflow.entity.WorkflowProcess;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkflowProcessMapper extends BaseMapper<WorkflowProcess> {

    Integer insertProcess(WorkflowProcess workflowProcess);

    List<WorkflowProcess> processListByIds(List<String> ids);

    List<WorkflowProcess>  findByBiz(String bizId);

    List<WorkflowTaskVo> findNextTaskNode(String id);

    Integer processStateStart2Pending(String id);

    Integer upTaskAssignee(@Param("map") Map<String, Object> map);

    IPage<WorkflowTaskVo> todoList(Page page,  @Param("task")WorkflowTaskVo task);

    IPage<WorkflowTaskVo> todoListByMap(Page page,@Param("task")WorkflowTaskVo task, @Param("map") Map<String,Object> map);

    String getPreviousTaskDataSrc(@Param("map") Map<String,Object> map);

    /**
     * 计算我的待办数量
     * @param userId
     * @param candidateGroup
     * @return
     */
    Long countUserTodoTotal(@Param("userId") String userId,@Param("candidateGroup") List<String> candidateGroup);

    /**
     * 计算我的已办数量
     * @param userId
     * @param candidateGroup
     * @return
     */
    Long countUserDoneTotal(@Param("userId") String userId,@Param("candidateGroup") List<String> candidateGroup);
}
