package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.OverseeIssueTodo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface OverseeIssueTodoMapper extends BaseMapper<OverseeIssueTodo> {

    List<OverseeIssueTodo> selectOverseeIssueTodoList(@Param("map") Map<String, Object> map);

}
