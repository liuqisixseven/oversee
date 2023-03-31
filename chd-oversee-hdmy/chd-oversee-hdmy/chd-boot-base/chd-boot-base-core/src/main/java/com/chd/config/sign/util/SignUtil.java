package com.chd.config.sign.util;

import com.alibaba.fastjson.JSONObject;
import com.chd.config.ChdBaseConfig;
import lombok.extern.slf4j.Slf4j;
import com.chd.common.constant.SymbolConstant;
import com.chd.common.exception.JeecgBootException;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.oConvertUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.SortedMap;

/**
 * 签名工具类
 * 
 * @author jeecg
 * @date 20210621
 */
@Slf4j
public class SignUtil {
    public static final String X_PATH_VARIABLE = "x-path-variable";

    /**
     * @param params
     *            所有的请求参数都会在这里进行排序加密
     * @return 验证签名结果
     */
    public static boolean verifySign(SortedMap<String, String> params,String headerSign) {
        if (params == null || StringUtils.isEmpty(headerSign)) {
            return false;
        }
        // 把参数加密
        String paramsSign = getParamsSign(params);
        log.info("Param Sign : {}", paramsSign);
        return !StringUtils.isEmpty(paramsSign) && headerSign.equals(paramsSign);
    }

    /**
     * @param params
     *            所有的请求参数都会在这里进行排序加密
     * @return 得到签名
     */
    public static String getParamsSign(SortedMap<String, String> params) {
        //去掉 Url 里的时间戳
        params.remove("_t");
        String paramsJsonStr = JSONObject.toJSONString(params);
        log.info("Param paramsJsonStr : {}", paramsJsonStr);
        //设置签名秘钥
        ChdBaseConfig chdBaseConfig = SpringContextUtils.getBean(ChdBaseConfig.class);
        String signatureSecret = chdBaseConfig.getSignatureSecret();
        String curlyBracket = SymbolConstant.DOLLAR + SymbolConstant.LEFT_CURLY_BRACKET;
        if(oConvertUtils.isEmpty(signatureSecret) || signatureSecret.contains(curlyBracket)){
            throw new JeecgBootException("签名密钥 ${chd.signatureSecret} 缺少配置 ！！");
        }
        return DigestUtils.md5DigestAsHex((paramsJsonStr + signatureSecret).getBytes()).toUpperCase();
    }
}