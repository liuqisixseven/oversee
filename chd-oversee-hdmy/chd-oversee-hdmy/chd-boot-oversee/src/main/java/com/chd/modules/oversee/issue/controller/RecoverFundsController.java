package com.chd.modules.oversee.issue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.query.QueryGenerator;
import com.chd.modules.oversee.issue.entity.RecoverFunds;
import com.chd.modules.oversee.issue.service.IRecoverFundsService;
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
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Api(tags="recover_funds")
@RestController
@RequestMapping("/issue/recoverFunds")
@Slf4j
public class RecoverFundsController extends JeecgController<RecoverFunds, IRecoverFundsService> {
	@Autowired
	private IRecoverFundsService recoverFundsService;

	/**
	 * 分页列表查询
	 *
	 * @param recoverFunds
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "recover_funds-分页列表查询")
	@ApiOperation(value="recover_funds-分页列表查询", notes="recover_funds-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RecoverFunds>> queryPageList(RecoverFunds recoverFunds,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RecoverFunds> queryWrapper = QueryGenerator.initQueryWrapper(recoverFunds, req.getParameterMap());
		Page<RecoverFunds> page = new Page<RecoverFunds>(pageNo, pageSize);
		IPage<RecoverFunds> pageList = recoverFundsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param recoverFunds
	 * @return
	 */
	@AutoLog(value = "recover_funds-添加")
	@ApiOperation(value="recover_funds-添加", notes="recover_funds-添加")
	//@RequiresPermissions("org.jeecg.modules.oversee:recover_funds:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RecoverFunds recoverFunds) {
		recoverFundsService.save(recoverFunds);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param recoverFunds
	 * @return
	 */
	@AutoLog(value = "recover_funds-编辑")
	@ApiOperation(value="recover_funds-编辑", notes="recover_funds-编辑")
	//@RequiresPermissions("org.jeecg.modules.oversee:recover_funds:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RecoverFunds recoverFunds) {
		recoverFundsService.updateById(recoverFunds);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "recover_funds-通过id删除")
	@ApiOperation(value="recover_funds-通过id删除", notes="recover_funds-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.oversee:recover_funds:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		recoverFundsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "recover_funds-批量删除")
	@ApiOperation(value="recover_funds-批量删除", notes="recover_funds-批量删除")
	//@RequiresPermissions("org.jeecg.modules.oversee:recover_funds:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.recoverFundsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "recover_funds-通过id查询")
	@ApiOperation(value="recover_funds-通过id查询", notes="recover_funds-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RecoverFunds> queryById(@RequestParam(name="id",required=true) String id) {
		RecoverFunds recoverFunds = recoverFundsService.getById(id);
		if(recoverFunds==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(recoverFunds);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param recoverFunds
    */
    //@RequiresPermissions("org.jeecg.modules.oversee:recover_funds:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RecoverFunds recoverFunds) {
        return super.exportXls(request, recoverFunds, RecoverFunds.class, "recover_funds");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("recover_funds:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, RecoverFunds.class);
    }

}
