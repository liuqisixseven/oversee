package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.IssuesRectificationMeasure;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: issues_rectification_measure
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IIssuesRectificationMeasureService extends IService<IssuesRectificationMeasure> {

    @Transactional
    int addOrUpdateList(List<IssuesRectificationMeasure> issuesRectificationMeasureList);

    @Transactional
    int addOrUpdateList(List<IssuesRectificationMeasure> issuesRectificationMeasureList, Long issueId, String departId, String updateUserId, String taskId,String updateBy);

    @Transactional
    int addOrUpdate(IssuesRectificationMeasure issuesRectificationMeasure);

    List<IssuesRectificationMeasure> srcToIssuesRectificationMeasureList(String str, Date rectificationTimeLimit);

    List<IssuesRectificationMeasure> selectIssuesRectificationMeasureList(Map<String, Object> map);
}
