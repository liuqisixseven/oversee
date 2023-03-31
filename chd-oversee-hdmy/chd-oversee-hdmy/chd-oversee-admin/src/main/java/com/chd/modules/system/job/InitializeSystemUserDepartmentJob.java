package com.chd.modules.system.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.common.util.oConvertUtils;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.system.controller.SysUserController;
import com.chd.modules.system.entity.*;
import com.chd.modules.system.mapper.SysUserDepartMapper;
import com.chd.modules.system.service.ISysDepartService;
import com.chd.modules.system.service.ISysUserDepartService;
import com.chd.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 同步定时任务测试
 *
 * 此处的同步是指 当定时任务的执行时间大于任务的时间间隔时
 * 会等待第一个任务执行完成才会走第二个任务
 *
 *
 * @author: taoyan
 * @date: 2020年06月19日
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class InitializeSystemUserDepartmentJob implements Job {


    private static IMyUserService myUserService;

    private static SysUserController sysUserController;

    private static ISysUserService sysUserService;

    private static ISysDepartService sysDepartService;

    private static ISysUserDepartService sysUserDepartService;

    @Autowired
    private SysUserDepartMapper sysUserDepartMapper;

    private static String usernameDefault = "admin";

    private static int pageSize = 20;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步InitializeSystemUserDepartmentJob任务调度开始 --- ");
        try {
            synchronized (MyUser.class){
                getServices();
                long count = myUserService.count();
                int totalPageCount = Long.valueOf(count).intValue() / pageSize + 1;
                for (int i=1; i<=totalPageCount;i++){
                    Page<SysUser> page = new Page<SysUser>(i, pageSize);
                    Page<SysUser> pageList = sysUserService.page(page);
                    if(null!=pageList){
                        List<SysUser> myUserList = pageList.getRecords();
                        if(null!=myUserList&&myUserList.size()>0){
                            for(SysUser sysUser : myUserList){
                                try{
                                    if(StringUtil.isNotEmpty(sysUser.getHdmyOrgId())){
                                        SysDepart sysDepart = sysDepartService.getOne(Wrappers.<SysDepart>lambdaQuery().eq(SysDepart::getId, sysUser.getHdmyOrgId()));
                                        if(null!=sysDepart&&StringUtil.isNotEmpty(sysDepart.getId())){
                                            if(sysUserDepartService.count(Wrappers.<SysUserDepart>lambdaQuery().eq(SysUserDepart::getUserId,sysUser.getId()).eq(SysUserDepart::getDepId,sysDepart.getId()))<=0){
                                                sysUserDepartService.save(new SysUserDepart(sysUser.getId(),sysDepart.getId()));
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }




    private static void getServices(){
        myUserService = SpringContextUtils.getBean(IMyUserService.class);
        sysUserController = SpringContextUtils.getBean(SysUserController.class);
        sysUserService = SpringContextUtils.getBean(ISysUserService.class);
        sysDepartService = SpringContextUtils.getBean(ISysDepartService.class);
        sysUserDepartService = SpringContextUtils.getBean(ISysUserDepartService.class);
    }


}
