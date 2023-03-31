package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.ReasonCancellation;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface ReasonCancellationMapper extends BaseMapper<ReasonCancellation> {

    int selectCount(@Param("map") Map<String, Object> map);

    BigDecimal selectRecoveryIllegalDisciplinaryFundsNumberSum(@Param("map") Map<String, Object> map);


    BigDecimal selectRecoverDamagesNumberSum(@Param("map") Map<String, Object> map);

}
