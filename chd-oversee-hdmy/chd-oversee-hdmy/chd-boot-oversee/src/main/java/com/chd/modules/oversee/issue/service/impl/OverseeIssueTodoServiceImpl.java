package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.entity.IssuesSupervisor;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueTodo;
import com.chd.modules.oversee.issue.job.SendTodoJob;
import com.chd.modules.oversee.issue.mapper.IssuesAllocationMapper;
import com.chd.modules.oversee.issue.mapper.IssuesSupervisorMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueTodoMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueTodoService;
import com.chd.modules.workflow.service.WorkflowConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class OverseeIssueTodoServiceImpl extends ServiceImpl<OverseeIssueTodoMapper, OverseeIssueTodo> implements IOverseeIssueTodoService {

    @Autowired
    OverseeIssueTodoMapper overseeIssueTodoMapper;

    @Autowired
    IssuesAllocationMapper issuesAllocationMapper;

    @Autowired
    IssuesSupervisorMapper issuesSupervisorMapper;

    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void synchronization(Long overseeIssueId){


    }


    @Override
    @Transactional
    public int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId){
        return addOrUpdate(dataId,roleType,issueId,updateUserId,null);
    }

    @Override
    @Transactional
    public int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId, String role){
        Assert.isTrue((null!=issueId&&issueId.intValue()>0),"请传递问题id");
        Assert.isTrue((null!=roleType&&roleType.intValue()>0),"请传递roleType");
        Assert.isTrue((StringUtil.isNotEmpty(dataId)),"请传递dataId");
        Assert.isTrue((StringUtil.isNotEmpty(updateUserId)),"请传递updateUserId");
        OverseeIssueTodo overseeIssueTodo = new OverseeIssueTodo();
        overseeIssueTodo.setIssueId(issueId);
//        overseeIssueTodo.setRoleType(roleType);
//        overseeIssueTodo.setDataId(dataId);
        overseeIssueTodo.setRole(role);
        overseeIssueTodo.setUpdateUserId(updateUserId);
        return  addOrUpdate(overseeIssueTodo);
    }


    @Override
    @Transactional
    public int addOrUpdate(OverseeIssueTodo overseeIssueTodo) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=overseeIssueTodo),"请传递overseeIssueTodo数据");
        Assert.isTrue((null!=overseeIssueTodo.getIssueId()&&overseeIssueTodo.getIssueId().intValue()>0),"请传递问题id");
        Assert.isTrue((StringUtil.isNotEmpty(overseeIssueTodo.getUpdateUserId())),"请传递updateUserId");

        overseeIssueTodo.setUpdateTime(new Date());

        String redisKey = BaseConstant.OVERSEE_ISSUE_TODO_EDIT_TASK_ID_KEY + overseeIssueTodo.getTaskId();
        if(redisTemplate.opsForValue().setIfAbsent(redisKey,redisKey, Duration.ofMinutes(5))){
            try{
                if(null==overseeIssueTodo.getId()||overseeIssueTodo.getId()<=0){
                    List<OverseeIssueTodo> overseeIssueTodos = overseeIssueTodoMapper.selectList(Wrappers.<OverseeIssueTodo>lambdaQuery().eq(OverseeIssueTodo::getIssueId, overseeIssueTodo.getIssueId()).eq(OverseeIssueTodo::getTaskId, overseeIssueTodo.getTaskId()).eq(OverseeIssueTodo::getDataType, 1));
                    if(null!=overseeIssueTodos&&overseeIssueTodos.size()>0){
                        OverseeIssueTodo overseeIssueTodoS = overseeIssueTodos.get(0);
                        if(null!=overseeIssueTodoS&&null!=overseeIssueTodoS.getId()&&overseeIssueTodoS.getId().intValue()>0){
                            overseeIssueTodo.setIssueId(overseeIssueTodoS.getIssueId());
                            if(null==overseeIssueTodo.getSendStatus()){
                                overseeIssueTodo.setSendStatus(overseeIssueTodoS.getSendStatus());
                                if(StringUtil.isEmpty(overseeIssueTodo.getUserId())){
                                    overseeIssueTodo.setUserId(overseeIssueTodoS.getUserId());
                                }
                            }
                        }
                    }
                }


                if(null!=overseeIssueTodo.getId()&&overseeIssueTodo.getId().intValue()>0){
                    addOrUpdateCount = overseeIssueTodoMapper.updateById(overseeIssueTodo);
                }else{
                    if(StringUtil.isEmpty(overseeIssueTodo.getUserId())){
                        overseeIssueTodo.setUserId(overseeIssueTodo.getUserId());
                    }
                    overseeIssueTodo.setCreateUserId(overseeIssueTodo.getUpdateUserId());
                    overseeIssueTodo.setCreateTime(new Date());
                    addOrUpdateCount = overseeIssueTodoMapper.insert(overseeIssueTodo);
                }
                if(addOrUpdateCount>0){
//                    查询比当前时间早的待办，并修改为已发送
                    if(null!=overseeIssueTodo.getIssueId()&&overseeIssueTodo.getIssueId().longValue()>0&&StringUtil.isNotEmpty(overseeIssueTodo.getUserId())){
                        List<OverseeIssueTodo> overseeIssueTodoList = overseeIssueTodoMapper.selectList(Wrappers.<OverseeIssueTodo>lambdaQuery().eq(OverseeIssueTodo::getIssueId, overseeIssueTodo.getIssueId()).eq(OverseeIssueTodo::getUserId, overseeIssueTodo.getUserId()).eq(OverseeIssueTodo::getDataType, 1));
                        if(CollectionUtils.isNotEmpty(overseeIssueTodoList)){
                            for(OverseeIssueTodo overseeIssueTodoS : overseeIssueTodoList){
                                if(null!=overseeIssueTodoS&&null!=overseeIssueTodoS.getId()&&overseeIssueTodoS.getId().intValue()!=overseeIssueTodo.getId().intValue()){
                                    OverseeIssueTodo overseeIssueTodoU = new OverseeIssueTodo();
                                    overseeIssueTodoU.setId(overseeIssueTodoS.getId());
                                    overseeIssueTodoU.setSendStatus(4);
                                    overseeIssueTodoU.setUpdateTime(new Date());
                                    overseeIssueTodoMapper.updateById(overseeIssueTodoU);
                                }
                            }
                        }
                    }


//                    if(overseeIssueTodo.getSendStatus()==null||overseeIssueTodo.getSendStatus().intValue()==-1){
//                        SendTodoJob.addRunnableToExecutorService(overseeIssueTodo.getId(),()->{
//                            SendTodoJob.sendTodo(overseeIssueTodo);
//                        });
//                    }
                }
            }finally {
                redisTemplate.delete(redisKey);
            }
        }
        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public void updateSendStatusByTaskId(String taskId, String userId) {
        try{
            if(StringUtil.isNotEmpty(taskId)){
                LambdaQueryWrapper<OverseeIssueTodo> overseeIssueTodoWrappers = Wrappers.<OverseeIssueTodo>lambdaQuery().eq(OverseeIssueTodo::getTaskId, taskId).eq(OverseeIssueTodo::getDataType, 1);
                if(StringUtil.isNotEmpty(userId)){
                    overseeIssueTodoWrappers.eq(OverseeIssueTodo::getUserId,userId);
                }
                List<OverseeIssueTodo> overseeIssueTodos = overseeIssueTodoMapper.selectList(overseeIssueTodoWrappers);
                if(null!=overseeIssueTodos&&overseeIssueTodos.size()>0){
                    for(OverseeIssueTodo overseeIssueTodoS : overseeIssueTodos){
                        overseeIssueTodoS.setSendStatus(4);
                        addOrUpdate(overseeIssueTodoS);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public List<OverseeIssueTodo> selectOverseeIssueTodoList(Map<String, Object> map) {
        return overseeIssueTodoMapper.selectOverseeIssueTodoList(map);
    }
}
