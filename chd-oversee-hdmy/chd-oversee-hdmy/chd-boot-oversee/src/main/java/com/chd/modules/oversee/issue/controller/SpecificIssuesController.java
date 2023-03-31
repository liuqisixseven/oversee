package com.chd.modules.oversee.issue.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;

import com.chd.modules.oversee.issue.entity.SpecificIssues;
import com.chd.modules.oversee.issue.service.ISpecificIssuesService;

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
 * @Description: specific_issues
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
@Api(tags="specific_issues")
@RestController
@RequestMapping("/issue/specificIssues")
@Slf4j
public class SpecificIssuesController extends JeecgController<SpecificIssues, ISpecificIssuesService> {
	@Autowired
	private ISpecificIssuesService specificIssuesService;

	/**
	 * 分页列表查询
	 *
	 * @param specificIssues
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "specific_issues-分页列表查询")
	@ApiOperation(value="specific_issues-分页列表查询", notes="specific_issues-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SpecificIssues>> queryPageList(SpecificIssues specificIssues,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SpecificIssues> queryWrapper = QueryGenerator.initQueryWrapper(specificIssues, req.getParameterMap());
		Page<SpecificIssues> page = new Page<SpecificIssues>(pageNo, pageSize);
		IPage<SpecificIssues> pageList = specificIssuesService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param specificIssues
	 * @return
	 */
	@AutoLog(value = "specific_issues-添加")
	@ApiOperation(value="specific_issues-添加", notes="specific_issues-添加")
	//@RequiresPermissions("com.chd.modules.oversee:specific_issues:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SpecificIssues specificIssues) {
		specificIssuesService.save(specificIssues);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param specificIssues
	 * @return
	 */
	@AutoLog(value = "specific_issues-编辑")
	@ApiOperation(value="specific_issues-编辑", notes="specific_issues-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:specific_issues:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SpecificIssues specificIssues) {
		specificIssuesService.updateById(specificIssues);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "specific_issues-通过id删除")
	@ApiOperation(value="specific_issues-通过id删除", notes="specific_issues-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:specific_issues:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		specificIssuesService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "specific_issues-批量删除")
	@ApiOperation(value="specific_issues-批量删除", notes="specific_issues-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:specific_issues:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.specificIssuesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "specific_issues-通过id查询")
	@ApiOperation(value="specific_issues-通过id查询", notes="specific_issues-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SpecificIssues> queryById(@RequestParam(name="id",required=true) String id) {
		SpecificIssues specificIssues = specificIssuesService.getById(id);
		if(specificIssues==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(specificIssues);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param specificIssues
    */
    //@RequiresPermissions("com.chd.modules.oversee:specific_issues:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SpecificIssues specificIssues) {
        return super.exportXls(request, specificIssues, SpecificIssues.class, "specific_issues");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("specific_issues:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SpecificIssues.class);
    }

}
