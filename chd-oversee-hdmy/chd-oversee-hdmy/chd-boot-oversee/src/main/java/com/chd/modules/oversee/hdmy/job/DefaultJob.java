package com.chd.modules.oversee.hdmy.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;

import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import org.quartz.*;

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
public class DefaultJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步InitializeSystemOrganizationJob任务调度开始 --- ");
        try {
            synchronized (MyOrg.class){
                IMyOrgService myOrgService = SpringContextUtils.getBean(IMyOrgService.class);

                List<MyOrg> hqList = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getParentOrgId, BaseConstant.HEADQUARTERS_ORG_ID));
                if(null!=hqList&&hqList.size()>0){

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }

}
