package com.chd.common.util.sensitive.strategy;

import com.chd.common.util.sensitive.SensitiveType;
import org.springframework.stereotype.Component;

@Component
public class MobileSensitiveStrategy implements SensitiveStrategy {
    @Override
    public SensitiveType getSensitiveType() {
	  return SensitiveType.Mobile;
    }

    @Override
    public String mask(String s) {
	  return mask(s,3,4);
    }
}
