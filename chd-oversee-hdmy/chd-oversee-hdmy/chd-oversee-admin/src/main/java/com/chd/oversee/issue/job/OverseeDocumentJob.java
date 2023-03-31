package com.chd.oversee.issue.job;

import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IOverseeWorkMoveService;
import com.chd.oversee.issue.service.OverseeIssueDocServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class OverseeDocumentJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步OverseeDocumentJob任务调度开始 --- ");
        try {
            synchronized (MyOrg.class){
                OverseeIssueDocServiceImpl overseeIssueDocService = SpringContextUtils.getBean(OverseeIssueDocServiceImpl.class);
                overseeIssueDocService.issueToDoc();
            }
        } catch (Exception e) {
            log.error("OverseeDocumentJob error:",e);
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }


}
