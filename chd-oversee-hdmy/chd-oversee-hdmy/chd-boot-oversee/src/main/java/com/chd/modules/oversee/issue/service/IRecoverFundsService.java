package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.RecoverFunds;
import com.chd.modules.oversee.issue.entity.RecoverFunds;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IRecoverFundsService extends IService<RecoverFunds> {

    int addOrUpdateList(List<RecoverFunds> recoverFundsList);

    int addOrUpdateList(List<RecoverFunds> recoverFundsList,Long issueId,String departId,String updateUserId,String taskId);

    int addOrUpdate(RecoverFunds recoverFunds);

    int selectCount(Map<String, Object> map);

    BigDecimal selectRecoveryIllegalDisciplinaryFundsNumberSum(@Param("map") Map<String, Object> map);


    BigDecimal selectRecoverDamagesNumberSum(@Param("map") Map<String, Object> map);

}
