package com.chd.common.modules.redis.client;

import com.chd.common.base.BaseMap;
import com.chd.common.constant.GlobalConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
* @Description: redis客户端
*/
@Configuration
public class ChdRedisClient {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 发送消息
     *
     * @param handlerName
     * @param params
     */
    public void sendMessage(String handlerName, BaseMap params) {
        params.put(GlobalConstants.HANDLER_NAME, handlerName);
        redisTemplate.convertAndSend(GlobalConstants.REDIS_TOPIC_NAME, params);
    }


}
