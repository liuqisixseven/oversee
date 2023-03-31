package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.common.api.vo.Result;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.ReasonCancellation;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IReasonCancellationService extends IService<ReasonCancellation> {

    int addOrUpdateList(List<ReasonCancellation> recoverFundsList);

    int addOrUpdateList(List<ReasonCancellation> recoverFundsList,Long issueId,String departId,String updateUserId,String taskId);

    int addOrUpdate(ReasonCancellation recoverFunds);

    int selectCount(Map<String, Object> map);

    public Map<String,Object> getCancelANumber(Map<String,Object> selectMap);

}
