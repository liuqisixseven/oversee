package com.chd.modules.oversee.issue.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;
import com.chd.modules.oversee.issue.entity.IssuesAllocation;
import com.chd.modules.oversee.issue.service.IIssuesAllocationService;

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
 * @Description: issues_allocation
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Api(tags="issues_allocation")
@RestController
@RequestMapping("/issue/issuesAllocation")
@Slf4j
public class IssuesAllocationController extends JeecgController<IssuesAllocation, IIssuesAllocationService> {
	@Autowired
	private IIssuesAllocationService issuesAllocationService;

	/**
	 * 分页列表查询
	 *
	 * @param issuesAllocation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "issues_allocation-分页列表查询")
	@ApiOperation(value="issues_allocation-分页列表查询", notes="issues_allocation-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<IssuesAllocation>> queryPageList(IssuesAllocation issuesAllocation,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
		QueryWrapper<IssuesAllocation> queryWrapper = QueryGenerator.initQueryWrapper(issuesAllocation, req.getParameterMap());
		Page<IssuesAllocation> page = new Page<IssuesAllocation>(pageNo, pageSize);
		IPage<IssuesAllocation> pageList = issuesAllocationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param issuesAllocation
	 * @return
	 */
	@AutoLog(value = "issues_allocation-添加")
	@ApiOperation(value="issues_allocation-添加", notes="issues_allocation-添加")
	//@RequiresPermissions("com.chd.modules.oversee:issues_allocation:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IssuesAllocation issuesAllocation) {
		issuesAllocationService.save(issuesAllocation);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param issuesAllocation
	 * @return
	 */
	@AutoLog(value = "issues_allocation-编辑")
	@ApiOperation(value="issues_allocation-编辑", notes="issues_allocation-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:issues_allocation:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IssuesAllocation issuesAllocation) {
		issuesAllocationService.updateById(issuesAllocation);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "issues_allocation-通过id删除")
	@ApiOperation(value="issues_allocation-通过id删除", notes="issues_allocation-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:issues_allocation:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		issuesAllocationService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "issues_allocation-批量删除")
	@ApiOperation(value="issues_allocation-批量删除", notes="issues_allocation-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:issues_allocation:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.issuesAllocationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "issues_allocation-通过id查询")
	@ApiOperation(value="issues_allocation-通过id查询", notes="issues_allocation-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IssuesAllocation> queryById(@RequestParam(name="id",required=true) String id) {
		IssuesAllocation issuesAllocation = issuesAllocationService.getById(id);
		if(issuesAllocation==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(issuesAllocation);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param issuesAllocation
    */
    //@RequiresPermissions("com.chd.modules.oversee:issues_allocation:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IssuesAllocation issuesAllocation) {
        return super.exportXls(request, issuesAllocation, IssuesAllocation.class, "issues_allocation");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("issues_allocation:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IssuesAllocation.class);
    }

}
