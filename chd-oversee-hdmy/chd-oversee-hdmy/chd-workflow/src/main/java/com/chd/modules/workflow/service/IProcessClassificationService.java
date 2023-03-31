package com.chd.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.workflow.entity.ProcessClassification;

import java.util.List;
import java.util.Map;

public interface IProcessClassificationService extends IService<ProcessClassification> {

    int addOrUpdate(ProcessClassification processClassification);

    IPage<ProcessClassification> selectProcessClassificationPageVo(Page<?> page, Map<String, Object> map);

    List<ProcessClassification> selectProcessClassificationList(Map<String, Object> map);


}
