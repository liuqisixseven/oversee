package com.chd.modules.oversee.hdmy.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IOverseeWorkMoveService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class OverseeWorkMoveJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步workMoveJob任务调度开始 --- ");
        try {
            synchronized (MyOrg.class){
                IOverseeWorkMoveService iOverseeWorkMoveService = SpringContextUtils.getBean(IOverseeWorkMoveService.class);
                iOverseeWorkMoveService.workMoveJob();
            }
        } catch (Exception e) {
            log.error("workMoveJob error:",e);
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }


}
