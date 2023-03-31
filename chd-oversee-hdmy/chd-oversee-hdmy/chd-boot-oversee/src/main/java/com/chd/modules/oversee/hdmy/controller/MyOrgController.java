package com.chd.modules.oversee.hdmy.controller;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;

import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.entity.MyOrgSettings;
import com.chd.modules.oversee.hdmy.service.IMyOrgService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.modules.oversee.hdmy.service.IMyOrgSettingsService;
import com.chd.modules.oversee.issue.base.IssueBaseController;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueQueryVo;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.chd.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.chd.common.aspect.annotation.AutoLog;

 /**
 * @Description: my_org
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Api(tags="组织架构管理")
@RestController
@RequestMapping("/hdmy/myOrg")
@Slf4j
public class MyOrgController extends IssueBaseController<MyOrg, IMyOrgService> {

	@Autowired
	private IMyOrgService myOrgService;

	 @Autowired
	 private IMyOrgSettingsService myOrgSettingsService;

	 @Autowired
	 private IOverseeIssueService overseeIssueService;

	 @Autowired
	 public RedisTemplate<String, Object> redisTemplate;

	/**
	 * 分页列表查询
	 *
	 * @param myOrg
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "my_org-分页列表查询")
	@ApiOperation(value="my_org-分页列表查询", notes="my_org-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<MyOrg>> queryPageList(MyOrg myOrg,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<MyOrg> queryWrapper = QueryGenerator.initQueryWrapper(myOrg, req.getParameterMap());
		Page<MyOrg> page = new Page<MyOrg>(pageNo, pageSize);
		IPage<MyOrg> pageList = myOrgService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	 @ApiOperation(value="my_org-公司查询", notes="my_org-公司查询")
	 @GetMapping(value = "/getOrganizeCompanyList")
	 public Result<List<MyOrg>> getOrganizeCompanyList(MyOrg myOrg, HttpServletRequest req) {
		 try {

			 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			 Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");

			 LambdaQueryWrapper<MyOrg> myOrgWrapper = Wrappers.<MyOrg>lambdaQuery();
			 if(null!=myOrg){
				 if(StringUtil.isNotEmpty(myOrg.getOrgId())){
					 myOrgWrapper.eq(MyOrg::getOrgId,myOrg.getOrgId());
				 }
				 if(null!=myOrg.getOrgLevel()){
					 myOrgWrapper.eq(MyOrg::getOrgLevel,myOrg.getOrgLevel());
				 }
				 if(StringUtil.isNotEmpty(myOrg.getOrgName())){
					 myOrgWrapper.like(MyOrg::getOrgShortName,"%"+myOrg.getOrgName()+"%");
				 }
			 }
			 List<String> responsibleUnitOrgIdList = myOrgService.getResponsibleUnitOrgIdList();
			 myOrgSettingsService.addOrUpdateByResponsibleUnitOrgIdList(responsibleUnitOrgIdList);

			 if(CollectionUtils.isNotEmpty(responsibleUnitOrgIdList)){
				 myOrgWrapper.in(MyOrg::getOrgId,responsibleUnitOrgIdList);
			 }

			 Map<String, Object> overseeIssueQueryVoMapData = getOverseeIssueQueryVoMapData(new OverseeIssueQueryVo());
			 Boolean isAll = (Boolean) overseeIssueQueryVoMapData.get("isAll");
			 if(null==isAll)isAll = false;
			 if(((isSystemRole()||isAll)?1:-1)!=1){
				 if(StringUtil.isNotEmpty(sysUser.getHdmyOrgId())){
					 MyOrg myOrgById = myOrgService.getOrganizeCompany(sysUser.getHdmyOrgId());
					 if(null!=myOrgById){
						 if(!(StringUtil.isNotEmpty(myOrgById.getOrgId())&&myOrgById.getOrgId().equals(BaseConstant.HEADQUARTERS_ORG_ID))){
							 myOrgWrapper.eq(MyOrg::getOrgId,myOrgById.getOrgId());
						 }else{
							 //                            华电煤业本部所有用户都可以看到所有部门的数据
						 }

					 }
				 }
			 }

			 List<MyOrg> list = myOrgService.list(myOrgWrapper);
			 if(CollectionUtils.isNotEmpty(list)){
				 List<MyOrg> newList = new ArrayList<>();
				 Map<String, Object> selectMap = Maps.newHashMap();
				 selectMap.put("idArray",list.stream().map((myOrgS)->myOrgS.getOrgId()).collect(Collectors.toList()));
				 List<MyOrgSettings> myOrgSettingsList = myOrgSettingsService.selectMyOrgSettingsList(selectMap);
				 if(CollectionUtils.isNotEmpty(myOrgSettingsList)){
					 for(MyOrg myOrgS : list){
						 int isShow = 1;
						 int sort = 100;
						 for(MyOrgSettings myOrgSettings : myOrgSettingsList){
							 if (null != myOrgSettings && StringUtil.isNotEmpty(myOrgSettings.getOrgId()) && myOrgSettings.getOrgId().equals(myOrgS.getOrgId())) {
								 if(null!=myOrgSettings.getIsShow()){
									 isShow = myOrgSettings.getIsShow();
								 }
								 if(null!=myOrgSettings.getSort()){
									 sort = myOrgSettings.getSort();
								 }
							 }
						 }
						 if(isShow == 1){
							 myOrgS.setSort(sort);
							 newList.add(myOrgS);
						 }
					 }
				 }
				 newList.sort((MyOrg o1, MyOrg o2)->o1.getSort().compareTo(o2.getSort()));
				 list = newList;
			 }

			 return Result.OK(list);
		 } catch (IllegalArgumentException e) {
			 log.error(e.getMessage(), e);
			 return Result.error(e.getMessage());
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 return Result.error("异常");
		 }

	 }




	/**
	 *   添加
	 *
	 * @param myOrg
	 * @return
	 */
	@AutoLog(value = "my_org-添加")
	@ApiOperation(value="my_org-添加", notes="my_org-添加")
	//@RequiresPermissions("com.chd.modules.oversee:my_org:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody MyOrg myOrg) {
		myOrgService.save(myOrg);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param myOrg
	 * @return
	 */
	@AutoLog(value = "my_org-编辑")
	@ApiOperation(value="my_org-编辑", notes="my_org-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:my_org:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody MyOrg myOrg) {
		myOrgService.updateById(myOrg);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "my_org-通过id删除")
	@ApiOperation(value="my_org-通过id删除", notes="my_org-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:my_org:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		myOrgService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "my_org-批量删除")
	@ApiOperation(value="my_org-批量删除", notes="my_org-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:my_org:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.myOrgService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "my_org-通过id查询")
	@ApiOperation(value="my_org-通过id查询", notes="my_org-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<MyOrg> queryById(@RequestParam(name="id",required=true) String id) {
		MyOrg myOrg = myOrgService.getById(id);
		if(myOrg==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(myOrg);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param myOrg
    */
    //@RequiresPermissions("com.chd.modules.oversee:my_org:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MyOrg myOrg) {
        return super.exportXls(request, myOrg, MyOrg.class, "my_org");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("my_org:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, MyOrg.class);
    }

}
