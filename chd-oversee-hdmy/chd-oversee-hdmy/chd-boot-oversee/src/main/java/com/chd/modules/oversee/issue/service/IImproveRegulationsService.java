package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.ImproveRegulations;

import java.util.List;
import java.util.Map;

/**
 * @Description: improve_regulations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IImproveRegulationsService extends IService<ImproveRegulations> {

    int addOrUpdateList(List<ImproveRegulations> improveRegulationsList);

    int addOrUpdateList(List<ImproveRegulations> improveRegulationsList,Long issueId,String departId,String updateUserId,String taskId);

    int addOrUpdate(ImproveRegulations improveRegulations);

    int selectCount(Map<String, Object> map);

}
