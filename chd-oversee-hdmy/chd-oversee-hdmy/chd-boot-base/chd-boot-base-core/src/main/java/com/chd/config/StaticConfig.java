package com.chd.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 设置静态参数初始化
 * 
 */
@Component
@Data
@ConfigurationProperties(prefix = "chd.oss")
public class StaticConfig {

    // @Value("${chd.oss.accessKey}")
    private String accessKeyId;

    // @Value("${chd.oss.secretKey}")
    private String accessKeySecret;

    // @Value(value = "${spring.mail.username}")
    private String emailFrom;

//    /**
//     * 签名密钥串
//     */
//    @Value(value = "${chd.signatureSecret}")
//    private String signatureSecret;


    /*@Bean
    public void initStatic() {
       DySmsHelper.setAccessKeyId(accessKeyId);
       DySmsHelper.setAccessKeySecret(accessKeySecret);
       EmailSendMsgHandle.setEmailFrom(emailFrom);
    }*/

}
