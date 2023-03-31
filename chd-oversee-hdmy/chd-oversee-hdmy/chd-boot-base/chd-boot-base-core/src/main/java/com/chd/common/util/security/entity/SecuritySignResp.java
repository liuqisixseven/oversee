package com.chd.common.util.security.entity;

import lombok.Data;

/**
 * @Description: SecuritySignResp
 * 
 */
@Data
public class SecuritySignResp {
    private String data;
    private String signData;
    private String aesKey;
}
