package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.CommonOpinions;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface ICommonOpinionsService extends IService<CommonOpinions> {

    public int addOrUpdate(CommonOpinions commonOpinions);


}
