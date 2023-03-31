package com.chd.modules.oversee.issue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.query.QueryGenerator;
import com.chd.modules.oversee.issue.entity.RectifyViolations;
import com.chd.modules.oversee.issue.service.IRectifyViolationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: rectify_violations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Api(tags="rectify_violations")
@RestController
@RequestMapping("/issue/rectifyViolations")
@Slf4j
public class RectifyViolationsController extends JeecgController<RectifyViolations, IRectifyViolationsService> {
	@Autowired
	private IRectifyViolationsService rectifyViolationsService;

	/**
	 * 分页列表查询
	 *
	 * @param rectifyViolations
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "rectify_violations-分页列表查询")
	@ApiOperation(value="rectify_violations-分页列表查询", notes="rectify_violations-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RectifyViolations>> queryPageList(RectifyViolations rectifyViolations,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RectifyViolations> queryWrapper = QueryGenerator.initQueryWrapper(rectifyViolations, req.getParameterMap());
		Page<RectifyViolations> page = new Page<RectifyViolations>(pageNo, pageSize);
		IPage<RectifyViolations> pageList = rectifyViolationsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param rectifyViolations
	 * @return
	 */
	@AutoLog(value = "rectify_violations-添加")
	@ApiOperation(value="rectify_violations-添加", notes="rectify_violations-添加")
	//@RequiresPermissions("org.jeecg.modules.oversee:rectify_violations:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RectifyViolations rectifyViolations) {
		rectifyViolationsService.save(rectifyViolations);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param rectifyViolations
	 * @return
	 */
	@AutoLog(value = "rectify_violations-编辑")
	@ApiOperation(value="rectify_violations-编辑", notes="rectify_violations-编辑")
	//@RequiresPermissions("org.jeecg.modules.oversee:rectify_violations:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RectifyViolations rectifyViolations) {
		rectifyViolationsService.updateById(rectifyViolations);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "rectify_violations-通过id删除")
	@ApiOperation(value="rectify_violations-通过id删除", notes="rectify_violations-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.oversee:rectify_violations:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		rectifyViolationsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "rectify_violations-批量删除")
	@ApiOperation(value="rectify_violations-批量删除", notes="rectify_violations-批量删除")
	//@RequiresPermissions("org.jeecg.modules.oversee:rectify_violations:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.rectifyViolationsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "rectify_violations-通过id查询")
	@ApiOperation(value="rectify_violations-通过id查询", notes="rectify_violations-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RectifyViolations> queryById(@RequestParam(name="id",required=true) String id) {
		RectifyViolations rectifyViolations = rectifyViolationsService.getById(id);
		if(rectifyViolations==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(rectifyViolations);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param rectifyViolations
    */
    //@RequiresPermissions("org.jeecg.modules.oversee:rectify_violations:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RectifyViolations rectifyViolations) {
        return super.exportXls(request, rectifyViolations, RectifyViolations.class, "rectify_violations");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("rectify_violations:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, RectifyViolations.class);
    }

}
