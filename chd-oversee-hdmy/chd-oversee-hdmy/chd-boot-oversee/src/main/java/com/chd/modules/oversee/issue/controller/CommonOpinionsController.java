package com.chd.modules.oversee.issue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.vo.DictModel;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.CommonOpinions;
import com.chd.modules.oversee.issue.service.ICommonOpinionsService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: morphological_categories
* @Author: jeecg-boot
* @Date:   2022-08-03
* @Version: V1.0
*/
@Api(tags="common_opinions")
@RestController
@RequestMapping("/issue/commonOpinions")
@Slf4j
public class CommonOpinionsController extends JeecgController<CommonOpinions, ICommonOpinionsService> {
   @Autowired
   private ICommonOpinionsService commonOpinionsService;

   /**
    * 分页列表查询
    *
    * @param commonOpinions
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "morphological_categories-分页列表查询")
   @ApiOperation(value="morphological_categories-分页列表查询", notes="morphological_categories-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<CommonOpinions>> queryPageList(CommonOpinions commonOpinions,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       try {
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
           Map<String, Object> selectMap = getSelectMap(req);
           Page<CommonOpinions> page = new Page<CommonOpinions>(pageNo, pageSize);
           LambdaQueryWrapper<CommonOpinions> commonOpinionsLambdaQueryWrapper = Wrappers.<CommonOpinions>lambdaQuery().eq(CommonOpinions::getUserId,sysUser.getId());
           if(null!=selectMap){
               if(null!=selectMap.get("value")){
                   commonOpinionsLambdaQueryWrapper.like(CommonOpinions::getValue,selectMap.get("value"));
               }
           }
           commonOpinionsLambdaQueryWrapper.orderByAsc(CommonOpinions::getSort);
           IPage<CommonOpinions> pageList = commonOpinionsService.page(page,commonOpinionsLambdaQueryWrapper);
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
     * @param commonOpinions
     * @param req
     * @return
     */
    @AutoLog(value = "morphological_categories-列表查询")
    @ApiOperation(value="morphological_categories-列表查询", notes="morphological_categories-分页列表查询")
    @GetMapping(value = "/getList")
    public Result<Map<String, Object>> getList(CommonOpinions commonOpinions,
                                                             HttpServletRequest req) {
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
            List<CommonOpinions> overseeIssueCategories = commonOpinionsService.list(Wrappers.<CommonOpinions>lambdaQuery().eq(CommonOpinions::getUserId,sysUser.getId()).orderByAsc(CommonOpinions::getSort));
            overseeIssueCategories = CollectionUtils.isNotEmpty(overseeIssueCategories)?overseeIssueCategories:new ArrayList<>();
            List<DictModel> dictModels = commonAPI.queryDictItemsByCode(BaseConstant.COMMON_OPINIONS_DICT_KEY);
            if(CollectionUtils.isNotEmpty(dictModels)){
                for(DictModel dictModel : dictModels){
                    if(null!=dictModel&&StringUtil.isNotEmpty(dictModel.getValue())){
                        CommonOpinions commonOpinionsDict = new CommonOpinions();
                        commonOpinionsDict.setValue(dictModel.getValue());
                        overseeIssueCategories.add(commonOpinionsDict);
                    }
                }
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("commonOpinionss",overseeIssueCategories);
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
    * @param commonOpinions
    * @return
    */
   @AutoLog(value = "morphological_categories-添加")
   @ApiOperation(value="morphological_categories-添加", notes="morphological_categories-添加")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody CommonOpinions commonOpinions) {
       return addOrUpdate(commonOpinions);
   }

   private Result<String> addOrUpdate(CommonOpinions commonOpinions){
       try {
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
           commonOpinions.setUserId(sysUser.getId());
           commonOpinions.setUpdateUserId(sysUser.getId());
           Assert.isTrue((commonOpinionsService.addOrUpdate(commonOpinions) > 0),"编辑问题大类异常");
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
    * @param commonOpinions
    * @return
    */
   @AutoLog(value = "morphological_categories-编辑")
   @ApiOperation(value="morphological_categories-编辑", notes="morphological_categories-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody CommonOpinions commonOpinions) {
       return addOrUpdate(commonOpinions);
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
       commonOpinionsService.removeById(id);
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
       this.commonOpinionsService.removeByIds(Arrays.asList(ids.split(",")));
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
   public Result<CommonOpinions> queryById(@RequestParam(name="id",required=true) String id) {
       CommonOpinions commonOpinions = commonOpinionsService.getById(id);
       if(commonOpinions==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(commonOpinions);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param commonOpinions
   */
   //@RequiresPermissions("com.chd.modules.oversee:morphological_categories:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, CommonOpinions commonOpinions) {
       return super.exportXls(request, commonOpinions, CommonOpinions.class, "morphological_categories");
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
       return super.importExcel(request, response, CommonOpinions.class);
   }

}
