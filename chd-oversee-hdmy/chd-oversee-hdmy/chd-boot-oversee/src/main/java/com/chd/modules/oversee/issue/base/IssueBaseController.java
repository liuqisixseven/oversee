package com.chd.modules.oversee.issue.base;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.issue.entity.OverseeIssueQueryVo;
import com.chd.modules.workflow.service.WorkflowUserService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import com.chd.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: Controller基类
 * @Author: dangzhenghui@163.com
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public class IssueBaseController<T, S extends IService<T>> extends JeecgController<T, S> {

    @Autowired
    private IMyOrgService myOrgService;

    @Autowired
    private WorkflowUserService workflowUserService;


    protected Map<String, Object> getOverseeIssueQueryVoMapData(OverseeIssueQueryVo query){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");
        Map<String, Object> map = Maps.newHashMap();
        boolean isAll = false;
        try{
            List<String> myOrgList = null;
            if (StringUtil.isNotEmpty(sysUser.getHdmyUserId())) {
                if(query.getSubmitState()!=null && query.getSubmitState()==0){
                    //未提交的问题只能提交人看到，其余人都看不到
                }else {
                    List<MyOrg> myOrgs = myOrgService.authorizeOrgList(sysUser.getHdmyUserId());
                    if (null != myOrgs && myOrgs.size() > 0) {
                        //			 暂时只处理一个用户只负责一个部门 加快查询速度
//				        query.setSelectOrgId(myOrgs.get(0).getOrgId());
                        myOrgList = myOrgs.stream().map((myOrg) -> myOrg.getOrgId()).collect(Collectors.toList());
                        if (null != myOrgList && myOrgList.size() > 0) {
                            if (!myOrgList.contains("JCB") && !myOrgList.contains("ZJL")) {
                                query.setSelectOrgIds(myOrgs.stream().map((myOrg) -> myOrg.getOrgId()).collect(Collectors.joining(",")));
                            } else {
                                isAll = true;
                            }
                        }
                    }
                    map.put("myOrgList",myOrgList);
                }
            }

            query.setLoginId(sysUser.getId());

            if (StringUtil.isNotEmpty(query.getCheckTimeSectionSrc())) {
                query.setCheckTimeSection(Arrays.asList(query.getCheckTimeSectionSrc().split(",")));
            }

            if (CollectionUtils.isNotEmpty(query.getCheckTimeSection())) {
                List<String> checkTimeSection = query.getCheckTimeSection();
                query.setCheckTimeGt(checkTimeSection.get(0));
                if (query.getCheckTimeSection().size() > 1) {
                    query.setCheckTimeLt(checkTimeSection.get(1));
                }
            }

            if (!workflowUserService.isWorkflowSuperUser(sysUser.getId())) {
                if (!isSystemRole() && !isAll) {
                    query.setSelectUserId(sysUser.getId());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        map.put("query",query);
        map.put("isAll",isAll);
        return map;
    }

}

