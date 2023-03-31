package com.chd.modules.oversee.hdmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.hdmy.entity.OverseeWorkMove;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface OverseeWorkMoveMapper extends BaseMapper<OverseeWorkMove> {
    void workMove(String fromUserId,String toUserId,boolean isJob);
    void workMoveJob();
}
