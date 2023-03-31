package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import com.chd.modules.oversee.issue.entity.OverseeIssueTodo;

import java.util.List;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IOverseeIssueTodoService extends IService<OverseeIssueTodo> {

    void synchronization(Long overseeIssueId);

    int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId);

    int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId, String role);

    public int addOrUpdate(OverseeIssueTodo overseeIssueRole);

    public void updateSendStatusByTaskId(String taskId,String userId);

    List<OverseeIssueTodo> selectOverseeIssueTodoList(Map<String, Object> map);

}
