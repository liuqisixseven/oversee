package com.chd.config.oss;

import com.chd.common.util.oss.OssBootUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 云存储 配置
 */
@Configuration
@ConfigurationProperties(prefix = "chd.oss")
public class OssConfiguration {

    // @Value("${chd.oss.endpoint}")
    private String endpoint;
    // @Value("${chd.oss.accessKey}")
    private String accessKeyId;
    // @Value("${chd.oss.secretKey}")
    private String accessKeySecret;
    // @Value("${chd.oss.bucketName}")
    private String bucketName;
    // @Value("${chd.oss.staticDomain:}")
    private String staticDomain;


    @Bean
    public void initOssBootConfiguration() {
        OssBootUtil.setEndPoint(endpoint);
        OssBootUtil.setAccessKeyId(accessKeyId);
        OssBootUtil.setAccessKeySecret(accessKeySecret);
        OssBootUtil.setBucketName(bucketName);
        OssBootUtil.setStaticDomain(staticDomain);
    }
}