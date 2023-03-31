package com.chd.modules.system.job;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oss.entity.OssFile;
import com.chd.modules.oss.entity.OssFileIssue;
import com.chd.modules.oss.service.IOssFileIssueService;
import com.chd.modules.oss.service.IOssFileService;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyOrgSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
public class InitializeOssFileDataJob implements Job {

    private static IOssFileService ossFileService;

    private static IOssFileIssueService ossFileIssueService;

    private static IMyOrgService myOrgService;



    private static String usernameDefault = "admin";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- 同步InitializeOssFileDataJob任务调度开始 --- ");

        getServices();
        try {
            synchronized (OssFile.class){
                List<OssFile> ossFileList = ossFileService.list(Wrappers.<OssFile>lambdaQuery().isNotNull(OssFile::getIssueIds));
                if(CollectionUtils.isNotEmpty(ossFileList)){
                    for(OssFile ossFile : ossFileList){
                        if(null!=ossFile){
                            boolean isUpdate = false;
                            if(StringUtil.isNotEmpty(ossFile.getIssueIds())){
                                if(ossFileIssueService.count(Wrappers.<OssFileIssue>lambdaQuery().eq(OssFileIssue::getOssFileId,ossFile.getId()))<=0){
                                    isUpdate = true;
                                }
                            }
                            if(StringUtil.isNotEmpty(ossFile.getResponsibleUnitOrgName())){
                                if(StringUtil.isEmpty(ossFile.getResponsibleUnitOrgId())){
                                    List<MyOrg> myOrgList = myOrgService.list(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getOrgShortName, ossFile.getResponsibleUnitOrgName()).or().eq(MyOrg::getOrgName, ossFile.getResponsibleUnitOrgName()));
                                    if(CollectionUtils.isNotEmpty(myOrgList)){
                                        MyOrg myOrg = myOrgList.get(0);
                                        if(null!=myOrg&&StringUtil.isNotEmpty(myOrg.getOrgId())){
                                            ossFile.setResponsibleUnitOrgId(myOrg.getOrgId());
                                            isUpdate = true;
                                        }
                                    }
                                }
                            }

                            if(isUpdate){
                                ossFileService.addOrUpdate(ossFile);
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
        if(null==ossFileService){
            ossFileService = SpringContextUtils.getBean(IOssFileService.class);
        }

        if(null==ossFileIssueService){
            ossFileIssueService = SpringContextUtils.getBean(IOssFileIssueService.class);
        }

        if(null==myOrgService){
            myOrgService = SpringContextUtils.getBean(IMyOrgService.class);
        }
    }





}
