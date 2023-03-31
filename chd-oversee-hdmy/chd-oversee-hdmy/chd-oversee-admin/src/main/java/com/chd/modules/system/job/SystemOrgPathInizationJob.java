package com.chd.modules.system.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
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
public class SystemOrgPathInizationJob implements Job {

    private static IMyOrgService myOrgService;

    private static ISysDepartService sysDepartService;

    private static ISysUserService sysUserService;

    private static String usernameDefault = "admin";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- SystemOrgPathsInizationJob 系统组织路径初始化任务调度开始 --- ");
        try {
            synchronized (MyOrg.class){
                getServices();
                initPathByMyOrgOrgId(BaseConstant.HEADQUARTERS_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.FGS_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.XMGS_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.QYGS_ID);
                initPathByMyOrgOrgId(BaseConstant.CGGS_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.JT_SCB_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.JTCWGL_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.JT_SJ_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.JR_ORG_ID);
                initPathByMyOrgOrgId(BaseConstant.DEVORG_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }

    private void initPathByMyOrgOrgId(String orgId){
        try{
            MyOrg parentOrg = myOrgService.getOne(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getOrgId, orgId));
            if(null!=parentOrg){
                parentOrg.setPath(orgId);
                initPathMyOrgOrg(parentOrg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initPathMyOrgOrg(MyOrg parentOrg){
        if(null!=parentOrg){
            try{
                updateMyOrgPath(parentOrg);
                List<MyOrg> hqOrgList = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getParentOrgId, parentOrg.getOrgId()));
                if(null!=hqOrgList&&hqOrgList.size()>0){
                    for(MyOrg myOrg : hqOrgList){
                        try{
                            myOrg.setPath(parentOrg.getPath() + "," + myOrg.getOrgId());
                            initPathMyOrgOrg(myOrg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void getServices(){
        myOrgService = SpringContextUtils.getBean(IMyOrgService.class);
        sysDepartService = SpringContextUtils.getBean(ISysDepartService.class);
        sysUserService = SpringContextUtils.getBean(ISysUserService.class);
    }

    private int updateMyOrgPath(MyOrg myOrg){
        int updateMyOrgPathCount =0;
        if(null!=myOrg){
            if(StringUtil.isNotEmpty(myOrg.getOrgId())&&StringUtil.isNotEmpty(myOrg.getPath())){
                MyOrg myOrgU = new MyOrg();
                myOrgU.setOrgId(myOrg.getOrgId());
                myOrgU.setPath(myOrg.getPath());
                myOrgService.updateById(myOrgU);

                List<SysDepart> sysDepartList = sysDepartService.list(Wrappers.<SysDepart>lambdaQuery().eq(SysDepart::getHdmyOrgId, myOrg.getOrgId()));
                if(null!=sysDepartList&&sysDepartList.size()>0){
                    SysDepart sysDepart = sysDepartList.get(0);
                    if(null!=sysDepart&&myOrg.getOrgId().equals(sysDepart.getHdmyOrgId())){
                        SysDepart sysDepartU = new SysDepart();
                        sysDepartU.setId(sysDepart.getId());
                        sysDepartU.setPath(myOrg.getPath());
                        sysDepartService.updateById(sysDepartU);
                    }
                }
            }
        }
        return updateMyOrgPathCount;
    }

}
