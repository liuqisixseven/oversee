package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.RectifyViolations;
import com.chd.modules.oversee.issue.entity.RectifyViolations;

import java.util.List;
import java.util.Map;

/**
 * @Description: rectify_violations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IRectifyViolationsService extends IService<RectifyViolations> {

    int addOrUpdateList(List<RectifyViolations> rectifyViolationsList);

    int addOrUpdateList(List<RectifyViolations> rectifyViolationsList,Long issueId,String departId,String updateUserId,String taskId);

    int addOrUpdate(RectifyViolations rectifyViolations);

    int selectCount(Map<String, Object> map);

}
