package com.chd.modules.oversee.hdmy.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chd.common.api.CommonAPI;
import com.chd.common.system.vo.DictModel;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.SpringContextUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.HttpClientUtil;
import com.chd.modules.oversee.hdmy.entity.LoginVo;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.mapper.MyUserMapper;
import com.chd.modules.oversee.hdmy.result.GuliException;
import com.chd.modules.oversee.hdmy.result.ResultCodeEnum;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.hdmy.util.JwtInfo;
import com.chd.modules.oversee.hdmy.util.JwtUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: my_user
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class MyUserServiceImpl extends ServiceImpl<MyUserMapper, MyUser> implements IMyUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    @Lazy
    protected CommonAPI commonAPI;

    @Autowired
    MyUserMapper myUserMapper;

    @Autowired
    IMyOrgService myOrgService;


    @Override
    public List<Map<String,String>> listUserByOrgId(Map<String,Object> paramsMap) {
        QueryWrapper<MyUser> queryWrapper = new QueryWrapper<>();
        String orgId = (String) paramsMap.get("orgId");

        Integer orgAll = (Integer) paramsMap.get("orgAll");
        Integer isGetOrgChildren = (Integer) paramsMap.get("isGetOrgChildren");
        if(null!=isGetOrgChildren&&isGetOrgChildren.intValue()==1){
            List<MyOrg> myOrgs = myOrgService.allSubbranchesAndSubsidiaries(orgId);
            List<String> orgIdList = new ArrayList<String>();
            orgIdList.add(orgId);
            if(CollectionUtils.isNotEmpty(myOrgs)){
                List<String> orgIds = myOrgs.stream().filter((myOrg) -> StringUtil.isNotEmpty(myOrg.getOrgId())).map((myOrg) -> myOrg.getOrgId()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(orgIds)){
                    orgIdList = orgIds;
                }
            }
            queryWrapper.in("org_id",orgIdList);
        }else{
            queryWrapper.eq("org_id", orgId);
        }

        Map<String, String> typeMap = new HashMap<>();
        commonAPI = SpringContextUtils.getBean(CommonAPI.class);
        List<DictModel> managerTitleList = commonAPI.queryDictItemsByCode(BaseConstant.TITLE_SET_DICT_KEY);
        List<String> rl = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(managerTitleList)) {
            for (DictModel dictModel : managerTitleList) {
                if (null != dictModel && StringUtil.isNotEmpty(dictModel.getValue())) {
                    rl.add(dictModel.getText());
                    typeMap.put(dictModel.getText(), dictModel.getValue());
                }
            }
        }

        if(null==orgAll||orgAll.intValue()!=1) {
            if (!CollectionUtils.isNotEmpty(managerTitleList)) {
                return null;
            }
            queryWrapper.in("titile", rl);
        }

        List<MyUser> myUserList= baseMapper.selectList(queryWrapper);

//        List<Map<String,String>> dataList = new ArrayList<>();  在parallelStream使用forEach会出现数据为null的情况

        List<Map<String,String>> dataList = myUserList.parallelStream().filter(user -> null!=user).map(user -> {
                Map<String,String> dataMap = new HashMap<>();
                dataMap.put("titleType",typeMap.get(user.getTitile()));
                dataMap.put("userId",user.getUserId());
                dataMap.put("userName",user.getUserName());
                return dataMap;
        }).collect(Collectors.toList());
        if(dataList.stream().anyMatch(map->null==map)){
            System.out.println("isNull");
        }
        return dataList;
    }

    @Override
    public String login(LoginVo loginVo, HttpServletRequest request) {
        String url = request.getHeader("url");
        String map = HttpClientUtil.HttpGet(url);
        if (com.aliyuncs.utils.StringUtils.isEmpty(map)){
//            return loginVo;
        }
        JSONObject json = JSONObject.parseObject(map);  //接收到所有传过来的json数据
        Boolean success = json.getBoolean("success");  //直接json转Boolean类型，用Boolean类型的success接收叫做success的数据
        String  data = json.getString("data");     //用String接收json数据中的data数据
        JSONObject dataj = JSONObject.parseObject(data);  //再将data转为单独的json数据
        String user_id = dataj.getString("idcode");  //再次将data转为String数据并接收
        System.out.println(user_id);
        String password = "000000";

        //校验：参数是否合法
        if(StringUtils.isEmpty(user_id)){
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验userid是否存在
        QueryWrapper<MyUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user_id);
        MyUser myUser = baseMapper.selectOne(queryWrapper);
        if(myUser == null){
            throw new GuliException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }


        //校验用户是否被禁用
        if(myUser.getEnable()== 0){
            throw new GuliException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //登录：生成token
        JwtInfo info = new JwtInfo();
        info.setUser_id(myUser.getUserId());
        info.setUser_name(myUser.getUserName());

        String jwtToken = JwtUtils.getJwtToken(info, 1800);

        return jwtToken;
    }


    @Override
    @Transactional
    public int addOrUpdate(MyUser myUser) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=myUser),"请传递myUser数据");
        if(null!=myUser.getUserId()){
            addOrUpdateCount = myUserMapper.updateById(myUser);
        }else {
            Assert.isTrue((StringUtil.isNotEmpty(myUser.getUserName())),"请传递用户名称");
            addOrUpdateCount = myUserMapper.insert(myUser);
        }
        return addOrUpdateCount;
    }


}
