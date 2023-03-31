package com.chd.modules.oversee.issue.job;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.CommonAPI;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.kafka.entity.KafkaHdmyData;
import com.chd.common.kafka.service.KafkaProducerService;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueDetailVo;
import com.chd.modules.oversee.issue.entity.OverseeIssueQueryVo;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.IOverseeIssueTodoService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import liquibase.pro.packaged.E;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.compress.utils.Lists;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class KafkaIssuesSendRecordingJob implements Job {

    private static IOverseeIssueService overseeIssueService;

    private static KafkaProducerService kafkaProducerService;

    private static  CommonAPI commonAPI;

    private static String kafkaFrom = "XSXC";

    private static String kafkaSendIssuesTopic = "xsxc_issues";

    private static int pageSize = 20;





    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    private void isCreateTopics(){

        try{
            Properties props = new Properties();
            props.put("bootstrap.servers", "10.101.100.96:9092,10.101.100.97:9092,10.101.100.98:9092");
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            AdminClient adminClient = KafkaAdminClient.create(props);//创建Topic

            // 创建topic前，可以先检查topic是否存在，如果已经存在，则不用再创建了
            Set<String> topics = adminClient.listTopics().names().get();
            if (topics.contains(kafkaSendIssuesTopic)) {
                log.info(kafkaSendIssuesTopic + "已存在");
//                System.out.println();
                return;
            }

            log.info(kafkaSendIssuesTopic + "创建中");

            adminClient.createTopics(Lists.newArrayList(new NewTopic(kafkaSendIssuesTopic,1,(short)1)));//一个分区
            adminClient.close();//关闭

            log.info(kafkaSendIssuesTopic + "创建完成");
        }catch (Exception e){
            e.printStackTrace();
            log.info(kafkaSendIssuesTopic + "创建异常");
        }



    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" --- KafkaIssuesSendRecordingJob 任务调度开始 --- ");
        getServices();
//        isCreateTopics();
        Integer isAll = 0;
        if(StringUtil.isNotEmpty(parameter)){
            try{
                JSONObject parameterJsonObject = JSONObject.parseObject(parameter);
                if(null!=parameterJsonObject){
                    if(null!=parameterJsonObject.getInteger("isAll")){
                        isAll = parameterJsonObject.getInteger("isAll");
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        isAll = isAll == null ? 0 : isAll;
        if(isAll.intValue()!=1){
            sendNewOverseeIssuePage();
        }else{
            sendAllOverseeIssuePage();
        }

    }

    private void sendNewOverseeIssuePage(){


//        LambdaQueryWrapper<OverseeIssue> overseeIssueWrapper = Wrappers.<OverseeIssue>lambdaQuery().eq(OverseeIssue::getSubmitState, OverseeConstants.SubmitState.Complete);
        Date startDate = DateUtils.str2Date((DateUtils.formatDate(new Date(), DateUtils.FORMAT_DATE) + " 00:00:01"), DateUtils.datetimeFormat.get());
        Date endDate = DateUtils.str2Date((DateUtils.formatDate(new Date(), DateUtils.FORMAT_DATE) + " 11:59:59"), DateUtils.datetimeFormat.get());
//        overseeIssueWrapper.between(OverseeIssue::getUpdateTime, startDate, endDate);


        OverseeIssueQueryVo query = new OverseeIssueQueryVo();
        query.setPageNo(Long.parseLong(String.valueOf(1)));
        query.setPageSize(Long.parseLong(String.valueOf(1000000)));
        query.setUpdateTimeGt(startDate);
        query.setUpdateTimeLt(endDate);
        IPage<OverseeIssueDetailVo> pageList = overseeIssueService.queryIssueDetailPage(query);
        if(null!=pageList&&null!=pageList.getRecords()&&pageList.getRecords().size()>0){
            sendOverseeIssueVoList(pageList.getRecords());
        }

    }

    private void sendAllOverseeIssuePage(){
        long count = overseeIssueService.count(Wrappers.<OverseeIssue>lambdaQuery().eq(OverseeIssue::getDataType,1));
        int totalPageCount = Long.valueOf(count).intValue() / pageSize + 1;
        for (int i=1; i<=totalPageCount;i++){
//            Page<OverseeIssue> page = new Page<OverseeIssue>(i, pageSize);
//            Page<OverseeIssue> pageList = overseeIssueService.page(page);

            OverseeIssueQueryVo query = new OverseeIssueQueryVo();
            query.setPageNo(Long.parseLong(String.valueOf(i)));
            query.setPageSize(Long.parseLong(String.valueOf(pageSize)));
            IPage<OverseeIssueDetailVo> pageList = overseeIssueService.queryIssueDetailPage(query);
            if(null!=pageList){
                List<OverseeIssueDetailVo> overseeIssueList = pageList.getRecords();
                overseeIssueService.getExamineAndVerifyData(overseeIssueList,false);
                sendOverseeIssueVoList(overseeIssueList);
            }
        }
    }

    private int testCount = 0;

    private void sendOverseeIssueVoList(List<OverseeIssueDetailVo> overseeIssueList){
        if(null!=overseeIssueList&&overseeIssueList.size()>0){
            for(OverseeIssueDetailVo overseeIssue : overseeIssueList){
                try{
                    if(null!=overseeIssue.getId()&&overseeIssue.getId().longValue()>0){

                        overseeIssue.setSourceSrc(commonAPI.translateDict(BaseConstant.ISSUE_SOURCE_DICT_KEY, overseeIssue.getSource() + ""));
                        overseeIssue.setSubmitStateSrc(commonAPI.translateDict(BaseConstant.ISSUE_SUBMIT_STATE_DICT_KEY, overseeIssue.getSubmitStateSrc() + ""));
                        overseeIssue.setSeveritySrc(commonAPI.translateDict(BaseConstant.ISSUE_SEVERITY_DICT_KEY, overseeIssue.getSubmitStateSrc() + ""));
                        overseeIssue.setCompletedTimeoutSrc(commonAPI.translateDict(BaseConstant.COMPLETED_TIMEOUT_DICT_KEY, overseeIssue.getSubmitStateSrc() + ""));


                        KafkaHdmyData kafkaHdmyData = new KafkaHdmyData();
                        kafkaHdmyData.setType(kafkaSendIssuesTopic.toUpperCase());
                        kafkaHdmyData.setFrom(kafkaFrom);
                        kafkaHdmyData.setTimestamp(DateUtils.formatDate(new Date(), DateUtils.FORMAT_DATETIME));
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("overseeIssue",overseeIssue);
                        kafkaHdmyData.setData(map);
                        log.info(" 准备发送 --- KafkaIssuesSendRecordingJob 准备发送 overseeIssueId : " + overseeIssue.getId());
//                        if(testCount<10){
//                            if(testCount<5){
//                                kafkaProducerService.sendMessage("aaaa2022","测试发送消息-------------KafkaIssuesSendRecordingJobTest");
//                                log.info(" test --- KafkaIssuesSendRecordingJobTest 已发送 overseeIssueId : " + overseeIssue.getId());
//                            }else{
//                                kafkaProducerService.sendMessage("test",kafkaHdmyData);
//                                log.info(" test --- KafkaIssuesSendRecordingJobTest 已发送 overseeIssueId : " + overseeIssue.getId());
//                            }
//                            testCount++;
//                        }
                        kafkaProducerService.sendMessage(kafkaSendIssuesTopic,kafkaHdmyData);
                        log.info(" --- KafkaIssuesSendRecordingJob 已发送 overseeIssueId : " + overseeIssue.getId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }



    private void sendOverseeIssueList(List<OverseeIssue> overseeIssueList){
        if(null!=overseeIssueList&&overseeIssueList.size()>0){
            for(OverseeIssue overseeIssue : overseeIssueList){
                try{
                    if(null!=overseeIssue.getId()&&overseeIssue.getId().longValue()>0){
                        KafkaHdmyData kafkaHdmyData = new KafkaHdmyData();
                        kafkaHdmyData.setType(kafkaSendIssuesTopic.toUpperCase());
                        kafkaHdmyData.setFrom(kafkaFrom);
                        kafkaHdmyData.setTimestamp(DateUtils.formatDate(new Date(), DateUtils.FORMAT_DATETIME));
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("overseeIssue",overseeIssue);
                        kafkaHdmyData.setData(map);
                        kafkaProducerService.sendMessage(kafkaSendIssuesTopic,kafkaHdmyData);
                        log.info(" --- KafkaIssuesSendRecordingJob 已发送 overseeIssueId : " + overseeIssue.getId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    private static void getServices(){
        if(null==overseeIssueService){
            overseeIssueService = SpringContextUtils.getBean(IOverseeIssueService.class);
        }

        if(null==kafkaProducerService){
            kafkaProducerService = SpringContextUtils.getBean(KafkaProducerService.class);
        }

        if(null==commonAPI){
            commonAPI = SpringContextUtils.getBean(CommonAPI.class);
        }
    }


}
