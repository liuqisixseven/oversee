package com.chd.common.util;

import org.springframework.lang.Nullable;

public class StringUtil {

    public static boolean isEmpty(@Nullable String str) {
        if(null!=str&&str.trim().length()>0){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isNotEmpty(@Nullable String str) {
        if(null!=str&&str.trim().length()>0){
            return true;
        }else{
            return false;
        }
    }

    public static String srcIsNullReturnDefaultCharacter(String str){
        if(null!=str&&str.trim().length()>0){
            return str;
        }
        return "";
    }

}
