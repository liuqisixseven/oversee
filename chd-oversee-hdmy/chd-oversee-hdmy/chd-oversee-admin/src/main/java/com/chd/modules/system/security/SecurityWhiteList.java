package com.chd.modules.system.security;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统安全白名单
 */
public class SecurityWhiteList {

    /**
     * 重复确认可用白名称
     */
    private static Map<String, String> MAP_DUPLICATE_CHECK = new HashMap<>();

    static {
        MAP_DUPLICATE_CHECK.put("sys_user", "email,username");
        MAP_DUPLICATE_CHECK.put("sys_sms_template", "template_code");
        MAP_DUPLICATE_CHECK.put("sys_dict", "dict_code");
        MAP_DUPLICATE_CHECK.put("sys_permission", "perms");
        MAP_DUPLICATE_CHECK.put("sys_role", "role_code");
        MAP_DUPLICATE_CHECK.put("sys_depart_role", "role_code");
        MAP_DUPLICATE_CHECK.put("sys_position", "code");
    }

    public static boolean canDuplicateCheck(String table,String column){
        String key=table.toLowerCase();
         if(StringUtils.isNotBlank(column) && MAP_DUPLICATE_CHECK.containsKey(key)){
             return ArrayUtils.contains(MAP_DUPLICATE_CHECK.get(key).split(","),column);
         }
         return false;
    }
}
