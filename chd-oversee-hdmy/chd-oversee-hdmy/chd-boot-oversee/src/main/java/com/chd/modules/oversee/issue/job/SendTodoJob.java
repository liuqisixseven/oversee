package com.chd.modules.oversee.issue.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.SpringContextUtils;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.issue.entity.OverseeIssueTodo;
import com.chd.modules.oversee.issue.service.IOverseeIssueTodoService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class SendTodoJob  implements Job {

    private static IOverseeIssueTodoService overseeIssueTodoService;

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static Map<String,Object> overseeIssueTodoMsp = Maps.newHashMap();

    private static String runnableOverseeIssueTodoIdListKey = "runnableOverseeIssueTodoIdList";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- SendTodoJob 任务调度开始 --- ");
        getServices();
        LambdaQueryWrapper<OverseeIssueTodo> overseeIssueTodoWrappers = Wrappers.<OverseeIssueTodo>lambdaQuery().eq(OverseeIssueTodo::getSendStatus, 0);
        List<Integer> runnableOverseeIssueTodoIdList = (List<Integer>) overseeIssueTodoMsp.get(runnableOverseeIssueTodoIdListKey);
        if(null!=runnableOverseeIssueTodoIdList&&runnableOverseeIssueTodoIdList.size()>0){
            overseeIssueTodoWrappers.notIn(OverseeIssueTodo::getId,runnableOverseeIssueTodoIdList);
        }
        List<OverseeIssueTodo> overseeIssueTodoList = overseeIssueTodoService.list(overseeIssueTodoWrappers);
        if(null!=overseeIssueTodoList&&overseeIssueTodoList.size()>0){
            for(OverseeIssueTodo overseeIssueTodo : overseeIssueTodoList){
                if(null!=overseeIssueTodo&&null!=overseeIssueTodo.getId()){
                    addRunnableToExecutorService(overseeIssueTodo.getId(),()->{
                        sendTodo(overseeIssueTodo);
                    });
                }
            }
        }
    }

    public static void sendTodo(OverseeIssueTodo overseeIssueTodo){
        getServices();
        if(null!=overseeIssueTodo){
            System.out.println("SendTodoJob :: sendTodo :: " + overseeIssueTodo.getId());
        }
    }

    public static void addRunnableToExecutorService(Integer overseeIssueTodoId,Runnable command){
        getServices();
        List<Integer> runnableOverseeIssueTodoIdList = (List<Integer>) overseeIssueTodoMsp.get(runnableOverseeIssueTodoIdListKey);
        if(null==runnableOverseeIssueTodoIdList){
            runnableOverseeIssueTodoIdList = new ArrayList<>();
        }
        runnableOverseeIssueTodoIdList.add(overseeIssueTodoId);
        executorService.execute(command);
    }

    private static void getServices(){
        if(null==overseeIssueTodoService){
            overseeIssueTodoService = SpringContextUtils.getBean(IOverseeIssueTodoService.class);
        }
    }
}
