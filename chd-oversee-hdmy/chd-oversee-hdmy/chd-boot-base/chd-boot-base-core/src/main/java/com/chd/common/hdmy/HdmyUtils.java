package com.chd.common.hdmy;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class HdmyUtils {

    public static Map<String,Object> mapToHdmyOrgMap(Map<String,Object> org){
        Map<String, Object> hdmyOrgMap = null;
        if (null != org) {
            hdmyOrgMap = Maps.newHashMap();
            hdmyOrgMap.put("jtCode2",org.get("jt_CODE2"));
            hdmyOrgMap.put("managerId",org.get("manager_ID"));
            hdmyOrgMap.put("areaCode",org.get("area_CODE"));
            hdmyOrgMap.put("znCode",org.get("zn_CODE"));
            hdmyOrgMap.put("streetAddress",org.get("street_ADDRESS"));
            hdmyOrgMap.put("postCode",org.get("post_CODE"));
            hdmyOrgMap.put("jjType",org.get("jj_TYPE"));
            hdmyOrgMap.put("hyType",org.get("hy_TYPE"));
            hdmyOrgMap.put("area",org.get("area"));
            hdmyOrgMap.put("employersNumber",org.get("employers_NUMBER"));
            hdmyOrgMap.put("gdValue",org.get("gd_VALUE"));
            hdmyOrgMap.put("anIncome",org.get("an_INCOME"));
            hdmyOrgMap.put("anProfit",org.get("an_PROFIT"));
            hdmyOrgMap.put("mainProducts",org.get("main_PRODUCTS"));
            hdmyOrgMap.put("constructionType",org.get("construction_TYPE"));
            hdmyOrgMap.put("parentOrgId",org.get("business_TYPE"));
            hdmyOrgMap.put("constructionType",org.get("parent_ORG_ID"));
            hdmyOrgMap.put("orgLevel",org.get("org_LEVEL"));
            hdmyOrgMap.put("upperSupervisorId",org.get("upper_SUPERVISOR_ID"));
            hdmyOrgMap.put("telephone",org.get("telephone"));


            hdmyOrgMap.put("retryTimes",org.get("retry_TIMES"));
            hdmyOrgMap.put("gkOrgId",org.get("gk_ORG_ID"));
            hdmyOrgMap.put("orgName",org.get("org_NAME"));
            hdmyOrgMap.put("orgShortName",org.get("org_SHORT_NAME"));
            hdmyOrgMap.put("zzjs",org.get("zzjs"));
            hdmyOrgMap.put("jtCode",org.get("jt_CODE"));
            hdmyOrgMap.put("synTime",org.get("syn_TIME"));
            hdmyOrgMap.put("createTime",org.get("create_TIME"));
            hdmyOrgMap.put("orgId",org.get("org_ID"));
            hdmyOrgMap.put("displayOrder",org.get("display_ORDER"));

            hdmyOrgMap.put("displayOrder",org.get("display_ORDER"));
            hdmyOrgMap.put("operationCode",org.get("operation_CODE"));
            hdmyOrgMap.put("synFlag",org.get("syn_FLAG"));

        }

        return hdmyOrgMap;
    }

    public static Map<String,Object> mapToHdmyUserMap(Map<String,Object> org){
        Map<String, Object> hdmyOrgMap = null;
        if (null != org) {
            hdmyOrgMap = Maps.newHashMap();
            hdmyOrgMap.put("orgId",org.get("org_ID"));
            hdmyOrgMap.put("userId",org.get("user_ID"));
            hdmyOrgMap.put("userName",org.get("user_NAME"));
            hdmyOrgMap.put("password",org.get("password"));
            hdmyOrgMap.put("createTime",org.get("create_TIME"));
            hdmyOrgMap.put("displayOrder",org.get("display_ORDER"));
            hdmyOrgMap.put("operationCode",org.get("operation_CODE"));
            hdmyOrgMap.put("synFlag",org.get("syn_FLAG"));
            hdmyOrgMap.put("promptQuestion",org.get("prompt_QUESTION"));

            hdmyOrgMap.put("promptAnswer",org.get("prompt_ANSWER"));
            hdmyOrgMap.put("sex",org.get("sex"));
            hdmyOrgMap.put("employerNumber",org.get("employer_NUMBER"));
            hdmyOrgMap.put("telNumber",org.get("tel_NUMBER"));
            hdmyOrgMap.put("mobile",org.get("mobile"));
            hdmyOrgMap.put("fasNumber",org.get("fas_NUMBER"));
            hdmyOrgMap.put("mail",org.get("mail"));
            hdmyOrgMap.put("titile",org.get("title"));
            hdmyOrgMap.put("enable",org.get("enable"));
            hdmyOrgMap.put("orgDuty",org.get("org_DUTY"));
            hdmyOrgMap.put("otherOrgId",org.get("other_ORG_ID"));
            hdmyOrgMap.put("retryTime",org.get("retry_TIME"));
            hdmyOrgMap.put("synTime",org.get("syn_TIME"));
        }
        return hdmyOrgMap;
    }



}
