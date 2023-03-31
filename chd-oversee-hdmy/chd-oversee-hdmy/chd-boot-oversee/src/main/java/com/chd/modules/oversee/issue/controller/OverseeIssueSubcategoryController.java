package com.chd.modules.oversee.issue.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.modules.oversee.issue.service.IOverseeIssueCategoryService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;

import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.chd.modules.oversee.issue.entity.OverseeIssueSubcategory;
import com.chd.modules.oversee.issue.service.IOverseeIssueSubcategoryService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.chd.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.chd.common.aspect.annotation.AutoLog;

 /**
 * @Description: oversee_issue_subcategory
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Api(tags="oversee_issue_subcategory")
@RestController
@RequestMapping("/issue/overseeIssueSubcategory")
@Slf4j
public class OverseeIssueSubcategoryController extends JeecgController<OverseeIssueSubcategory, IOverseeIssueSubcategoryService> {

	@Autowired
	private IOverseeIssueSubcategoryService overseeIssueSubcategoryService;

	 @Autowired
	 private IOverseeIssueCategoryService overseeIssueCategoryService;

	/**
	 * 分页列表查询
	 *
	 * @param overseeIssueSubcategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "oversee_issue_subcategory-分页列表查询")
	@ApiOperation(value="oversee_issue_subcategory-分页列表查询", notes="oversee_issue_subcategory-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OverseeIssueSubcategory>> queryPageList(OverseeIssueSubcategory overseeIssueSubcategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		try {
			Map<String, Object> selectMap = getSelectMap(req,"name");
//			if(null!=selectMap.get("issueCategoryId")&&eqFieldValueToInteger(selectMap.get("issueCategoryId")).intValue()<=0){
//				selectMap.remove("issueCategoryId");
//			}
			Page<OverseeIssueCategory> page = new Page<OverseeIssueCategory>(pageNo, pageSize);
			IPage<OverseeIssueSubcategory> pageList = overseeIssueSubcategoryService.selectOverseeIssueSubcategoryPageVo(page,selectMap);
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
	  * @param overseeIssueSubcategory
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "oversee_issue_subcategory-列表查询")
	 @ApiOperation(value="oversee_issue_subcategory-列表查询", notes="oversee_issue_subcategory-分页列表查询")
	 @GetMapping(value = "/getList")
	 public Result<Map<String, Object>> getList(OverseeIssueSubcategory overseeIssueSubcategory,
												HttpServletRequest req) {
		 try {
			 Map<String, Object> selectMap = getSelectMap(req,"name");
			 List<OverseeIssueSubcategory> overseeIssueSubcategorys = overseeIssueSubcategoryService.selectOverseeIssueSubcategoryList(selectMap);
			 Map<String, Object> map = Maps.newHashMap();
			 map.put("overseeIssueSubcategorys",overseeIssueSubcategorys);
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
	 * @param overseeIssueSubcategory
	 * @return
	 */
	@AutoLog(value = "oversee_issue_subcategory-添加")
	@ApiOperation(value="oversee_issue_subcategory-添加", notes="oversee_issue_subcategory-添加")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_subcategory:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody OverseeIssueSubcategory overseeIssueSubcategory) {
		return addOrUpdate(overseeIssueSubcategory);
	}

	 private Result<String> addOrUpdate(OverseeIssueSubcategory overseeIssueSubcategory){
		 try {
			 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			 Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
			 overseeIssueSubcategory.setUpdateUserId(sysUser.getId());
			 Assert.isTrue((overseeIssueSubcategoryService.addOrUpdate(overseeIssueSubcategory) > 0),"编辑问题大类异常");
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
	 * @param overseeIssueSubcategory
	 * @return
	 */
	@AutoLog(value = "oversee_issue_subcategory-编辑")
	@ApiOperation(value="oversee_issue_subcategory-编辑", notes="oversee_issue_subcategory-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_subcategory:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody OverseeIssueSubcategory overseeIssueSubcategory) {
		return addOrUpdate(overseeIssueSubcategory);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "oversee_issue_subcategory-通过id删除")
	@ApiOperation(value="oversee_issue_subcategory-通过id删除", notes="oversee_issue_subcategory-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_subcategory:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		overseeIssueSubcategoryService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "oversee_issue_subcategory-批量删除")
	@ApiOperation(value="oversee_issue_subcategory-批量删除", notes="oversee_issue_subcategory-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_subcategory:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.overseeIssueSubcategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "oversee_issue_subcategory-通过id查询")
	@ApiOperation(value="oversee_issue_subcategory-通过id查询", notes="oversee_issue_subcategory-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<OverseeIssueSubcategory> queryById(@RequestParam(name="id",required=true) String id) {
		OverseeIssueSubcategory overseeIssueSubcategory = overseeIssueSubcategoryService.getById(id);
		if(overseeIssueSubcategory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(overseeIssueSubcategory);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param overseeIssueSubcategory
    */
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_subcategory:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OverseeIssueSubcategory overseeIssueSubcategory) {
		Class<OverseeIssueSubcategory> clazz = OverseeIssueSubcategory.class;
		String title = "oversee_issue_subcategory";
		List<OverseeIssueSubcategory> exportList = null;
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		try {
			exportList = super.exportXlsReturnDataList(request, overseeIssueSubcategory);
			if(null!=exportList&&exportList.size()>0){
				for(OverseeIssueSubcategory overseeIssueSubcategoryS : exportList){
					if(null!=overseeIssueSubcategoryS){
						if(null!=overseeIssueSubcategoryS.getIssueCategoryId()){
							OverseeIssueCategory overseeIssueCategoryById = overseeIssueCategoryService.getById(overseeIssueSubcategoryS.getIssueCategoryId());
							if(null!=overseeIssueCategoryById){
								overseeIssueSubcategoryS.setIssueCategoryName(overseeIssueCategoryById.getName());
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
    //@RequiresPermissions("oversee_issue_subcategory:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
			List<OverseeIssueSubcategory> overseeIssueSubcategorys = super.importExcelReturnDataList(request, response, OverseeIssueSubcategory.class);
			Assert.isTrue((null != overseeIssueSubcategorys && overseeIssueSubcategorys.size() > 0), "导入文件未包含数据");
			int count = 0;
			for(OverseeIssueSubcategory overseeIssueSubcategory : overseeIssueSubcategorys){
				try{
					if(null!=overseeIssueSubcategory){
						overseeIssueSubcategory.setUpdateUserId(sysUser.getId());
						if(StringUtil.isNotEmpty(overseeIssueSubcategory.getName())){
							overseeIssueSubcategory.setName(overseeIssueSubcategory.getName().trim());
							overseeIssueSubcategory.setIssueCategoryName(overseeIssueSubcategory.getIssueCategoryName().trim());
							List<OverseeIssueSubcategory> overseeIssueSubcategoryList = overseeIssueSubcategoryService.list(Wrappers.<OverseeIssueSubcategory>lambdaQuery().eq(OverseeIssueSubcategory::getName, overseeIssueSubcategory.getName()).eq(OverseeIssueSubcategory::getDataType, 1));
							if(CollectionUtils.isNotEmpty(overseeIssueSubcategoryList)){
								overseeIssueSubcategory.setId(overseeIssueSubcategoryList.get(0).getId());
							}
							List<OverseeIssueCategory> overseeIssueCategoryList = overseeIssueCategoryService.list(Wrappers.<OverseeIssueCategory>lambdaQuery().eq(OverseeIssueCategory::getName, overseeIssueSubcategory.getIssueCategoryName()).eq(OverseeIssueCategory::getDataType, 1));
							if(CollectionUtils.isNotEmpty(overseeIssueCategoryList)){
								overseeIssueSubcategory.setIssueCategoryId(overseeIssueCategoryList.get(0).getId());
							}
							count += overseeIssueSubcategoryService.addOrUpdate(overseeIssueSubcategory);
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
