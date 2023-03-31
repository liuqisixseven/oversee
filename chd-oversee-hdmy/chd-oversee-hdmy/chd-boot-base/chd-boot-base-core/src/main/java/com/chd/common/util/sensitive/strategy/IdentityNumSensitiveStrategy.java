package com.chd.common.util.sensitive.strategy;

import com.chd.common.util.sensitive.SensitiveType;
import org.springframework.stereotype.Component;

@Component
public class IdentityNumSensitiveStrategy implements SensitiveStrategy {
    @Override
    public SensitiveType getSensitiveType() {
	  return SensitiveType.IdentityNum;
    }

    @Override
    public String mask(String s) {
	  return mask(s, 6, 10);
    }
}
