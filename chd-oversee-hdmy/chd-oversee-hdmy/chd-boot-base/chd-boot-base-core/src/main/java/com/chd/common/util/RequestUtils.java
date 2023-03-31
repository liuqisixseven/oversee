package com.chd.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class RequestUtils {

    public static Map<String, String> conversionMap(Map<String, String[]> parameterMap){
        Map<String, String> map = new HashMap<String, String>();
        try{
            if(null!=parameterMap){
                for(Map.Entry<String, String[]> entry : parameterMap.entrySet()){
                    if(null!=entry.getValue()&&entry.getValue().length>0){
                        String vals = "";
                        for(String val : entry.getValue()){
                            vals += val + ",";
                        }
                        if(!vals.equals("")){
                            vals = vals.substring(0, vals.length()-1);
                        }
                        map.put(entry.getKey(),vals);
                    }else{
                        map.put(entry.getKey(),null);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, Object> conversionObjectMap(Map<String, String[]> parameterMap){
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            if(null!=parameterMap){
                for(Map.Entry<String, String[]> entry : parameterMap.entrySet()){
                    if(null!=entry.getValue()&&entry.getValue().length>0){
                        String vals = "";
                        for(String val : entry.getValue()){
                            vals += val + ",";
                        }
                        if(!vals.equals("")){
                            vals = vals.substring(0, vals.length()-1);
                        }
                        map.put(entry.getKey(),vals);
                    }else{
                        map.put(entry.getKey(),null);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Map getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value += valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    public static String getDomain(HttpServletRequest req){
        StringBuffer url = req.getRequestURL();
        String domain = url.delete(url.length() - req.getRequestURI().length(), url.length()).append("/").toString();

        return domain;
    }

    public static void login(String username,String psw){
        UsernamePasswordToken token = new UsernamePasswordToken(username, psw);
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(token);
    }



}
