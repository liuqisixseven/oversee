package com.chd.common.es;

 
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {
    
    
}
