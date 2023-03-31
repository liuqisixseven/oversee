package com.chd.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.chd.common.api.vo.Result;
import com.chd.common.constant.CacheConstant;
import com.chd.common.constant.CommonConstant;
import com.chd.common.system.util.JwtUtil;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.RedisUtil;
import com.chd.common.util.oConvertUtils;
import com.chd.modules.base.service.BaseCommonService;
import com.chd.modules.system.service.ISysUserService;
import com.chd.modules.system.service.impl.SysBaseApiImpl;
import com.chd.modules.system.vo.SysUserOnlineVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 在线用户
 * @Author: chenli
 * @Date: 2020-06-07
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/online")
@Slf4j
public class SysUserOnlineController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    public ISysUserService userService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;

    @Resource
    private BaseCommonService baseCommonService;

    private String tokenMd5(String token,String myselfToken,String username){
        return Md5Util.md5Encode(token+myselfToken+username,"utf-8");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<Page<SysUserOnlineVO>> list(@RequestParam(name="username", required=false) String username,
                                              @RequestParam(name="pageNo", defaultValue="1") Integer pageNo, @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest request) {
        LoginUser myself = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String myselfToken=request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
        List<SysUserOnlineVO> onlineList = new ArrayList<SysUserOnlineVO>();
        for (String key : keys) {
//            String token = (String)redisUtil.get(key);
            String token = key.substring(CommonConstant.PREFIX_USER_TOKEN.length());
            if (StringUtils.isNotEmpty(token)) {
                SysUserOnlineVO online = new SysUserOnlineVO();
//                online.setToken(token);
                //TODO 改成一次性查询
                LoginUser loginUser = sysBaseApi.getUserByName(JwtUtil.getUsername(token));
                if (loginUser != null) {
                    //update-begin---author:wangshuai ---date:20220104  for：[JTC-382]在线用户查询无效------------
                    //验证用户名是否与传过来的用户名相同
                    boolean isMatchUsername=true;
                    //判断用户名是否为空，并且当前循环的用户不包含传过来的用户名，那么就设成false
                    if(oConvertUtils.isNotEmpty(username) && !loginUser.getUsername().contains(username)){
                        isMatchUsername = false;
                    }
                    if(token.equals(myselfToken)){
                        online.setMyself(true);
                    }
                    if(isMatchUsername){
                        online.setAvatar(loginUser.getAvatar());
                        online.setRealname(loginUser.getRealname());
                        online.setUsername(loginUser.getUsername());
                        online.setToken(tokenMd5(token,myselfToken,loginUser.getUsername()));
//                        BeanUtils.copyProperties(loginUser, online);
                        onlineList.add(online);
                    }
                    //update-end---author:wangshuai ---date:20220104  for：[JTC-382]在线用户查询无效------------
                }
            }
        }
        Collections.reverse(onlineList);

        Page<SysUserOnlineVO> page = new Page<SysUserOnlineVO>(pageNo, pageSize);
        int count = onlineList.size();
        List<SysUserOnlineVO> pages = new ArrayList<>();
        // 计算当前页第一条数据的下标
        int currId = pageNo > 1 ? (pageNo - 1) * pageSize : 0;
        for (int i = 0; i < pageSize && i < count - currId; i++) {
            pages.add(onlineList.get(currId + i));
        }
        page.setSize(pageSize);
        page.setCurrent(pageNo);
        page.setTotal(count);
        // 计算分页总页数
        page.setPages(count % 10 == 0 ? count / 10 : count / 10 + 1);
        page.setRecords(pages);

        Result<Page<SysUserOnlineVO>> result = new Result<Page<SysUserOnlineVO>>();
        result.setSuccess(true);
        result.setResult(page);
        return result;
    }

    /**
     * 强退用户
     */
    @RequestMapping(value = "/forceLogout",method = RequestMethod.POST)
    public Result<Object> forceLogout(@RequestBody SysUserOnlineVO online,HttpServletRequest request) {
        //用户退出逻辑
        if(oConvertUtils.isEmpty(online.getToken())) {
            return Result.error("退出登录失败！");
        }
        String myselfToken=request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
        List<SysUserOnlineVO> onlineList = new ArrayList<SysUserOnlineVO>();
        for (String key : keys) {
//            String token = (String) redisUtil.get(key);
            String token = key.substring(CommonConstant.PREFIX_USER_TOKEN.length());
            if (StringUtils.isNotEmpty(token)) {
                String forceToken=tokenMd5(token,myselfToken,online.getUsername());
                if(forceToken.equals(online.getToken())){
                    LoginUser sysUser = sysBaseApi.getUserByName(online.getUsername());
                    if(sysUser!=null) {
                        baseCommonService.addLog("强制: "+sysUser.getRealname()+"退出成功！", CommonConstant.LOG_TYPE_1, null,sysUser);
                        log.info(" 强制  "+sysUser.getRealname()+"退出成功！ ");
                        //清空用户登录Token缓存
                        redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
                        //清空用户登录Shiro权限缓存
                        redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
                        //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
                        redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
                        //调用shiro的logout
                        SecurityUtils.getSubject().logout();
                        return Result.ok("退出登录成功！");
                    }else {
                        return Result.error("Token无效!");
                    }
                }
            }
        }
        return Result.error("Token无效!");

    }
}
