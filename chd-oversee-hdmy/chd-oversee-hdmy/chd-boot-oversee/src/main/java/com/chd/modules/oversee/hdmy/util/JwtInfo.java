package com.chd.modules.oversee.hdmy.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author helen
 * @since 2020/4/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtInfo {
    private String user_id;
    private String user_name;
}
