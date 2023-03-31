package com.chd.common.util.sensitive.strategy;

import com.chd.common.util.sensitive.SensitiveType;

/**
 * 新建策略，实现本接口即可
 */
public interface SensitiveStrategy {

    SensitiveType getSensitiveType();

    String mask(String s);

    public default String mask(String s, int from, int len) {
	  try {
		char[] cs = s.toCharArray();
		for (int i = from; i < from + len && i < s.length(); i++) {
		    cs[i] = '*';
		}
		return new String(cs);
	  } catch (Exception ee) {

	  }
	  return "";
    }
}
