package com.chd.modules.oversee.issue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.query.QueryGenerator;
import com.chd.modules.oversee.issue.entity.ImproveRegulations;
import com.chd.modules.oversee.issue.service.IImproveRegulationsService;
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
 * @Description: improve_regulations
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Api(tags="improve_regulations")
@RestController
@RequestMapping("/issue/improveRegulations")
@Slf4j
public class ImproveRegulationsController extends JeecgController<ImproveRegulations, IImproveRegulationsService> {
	@Autowired
	private IImproveRegulationsService improveRegulationsService;

	/**
	 * 分页列表查询
	 *
	 * @param improveRegulations
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "improve_regulations-分页列表查询")
	@ApiOperation(value="improve_regulations-分页列表查询", notes="improve_regulations-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImproveRegulations>> queryPageList(ImproveRegulations improveRegulations,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ImproveRegulations> queryWrapper = QueryGenerator.initQueryWrapper(improveRegulations, req.getParameterMap());
		Page<ImproveRegulations> page = new Page<ImproveRegulations>(pageNo, pageSize);
		IPage<ImproveRegulations> pageList = improveRegulationsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param improveRegulations
	 * @return
	 */
	@AutoLog(value = "improve_regulations-添加")
	@ApiOperation(value="improve_regulations-添加", notes="improve_regulations-添加")
	//@RequiresPermissions("org.jeecg.modules.oversee:improve_regulations:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ImproveRegulations improveRegulations) {
		improveRegulationsService.save(improveRegulations);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param improveRegulations
	 * @return
	 */
	@AutoLog(value = "improve_regulations-编辑")
	@ApiOperation(value="improve_regulations-编辑", notes="improve_regulations-编辑")
	//@RequiresPermissions("org.jeecg.modules.oversee:improve_regulations:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ImproveRegulations improveRegulations) {
		improveRegulationsService.updateById(improveRegulations);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "improve_regulations-通过id删除")
	@ApiOperation(value="improve_regulations-通过id删除", notes="improve_regulations-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.oversee:improve_regulations:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		improveRegulationsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "improve_regulations-批量删除")
	@ApiOperation(value="improve_regulations-批量删除", notes="improve_regulations-批量删除")
	//@RequiresPermissions("org.jeecg.modules.oversee:improve_regulations:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.improveRegulationsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "improve_regulations-通过id查询")
	@ApiOperation(value="improve_regulations-通过id查询", notes="improve_regulations-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ImproveRegulations> queryById(@RequestParam(name="id",required=true) String id) {
		ImproveRegulations improveRegulations = improveRegulationsService.getById(id);
		if(improveRegulations==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(improveRegulations);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param improveRegulations
    */
    //@RequiresPermissions("org.jeecg.modules.oversee:improve_regulations:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ImproveRegulations improveRegulations) {
        return super.exportXls(request, improveRegulations, ImproveRegulations.class, "improve_regulations");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("improve_regulations:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ImproveRegulations.class);
    }

}
