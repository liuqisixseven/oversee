package com.chd.modules.oversee.hdmy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyOrgSettings;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;
import com.chd.modules.oversee.hdmy.service.IMyOrgSettingsService;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
* @Description: my_org_settings
* @Author: jeecg-boot
* @Date:   2022-08-08
* @Version: V1.0
*/
@Api(tags="组织架构管理")
@RestController
@RequestMapping("/hdmy/myOrgSettings")
@Slf4j
public class MyOrgSettingsController extends JeecgController<MyOrgSettings, IMyOrgSettingsService> {

   @Autowired
   private IMyOrgSettingsService myOrgSettingsService;

    @Autowired
    private IMyOrgService myOrgService;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

   /**
    * 分页列表查询
    *
    * @param myOrg
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "my_org_settings-分页列表查询")
   @ApiOperation(value="my_org_settings-分页列表查询", notes="my_org_settings-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<MyOrgSettings>> queryPageList(MyOrgSettings myOrg,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {

       Map<String, Object> selectMap = Maps.newHashMap();

       List<String> responsibleDepartmentList = myOrgService.getResponsibleDepartmentList();
       if(CollectionUtils.isNotEmpty(responsibleDepartmentList)){
           selectMap.put("idArray",responsibleDepartmentList);
       }
       Page<MyOrgSettings> page = new Page<MyOrgSettings>(pageNo, pageSize);
       IPage<MyOrgSettings> pageList = myOrgSettingsService.selectMyOrgSettingsPageVo(page,selectMap);
       return Result.OK(pageList);
   }






   /**
    *   添加
    *
    * @param myOrg
    * @return
    */
   @AutoLog(value = "my_org_settings-添加")
   @ApiOperation(value="my_org_settings-添加", notes="my_org_settings-添加")
   //@RequiresPermissions("com.chd.modules.oversee:my_org_settings:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody MyOrgSettings myOrg) {
       myOrgSettingsService.save(myOrg);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param myOrgSettings
    * @return
    */
   @AutoLog(value = "my_org_settings-编辑")
   @ApiOperation(value="my_org_settings-编辑", notes="my_org_settings-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:my_org_settings:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody MyOrgSettings myOrgSettings) {
       LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
       myOrgSettings.setUpdateUserId(sysUser.getId());
       myOrgSettingsService.addOrUpdate(myOrgSettings);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "my_org_settings-通过id删除")
   @ApiOperation(value="my_org_settings-通过id删除", notes="my_org_settings-通过id删除")
   //@RequiresPermissions("com.chd.modules.oversee:my_org_settings:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       myOrgSettingsService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "my_org_settings-批量删除")
   @ApiOperation(value="my_org_settings-批量删除", notes="my_org_settings-批量删除")
   //@RequiresPermissions("com.chd.modules.oversee:my_org_settings:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.myOrgSettingsService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "my_org_settings-通过id查询")
   @ApiOperation(value="my_org_settings-通过id查询", notes="my_org_settings-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<MyOrgSettings> queryById(@RequestParam(name="id",required=true) String id) {
       MyOrgSettings myOrg = myOrgSettingsService.getById(id);
       if(myOrg==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(myOrg);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param myOrg
   */
   //@RequiresPermissions("com.chd.modules.oversee:my_org_settings:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, MyOrgSettings myOrg) {
       return super.exportXls(request, myOrg, MyOrgSettings.class, "my_org_settings");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   //@RequiresPermissions("my_org_settings:importExcel")
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return super.importExcel(request, response, MyOrgSettings.class);
   }

}
