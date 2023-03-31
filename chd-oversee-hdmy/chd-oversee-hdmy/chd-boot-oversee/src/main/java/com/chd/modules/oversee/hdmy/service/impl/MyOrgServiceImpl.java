package com.chd.modules.oversee.hdmy.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.api.CommonAPI;
import com.chd.common.system.vo.DictModel;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.mapper.MyOrgMapper;
import com.chd.modules.oversee.hdmy.mapper.MyUserMapper;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyOrgSettingsService;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class MyOrgServiceImpl extends ServiceImpl<MyOrgMapper, MyOrg> implements IMyOrgService {

    @Autowired
    MyOrgMapper myOrgMapper;

    @Autowired
    IMyOrgSettingsService myOrgSettingsService;

    @Autowired
    MyUserMapper myUserMapper;

    @Autowired
    @Lazy
    protected CommonAPI commonAPI;

    @Autowired
    @Lazy
    private IOverseeIssueService overseeIssueService;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Override
    public MyOrg getOrganizeCompany(String orgId) {
        if(StringUtil.isNotEmpty(orgId)){
            List<MyOrg> myOrgs = myOrgMapper.selectList(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getOrgId, orgId));
            if(null!=myOrgs&&myOrgs.size()>0){
                MyOrg myOrg = myOrgs.get(0);
                if(null!=myOrg){
//                    if(null!=myOrg.getOrgLevel()&&(myOrg.getOrgLevel().intValue()==2||myOrg.getOrgLevel().intValue()==1)){
//                        return myOrg;
//                    }
                    if(null!=myOrg.getOrgLevel()&&(myOrg.getOrgLevel().intValue()==2)){
                        return myOrg;
                    }else if(StringUtil.isNotEmpty(myOrg.getParentOrgId())){
                        return getOrganizeCompany(myOrg.getParentOrgId());
                    }
                }
            }
        }
        return null;
    }



    @Override
    public String getOrganizeCompanyOrgName(String orgId) {
        MyOrg organizeCompany = getOrganizeCompany(orgId);
        if(null!=organizeCompany){
            return organizeCompany.getOrgName();
        }
        return null;
    }

    @Override
    public List<String> getResponsibleUnitOrgIdList(){
        return getResponsibleUnitOrgIdList(false);
    }

    @Override
    public List<String> getResponsibleUnitOrgIdList(boolean isNotCache){
        List<String> responsibleUnitOrgIdList = null;
        Map<String,Object> responsibleUnitOrgIdListMap = Maps.newHashMap();
        try{
            String responsibleUnitOrgIdListMapRedisKey = BaseConstant.MY_ORG_RESPONSIBLE_UNIT_ORG_ID_LIST_MAP_KEY;
            if(!isNotCache){
                responsibleUnitOrgIdListMap = (Map<String, Object>) redisTemplate.opsForValue().get(responsibleUnitOrgIdListMapRedisKey);
                if(null!=responsibleUnitOrgIdListMap){
                    String dateSrc = (String) responsibleUnitOrgIdListMap.get(BaseConstant.MY_ORG_DATE_SRC_KEY);
                    if(StringUtil.isNotEmpty(dateSrc)&&(DateUtils.str2Date(DateUtils.date2Str(new Date(),DateUtils.date_sdf.get()),DateUtils.date_sdf.get()).getTime()<=DateUtils.str2Date(dateSrc,DateUtils.date_sdf.get()).getTime())){
                        responsibleUnitOrgIdList = (List<String>) responsibleUnitOrgIdListMap.get(BaseConstant.MY_ORG_RESPONSIBLE_UNIT_ORG_ID_LIST_KEY);
                    }
                }
            }
            if(CollectionUtils.isEmpty(responsibleUnitOrgIdList)){
                responsibleUnitOrgIdListMap = Maps.newHashMap();
                responsibleUnitOrgIdList = getResponsibleDepartmentList();
                responsibleUnitOrgIdListMap.put(BaseConstant.MY_ORG_DATE_SRC_KEY,DateUtils.date2Str(new Date(),DateUtils.date_sdf.get()));
                responsibleUnitOrgIdListMap.put(BaseConstant.MY_ORG_RESPONSIBLE_UNIT_ORG_ID_LIST_KEY,responsibleUnitOrgIdList);
                redisTemplate.opsForValue().set(responsibleUnitOrgIdListMapRedisKey,responsibleUnitOrgIdListMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return responsibleUnitOrgIdList;
    }


    @Override
    public List<String> getResponsibleDepartmentList(){
        List<String> responsibleUnitOrgIdList = null;
        List<OverseeIssue> overseeIssueList = overseeIssueService.list(Wrappers.<OverseeIssue>lambdaQuery().eq(OverseeIssue::getDataType, 1).groupBy(OverseeIssue::getResponsibleUnitOrgId));
        if(null!=overseeIssueList&&overseeIssueList.size()>0){
            responsibleUnitOrgIdList = overseeIssueList.stream().filter((overseeIssue) -> null != overseeIssue && StringUtil.isNotEmpty(overseeIssue.getResponsibleUnitOrgId())).map((overseeIssue) -> overseeIssue.getResponsibleUnitOrgId()).collect(Collectors.toList());
        }
        return responsibleUnitOrgIdList;
    }

    @Override
    public List<MyOrg> authorizeOrgList(String userId) {
        Assert.isTrue((StringUtil.isNotEmpty(userId)),"请传递userId");
        List<MyOrg> myOrgs = myOrgMapper.selectList(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getManagerId, userId).or().eq(MyOrg::getUpperSupervisorId,userId));

        MyUser myUserById = myUserMapper.selectById(userId);
        if(null!=myUserById){

            List<MyOrg> allSubbranchesAndSubsidiariesMyOrgList = new ArrayList<>();
            if(StringUtil.isNotEmpty(myUserById.getOrgId())){

                if(StringUtil.isNotEmpty(myUserById.getOrgDuty())&&(myUserById.getOrgDuty().trim().equals("1")||myUserById.getOrgDuty().trim().equals("2"))){
                    List<MyOrg> myOrgCompanyOrDepartmenList = allSubbranchesAndSubsidiaries(myUserById.getOrgId());
                    if(CollectionUtils.isEmpty(myOrgCompanyOrDepartmenList)){
                        allSubbranchesAndSubsidiariesMyOrgList.addAll(myOrgCompanyOrDepartmenList);
                    }
                }

                MyOrg myOrg = myOrgMapper.selectById(myUserById.getOrgId());
                String orgName = StringUtil.isNotEmpty(myOrg.getOrgName())?myOrg.getOrgName().trim():null;
                String orgShortName = StringUtil.isNotEmpty(myOrg.getOrgShortName())?myOrg.getOrgShortName().trim():null;

                List<DictModel> managerTitleList = commonAPI.queryDictItemsByCode(BaseConstant.MANAGER_TITLE_DICT_KEY);
                if(CollectionUtils.isNotEmpty(managerTitleList)){
                    for(DictModel dictModel : managerTitleList){
                        if(null!=dictModel&&StringUtil.isNotEmpty(dictModel.getValue())&&
                                        (StringUtil.isNotEmpty(myUserById.getTitile())&&(dictModel.getValue().indexOf(myUserById.getTitile())!=-1||myUserById.getTitile().indexOf(dictModel.getValue())!=-1))
                        ){
                            List<MyOrg> myOrgDepartmentList = allSubbranchesAndSubsidiaries(myUserById.getOrgId());
                            if(CollectionUtils.isNotEmpty(myOrgDepartmentList)){
                                allSubbranchesAndSubsidiariesMyOrgList.addAll(myOrgDepartmentList);
                            }
                        }
                    }
                }

//                部门名称包含公司领导的用户 有公司领导的权限
                String companyLeadersDepartment = "公司领导";
//                监督部部门也有所有权限
                String watchdogDepartment = "监督部";
                if(
                        (StringUtil.isNotEmpty(orgName)&&(companyLeadersDepartment.indexOf(orgName)!=-1||orgName.indexOf(companyLeadersDepartment)!=-1))
                         ||(StringUtil.isNotEmpty(orgShortName)&&(companyLeadersDepartment.indexOf(orgShortName)!=-1||orgShortName.indexOf(companyLeadersDepartment)!=-1))
                         ||(StringUtil.isNotEmpty(orgName)&&(watchdogDepartment.indexOf(orgName)!=-1||orgName.indexOf(watchdogDepartment)!=-1))
                                ||(StringUtil.isNotEmpty(orgShortName)&&(watchdogDepartment.indexOf(orgShortName)!=-1||orgShortName.indexOf(watchdogDepartment)!=-1))
                ){
//                            查找当前部门的公司
                    MyOrg organizeCompany = getOrganizeCompany(myUserById.getOrgId());
                    if(null!=organizeCompany&&StringUtil.isNotEmpty(organizeCompany.getOrgId())){
                        List<MyOrg> myOrgCompanyList = allSubbranchesAndSubsidiaries(organizeCompany.getOrgId());
                        if(CollectionUtils.isNotEmpty(myOrgCompanyList)){
                            allSubbranchesAndSubsidiariesMyOrgList.addAll(myOrgCompanyList);
                        }
                    }
                }

            }

            if(CollectionUtils.isNotEmpty(allSubbranchesAndSubsidiariesMyOrgList)&&allSubbranchesAndSubsidiariesMyOrgList.size()>0){
                if(CollectionUtils.isEmpty(myOrgs)){
                    myOrgs = new ArrayList<>();
                }
                myOrgs.addAll(allSubbranchesAndSubsidiariesMyOrgList);
            }
        }

        return myOrgs;
    }

//    获取当前orgId对应公司部门
//    以及当前orgId对应公司部门下的所有子部门
    public List<MyOrg> allSubbranchesAndSubsidiaries(String orgId){
        if(StringUtil.isNotEmpty(orgId)){
            MyOrg myOrg = myOrgMapper.selectById(orgId);
            if(null!=myOrg&&StringUtil.isNotEmpty(myOrg.getPath())){
                List<MyOrg> myOrgList = myOrgMapper.selectList(Wrappers.<MyOrg>lambdaQuery().likeRight(MyOrg::getPath, myOrg.getPath()));
                if(CollectionUtils.isEmpty(myOrgList)){
                    myOrgList = new ArrayList<>();
                    myOrgList.add(myOrg);
                }
                return myOrgList;
            }
        }
        return  null;
    }

    @Override
    @Transactional
    public int addOrUpdate(MyOrg myOrg) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=myOrg),"请传递myOrg数据");
        if(null!=myOrg.getOrgId()){
            addOrUpdateCount = myOrgMapper.updateById(myOrg);
        }else {
            Assert.isTrue((StringUtil.isNotEmpty(myOrg.getOrgName())),"请传递单位名称");
            addOrUpdateCount = myOrgMapper.insert(myOrg);
        }
        return addOrUpdateCount;
    }


}
