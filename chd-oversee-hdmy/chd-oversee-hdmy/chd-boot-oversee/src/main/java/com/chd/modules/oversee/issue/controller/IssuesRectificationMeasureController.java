package com.chd.modules.oversee.issue.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;

import com.chd.modules.oversee.issue.entity.IssuesRectificationMeasure;
import com.chd.modules.oversee.issue.service.IIssuesRectificationMeasureService;

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
 * @Description: issues_rectification_measure
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Api(tags="issues_rectification_measure")
@RestController
@RequestMapping("/issue/issuesRectificationMeasure")
@Slf4j
public class IssuesRectificationMeasureController extends JeecgController<IssuesRectificationMeasure, IIssuesRectificationMeasureService> {
	@Autowired
	private IIssuesRectificationMeasureService issuesRectificationMeasureService;

	/**
	 * 分页列表查询
	 *
	 * @param issuesRectificationMeasure
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "issues_rectification_measure-分页列表查询")
	@ApiOperation(value="issues_rectification_measure-分页列表查询", notes="issues_rectification_measure-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<IssuesRectificationMeasure>> queryPageList(IssuesRectificationMeasure issuesRectificationMeasure,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<IssuesRectificationMeasure> queryWrapper = QueryGenerator.initQueryWrapper(issuesRectificationMeasure, req.getParameterMap());
		Page<IssuesRectificationMeasure> page = new Page<IssuesRectificationMeasure>(pageNo, pageSize);
		IPage<IssuesRectificationMeasure> pageList = issuesRectificationMeasureService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param issuesRectificationMeasure
	 * @return
	 */
	@AutoLog(value = "issues_rectification_measure-添加")
	@ApiOperation(value="issues_rectification_measure-添加", notes="issues_rectification_measure-添加")
	//@RequiresPermissions("com.chd.modules.oversee:issues_rectification_measure:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody IssuesRectificationMeasure issuesRectificationMeasure) {
		issuesRectificationMeasureService.save(issuesRectificationMeasure);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param issuesRectificationMeasure
	 * @return
	 */
	@AutoLog(value = "issues_rectification_measure-编辑")
	@ApiOperation(value="issues_rectification_measure-编辑", notes="issues_rectification_measure-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:issues_rectification_measure:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody IssuesRectificationMeasure issuesRectificationMeasure) {
		issuesRectificationMeasureService.updateById(issuesRectificationMeasure);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "issues_rectification_measure-通过id删除")
	@ApiOperation(value="issues_rectification_measure-通过id删除", notes="issues_rectification_measure-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:issues_rectification_measure:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		issuesRectificationMeasureService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "issues_rectification_measure-批量删除")
	@ApiOperation(value="issues_rectification_measure-批量删除", notes="issues_rectification_measure-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:issues_rectification_measure:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.issuesRectificationMeasureService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "issues_rectification_measure-通过id查询")
	@ApiOperation(value="issues_rectification_measure-通过id查询", notes="issues_rectification_measure-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IssuesRectificationMeasure> queryById(@RequestParam(name="id",required=true) String id) {
		IssuesRectificationMeasure issuesRectificationMeasure = issuesRectificationMeasureService.getById(id);
		if(issuesRectificationMeasure==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(issuesRectificationMeasure);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param issuesRectificationMeasure
    */
    //@RequiresPermissions("com.chd.modules.oversee:issues_rectification_measure:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, IssuesRectificationMeasure issuesRectificationMeasure) {
        return super.exportXls(request, issuesRectificationMeasure, IssuesRectificationMeasure.class, "issues_rectification_measure");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("issues_rectification_measure:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, IssuesRectificationMeasure.class);
    }

}
