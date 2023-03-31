package com.chd.modules.oversee.issue.mapper;

import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueAppendix;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_appendix
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface OverseeIssueAppendixMapper extends BaseMapper<OverseeIssueAppendix> {

    List<OverseeIssueAppendix> selectOverseeIssueAppendixList(@Param("map")  Map<String, Object> map);

    List<Map<String,Object>> getOssFileList(@Param("map")  Map<String, Object> map);

}
