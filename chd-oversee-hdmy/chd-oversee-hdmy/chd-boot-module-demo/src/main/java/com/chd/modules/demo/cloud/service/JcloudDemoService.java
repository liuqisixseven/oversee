package com.chd.modules.demo.cloud.service;

import com.chd.common.api.vo.Result;

/**
 * @Description: JcloudDemoService接口
 * 
 */
public interface JcloudDemoService {

    /**
     * 获取信息（测试）
     * @param name 姓名
     * @return "Hello，" + name
     */
    String getMessage(String name);
}
