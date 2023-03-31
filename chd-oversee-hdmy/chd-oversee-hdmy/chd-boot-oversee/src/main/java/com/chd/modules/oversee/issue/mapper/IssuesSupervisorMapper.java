package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chd.modules.oversee.issue.entity.IssuesSupervisor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IssuesSupervisorMapper extends BaseMapper<IssuesSupervisor> {

    List<IssuesSupervisor> selectIssuesSupervisorList(@Param("map") Map<String, Object> map);

}
