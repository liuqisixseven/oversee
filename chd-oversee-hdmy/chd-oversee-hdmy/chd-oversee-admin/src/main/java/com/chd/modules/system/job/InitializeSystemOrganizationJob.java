package com.chd.modules.system.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
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
public class InitializeSystemOrganizationJob implements Job {

    private static IMyOrgService myOrgService;

    private static ISysDepartService sysDepartService;

    private static ISysUserService sysUserService;

    private static String usernameDefault = "admin";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步InitializeSystemOrganizationJob任务调度开始 --- ");
        try {
            synchronized (MyOrg.class){
                getServices();
                addSysDepartByMyOrgOrgId(BaseConstant.HEADQUARTERS_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.FGS_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.XMGS_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.QYGS_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.CGGS_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.JT_SCB_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.JTCWGL_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.JT_SJ_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.JR_ORG_ID);
                addSysDepartByMyOrgOrgId(BaseConstant.DEVORG_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //测试发现 每5秒执行一次
        log.info(" --- 执行完毕，时间："+ DateUtils.now()+"---");
    }

    private void addSysDepartByMyOrgOrgId(String orgId){
        try{
            MyOrg parentOrg = myOrgService.getOne(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getOrgId, orgId));
            if(null!=parentOrg){
                parentOrg.setPath(orgId);
                parentOrg.setParentOrgId(null);
                addSysDepartByMyOrgOrg(parentOrg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addSysDepartByMyOrgOrg(MyOrg parentOrg){
        if(null!=parentOrg){
            HdmyHandleUtils.addOrUpdateSystemDepart(parentOrg);
            List<MyOrg> hqOrgList = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getParentOrgId, parentOrg.getOrgId()));
            if(null!=hqOrgList&&hqOrgList.size()>0){
                for(MyOrg myOrg : hqOrgList){
                    try{
                        myOrg.setPath(parentOrg.getPath() + "," + myOrg.getOrgId());
                        addSysDepartByMyOrgOrg(myOrg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void getServices(){
        if(null==myOrgService){
            myOrgService = SpringContextUtils.getBean(IMyOrgService.class);
        }
        if(null==sysDepartService){
            sysDepartService = SpringContextUtils.getBean(ISysDepartService.class);
        }
        if(null==sysUserService){
            sysUserService = SpringContextUtils.getBean(ISysUserService.class);
        }
    }





}
