package com.chd.common.util.sensitive;


import com.chd.common.util.sensitive.strategy.SensitiveStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class SensitiveStrategyService {

    private static Map<SensitiveType, SensitiveStrategy> sensitiveStrategy = new HashMap<>();

    private static ThreadLocal<Boolean> enableMask = ThreadLocal.withInitial(() -> false);


    public SensitiveStrategyService(List<SensitiveStrategy> sensitivyStrategies){
	for(SensitiveStrategy strategy: sensitivyStrategies){
	    log.info("SensitiveStrategyService|add|"+strategy.getSensitiveType()+"|"+strategy);
	    sensitiveStrategy.put(strategy.getSensitiveType(),strategy);
	}
    }

    public static SensitiveStrategy getSensitiveStrategy(SensitiveType type) {
	  if (sensitiveStrategy.containsKey(type)) {
		return sensitiveStrategy.get(type);
	  }
	  return null;
    }

    public static void setEnableMask() {
	  enableMask.set(true);
    }

    public static void clearEnableMask() {
	  enableMask.remove();
    }

    public static boolean isMaskEnable() {
	  return enableMask.get();
    }

    public static Object mask(JsonSensitive ssonSensitiveType, Object vo) {
	  try {
		//没有标记
		if (null == ssonSensitiveType || ssonSensitiveType.value() == null) {
		    return vo;
		}
		//该用户不需要打码
		if (!isMaskEnable()) {
		    return vo;
		}
		//未知标记
		SensitiveStrategy s = getSensitiveStrategy(ssonSensitiveType.value());
		if (s != null) {
		    if (vo instanceof String) {
			  return s.mask((String) vo);
		    } else {
			  return null;
		    }
		}
	  } catch (Exception ee) {}
	  return null;
    }
}
