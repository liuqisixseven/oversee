package com.chd.modules.oversee.hdmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.hdmy.entity.OverseeWorkMove;

import java.util.List;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IOverseeWorkMoveService extends IService<OverseeWorkMove> {
    void workMove(String fromUserId,String toUserId,boolean isJob);
    void workMoveJob();
}
