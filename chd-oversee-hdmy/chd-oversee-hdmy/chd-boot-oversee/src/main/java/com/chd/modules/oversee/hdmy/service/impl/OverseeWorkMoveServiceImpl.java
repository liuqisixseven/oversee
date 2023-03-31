package com.chd.modules.oversee.hdmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.hdmy.entity.OverseeWorkMove;
import com.chd.modules.oversee.hdmy.mapper.OverseeWorkMoveMapper;
import com.chd.modules.oversee.hdmy.service.IOverseeWorkMoveService;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import com.chd.modules.workflow.service.WorkflowTaskService;
import com.chd.modules.workflow.vo.WorkflowQueryVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date: 2022-08-03
 * @Version: V1.0
 */
@Service()
public class OverseeWorkMoveServiceImpl extends ServiceImpl<OverseeWorkMoveMapper, OverseeWorkMove> implements IOverseeWorkMoveService {
    @Autowired
    private WorkflowTaskService workflowTaskService;
    @Autowired
    private WorkflowProcessMapper workflowProcessMapper;
    @Autowired
    private TaskService taskService;
    public void workMove(String fromUserId,String toUserId,boolean isJob){
        WorkflowQueryVo query = new WorkflowQueryVo();
        query.setPageNo(1);
        query.setPageSize(1000);
        Map<String, Object> map = new HashMap<>();
        IPage<WorkflowTaskVo> rlt = workflowTaskService.todoTaskList(query, fromUserId, map);
        List<String> ids = new ArrayList<>();
        rlt.getRecords().forEach(a -> {
            ids.add(a.getProcessId());
            TaskEntity currTask = (TaskEntity) taskService.createTaskQuery().processInstanceId(a.getProcessId()).singleResult();
            taskService.setAssignee(currTask.getId(), toUserId);
        });
        if (CollectionUtils.isNotEmpty(ids)) {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("ids", ids);
            variables.put("userId", toUserId);
            workflowProcessMapper.upTaskAssignee(variables);
        }
        //前端传的工作交接才运行，后端跑的不允许进
        if(!isJob) {
            //保存工作交接数据
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            OverseeWorkMove overseeWorkMove = new OverseeWorkMove();
            overseeWorkMove.setCreateUserId(sysUser.getId());
            overseeWorkMove.setFromUserId(fromUserId);
            overseeWorkMove.setToUserId(toUserId);
            overseeWorkMove.setStatus(1);
            this.save(overseeWorkMove);
        }
    }


//    @Scheduled(fixedRateString = "${oversee.workmove.refresh-job-time:600000}")
    public void workMoveJob() {
        log.debug("oversee.workmove.refresh-job-time!!!");
        LambdaQueryWrapper<OverseeWorkMove> query = new LambdaQueryWrapper<OverseeWorkMove>()
                .eq(OverseeWorkMove::getStatus, 1);
        List<OverseeWorkMove> overseeWorkMoveList= this.list(query);
        overseeWorkMoveList.forEach(a->{
            workMove(a.getFromUserId(),a.getToUserId(),true);
        });
    }
}
