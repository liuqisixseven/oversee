package com.chd.modules.workflow.service.impl;

import com.chd.common.base.BaseMap;
import com.chd.common.modules.redis.client.ChdRedisClient;
import com.chd.common.util.DateUtils;
import com.chd.modules.workflow.service.WorkflowConstants;
import com.chd.modules.workflow.service.WorkflowMessageService;
import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import com.google.common.collect.Maps;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowMessageServiceImpl implements WorkflowMessageService {

    @Autowired
    private ChdRedisClient chdRedisClient;

    @Override
    public void sendMessage(String msgType, String bizId, Map<String, Object> param) {
        BaseMap baseMap = new BaseMap();
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.BIZ_ID, bizId);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.MESSAGE_TYPE, msgType);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.DATA, param);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.TIME, DateUtils.formatDate(new Date(),DateUtils.FORMAT_DATETIME));
        chdRedisClient.sendMessage(WorkflowConstants.NOTICE_HANDLE.OVERSEE_PROCESS_NOTICE, baseMap);

    }

    @Override
    public void sendTaskForm(String bizId, Task task, WorkflowUserVo user, WorkflowTaskFormVo formData) {
        WorkflowTaskVo taskVo=new WorkflowTaskVo(task);
        BaseMap baseMap = new BaseMap();
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.BIZ_ID, bizId);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.MESSAGE_TYPE, WorkflowConstants.NOTICE_MESSAGE_TYPE.TASK_FORM_DATA);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.DATA, formData);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.USER, user);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.TASK, taskVo);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.TIME, DateUtils.formatDate(new Date(),DateUtils.FORMAT_DATETIME));
        chdRedisClient.sendMessage(WorkflowConstants.NOTICE_HANDLE.OVERSEE_PROCESS_NOTICE, baseMap);
    }

    @Override
    public void sendNextTaskList(String bizId, List<WorkflowUserTaskVo> userTasks) {
        BaseMap baseMap = new BaseMap();
        Map<String, Object> map = Maps.newHashMap();
        map.put(WorkflowConstants.USER_TASKS_KEY,userTasks);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.BIZ_ID, bizId);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.MESSAGE_TYPE, WorkflowConstants.NOTICE_MESSAGE_TYPE.NEXT_TASK_LIST);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.DATA, map);
        baseMap.put(WorkflowConstants.NOTICE_PARAM_KEY.TIME, DateUtils.formatDate(new Date(),DateUtils.FORMAT_DATETIME));
        chdRedisClient.sendMessage(WorkflowConstants.NOTICE_HANDLE.OVERSEE_PROCESS_NOTICE, baseMap);
    }
}
