package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.OverseeIssueRole;
import com.chd.modules.oversee.issue.entity.ReasonCancellation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IOverseeIssueRoleService extends IService<OverseeIssueRole> {

    void synchronization(Long overseeIssueId);

    @Transactional
    int isAuthorizeSupervise(Long issueId, String orgIds, String userIds);

    @Transactional
    int isAuthorizeSupervise(Long issueId, String orgIds, String userIds, String sources);

    int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId);

    int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId, String role);

    @Transactional
    int addOrUpdate(String dataId, Integer roleType, Long issueId, String updateUserId, String role, String source);

    public int addOrUpdate(OverseeIssueRole overseeIssueRole);

}
