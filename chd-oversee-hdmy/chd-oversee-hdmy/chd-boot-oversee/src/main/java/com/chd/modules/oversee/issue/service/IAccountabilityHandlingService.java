package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.AccountabilityHandling;
import com.chd.modules.oversee.issue.entity.OverseeIssue;

import java.util.List;
import java.util.Map;

/**
 * @Description: accountability_handling
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IAccountabilityHandlingService extends IService<AccountabilityHandling> {

    int addOrUpdateList(List<AccountabilityHandling> accountabilityHandlingList);

    int addOrUpdateList(List<AccountabilityHandling> accountabilityHandlingList,Long issueId,String departId,String updateUserId,String taskId);

    int addOrUpdate(AccountabilityHandling accountabilityHandling);


    int selectCount(Map<String, Object> map);

}
