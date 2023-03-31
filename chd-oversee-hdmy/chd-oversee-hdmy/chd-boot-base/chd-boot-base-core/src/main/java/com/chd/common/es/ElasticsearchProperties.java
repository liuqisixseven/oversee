package com.chd.common.es;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    private String baseUrl;
    private Boolean checkEnabled=false;
}
