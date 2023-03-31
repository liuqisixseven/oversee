package com.chd.modules.oversee.issue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.chd.modules.oversee.issue.service.IMorphologicalCategoriesService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
* @Description: morphological_categories
* @Author: jeecg-boot
* @Date:   2022-08-03
* @Version: V1.0
*/
@Api(tags="morphological_categories")
@RestController
@RequestMapping("/issue/morphologicalCategories")
@Slf4j
public class MorphologicalCategoriesController extends JeecgController<MorphologicalCategories, IMorphologicalCategoriesService> {

   @Autowired
   private IMorphologicalCategoriesService morphologicalCategoriesService;

   /**
    * 分页列表查询
    *
    * @param morphologicalCategories
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "morphological_categories-分页列表查询")
   @ApiOperation(value="morphological_categories-分页列表查询", notes="morphological_categories-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<MorphologicalCategories>> queryPageList(MorphologicalCategories morphologicalCategories,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       try {
           Map<String, Object> selectMap = getSelectMap(req,"name");
           Page<MorphologicalCategories> page = new Page<MorphologicalCategories>(pageNo, pageSize);
           IPage<MorphologicalCategories> pageList = morphologicalCategoriesService.selectMorphologicalCategoriesPageVo(page,selectMap);
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
     * @param morphologicalCategories
     * @param req
     * @return
     */
    @AutoLog(value = "morphological_categories-列表查询")
    @ApiOperation(value="morphological_categories-列表查询", notes="morphological_categories-分页列表查询")
    @GetMapping(value = "/getList")
    public Result<Map<String, Object>> getList(MorphologicalCategories morphologicalCategories,
                                                             HttpServletRequest req) {
        try {
            Map<String, Object> selectMap = getSelectMap(req,"name");
            List<MorphologicalCategories> overseeIssueCategories = morphologicalCategoriesService.selectMorphologicalCategoriesList(selectMap);
            Map<String, Object> map = Maps.newHashMap();
            map.put("morphologicalCategoriess",overseeIssueCategories);
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
    * @param morphologicalCategories
    * @return
    */
   @AutoLog(value = "morphological_categories-添加")
   @ApiOperation(value="morphological_categories-添加", notes="morphological_categories-添加")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody MorphologicalCategories morphologicalCategories) {
       return addOrUpdate(morphologicalCategories);
   }

   private Result<String> addOrUpdate(MorphologicalCategories morphologicalCategories){
       try {
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
           morphologicalCategories.setUpdateUserId(sysUser.getId());
           Assert.isTrue((morphologicalCategoriesService.addOrUpdate(morphologicalCategories) > 0),"编辑问题大类异常");
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
    * @param morphologicalCategories
    * @return
    */
   @AutoLog(value = "morphological_categories-编辑")
   @ApiOperation(value="morphological_categories-编辑", notes="morphological_categories-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody MorphologicalCategories morphologicalCategories) {
       return addOrUpdate(morphologicalCategories);
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "morphological_categories-通过id删除")
   @ApiOperation(value="morphological_categories-通过id删除", notes="morphological_categories-通过id删除")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       morphologicalCategoriesService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "morphological_categories-批量删除")
   @ApiOperation(value="morphological_categories-批量删除", notes="morphological_categories-批量删除")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.morphologicalCategoriesService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "morphological_categories-通过id查询")
   @ApiOperation(value="morphological_categories-通过id查询", notes="morphological_categories-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<MorphologicalCategories> queryById(@RequestParam(name="id",required=true) String id) {
       MorphologicalCategories morphologicalCategories = morphologicalCategoriesService.getById(id);
       if(morphologicalCategories==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(morphologicalCategories);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param morphologicalCategories
   */
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, MorphologicalCategories morphologicalCategories) {
       return super.exportXls(request, morphologicalCategories, MorphologicalCategories.class, "morphological_categories");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   //@RequiresPermissions("morphological_categories:importExcel")
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       try {
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
           List<MorphologicalCategories> morphologicalCategoriess = super.importExcelReturnDataList(request, response, MorphologicalCategories.class);
           Assert.isTrue((null != morphologicalCategoriess && morphologicalCategoriess.size() > 0), "导入文件未包含数据");
           int count = 0;
           for(MorphologicalCategories morphologicalCategories : morphologicalCategoriess){
               try{
                   if(null!=morphologicalCategories){
                       morphologicalCategories.setUpdateUserId(sysUser.getId());
                       if(StringUtil.isNotEmpty(morphologicalCategories.getName())){
                           morphologicalCategories.setName(morphologicalCategories.getName().trim());
                           List<MorphologicalCategories> overseeIssueCategoryList = morphologicalCategoriesService.list(Wrappers.<MorphologicalCategories>lambdaQuery().eq(MorphologicalCategories::getName, morphologicalCategories.getName()).eq(MorphologicalCategories::getDataType, 1));
                           if(CollectionUtils.isEmpty(overseeIssueCategoryList)){
                               count += morphologicalCategoriesService.addOrUpdate(morphologicalCategories);
                           }
                       }
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
           return Result.ok("文件导入成功！共导入：" + count + "条数据");
       }catch (IllegalArgumentException e){
           log.error(e.getMessage(), e);
           return Result.error(e.getMessage());
       }catch (Exception e){
           String msg = e.getMessage();
           log.error(msg, e);
           if(msg!=null && msg.indexOf("Duplicate entry")>=0){
               return Result.error("文件导入失败:有重复数据！");
           }else{
               return Result.error("文件导入失败:" + e.getMessage());
           }
       }
   }

}
