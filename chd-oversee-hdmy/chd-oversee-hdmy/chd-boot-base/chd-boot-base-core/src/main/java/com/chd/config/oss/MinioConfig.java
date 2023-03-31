package com.chd.config.oss;

import lombok.extern.slf4j.Slf4j;
import com.chd.common.constant.CommonConstant;
import com.chd.common.constant.SymbolConstant;
import com.chd.common.util.MinioUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio文件上传配置文件 
 */
@Slf4j
@Configuration
public class MinioConfig {
    @Value(value = "${chd.minio.minio_url}")
    private String minioUrl;
    @Value(value = "${chd.minio.minio_name}")
    private String minioName;
    @Value(value = "${chd.minio.minio_pass}")
    private String minioPass;
    @Value(value = "${chd.minio.bucketName}")
    private String bucketName;

    @Bean
    public void initMinio(){
        if(!minioUrl.startsWith(CommonConstant.STR_HTTP)){
            minioUrl = "http://" + minioUrl;
        }
        if(!minioUrl.endsWith(SymbolConstant.SINGLE_SLASH)){
            minioUrl = minioUrl.concat(SymbolConstant.SINGLE_SLASH);
        }
        MinioUtil.setMinioUrl(minioUrl);
        MinioUtil.setMinioName(minioName);
        MinioUtil.setMinioPass(minioPass);
        MinioUtil.setBucketName(bucketName);
    }

}
