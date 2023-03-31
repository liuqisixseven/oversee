package com.chd.common.modules.redis.receiver;


import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import com.chd.common.base.BaseMap;
import com.chd.common.constant.GlobalConstants;
import com.chd.common.modules.redis.listener.ChdRedisListener;
import com.chd.common.util.SpringContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author zyf
 */
@Component
@Data
public class RedisReceiver {


    /**
     * 接受消息并调用业务逻辑处理器
     *
     * @param params
     */
    public void onMessage(BaseMap params) {
        Object handlerName = params.get(GlobalConstants.HANDLER_NAME);
        ChdRedisListener messageListener = SpringContextHolder.getHandler(handlerName.toString(), ChdRedisListener.class);
        if (ObjectUtil.isNotEmpty(messageListener)) {
            messageListener.onMessage(params);
        }
    }

}
