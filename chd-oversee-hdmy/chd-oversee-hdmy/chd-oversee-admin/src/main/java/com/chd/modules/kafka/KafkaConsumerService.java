package com.chd.modules.kafka;

import com.alibaba.fastjson.JSONObject;
import com.chd.common.hdmy.HdmyUtils;
import com.chd.common.kafka.entity.KafkaHdmyData;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.system.util.HdmyHandleUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class KafkaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    IMyOrgService myOrgService;

    @Autowired
    IMyUserService myUserService;

    //不指定group，默认取yml里配置的
    @KafkaListener(topics = {"test"})
    public void onMessage1(ConsumerRecord<?, ?> consumerRecord) {
        Optional<?> optional = Optional.ofNullable(consumerRecord.value());
        if (optional.isPresent()) {
            Object msg = optional.get();
            logger.info("message:{}", msg);
        }
    }

    @KafkaListener(groupId = "XSXC_hdmyou_user",topics = {"hdmyou_user"})
    public void onMessageUser(ConsumerRecord<?, ?> consumerRecord) {
        Optional<?> optional = Optional.ofNullable(consumerRecord.value());
        if (optional.isPresent()) {
            Object msg = optional.get();
            logger.warn("XSXC_hdmyou_user message:{}", msg);
            if(msg instanceof String){

            }
        }
    }

    @KafkaListener(groupId = "XSXC_hdmyou_orguser",topics = {"hdmyou_orguser"})
    public void onMessageOrgUser(ConsumerRecord<?, ?> consumerRecord) {
        Optional<?> optional = Optional.ofNullable(consumerRecord.value());
        if (optional.isPresent()) {
            Object msg = optional.get();
            logger.warn("hdmyou_orguser message:{}", msg);
            if(msg instanceof String){
                KafkaHdmyData kafkaHdmyData = JSONObject.parseObject(msg.toString(), KafkaHdmyData.class);
                if(null!=kafkaHdmyData){
                    String type = kafkaHdmyData.getType();
                    if(StringUtil.isNotEmpty(type)){
                        if(type.equals(KafkaHdmyData.USER_TYPE)){
                            Map<String, Object> map = HdmyUtils.mapToHdmyUserMap(kafkaHdmyData.getData());
                            if(null!=map){
                                MyUser myUser = JSONObject.parseObject(JSONObject.toJSONString(map), MyUser.class);
                                if(null!=myUser){
                                    int addOrUpdateCount = myUserService.addOrUpdate(myUser);
                                    System.out.println(KafkaHdmyData.USER_TYPE + "oversee_hdmyou_orguser addOrUpdateCount : " + addOrUpdateCount);
                                    if(addOrUpdateCount>0){
                                        HdmyHandleUtils.sysUserHandleByMyUser(myUser);
                                    }
                                }
                            }
                        }else if(type.equals(KafkaHdmyData.ORG_TYPE)){
                            Map<String, Object> map = HdmyUtils.mapToHdmyOrgMap(kafkaHdmyData.getData());
                            if(null!=map){
                                MyOrg myOrg = JSONObject.parseObject(JSONObject.toJSONString(map), MyOrg.class);
                                if(null!=myOrg){
                                    int addOrUpdateCount = myOrgService.addOrUpdate(myOrg);
                                    System.out.println(KafkaHdmyData.ORG_TYPE + "oversee_hdmyou_orguser addOrUpdateCount : " + addOrUpdateCount);
                                    if(addOrUpdateCount>0){
                                        HdmyHandleUtils.addOrUpdateSystemDepart(myOrg);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @KafkaListener(groupId = "XSXC_hdmyou_org",topics = {"hdmyou_org"})
    public void onMessageOrg(ConsumerRecord<?, ?> consumerRecord) {
        Optional<?> optional = Optional.ofNullable(consumerRecord.value());
        if (optional.isPresent()) {
            Object msg = optional.get();
            logger.warn("hdmyou_org message:{}", msg);
            if(msg instanceof String){
                KafkaHdmyData kafkaHdmyData = JSONObject.parseObject(msg.toString(), KafkaHdmyData.class);
                if(null!=kafkaHdmyData){
                    Map<String, Object> map = HdmyUtils.mapToHdmyOrgMap(kafkaHdmyData.getData());
                    if(null!=map){
                        MyOrg myOrg = JSONObject.parseObject(JSONObject.toJSONString(map), MyOrg.class);
                        if(null!=myOrg){
                            int addOrUpdateCount = myOrgService.addOrUpdate(myOrg);
                            System.out.println("oversee_hdmyou_org addOrUpdateCount : " + addOrUpdateCount);
                            if(addOrUpdateCount>0){
                                HdmyHandleUtils.addOrUpdateSystemDepart(myOrg);
                            }
                        }
                    }
                }
            }
        }
    }



    @KafkaListener(groupId = "XSXC_xsxc_issues",topics = {"xsxc_issues"})
    public void onMessageIssues(ConsumerRecord<?, ?> consumerRecord) {
        Optional<?> optional = Optional.ofNullable(consumerRecord.value());
        if (optional.isPresent()) {
            Object msg = optional.get();
            logger.warn("XSXC_xsxc_issues message:{}", msg);
            if(msg instanceof String){

            }
        }
    }




}
