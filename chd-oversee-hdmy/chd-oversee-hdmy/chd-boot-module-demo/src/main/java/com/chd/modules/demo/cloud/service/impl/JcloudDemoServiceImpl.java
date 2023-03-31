package com.chd.modules.demo.cloud.service.impl;

import com.chd.common.api.vo.Result;
import com.chd.modules.demo.cloud.service.JcloudDemoService;
import org.springframework.stereotype.Service;

/**
 * @Description: JcloudDemoServiceImpl实现类
 * 
 */
@Service
public class JcloudDemoServiceImpl implements JcloudDemoService {
    @Override
    public String getMessage(String name) {
        String resMsg = "Hello，我是jeecg-demo服务节点，收到你的消息：【 "+ name +" 】";
        return resMsg;
    }
}
