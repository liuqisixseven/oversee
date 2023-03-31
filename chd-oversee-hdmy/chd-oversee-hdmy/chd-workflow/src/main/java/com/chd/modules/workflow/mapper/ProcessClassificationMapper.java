package com.chd.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.workflow.entity.ProcessClassification;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProcessClassificationMapper extends BaseMapper<ProcessClassification> {

    IPage<ProcessClassification> selectProcessClassificationPageVo(Page<?> page,@Param("map") Map<String, Object> map);

    List<ProcessClassification> selectProcessClassificationList(@Param("map") Map<String, Object> map);


}
