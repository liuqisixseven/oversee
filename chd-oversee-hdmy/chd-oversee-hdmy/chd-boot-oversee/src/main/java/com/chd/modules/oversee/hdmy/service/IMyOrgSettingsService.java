package com.chd.modules.oversee.hdmy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.hdmy.entity.MyOrgSettings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
public interface IMyOrgSettingsService extends IService<MyOrgSettings> {

    IPage<MyOrgSettings> selectMyOrgSettingsPageVo(Page<?> page, Map<String, Object> map);

    @Transactional
    int addOrUpdate(MyOrgSettings myOrgSettings);

    int addOrUpdateByResponsibleUnitOrgIdList(List<String> responsibleUnitOrgIdList);

    List<MyOrgSettings> selectMyOrgSettingsList(Map<String, Object> map);


}
