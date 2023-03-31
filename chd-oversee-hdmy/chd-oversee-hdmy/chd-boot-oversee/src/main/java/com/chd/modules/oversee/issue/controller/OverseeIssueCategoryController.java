package com.chd.modules.oversee.issue.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.StringUtil;

import com.chd.modules.oversee.issue.entity.OverseeIssueCategory;
import com.chd.modules.oversee.issue.service.IOverseeIssueCategoryService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.chd.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.chd.common.aspect.annotation.AutoLog;

 /**
 * @Description: oversee_issue_category
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Api(tags="oversee_issue_category")
@RestController
@RequestMapping("/issue/overseeIssueCategory")
@Slf4j
public class OverseeIssueCategoryController extends JeecgController<OverseeIssueCategory, IOverseeIssueCategoryService> {

	@Autowired
	private IOverseeIssueCategoryService overseeIssueCategoryService;

	/**
	 * 分页列表查询
	 *
	 * @param overseeIssueCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oversee_issue_category-分页列表查询")
	@ApiOperation(value="oversee_issue_category-分页列表查询", notes="oversee_issue_category-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OverseeIssueCategory>> queryPageList(OverseeIssueCategory overseeIssueCategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		try {
			Map<String, Object> selectMap = getSelectMap(req,"name");
			Page<OverseeIssueCategory> page = new Page<OverseeIssueCategory>(pageNo, pageSize);
			IPage<OverseeIssueCategory> pageList = overseeIssueCategoryService.selectOverseeIssueCategoryPageVo(page,selectMap);
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
	  * @param overseeIssueCategory
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "oversee_issue_category-列表查询")
	 @ApiOperation(value="oversee_issue_category-列表查询", notes="oversee_issue_category-分页列表查询")
	 @GetMapping(value = "/getList")
	 public Result<Map<String, Object>> getList(OverseeIssueCategory overseeIssueCategory,
															  HttpServletRequest req) {
		 try {
			 Map<String, Object> selectMap = getSelectMap(req,"name");
			 List<OverseeIssueCategory> overseeIssueCategories = overseeIssueCategoryService.selectOverseeIssueCategoryList(selectMap);
			 Map<String, Object> map = Maps.newHashMap();
			 map.put("overseeIssueCategories",overseeIssueCategories);
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
	 * @param overseeIssueCategory
	 * @return
	 */
	@AutoLog(value = "oversee_issue_category-添加")
	@ApiOperation(value="oversee_issue_category-添加", notes="oversee_issue_category-添加")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_category:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody OverseeIssueCategory overseeIssueCategory) {
		return addOrUpdate(overseeIssueCategory);
	}

	private Result<String> addOrUpdate(OverseeIssueCategory overseeIssueCategory){
		try {
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			Assert.isTrue((null!=sysUser&& StringUtil.isNotEmpty(sysUser.getId())),"未查询到登录数据");
			overseeIssueCategory.setUpdateUserId(sysUser.getId());
			Assert.isTrue((overseeIssueCategoryService.addOrUpdate(overseeIssueCategory) > 0),"编辑问题大类异常");
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
	 * @param overseeIssueCategory
	 * @return
	 */
	@AutoLog(value = "oversee_issue_category-编辑")
	@ApiOperation(value="oversee_issue_category-编辑", notes="oversee_issue_category-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_category:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody OverseeIssueCategory overseeIssueCategory) {
		return addOrUpdate(overseeIssueCategory);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "oversee_issue_category-通过id删除")
	@ApiOperation(value="oversee_issue_category-通过id删除", notes="oversee_issue_category-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_category:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		overseeIssueCategoryService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "oversee_issue_category-批量删除")
	@ApiOperation(value="oversee_issue_category-批量删除", notes="oversee_issue_category-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_category:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.overseeIssueCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "oversee_issue_category-通过id查询")
	@ApiOperation(value="oversee_issue_category-通过id查询", notes="oversee_issue_category-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<OverseeIssueCategory> queryById(@RequestParam(name="id",required=true) String id) {
		OverseeIssueCategory overseeIssueCategory = overseeIssueCategoryService.getById(id);
		if(overseeIssueCategory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(overseeIssueCategory);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param overseeIssueCategory
    */
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_category:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OverseeIssueCategory overseeIssueCategory) {
        return super.exportXls(request, overseeIssueCategory, OverseeIssueCategory.class, "oversee_issue_category");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("oversee_issue_category:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未查询到登录数据");
			List<OverseeIssueCategory> overseeIssueCategories = super.importExcelReturnDataList(request, response, OverseeIssueCategory.class);
			Assert.isTrue((null != overseeIssueCategories && overseeIssueCategories.size() > 0), "导入文件未包含数据");
			int count = 0;
		    for(OverseeIssueCategory overseeIssueCategorie : overseeIssueCategories){
				try{
					if(null!=overseeIssueCategorie){
						overseeIssueCategorie.setUpdateUserId(sysUser.getId());
						if(StringUtil.isNotEmpty(overseeIssueCategorie.getName())){
							overseeIssueCategorie.setName(overseeIssueCategorie.getName().trim());
							List<OverseeIssueCategory> overseeIssueCategoryList = overseeIssueCategoryService.list(Wrappers.<OverseeIssueCategory>lambdaQuery().eq(OverseeIssueCategory::getName, overseeIssueCategorie.getName()).eq(OverseeIssueCategory::getDataType, 1));
							if(CollectionUtils.isEmpty(overseeIssueCategoryList)){
								count += overseeIssueCategoryService.addOrUpdate(overseeIssueCategorie);
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
