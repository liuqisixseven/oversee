package com.chd.modules.system.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.system.controller.SysUserController;
import com.chd.modules.system.entity.SysDepart;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.service.ISysDepartService;
import com.chd.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Date;
import java.util.List;

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
public class UpdateSystemUserPasswordJob implements Job {


    private static IMyUserService myUserService;

    private static SysUserController sysUserController;

    private static ISysUserService sysUserService;

    private static ISysDepartService sysDepartService;

    private static String usernameDefault = "admin";

    private static int pageSize = 20;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步UpdateSystemUserPasswordJob任务调度开始 --- ");
        try {
            synchronized (MyUser.class){
                getServices();
                List<SysUser> sysUserList = sysUserService.list();
                if(null!=sysUserList&&sysUserList.size()>0){
                    for(SysUser sysUser : sysUserList){
                        try {
                            if(null!=sysUser){
                                if(StringUtil.isNotEmpty(sysUser.getUsername())&&!sysUser.getUsername().equals("admin")){
                                    sysUser.setPassword("000");
                                    sysUserService.changePassword(sysUser);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
    }


}
