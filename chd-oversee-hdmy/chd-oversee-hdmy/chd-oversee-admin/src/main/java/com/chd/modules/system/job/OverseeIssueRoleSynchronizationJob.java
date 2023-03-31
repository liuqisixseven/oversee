package com.chd.modules.system.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.service.IIssuesAllocationService;
import com.chd.modules.oversee.issue.service.IIssuesSupervisorService;
import com.chd.modules.oversee.issue.service.IOverseeIssueRoleService;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.system.controller.SysUserController;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.service.ISysDepartService;
import com.chd.modules.system.service.ISysUserService;
import com.chd.modules.workflow.service.WorkflowProcessDepartService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
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
public class OverseeIssueRoleSynchronizationJob implements Job {

    private static IOverseeIssueService overseeIssueService;

    private static IOverseeIssueRoleService overseeIssueRoleService;

    private static String usernameDefault = "admin";

    private static int pageSize = 20;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步OverseeIssueRoleSynchronizationJob任务调度开始 --- ");
        try {
            synchronized (MyUser.class){
                getServices();
                List<OverseeIssue> overseeIssueList = overseeIssueService.list(Wrappers.<OverseeIssue>lambdaQuery().eq(OverseeIssue::getDataType,1));
                if(null!=overseeIssueList&&overseeIssueList.size()>0){
                    for(OverseeIssue overseeIssue : overseeIssueList){
                        try {
                            if(null!=overseeIssue&&null!=overseeIssue.getId()){
                                overseeIssueRoleService.synchronization(overseeIssue.getId());
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
        overseeIssueService = SpringContextUtils.getBean(IOverseeIssueService.class);
        overseeIssueRoleService = SpringContextUtils.getBean(IOverseeIssueRoleService.class);
    }


}
