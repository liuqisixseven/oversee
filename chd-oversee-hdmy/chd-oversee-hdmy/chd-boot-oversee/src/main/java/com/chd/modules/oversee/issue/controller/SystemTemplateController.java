package com.chd.modules.oversee.issue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.SystemTemplate;
import com.chd.modules.oversee.issue.service.ISystemTemplateService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: system_template
* @Author: jeecg-boot
* @Date:   2022-08-03
* @Version: V1.0
*/
@Api(tags="system_template")
@RestController
@RequestMapping("/issue/systemTemplate")
@Slf4j
public class SystemTemplateController extends JeecgController<SystemTemplate, ISystemTemplateService> {
   @Autowired
   private ISystemTemplateService systemTemplateService;

   /**
    * 分页列表查询
    *
    * @param systemTemplate
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "system_template-分页列表查询")
   @ApiOperation(value="system_template-分页列表查询", notes="system_template-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SystemTemplate>> queryPageList(SystemTemplate systemTemplate,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       try {
           Map<String, Object> selectMap = getSelectMap(req,"name");
           Page<SystemTemplate> page = new Page<SystemTemplate>(pageNo, pageSize);
           IPage<SystemTemplate> pageList = systemTemplateService.selectSystemTemplatePageVo(page,selectMap);
           return Result.OK(pageList);
       }catch (IllegalArgumentException e){
           log.error(e.getMessage(), e);
           return Result.error(e.getMessage());
       }catch (Exception e){
           log.error(e.getMessage(), e);
           return Result.error("异常");
       }
   }

    /**
     * 列表查询
     *
     * @param systemTemplate
     * @param req
     * @return
     */
    @AutoLog(value = "system_template-列表查询")
    @ApiOperation(value="system_template-列表查询", notes="system_template-分页列表查询")
    @GetMapping(value = "/getList")
    public Result<Map<String, Object>> getList(SystemTemplate systemTemplate,
                                                             HttpServletRequest req) {
        try {
            Map<String, Object> selectMap = getSelectMap(req,"name");
            List<SystemTemplate> overseeIssueCategories = systemTemplateService.selectSystemTemplateList(selectMap);
            Map<String, Object> map = Maps.newHashMap();
            map.put("systemTemplates",overseeIssueCategories);
            return Result.OK(map);
        }catch (IllegalArgumentException e){
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return Result.error("异常");
        }
    }

   /**
    *   添加
    *
    * @param systemTemplate
    * @return
    */
   @AutoLog(value = "system_template-添加")
   @ApiOperation(value="system_template-添加", notes="system_template-添加")
   //@RequiresPermissions("com.chd.modules.oversee:system_template:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SystemTemplate systemTemplate) {
       return addOrUpdate(systemTemplate);
   }

   private Result<String> addOrUpdate(SystemTemplate systemTemplate){
       try {
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
           systemTemplate.setUpdateUserId(sysUser.getId());
           Assert.isTrue((systemTemplateService.addOrUpdate(systemTemplate) > 0),"编辑问题大类异常");
           return Result.OK("编辑成功！");
       }catch (IllegalArgumentException e){
           log.error(e.getMessage(), e);
           return Result.error(e.getMessage());
       }catch (Exception e){
           log.error(e.getMessage(), e);
           return Result.error("异常");
       }
   }

   /**
    *  编辑
    *
    * @param systemTemplate
    * @return
    */
   @AutoLog(value = "system_template-编辑")
   @ApiOperation(value="system_template-编辑", notes="system_template-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:system_template:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SystemTemplate systemTemplate) {
       return addOrUpdate(systemTemplate);
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "system_template-通过id删除")
   @ApiOperation(value="system_template-通过id删除", notes="system_template-通过id删除")
   //@RequiresPermissions("com.chd.modules.oversee:system_template:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       systemTemplateService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "system_template-批量删除")
   @ApiOperation(value="system_template-批量删除", notes="system_template-批量删除")
   //@RequiresPermissions("com.chd.modules.oversee:system_template:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.systemTemplateService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "system_template-通过id查询")
   @ApiOperation(value="system_template-通过id查询", notes="system_template-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SystemTemplate> queryById(@RequestParam(name="id",required=true) String id) {
       SystemTemplate systemTemplate = systemTemplateService.getById(id);
       if(systemTemplate==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(systemTemplate);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param systemTemplate
   */
   //@RequiresPermissions("com.chd.modules.oversee:system_template:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, SystemTemplate systemTemplate) {
       return super.exportXls(request, systemTemplate, SystemTemplate.class, "system_template");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   //@RequiresPermissions("system_template:importExcel")
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return super.importExcel(request, response, SystemTemplate.class);
   }

}
