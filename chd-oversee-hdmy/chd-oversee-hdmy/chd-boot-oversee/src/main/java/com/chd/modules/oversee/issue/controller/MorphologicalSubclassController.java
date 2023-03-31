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
import com.chd.modules.oversee.issue.entity.MorphologicalSubclass;
import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.chd.modules.oversee.issue.service.IMorphologicalCategoriesService;
import com.chd.modules.oversee.issue.service.IMorphologicalSubclassService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: morphological_subclass
* @Author: jeecg-boot
* @Date:   2022-08-03
* @Version: V1.0
*/
@Api(tags="morphological_subclass")
@RestController
@RequestMapping("/issue/morphologicalSubclass")
@Slf4j
public class MorphologicalSubclassController extends JeecgController<MorphologicalSubclass, IMorphologicalSubclassService> {

   @Autowired
   private IMorphologicalSubclassService morphologicalSubclassService;

    @Autowired
    private IMorphologicalCategoriesService morphologicalCategoriesService;

   /**
    * 分页列表查询
    *
    * @param morphologicalSubclass
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "morphological_subclass-分页列表查询")
   @ApiOperation(value="morphological_subclass-分页列表查询", notes="morphological_subclass-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<MorphologicalSubclass>> queryPageList(MorphologicalSubclass morphologicalSubclass,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       try {
           Map<String, Object> selectMap = getSelectMap(req,"name");
           Page<MorphologicalCategories> page = new Page<MorphologicalCategories>(pageNo, pageSize);
           IPage<MorphologicalSubclass> pageList = morphologicalSubclassService.selectMorphologicalSubclassPageVo(page,selectMap);
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
     * @param morphologicalSubclass
     * @param req
     * @return
     */
    @AutoLog(value = "morphological_subclass-列表查询")
    @ApiOperation(value="morphological_subclass-列表查询", notes="morphological_subclass-分页列表查询")
    @GetMapping(value = "/getList")
    public Result<Map<String, Object>> getList(MorphologicalSubclass morphologicalSubclass,
                                               HttpServletRequest req) {
        try {
            Map<String, Object> selectMap = getSelectMap(req,"name");
            List<MorphologicalSubclass> morphologicalSubclasss = morphologicalSubclassService.selectMorphologicalSubclassList(selectMap);
            Map<String, Object> map = Maps.newHashMap();
            map.put("morphologicalSubclasss",morphologicalSubclasss);
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
    * @param morphologicalSubclass
    * @return
    */
   @AutoLog(value = "morphological_subclass-添加")
   @ApiOperation(value="morphological_subclass-添加", notes="morphological_subclass-添加")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_subclass:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody MorphologicalSubclass morphologicalSubclass) {
       return addOrUpdate(morphologicalSubclass);
   }

    private Result<String> addOrUpdate(MorphologicalSubclass morphologicalSubclass){
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
            morphologicalSubclass.setUpdateUserId(sysUser.getId());
            Assert.isTrue((morphologicalSubclassService.addOrUpdate(morphologicalSubclass) > 0),"编辑问题大类异常");
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
    * @param morphologicalSubclass
    * @return
    */
   @AutoLog(value = "morphological_subclass-编辑")
   @ApiOperation(value="morphological_subclass-编辑", notes="morphological_subclass-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_subclass:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody MorphologicalSubclass morphologicalSubclass) {
       return addOrUpdate(morphologicalSubclass);
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "morphological_subclass-通过id删除")
   @ApiOperation(value="morphological_subclass-通过id删除", notes="morphological_subclass-通过id删除")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_subclass:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       morphologicalSubclassService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "morphological_subclass-批量删除")
   @ApiOperation(value="morphological_subclass-批量删除", notes="morphological_subclass-批量删除")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_subclass:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.morphologicalSubclassService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "morphological_subclass-通过id查询")
   @ApiOperation(value="morphological_subclass-通过id查询", notes="morphological_subclass-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<MorphologicalSubclass> queryById(@RequestParam(name="id",required=true) String id) {
       MorphologicalSubclass morphologicalSubclass = morphologicalSubclassService.getById(id);
       if(morphologicalSubclass==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(morphologicalSubclass);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param morphologicalSubclass
   */
   //@RequiresPermissions("com.chd.modules.oversee:morphological_subclass:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, MorphologicalSubclass morphologicalSubclass) {
       Class<MorphologicalSubclass> clazz = MorphologicalSubclass.class;
       String title = "morphological_subclass";
       List<MorphologicalSubclass> exportList = null;
       LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       try {
           exportList = super.exportXlsReturnDataList(request, morphologicalSubclass);
           if(null!=exportList&&exportList.size()>0){
               for(MorphologicalSubclass morphologicalSubclassS : exportList){
                   if(null!=morphologicalSubclassS){
                       if(null!=morphologicalSubclassS.getMorphologicalCategoriesId()){
                           MorphologicalCategories morphologicalCategoriesById = morphologicalCategoriesService.getById(morphologicalSubclassS.getMorphologicalCategoriesId());
                           if(null!=morphologicalCategoriesById){
                               morphologicalSubclassS.setMorphologicalCategoriesName(morphologicalCategoriesById.getName());
                           }
                       }
                   }
               }
           }
       }catch (Exception e){
           e.printStackTrace();
           log.error(e.getMessage(),e);
       }
       exportList = null!=exportList?exportList:new ArrayList<>();
       // Step.3 AutoPoi 导出Excel
       ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
       //此处设置的filename无效 ,前端会重更新设置一下
       mv.addObject(NormalExcelConstants.FILE_NAME, title);
       mv.addObject(NormalExcelConstants.CLASS, clazz);
       //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
       ExportParams exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title);
       exportParams.setImageBasePath(upLoadPath);
       //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
       mv.addObject(NormalExcelConstants.PARAMS,exportParams);
       mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
       return mv;
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   //@RequiresPermissions("morphological_subclass:importExcel")
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       try {
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
           List<MorphologicalSubclass> morphologicalSubclasss = super.importExcelReturnDataList(request, response, MorphologicalSubclass.class);
           Assert.isTrue((null != morphologicalSubclasss && morphologicalSubclasss.size() > 0), "导入文件未包含数据");
           int count = 0;
           for(MorphologicalSubclass morphologicalSubclass : morphologicalSubclasss){
               try{
                   if(null!=morphologicalSubclass){
                       morphologicalSubclass.setUpdateUserId(sysUser.getId());
                       if(StringUtil.isNotEmpty(morphologicalSubclass.getName())){
                           morphologicalSubclass.setName(morphologicalSubclass.getName().trim());
                           morphologicalSubclass.setMorphologicalCategoriesName(morphologicalSubclass.getMorphologicalCategoriesName().trim());
                           List<MorphologicalSubclass> morphologicalSubclassList = morphologicalSubclassService.list(Wrappers.<MorphologicalSubclass>lambdaQuery().eq(MorphologicalSubclass::getName, morphologicalSubclass.getName()).eq(MorphologicalSubclass::getDataType, 1));
                           if(CollectionUtils.isNotEmpty(morphologicalSubclassList)){
                               morphologicalSubclass.setId(morphologicalSubclassList.get(0).getId());
                           }
                           List<MorphologicalCategories> morphologicalCategoriesList = morphologicalCategoriesService.list(Wrappers.<MorphologicalCategories>lambdaQuery().eq(MorphologicalCategories::getName, morphologicalSubclass.getMorphologicalCategoriesName()).eq(MorphologicalCategories::getDataType, 1));
                           if(CollectionUtils.isNotEmpty(morphologicalCategoriesList)){
                               morphologicalSubclass.setMorphologicalCategoriesId(morphologicalCategoriesList.get(0).getId());
                           }
                           count += morphologicalSubclassService.addOrUpdate(morphologicalSubclass);
                       }
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
           return Result.ok("文件导入成功！共编辑：" + count + "条数据");
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
