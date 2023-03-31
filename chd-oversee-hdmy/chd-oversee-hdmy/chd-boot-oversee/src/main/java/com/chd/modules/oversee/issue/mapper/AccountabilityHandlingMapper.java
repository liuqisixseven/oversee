package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.AccountabilityHandling;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Description: accountability_handling
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface AccountabilityHandlingMapper extends BaseMapper<AccountabilityHandling> {

    int selectCount(@Param("map")  Map<String, Object> map);

}
