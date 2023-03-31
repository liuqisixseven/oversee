package com.chd.modules.system.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrgSettings;
import com.chd.modules.oversee.hdmy.service.IMyOrgSettingsService;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.service.ISysUserService;
import com.chd.modules.system.util.HdmyHandleUtils;
import lombok.extern.slf4j.Slf4j;

import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.system.entity.SysDepart;
import com.chd.modules.system.service.ISysDepartService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.*;

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
public class InitializeMyOrgSettingsDataJob implements Job {

    private static IMyOrgService myOrgService;

    private static IMyOrgSettingsService myOrgSettingsService;

    private static String usernameDefault = "admin";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步InitializeSystemOrganizationJob任务调度开始 --- ");

        getServices();
        try {
            synchronized (MyOrg.class){
                List<String> responsibleUnitOrgIdList = myOrgService.getResponsibleUnitOrgIdList(true);
                myOrgSettingsService.addOrUpdateByResponsibleUnitOrgIdList(responsibleUnitOrgIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }



    private static void getServices(){
        if(null==myOrgService){
            myOrgService = SpringContextUtils.getBean(IMyOrgService.class);
        }

        if(null==myOrgSettingsService){
            myOrgSettingsService = SpringContextUtils.getBean(IMyOrgSettingsService.class);
        }
    }





}
