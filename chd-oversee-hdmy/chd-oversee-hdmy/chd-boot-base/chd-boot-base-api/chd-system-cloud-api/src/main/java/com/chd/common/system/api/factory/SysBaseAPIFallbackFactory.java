package com.chd.common.system.api.factory;

import org.springframework.cloud.openfeign.FallbackFactory;
import com.chd.common.system.api.ISysBaseAPI;
import com.chd.common.system.api.fallback.SysBaseAPIFallback;
import org.springframework.stereotype.Component;

/**
 * @Description: SysBaseAPIFallbackFactory
 * 
 */
@Component
public class SysBaseAPIFallbackFactory implements FallbackFactory<ISysBaseAPI> {

    @Override
    public ISysBaseAPI create(Throwable throwable) {
        SysBaseAPIFallback fallback = new SysBaseAPIFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}