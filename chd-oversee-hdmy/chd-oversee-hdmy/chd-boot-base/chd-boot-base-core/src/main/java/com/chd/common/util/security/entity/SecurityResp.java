package com.chd.common.util.security.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @Description: SecurityResp
 * 
 */
@Data
public class SecurityResp {
    private Boolean success;
    private JSONObject data;
}
