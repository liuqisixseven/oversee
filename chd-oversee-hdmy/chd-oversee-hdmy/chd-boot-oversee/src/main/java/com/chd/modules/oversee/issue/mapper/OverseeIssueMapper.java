package com.chd.modules.oversee.issue.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.issue.entity.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface OverseeIssueMapper extends BaseMapper<OverseeIssue> {

    IPage<OverseeIssue> selectOverseeIssuePageVo(Page<?> page,@Param("map")  Map<String, Object> map);

    List<OverseeIssue> selectOverseeIssueList(@Param("map")  Map<String, Object> map);

    OverseeIssueDetailVo getIssueDetailById(Long issueId);

    IPage<OverseeIssueDetailVo> queryIssueDetailPage(OverseeIssueQueryVo<?> query);

    IPage<OssFileVo> queryOssFilePage(OverseeIssueQueryVo<?> query);

    List<IssueAnalysisItemVo> queryIssueDetailAnalysisList(Map<String, Object> map);

    Integer updateSubmitState(OverseeIssue overseeIssue);

}
