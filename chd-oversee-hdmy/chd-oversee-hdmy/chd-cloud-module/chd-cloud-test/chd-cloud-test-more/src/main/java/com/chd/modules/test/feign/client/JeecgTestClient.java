package com.chd.modules.test.feign.client;

import com.chd.common.api.vo.Result;

import com.chd.common.constant.ServiceNameConstants;
import com.chd.config.FeignConfig;
import com.chd.modules.test.constant.CloudConstant;
import com.chd.modules.test.feign.factory.JeecgTestClientFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 常规feign接口定义
 * @author: zyf
 * @date: 2022/04/21
 */
@FeignClient(value = ServiceNameConstants.SERVICE_DEMO, configuration = FeignConfig.class,fallbackFactory = JeecgTestClientFactory.class)
@Component
public interface JeecgTestClient {

    /**
     * feign测试方法
     * @param name
     * @return
     */
    @GetMapping(value = "/test/getMessage")
    String getMessage(@RequestParam(value = "name",required = false) String name);
}
