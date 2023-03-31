package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.issue.entity.SystemTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Description: oversee_issue_category
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface ISystemTemplateService extends IService<SystemTemplate> {

    int addOrUpdate(SystemTemplate systemTemplate);

    IPage<SystemTemplate> selectSystemTemplatePageVo(Page<?> page, Map<String, Object> map);

    List<SystemTemplate> selectSystemTemplateList(Map<String, Object> map);


}
