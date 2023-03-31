package com.chd.common.modules.redis.listener;

import com.chd.common.base.BaseMap;

/**
 * 自定义Redis消息监听
 */
public interface ChdRedisListener {
    /**
     * 接受消息
     *
     * @param message
     */
    void onMessage(BaseMap message);

}
