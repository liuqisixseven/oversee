package com.chd.modules.oversee.hdmy.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author helen
 * @since 2020/4/30
 */
@Data
public class LoginVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String user_id;
    private String password;
}
