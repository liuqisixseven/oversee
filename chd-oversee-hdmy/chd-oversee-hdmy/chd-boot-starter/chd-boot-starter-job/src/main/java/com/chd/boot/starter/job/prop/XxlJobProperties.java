package com.chd.boot.starter.job.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "chd.xxljob")
public class XxlJobProperties {


    private String adminAddresses;


    private String appname;


    private String ip;


    private int port;


    private String accessToken;


    private String logPath;


    private int logRetentionDays;

    /**
     * 是否开启xxljob
     */
    private Boolean enable = true;
}
