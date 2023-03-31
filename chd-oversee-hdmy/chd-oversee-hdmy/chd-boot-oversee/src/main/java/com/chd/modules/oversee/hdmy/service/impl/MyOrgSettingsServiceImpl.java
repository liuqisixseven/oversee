package com.chd.modules.oversee.hdmy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.api.CommonAPI;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrgSettings;
import com.chd.modules.oversee.hdmy.mapper.MyOrgSettingsMapper;
import com.chd.modules.oversee.hdmy.mapper.MyUserMapper;
import com.chd.modules.oversee.hdmy.service.IMyOrgSettingsService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class MyOrgSettingsServiceImpl extends ServiceImpl<MyOrgSettingsMapper, MyOrgSettings> implements IMyOrgSettingsService {

    @Autowired
    MyOrgSettingsMapper myOrgSettingsMapper;

    @Autowired
    @Lazy
    protected CommonAPI commonAPI;


    @Override
    public IPage<MyOrgSettings> selectMyOrgSettingsPageVo(Page<?> page, Map<String, Object> map) {
        IPage<MyOrgSettings> myOrgSettingsIPage = myOrgSettingsMapper.selectMyOrgSettingsPageVo(page, map);
        return myOrgSettingsIPage;
    }

    @Override
    @Transactional
    public int addOrUpdate(MyOrgSettings myOrgSettings) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=myOrgSettings),"请传递数据");
        Assert.isTrue((StringUtil.isNotEmpty(myOrgSettings.getOrgId())),"请传递id");
        Assert.isTrue((StringUtil.isNotEmpty(myOrgSettings.getUpdateUserId())),"未获取到修改用户id");
        if(null==myOrgSettings.getSort()){
            myOrgSettings.setSort(100);
        }

        myOrgSettings.setUpdateTime(new Date());

        MyOrgSettings myOrgSettingsS = myOrgSettingsMapper.selectById(myOrgSettings.getOrgId());
        if(null!=myOrgSettingsS&&StringUtil.isNotEmpty(myOrgSettingsS.getOrgId())){
            addOrUpdateCount = myOrgSettingsMapper.updateById(myOrgSettings);
        }else{
            myOrgSettings.setCreateUserId(myOrgSettings.getUpdateUserId());
            myOrgSettings.setCreateTime(myOrgSettings.getUpdateTime());
            myOrgSettings.setDataType(OverseeConstants.DataType.Enable);
            addOrUpdateCount = myOrgSettingsMapper.insert(myOrgSettings);
        }
        return addOrUpdateCount;
    }

    @Override
    @Transactional
    public int addOrUpdateByResponsibleUnitOrgIdList(List<String> responsibleUnitOrgIdList) {
        int addOrUpdateCount = 0;
        if(CollectionUtils.isNotEmpty(responsibleUnitOrgIdList)){
            for(String responsibleUnitOrgId : responsibleUnitOrgIdList){
                if(StringUtil.isNotEmpty(responsibleUnitOrgId)){
                    MyOrgSettings myOrgSettingsS = myOrgSettingsMapper.selectById(responsibleUnitOrgId);
                    if(null==myOrgSettingsS){
                        MyOrgSettings myOrgSettings = new MyOrgSettings();
                        myOrgSettings.setOrgId(responsibleUnitOrgId);
                        myOrgSettings.setIsShow(1);
                        myOrgSettings.setSort(100);
                        myOrgSettings.setUpdateUserId("admin");
                        addOrUpdateCount += addOrUpdate(myOrgSettings);
                    }
                }
            }
        }
        return addOrUpdateCount;
    }


    @Override
    public List<MyOrgSettings> selectMyOrgSettingsList(Map<String, Object> map) {
        return myOrgSettingsMapper.selectMyOrgSettingsList(map);
    }
}
