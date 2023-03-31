package com.chd.modules.oversee.issue.controller;

import com.alibaba.fastjson.JSONObject;
import com.chd.common.api.vo.Result;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.Base64ImageUtil;
import com.chd.modules.oversee.issue.entity.IssueAnalysisQueryVo;
import com.chd.modules.oversee.issue.entity.OverseeUserSignature;
import com.chd.modules.oversee.issue.service.IOverseeUserSignatureService;
import com.google.common.collect.Maps;
import liquibase.pro.packaged.P;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oversee/signature")
@Slf4j
public class OverseeUserSignatureController {

    @Autowired
    private IOverseeUserSignatureService overseeUserSignatureService;

    @PostMapping("/save")
    public Result save(@RequestBody OverseeUserSignature signature){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        signature.setUpdateBy(sysUser.getRealname());
        signature.setUserId(sysUser.getId());
        signature.setRotate(-1);
        return scanSave(signature);
//        Integer result =null;
//        if(StringUtils.isNotBlank( signature.getSignatureData())) {
//            OverseeUserSignature dbSignature= overseeUserSignatureService.findByUserId(sysUser.getId());
//            if (dbSignature == null) {
//                result = overseeUserSignatureService.insert(signature);
//            } else {
//                signature.setId(dbSignature.getId());
//                overseeUserSignatureService.updateById(signature);
//            }
//        }
//       return Result.OK(result);
    }

    @PostMapping("/scanSave")
    public Result scanSave(@RequestBody OverseeUserSignature signature){
        try {
            Assert.isTrue((null!=signature&&StringUtils.isNotBlank(signature.getUserId())),"请传递签名参数");
            Assert.isTrue((StringUtils.isNotBlank(signature.getSignatureData())),"请传递签名数据");
            signature.setUpdateBy(signature.getUserId());
            if(null!=signature.getRotate()&&signature.getRotate()==1){
                try{
//                    signature.setSignatureData("data:image/png;base64,"+Base64ImageUtil.rotateImage(signature.getSignatureData().replace("data:image/png;base64,",""),90));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Integer result =null;
            if(StringUtils.isNotBlank( signature.getSignatureData())) {
                OverseeUserSignature dbSignature= overseeUserSignatureService.findByUserId(signature.getUserId());
                if (dbSignature == null) {
                    result = overseeUserSignatureService.insert(signature);
                } else {
                    signature.setId(dbSignature.getId());
                    overseeUserSignatureService.updateById(signature);
                }
            }
            return Result.OK(result);
        }catch (IllegalArgumentException e){
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

    @GetMapping("/self")
    public Result getSignature(){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        OverseeUserSignature dbSignature= overseeUserSignatureService.findByUserId(sysUser.getId());
        return Result.OK(dbSignature);
    }

    @RequestMapping(value = "/toSign.do")
    public ModelAndView toSign(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("oversee/sign");
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if(null!=sysUser){
                view.addObject("userId",sysUser.getId());
            }
        } catch (Exception e) {

        }
        return view;
    }

}
