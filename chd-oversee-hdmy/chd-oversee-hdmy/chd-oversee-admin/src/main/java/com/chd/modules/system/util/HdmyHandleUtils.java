package com.chd.modules.system.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.system.controller.SysUserController;
import com.chd.modules.system.entity.SysDepart;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.entity.SysUserDepart;
import com.chd.modules.system.service.ISysDepartService;
import com.chd.modules.system.service.ISysUserDepartService;
import com.chd.modules.system.service.ISysUserService;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HdmyHandleUtils {

    private static IMyUserService myUserService;

    private static SysUserController sysUserController;

    private static ISysUserService sysUserService;

    private static ISysDepartService sysDepartService;

    private static ISysUserDepartService sysUserDepartService;

    private static IMyOrgService myOrgService;

    private static String usernameDefault = "admin";

    public static void sysUserHandleByMyUser(MyUser myUser){
        if(StringUtil.isNotEmpty(myUser.getUserId())&&!"admin".equals(myUser.getUserId())){
//            List<SysUser> sysUserList = sysUserService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, myUser.getUserId()));
            List<SysUser> sysUserList = sysUserService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getHdmyUserId, myUser.getUserId()));
            JSONObject myUserJsonObject = myUserToJSONObject(myUser);
            if(CollectionUtils.isNotEmpty(sysUserList)){
                if(null!=sysUserList.get(0)){
                    SysUser sysUser = sysUserList.get(0);
                    myUserJsonObject.put("id",sysUser.getId());
                    sysUserController.edit(myUserJsonObject);
                }

            }else{
                String id = myUserJsonObject.getString("id");
                boolean isAdd = true;
                if(StringUtil.isNotEmpty(id)){
                    SysUser sysUserById = sysUserService.getById(id);
                    if(null!=sysUserById&&StringUtil.isNotEmpty(sysUserById.getId())&&sysUserById.getId().equals(id)){
                        isAdd = false;
                    }
                }
                if(isAdd){
                    sysUserController.add(myUserJsonObject);
                }else{
                    sysUserController.edit(myUserJsonObject);
                }
            }
//            sysUserList = sysUserService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, myUser.getUserId()));
            sysUserList = sysUserService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getHdmyUserId, myUser.getUserId()));
            if(null!=sysUserList&&sysUserList.size()>0){
                sysDepartHandleBySysUser(sysUserList.get(0));
            }

        }
    }

    public static JSONObject myUserToJSONObject(MyUser myUser){
        JSONObject jsonObject = null;
        if(null!=myUser&& StringUtil.isNotEmpty(myUser.getUserId())){
            jsonObject = new JSONObject();
            jsonObject.put("id",myUser.getUserId());
            jsonObject.put("username",myUser.getUserId());
            jsonObject.put("hdmyUserId",myUser.getUserId());
            jsonObject.put("hdmyOrgId",myUser.getOrgId());
            jsonObject.put("selecteddeparts",myUser.getOrgId());
            jsonObject.put("realname",myUser.getUserName());
//            if(StringUtil.isEmpty(myUser.getPassword())){
//                myUser.setPassword("000");
//            }
            myUser.setPassword("000");
            jsonObject.put("password",myUser.getPassword());
            jsonObject.put("sex",(StringUtil.isNotEmpty(myUser.getSex())?("m".equals(myUser.getSex())?"1":"2"):"1"));
            jsonObject.put("email",myUser.getMail());
            if(StringUtil.isNotEmpty(myUser.getOrgId())){
                SysDepart sysDepart = sysDepartService.getOne(Wrappers.<SysDepart>lambdaQuery().eq(SysDepart::getId, myUser.getOrgId()));
                if(null!=sysDepart&&StringUtil.isNotEmpty(sysDepart.getOrgCode())){
                    jsonObject.put("orgCode",sysDepart.getOrgCode());
                }
            }
            jsonObject.put("post",myUser.getTitile());
            jsonObject.put("createBy",usernameDefault);
            jsonObject.put("updateBy",usernameDefault);
            jsonObject.put("createTime",new Date());
            jsonObject.put("updateTime",new Date());
            jsonObject.put("userIdentity",1);
        }
        return jsonObject;
    }



    public static int addOrUpdateSystemDepart(MyOrg myOrg){
        int addOrUpdateCount =0;
        if(null!=myOrg){
            if(StringUtil.isNotEmpty(myOrg.getOrgId())){
                synchronized (SysDepart.class){
                    if(StringUtil.isNotEmpty(myOrg.getOrgId())&&StringUtil.isNotEmpty(myOrg.getPath())){
                        MyOrg myOrgU = new MyOrg();
                        myOrgU.setOrgId(myOrg.getOrgId());
                        myOrgU.setPath(myOrg.getPath());
                        myOrgService.updateById(myOrgU);
                    }
                    List<SysDepart> sysDepartList = sysDepartService.list(Wrappers.<SysDepart>lambdaQuery().eq(SysDepart::getId, myOrg.getOrgId()));
                    if(null==sysDepartList||sysDepartList.size()<=0){
                        SysDepart sysDepart = myOrgToSysDepart(myOrg);
                        sysDepartService.saveDepartData(sysDepart,usernameDefault);
                        addOrUpdateCount++;
                    }else {
                        SysDepart sysDepart = sysDepartList.get(0);
                        if(null!=sysDepart&&sysDepart.getId().equals(myOrg.getOrgId())){
                            List<SysUser> sysUserList = sysUserService.list(Wrappers.<SysUser>lambdaQuery().apply(" FIND_IN_SET( '" + sysDepart.getId() + "',depart_ids )"));
                            if(CollectionUtils.isNotEmpty(sysUserList)){
                                sysDepart.setOldDirectorUserIds(sysUserList.stream().map((sysUser)->sysUser.getId()).collect(Collectors.joining(",")));
                            }
                            sysDepart.setHdmyOrgId(myOrg.getOrgId());
                            sysDepart.setDepartName((StringUtil.isNotEmpty(myOrg.getOrgShortName()))?myOrg.getOrgShortName():myOrg.getOrgName());
                            if(StringUtil.isNotEmpty(myOrg.getManagerId())){
                                SysUser managerSysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getHdmyUserId, myOrg.getManagerId()));
                                if(null!=managerSysUser&&StringUtil.isNotEmpty(managerSysUser.getId())){
                                    sysDepart.setDirectorUserIds(managerSysUser.getId());
                                }
                            }
                            if(StringUtil.isNotEmpty(myOrg.getUpperSupervisorId())){
                                SysUser upperSupervisorSysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getHdmyUserId, myOrg.getUpperSupervisorId()));
                                if(null!=upperSupervisorSysUser&&StringUtil.isNotEmpty(upperSupervisorSysUser.getId())){
                                    sysDepart.setUpperSupervisorId(upperSupervisorSysUser.getId());
                                    if(StringUtil.isNotEmpty(upperSupervisorSysUser.getUsername())){
                                        sysDepart.setUpperSupervisorName(upperSupervisorSysUser.getUsername());
                                    }
                                }
                            }
                            sysDepart.setPath(myOrg.getPath());
                            sysDepartService.updateDepartDataById(sysDepart,usernameDefault);
                            addOrUpdateCount++;
                        }
                    }
                }
            }
        }
        return addOrUpdateCount;
    }


    public static SysDepart myOrgToSysDepart(MyOrg myOrg){
        return myOrgToSysDepart(myOrg,null);
    }

    public static SysDepart myOrgToSysDepart(MyOrg myOrg,SysDepart sysDepart){
        if(null==sysDepart){
            sysDepart = new SysDepart();
        }
        sysDepart.setId(myOrg.getOrgId());
        sysDepart.setDepartName(StringUtil.isNotEmpty(myOrg.getOrgShortName())?myOrg.getOrgShortName():myOrg.getOrgName());
        sysDepart.setDepartNameAbbr(myOrg.getOrgShortName());
        if(null!=myOrg.getOrgLevel()){
            sysDepart.setOrgCategory(myOrg.getOrgLevel().toString());
        }
        sysDepart.setParentId(myOrg.getParentOrgId());
        sysDepart.setDelFlag("0");
        sysDepart.setCreateBy(usernameDefault);
        sysDepart.setUpdateBy(usernameDefault);
        sysDepart.setCreateTime(new Date());
        sysDepart.setUpdateTime(new Date());
        sysDepart.setHdmyOrgId(myOrg.getOrgId());
        sysDepart.setPath(myOrg.getPath());
        return sysDepart;
    }


    public static void sysDepartHandleBySysUser(SysUser sysUser){
        if(null!=sysUser){
            if(StringUtil.isNotEmpty(sysUser.getHdmyOrgId())){
                SysDepart sysDepart = sysDepartService.getOne(Wrappers.<SysDepart>lambdaQuery().eq(SysDepart::getId, sysUser.getHdmyOrgId()));
                if(null!=sysDepart&&StringUtil.isNotEmpty(sysDepart.getId())){
                    if(sysUserDepartService.count(Wrappers.<SysUserDepart>lambdaQuery().eq(SysUserDepart::getUserId,sysUser.getId()).eq(SysUserDepart::getDepId,sysDepart.getId()))<=0){
                        sysUserDepartService.save(new SysUserDepart(sysUser.getId(),sysDepart.getId()));
                    }
                }
            }
        }
    }


    static {
        myUserService = SpringContextUtils.getBean(IMyUserService.class);
        sysUserController = SpringContextUtils.getBean(SysUserController.class);
        sysUserService = SpringContextUtils.getBean(ISysUserService.class);
        sysDepartService = SpringContextUtils.getBean(ISysDepartService.class);
        sysUserDepartService = SpringContextUtils.getBean(ISysUserDepartService.class);
        myOrgService = SpringContextUtils.getBean(IMyOrgService.class);
    }
}
