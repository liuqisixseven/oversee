package com.chd.modules.oversee.hdmy.service;

import com.chd.modules.oversee.hdmy.entity.LoginVo;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: my_user
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
public interface IMyUserService extends IService<MyUser> {

    String login(LoginVo loginVo,HttpServletRequest request);

    List<Map<String,String>> listUserByOrgId(Map<String,Object> paramsMap);


    int addOrUpdate(MyUser myUser);

}
