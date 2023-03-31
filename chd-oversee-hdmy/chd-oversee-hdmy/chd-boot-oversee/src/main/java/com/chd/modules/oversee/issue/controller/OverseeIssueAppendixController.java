package com.chd.modules.oversee.issue.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;

import com.chd.modules.oversee.issue.entity.OverseeIssueAppendix;
import com.chd.modules.oversee.issue.service.IOverseeIssueAppendixService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.chd.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.chd.common.aspect.annotation.AutoLog;

 /**
 * @Description: oversee_issue_appendix
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Api(tags="oversee_issue_appendix")
@RestController
@RequestMapping("/issue/overseeIssueAppendix")
@Slf4j
public class OverseeIssueAppendixController extends JeecgController<OverseeIssueAppendix, IOverseeIssueAppendixService> {
	@Autowired
	private IOverseeIssueAppendixService overseeIssueAppendixService;

	/**
	 * 分页列表查询
	 *
	 * @param overseeIssueAppendix
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "oversee_issue_appendix-分页列表查询")
	@ApiOperation(value="oversee_issue_appendix-分页列表查询", notes="oversee_issue_appendix-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OverseeIssueAppendix>> queryPageList(OverseeIssueAppendix overseeIssueAppendix,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OverseeIssueAppendix> queryWrapper = QueryGenerator.initQueryWrapper(overseeIssueAppendix, req.getParameterMap());
		Page<OverseeIssueAppendix> page = new Page<OverseeIssueAppendix>(pageNo, pageSize);
		IPage<OverseeIssueAppendix> pageList = overseeIssueAppendixService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param overseeIssueAppendix
	 * @return
	 */
	@AutoLog(value = "oversee_issue_appendix-添加")
	@ApiOperation(value="oversee_issue_appendix-添加", notes="oversee_issue_appendix-添加")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_appendix:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody OverseeIssueAppendix overseeIssueAppendix) {
		overseeIssueAppendixService.save(overseeIssueAppendix);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param overseeIssueAppendix
	 * @return
	 */
	@AutoLog(value = "oversee_issue_appendix-编辑")
	@ApiOperation(value="oversee_issue_appendix-编辑", notes="oversee_issue_appendix-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_appendix:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody OverseeIssueAppendix overseeIssueAppendix) {
		overseeIssueAppendixService.updateById(overseeIssueAppendix);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "oversee_issue_appendix-通过id删除")
	@ApiOperation(value="oversee_issue_appendix-通过id删除", notes="oversee_issue_appendix-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_appendix:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		overseeIssueAppendixService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "oversee_issue_appendix-批量删除")
	@ApiOperation(value="oversee_issue_appendix-批量删除", notes="oversee_issue_appendix-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:oversee_issue_appendix:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.overseeIssueAppendixService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "oversee_issue_appendix-通过id查询")
	@ApiOperation(value="oversee_issue_appendix-通过id查询", notes="oversee_issue_appendix-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<OverseeIssueAppendix> queryById(@RequestParam(name="id",required=true) String id) {
		OverseeIssueAppendix overseeIssueAppendix = overseeIssueAppendixService.getById(id);
		if(overseeIssueAppendix==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(overseeIssueAppendix);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param overseeIssueAppendix
    */
    //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_appendix:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OverseeIssueAppendix overseeIssueAppendix) {
        return super.exportXls(request, overseeIssueAppendix, OverseeIssueAppendix.class, "oversee_issue_appendix");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("oversee_issue_appendix:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, OverseeIssueAppendix.class);
    }

}
