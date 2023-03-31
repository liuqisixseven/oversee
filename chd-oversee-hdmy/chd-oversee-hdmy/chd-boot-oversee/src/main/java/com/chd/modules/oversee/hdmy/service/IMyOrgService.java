package com.chd.modules.oversee.hdmy.service;

import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
public interface IMyOrgService extends IService<MyOrg> {

    MyOrg getOrganizeCompany(String orgId);

    String getOrganizeCompanyOrgName(String orgId);

    List<String> getResponsibleUnitOrgIdList();

    List<String> getResponsibleUnitOrgIdList(boolean isNotCache);

    List<String> getResponsibleDepartmentList();

    List<MyOrg> authorizeOrgList(String userId);

    int addOrUpdate(MyOrg myOrg);

    public List<MyOrg> allSubbranchesAndSubsidiaries(String orgId);

}
