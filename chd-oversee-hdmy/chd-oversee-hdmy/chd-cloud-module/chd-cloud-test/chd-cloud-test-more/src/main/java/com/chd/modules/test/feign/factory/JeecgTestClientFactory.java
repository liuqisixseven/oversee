package com.chd.modules.test.feign.factory;




import org.springframework.cloud.openfeign.FallbackFactory;
import com.chd.modules.test.feign.client.JeecgTestClient;
import com.chd.modules.test.feign.fallback.JeecgTestFallback;
import org.springframework.stereotype.Component;

/**
 * @author qinfeng
 */
@Component
public class JeecgTestClientFactory implements FallbackFactory<JeecgTestClient> {

    @Override
    public JeecgTestClient create(Throwable throwable) {
        JeecgTestFallback fallback = new JeecgTestFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}
