package com.chd.modules.oversee.hdmy.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.HttpClientUtil;
import com.chd.modules.oversee.hdmy.entity.LoginVo;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.result.GuliException;
import com.chd.modules.oversee.hdmy.result.R;
import com.chd.modules.oversee.hdmy.result.ResultCodeEnum;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.hdmy.service.IOverseeWorkMoveService;
import com.chd.modules.oversee.hdmy.util.JwtInfo;
import com.chd.modules.oversee.hdmy.util.JwtUtils;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import com.chd.modules.workflow.service.WorkflowTaskService;
import com.chd.modules.workflow.vo.WorkflowQueryVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

 /**
 * @Description: my_user
 * @Author: jeecg-boot
 * @Date:   2022-08-08
 * @Version: V1.0
 */
@Api(tags="华电煤业用户管理")
@RestController
@RequestMapping("/hdmy/myUser")
@Slf4j
public class MyUserController extends JeecgController<MyUser, IMyUserService> {
	@Autowired
	private IMyUserService myUserService;
	 @Autowired
	 private IOverseeWorkMoveService iOverseeWorkMoveService;
	/**
	 * 分页列表查询
	 *
	 * @param myUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "my_user-分页列表查询")
	@ApiOperation(value="my_user-分页列表查询", notes="my_user-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<MyUser>> queryPageList(MyUser myUser,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<MyUser> queryWrapper = QueryGenerator.initQueryWrapper(myUser, req.getParameterMap());
		Page<MyUser> page = new Page<MyUser>(pageNo, pageSize);
		IPage<MyUser> pageList = myUserService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param myUser
	 * @return
	 */
	@AutoLog(value = "my_user-添加")
	@ApiOperation(value="my_user-添加", notes="my_user-添加")
	//@RequiresPermissions("com.chd.modules.oversee:my_user:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody MyUser myUser) {
		myUserService.save(myUser);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param myUser
	 * @return
	 */
	@AutoLog(value = "my_user-编辑")
	@ApiOperation(value="my_user-编辑", notes="my_user-编辑")
	//@RequiresPermissions("com.chd.modules.oversee:my_user:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody MyUser myUser) {
		myUserService.updateById(myUser);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "my_user-通过id删除")
	@ApiOperation(value="my_user-通过id删除", notes="my_user-通过id删除")
	//@RequiresPermissions("com.chd.modules.oversee:my_user:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		myUserService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "my_user-批量删除")
	@ApiOperation(value="my_user-批量删除", notes="my_user-批量删除")
	//@RequiresPermissions("com.chd.modules.oversee:my_user:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.myUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "my_user-通过id查询")
	@ApiOperation(value="通过id查询用户", notes="通过id查询")
	@GetMapping(value = "/queryById")
	public Result<MyUser> queryById(@RequestParam(name="id",required=true) String id) {
		MyUser myUser = myUserService.getById(id);
		if(myUser==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(myUser);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param myUser
    */
    //@RequiresPermissions("com.chd.modules.oversee:my_user:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MyUser myUser) {
        return super.exportXls(request, myUser, MyUser.class, "my_user");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("my_user:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, MyUser.class);
    }

	 @ApiOperation(value = "根据部门id获取用户列表")
	 @PostMapping("/listUserByOrgId")
	 public R listUserByOrgId(@RequestBody Map<String,Object> paramsMap) {
		 String orgId = null;
		 if(null!=paramsMap&&(StringUtil.isNotEmpty((String) paramsMap.get("orgId")))){
			 orgId = (String) paramsMap.get("orgId");
		 }else{
			 return R.error().message("请求参数错误");
		 }
		 List<Map<String,String>> result = myUserService.listUserByOrgId(paramsMap);
		 return R.ok().data("userInfo",result);
	 }

	 @ApiOperation(value = "根据部门id获取用户列表")
	 @PostMapping("/userListByOrgId")
	 public Result<Map<String,Object>> userListByOrgId(@RequestBody Map<String,Object> paramsMap) {
		 String orgId = null;
		 if(null!=paramsMap&&(StringUtil.isNotEmpty((String) paramsMap.get("orgId")))){
			 orgId = (String) paramsMap.get("orgId");
		 }else{
			 return Result.error("请求参数错误");
		 }
		 List<Map<String,String>> result = myUserService.listUserByOrgId(paramsMap);
		 return null;
	 }

	 @ApiOperation(value = "用户登录")
	 @PostMapping("/login")
	 public R login(@RequestBody LoginVo loginVo,HttpServletRequest request){
		 String token = myUserService.login(loginVo,request);
		 return R.ok().data("token", token).message("登录成功");
	 }

	 @ApiOperation(value = "根据token获取登录信息")
	 @GetMapping("/get-login-info")
	 public R getLoginInfo(HttpServletRequest request){
		 try {
			 JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
			 return R.ok().data("userInfo", jwtInfo);
		 } catch (Exception e) {
			 log.error("解析用户信息失败：" + e.getMessage());
			 throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
		 }
	 }

	 @ApiOperation(value = "根据userid获取登录信息")
	 @GetMapping("/get-login-infoUserid")
	 public R getLoginInfoUserid(HttpServletRequest request){
		 String url = request.getHeader("url");
		 String map = HttpClientUtil.HttpGet(url);
		 if (StringUtils.isEmpty(map)){
//            return loginVo;
		 }
		 JSONObject json = JSONObject.parseObject(map);  //接收到所有传过来的json数据
		 Boolean success = json.getBoolean("success");  //直接json转Boolean类型，用Boolean类型的success接收叫做success的数据
		 String  data = json.getString("data");     //用String接收json数据中的data数据
		 System.out.println(data);
		 JSONObject dataj = JSONObject.parseObject(data);  //再将data转为单独的json数据
		 String user_id = dataj.getString("idcode");  //再次将data转为String数据并接收
		 MyUser myUser = myUserService.getById(user_id);
		 return R.ok().data("userInfo",myUser);
	 }

	 @ApiOperation(value = "华电煤业用户工作交接")
	 @GetMapping("/userWorkMove")
	 @ApiImplicitParams(value = {
			 @ApiImplicitParam(name = "fromUserId",value = "交接人",dataType = "String",required = true),
			 @ApiImplicitParam(name = "toUserId",value = "被交接人",dataType = "String",required = true)
	 })
	 public R setUserWorkMove(String fromUserId,String toUserId){
		 iOverseeWorkMoveService.workMove(fromUserId,toUserId,false);
		 return R.ok().message("工作交接成功");
	 }

 }
