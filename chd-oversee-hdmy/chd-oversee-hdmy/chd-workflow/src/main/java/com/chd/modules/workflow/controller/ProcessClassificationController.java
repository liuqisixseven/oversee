package com.chd.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.CommonAPI;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.constant.CommonConstant;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.system.vo.SysDepartModel;
import com.chd.common.system.vo.SysUserCacheInfo;
import com.chd.common.util.StringUtil;
import com.chd.common.util.poi.ExcelUtils;

import com.chd.modules.workflow.entity.ProcessClassification;
import com.chd.modules.workflow.service.IProcessClassificationService;
import com.chd.modules.workflow.service.WorkflowUserService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
* @Description: process_classification
* @Author: jeecg-boot
* @Date:   2022-08-03
* @Version: V1.0
*/
@Api(tags="process_classification")
@RestController
@RequestMapping("/workflow/processClassification")
@Slf4j
public class ProcessClassificationController extends JeecgController<ProcessClassification, IProcessClassificationService> {

   @Autowired
   private IProcessClassificationService processClassificationService;




   /**
    * 分页列表查询
    *
    * @param processClassification
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "process_classification-分页列表查询")
   @ApiOperation(value="process_classification-分页列表查询", notes="process_classification-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<ProcessClassification>> queryPageList(ProcessClassification processClassification,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       try {
           Map<String, Object> selectMap = getSelectMap(req,"num","title","subtitle");
           Page<ProcessClassification> page = new Page<ProcessClassification>(pageNo, pageSize);
           IPage<ProcessClassification> pageList = processClassificationService.selectProcessClassificationPageVo(page,selectMap);
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
     * @param req
     * @return
     */
    //@AutoLog(value = "process_classification-分页列表查询")
    @ApiOperation(value="process_classification-分页列表查询", notes="process_classification-分页列表查询")
    @GetMapping(value = "/getList")
    public Result<List<Map<String, Object>>> getList(
                                                              HttpServletRequest req) {
        try {
            Map<String, Object> selectMap = getSelectMap(req,"num","title","subtitle");
            List<ProcessClassification> processClassifications = processClassificationService.selectProcessClassificationList(selectMap);
            List<Map<String, Object>> processClassificationMaps = new ArrayList<>();
            if(null!=processClassifications&&processClassifications.size()>0){
                for(ProcessClassification processClassification : processClassifications){
                    if(null!=processClassification){
                        if(StringUtil.isNotEmpty(processClassification.getName())&&StringUtil.isNotEmpty(processClassification.getValue())){
                            Map<String, Object> processClassificationMap = Maps.newHashMap();
                            processClassificationMap.put("label",processClassification.getName());
                            processClassificationMap.put("text",processClassification.getName());
                            processClassificationMap.put("title",processClassification.getName());
                            processClassificationMap.put("value",processClassification.getValue());
                            processClassificationMaps.add(processClassificationMap);
                        }
                    }
                }
            }
            return Result.OK(processClassificationMaps);
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
    * @param processClassification
    * @return
    */
   @AutoLog(value = "process_classification-添加")
   @ApiOperation(value="process_classification-添加", notes="process_classification-添加")
   //@RequiresPermissions("com.chd.modules.oversee:process_classification:add")
   @PostMapping(value = "/add")
   public Result<Map<String,Object>> add(@RequestBody ProcessClassification processClassification) {
       return addOrUpdate(processClassification);
   }

   private Result<Map<String,Object>> addOrUpdate(ProcessClassification processClassification){
        try {
            Result<Map<String,Object>> result = Result.OK("保存成功！");
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
            processClassification.setUpdateUserId(sysUser.getId());
            Assert.isTrue((processClassificationService.addOrUpdate(processClassification) > 0),"编辑问题异常");
            Map<String, Object> data = Maps.newHashMap();
            data.put("processClassification",processClassification);
            result.setResult(data);
            return result;
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
    * @param processClassification
    * @return
    */
   @AutoLog(value = "process_classification-编辑")
   @ApiOperation(value="process_classification-编辑", notes="process_classification-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:process_classification:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<Map<String,Object>> edit(@RequestBody ProcessClassification processClassification) {
       return addOrUpdate(processClassification);
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "process_classification-通过id删除")
   @ApiOperation(value="process_classification-通过id删除", notes="process_classification-通过id删除")
   //@RequiresPermissions("com.chd.modules.oversee:process_classification:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		processClassificationService.removeById(id);
       try {
           Assert.notNull(id,"参数错误");
           LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
           Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
           ProcessClassification processClassification = new ProcessClassification();
           processClassification.setId(Integer.parseInt(id));
           processClassification.setUpdateTime(new Date());
           processClassification.setUpdateUserId(sysUser.getId());
           processClassification.setDataType(-1);
           boolean isUpdate = processClassificationService.updateById(processClassification);
           Assert.isTrue((isUpdate),"删除异常");
           return Result.OK("删除成功!");
       }catch (IllegalArgumentException e){
           log.error(e.getMessage(), e);
           return Result.error(e.getMessage());
       }catch (Exception e){
           log.error(e.getMessage(), e);
           return Result.error("异常");
       }

   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "process_classification-批量删除")
   @ApiOperation(value="process_classification-批量删除", notes="process_classification-批量删除")
   //@RequiresPermissions("com.chd.modules.oversee:process_classification:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.processClassificationService.removeByIds(Arrays.asList(ids.split(",")));
       try {
           Assert.isTrue((StringUtil.isNotEmpty(ids)),"请传递数据ids");
           List<String> idArray = Arrays.asList(ids.split(","));
           if(null!=idArray&&idArray.size()>0){
               for(String id : idArray){
                   if(StringUtils.isNotBlank(id)) {
                       Result<String> deleteResult = delete(id);
                       Assert.isTrue((null != deleteResult && null != deleteResult.getCode() && deleteResult.getCode() == CommonConstant.SC_OK_200), "删除异常");
                   }
               }
           }
           return Result.OK("批量删除成功!");
       }catch (IllegalArgumentException e){
           log.error(e.getMessage(), e);
           return Result.error(e.getMessage());
       }catch (Exception e){
           log.error(e.getMessage(), e);
           return Result.error("异常");
       }

   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "process_classification-通过id查询")
   @ApiOperation(value="process_classification-通过id查询", notes="process_classification-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<ProcessClassification> queryById(@RequestParam(name="id",required=true) Integer id) {
       try {
           Assert.notNull(id,"请传递查询数据");
           ProcessClassification processClassification = processClassificationService.getById(id);
           if(processClassification==null) {
               return Result.error("未找到对应数据");
           }
           return Result.OK(processClassification);
       }catch (IllegalArgumentException e){
           log.error(e.getMessage(), e);
           return Result.error(e.getMessage());
       }catch (Exception e){
           log.error(e.getMessage(), e);
           return Result.error("异常");
       }

   }




   /**
   * 导出excel
   *
   * @param request
   * @param processClassification
   */
   //@RequiresPermissions("com.chd.modules.oversee:process_classification:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, ProcessClassification processClassification) {
       return super.exportXls(request, processClassification, ProcessClassification.class, "process_classification");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */

}
