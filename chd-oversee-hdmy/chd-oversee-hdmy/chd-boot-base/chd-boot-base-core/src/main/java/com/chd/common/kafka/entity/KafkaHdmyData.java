package com.chd.common.kafka.entity;

import lombok.Data;

import java.util.Map;

@Data
public class KafkaHdmyData {

    public static String USER_TYPE = "HDMYUSER_USER";

    public static String ORG_TYPE = "HDMYUSER_ORG";

    private Object pkey;
    private String deptid;
    private Object pid;
    private Object writedate;
    private Object computerid;
    private String type;
    private String from;
    private String to;
    private String timestamp;
    private long interval;
    private long interval1;
    private Map<String,Object> data;
    private Object doclass;
    private Map<String,Object> others;


    public KafkaHdmyData(){

    }
}
