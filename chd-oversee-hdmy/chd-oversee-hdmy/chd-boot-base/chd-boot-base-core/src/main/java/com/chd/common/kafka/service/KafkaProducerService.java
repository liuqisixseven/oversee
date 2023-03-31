package com.chd.common.kafka.service;

import com.alibaba.fastjson.JSON;
import com.chd.common.kafka.entity.KafkaHdmyData;
import com.google.common.collect.Maps;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaProducerService {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic,KafkaHdmyData kafkaHdmyData) {
        kafkaTemplate.send(topic, JSON.toJSONString(kafkaHdmyData));
    }

    public void sendMessage1(String topic,KafkaHdmyData kafkaHdmyData) {
        kafkaTemplate.send(topic, kafkaHdmyData);
    }

    public void sendMessage2(String topic,Map<String,Object> map) {
        kafkaTemplate.send(topic, map);
    }

    public void sendMessage(String topic,String message) {
        KafkaHdmyData kafkaHdmyData = new KafkaHdmyData();
        kafkaHdmyData.setDeptid("1");
        kafkaHdmyData.setPid("1");
        Map<String, Object> map = Maps.newHashMap();
        map.put("message",message);
        kafkaHdmyData.setData(map);
        kafkaTemplate.send(topic, JSON.toJSONString(kafkaHdmyData));
    }

}
