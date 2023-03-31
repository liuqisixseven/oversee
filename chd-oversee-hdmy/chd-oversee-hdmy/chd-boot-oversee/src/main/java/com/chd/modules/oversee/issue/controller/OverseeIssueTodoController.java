package com.chd.modules.oversee.issue.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.CommonAPI;
import com.chd.common.api.vo.Result;
import com.chd.common.aspect.annotation.AutoLog;
import com.chd.common.system.base.controller.JeecgController;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.system.vo.DictModel;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.system.vo.SysUserCacheInfo;
import com.chd.common.util.BaseConstant;
import com.chd.common.util.DateUtils;
import com.chd.common.util.HTMLUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.hdmy.entity.MyUser;
import com.chd.modules.oversee.hdmy.service.IMyUserService;
import com.chd.modules.oversee.issue.entity.OverseeIssue;
import com.chd.modules.oversee.issue.entity.OverseeIssueTodo;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.IOverseeIssueTodoService;
import com.chd.modules.workflow.service.WorkflowTaskService;
import com.chd.modules.workflow.vo.WorkflowQueryVo;
import com.chd.modules.workflow.vo.WorkflowTaskVo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
* @Description: oversee_issue_todo
* @Author: jeecg-boot
* @Date:   2022-08-03
* @Version: V1.0
*/
@Api(tags="oversee_issue_todo")
@RestController
@RequestMapping("/issue/overseeIssueTodo")
@Slf4j
public class OverseeIssueTodoController extends JeecgController<OverseeIssueTodo, IOverseeIssueTodoService> {
   @Autowired
   private IOverseeIssueTodoService overseeIssueTodoService;

   @Autowired
   private IOverseeIssueService overseeIssueService;

   @Autowired
   private IMyUserService myUserService;

   @Autowired
   private CommonAPI commonAPI;

    @Value("${chd.hdmyDomainUrl}")
    private String hdmyDomainUrl;

    @Autowired
    private WorkflowTaskService workflowTaskService;

   private static String AUTHORIZED_LOGIN_KEY = "authorizedLogin";




    @ApiOperation(value="oversee_issue_todo-分页列表查询", notes="oversee_issue_todo-分页列表查询")
    @GetMapping(value = "/todolist4ajax")
    public String todolist4ajax(@RequestParam(name="callback") String callbackName,
                                                         @RequestParam(name="userId", defaultValue="") String userId,
                                                         @RequestParam(name="sessionId", defaultValue="") String sessionId,
                                                         @RequestParam(name="t", defaultValue="") String t,
                                                         HttpServletRequest req) {

        boolean isSendTodo = true;
        try {
            List<DictModel> isSendTodoList = commonAPI.queryDictItemsByCode(BaseConstant.IS_SEND_TODO_DICT_KEY);
            if(CollectionUtils.isNotEmpty(isSendTodoList)){
                for(DictModel dictModel : isSendTodoList){
                    if(StringUtil.isNotEmpty(dictModel.getTitle())&&dictModel.getTitle().equals(BaseConstant.IS_SEND_TODO_DICT_KEY)){
                        if(null!=dictModel.getValue()&&dictModel.getValue().trim().equals("0")){
                            isSendTodo = false;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        callbackName = StringUtil.isNotEmpty(callbackName)?callbackName:"";
        String todoListParameter = "";
        t = StringUtil.isNotEmpty(t)?t:new Date().getTime()+"";
        if(StringUtil.isNotEmpty(userId)){
            try {
                userId = URLDecoder.decode(userId,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            if(isSendTodo){
                IPage<WorkflowTaskVo> result= workflowTaskService.todoTaskList(new WorkflowQueryVo(1,200),userId,Maps.newHashMap());
                if(null!=result&&null!=result.getRecords()&&result.getRecords().size()>0){
                    List<WorkflowTaskVo> workflowTaskVoList = result.getRecords();
                    List<Map<String, Object>> maps = overseeIssueTodoListToListMap((overseeIssueTodoToOverseeIssueTodoList(workflowTaskVoList)),userId,sessionId,t);
                    if(null!=maps&&maps.size()>0){
//                    System.out.println("maps size : " + maps.size());
                        todoListParameter = JSONObject.toJSONString(maps);
                    }
                }
            }
        }


        String dataString = callbackName + "(";
        if(StringUtil.isNotEmpty(todoListParameter)){
            dataString+=todoListParameter;
        }
        dataString+=")";

        return dataString;
    }

    private List<OverseeIssueTodo> overseeIssueTodoToOverseeIssueTodoList(List<WorkflowTaskVo> workflowTaskVoList){
        List<OverseeIssueTodo> overseeIssueTodos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(workflowTaskVoList)){
            for(WorkflowTaskVo workflowTaskVo : workflowTaskVoList){
                try{
                    if(null!=workflowTaskVo){
                        OverseeIssueTodo overseeIssueTodo = new OverseeIssueTodo();
                        if(StringUtil.isNotEmpty(workflowTaskVo.getBizId())){
                            overseeIssueTodo.setIssueId(Long.parseLong(workflowTaskVo.getBizId()));
                        }
                        overseeIssueTodo.setTaskId(workflowTaskVo.getTaskId());
                        if(StringUtil.isNotEmpty(workflowTaskVo.getPreviousTaskUserEndTimes())){
                            overseeIssueTodo.setCreateTime(DateUtils.parseDate(workflowTaskVo.getPreviousTaskUserEndTimes(),DateUtils.FORMAT_DATETIME));
                        }
                        overseeIssueTodo.setPreviousTaskUserNames(workflowTaskVo.getPreviousTaskUserNames());
                        overseeIssueTodos.add(overseeIssueTodo);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return overseeIssueTodos;
    }




    private List<Map<String,Object>> overseeIssueTodoListToListMap(List<OverseeIssueTodo> overseeIssueTodoList,String userId,String sessionId,String t){
        List<Map<String,Object>> listMap = new ArrayList<>();
        if(null!=overseeIssueTodoList&&overseeIssueTodoList.size()>0){
            for(OverseeIssueTodo overseeIssueTodo : overseeIssueTodoList){
                try{
                    if(null!=overseeIssueTodo&&null!=overseeIssueTodo.getIssueId()){
                        Long issueId = overseeIssueTodo.getIssueId();

                        Map<String, Object> map = Maps.newHashMap();
                        map.put("sysId","report");
                        OverseeIssue redisCacheOverseeIssue = overseeIssueService.getRedisCacheOverseeIssue(issueId);
                        if(null!=redisCacheOverseeIssue&&null!=redisCacheOverseeIssue.getId()&&redisCacheOverseeIssue.getId().intValue()>0){
                            String dbName = "";
                            if(StringUtil.isNotEmpty(redisCacheOverseeIssue.getSpecificIssuesContent())){
                                dbName = HTMLUtils.getTitle(redisCacheOverseeIssue.getSpecificIssuesContent());
                            }
                            if(StringUtil.isEmpty(dbName)){
                                dbName = "巡视巡察系统 : " + redisCacheOverseeIssue.getNum();
                            }
                            map.put("dbName",dbName);
                        }
                        String previousUserName = null;
                        if(StringUtil.isNotEmpty(overseeIssueTodo.getPreviousUserIds())){
                            List<String> previousUserIdArray = Arrays.asList(overseeIssueTodo.getPreviousUserIds().split(","));
                            if(CollectionUtils.isNotEmpty(previousUserIdArray)){
                                String previousUserId = previousUserIdArray.get(0);
                                SysUserCacheInfo sysUserById = commonAPI.getSysUserById(previousUserId);
                                if(null!=sysUserById&&StringUtil.isEmpty(sysUserById.getSysUserName())){
                                    previousUserName = sysUserById.getSysUserName();
                                }else{
                                    MyUser myUserById = myUserService.getById(previousUserId);
                                    if(null!=myUserById){
                                        if(StringUtil.isNotEmpty(myUserById.getUserName())){
                                            previousUserName = myUserById.getUserName();
                                        }
                                    }
                                }
                            }
                        }
                        if(StringUtil.isEmpty(previousUserName)&&StringUtil.isNotEmpty(overseeIssueTodo.getUserId())){
                            SysUserCacheInfo sysUserById = commonAPI.getSysUserById(overseeIssueTodo.getUserId());
                            if(null!=sysUserById&&StringUtil.isEmpty(sysUserById.getSysUserName())){
                                previousUserName = sysUserById.getSysUserName();
                            }else{
                                MyUser myUserById = myUserService.getById(overseeIssueTodo.getUserId());
                                if(null!=myUserById){
                                    if(StringUtil.isNotEmpty(myUserById.getUserName())){
                                        previousUserName = myUserById.getUserName();
                                    }
                                }
                            }
                        }
                        if(StringUtil.isNotEmpty(overseeIssueTodo.getPreviousTaskUserNames())){
                            previousUserName = overseeIssueTodo.getPreviousTaskUserNames();
                        }
                        if(StringUtil.isEmpty(previousUserName)){
                            previousUserName = overseeIssueTodo.getUserId();
                        }
                        map.put("upMan",previousUserName);
                        String upDateTime = "";
                        if(null!=overseeIssueTodo.getCreateTime()){
                            upDateTime = DateUtils.formatDate(overseeIssueTodo.getCreateTime(),"yyyy年MM月dd日");
                        }else{
                            upDateTime = DateUtils.formatDate(new Date(),"yyyy年MM月dd日");
                        }
                        map.put("upDateTime", upDateTime);
                        map.put("queryUrl", hdmyDomainUrl + File.separator + AUTHORIZED_LOGIN_KEY + "?overseeIssueTodoId="+(null!=overseeIssueTodo.getId()?overseeIssueTodo.getId():"")+"&toType=1&userId="+userId+"&sessionId="+sessionId+"&t="+t);

                        listMap.add(map);


                        if(null!=overseeIssueTodo.getId()&&overseeIssueTodo.getId().intValue()>0){
                            OverseeIssueTodo overseeIssueTodoU = new OverseeIssueTodo();
                            overseeIssueTodoU.setId(overseeIssueTodo.getId());
                            overseeIssueTodoU.setSendStatus(1);
                            overseeIssueTodoU.setUpdateTime(new Date());
                            overseeIssueTodoService.updateById(overseeIssueTodoU);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return listMap;
    }

    @ApiOperation(value="oversee_issue_todo-分页列表查询", notes="oversee_issue_todo-分页列表查询")
    @GetMapping(value = "/todolist4ajaxOld")
    public String todolist4ajaxOld(@RequestParam(name="callback") String callbackName,
                                   @RequestParam(name="userId", defaultValue="") String userId,
                                   @RequestParam(name="sessionId", defaultValue="") String sessionId,
                                   @RequestParam(name="t", defaultValue="") String t,
                                   HttpServletRequest req) {
        callbackName = StringUtil.isNotEmpty(callbackName)?callbackName:"";
        String todoListParameter = "";
        t = StringUtil.isNotEmpty(t)?t:new Date().getTime()+"";
        if(StringUtil.isNotEmpty(userId)){
            try {
                userId = URLDecoder.decode(userId,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
//            List<OverseeIssueTodo> overseeIssueTodoList = overseeIssueTodoService.list(Wrappers.<OverseeIssueTodo>lambdaQuery().eq(OverseeIssueTodo::getUserId, userId).ne(OverseeIssueTodo::getSendStatus,4).eq(OverseeIssueTodo::getDataType, 1));
            Map<String, Object> selectMap = Maps.newHashMap();
            selectMap.put("userId",userId);
            selectMap.put("sendStatusNot",4);
//            selectMap.put("isGroupByIssueId",1);
            List<OverseeIssueTodo> overseeIssueTodoList = overseeIssueTodoService.selectOverseeIssueTodoList(selectMap);
            List<Map<String, Object>> maps = overseeIssueTodoListToListMap(overseeIssueTodoList,userId,sessionId,t);
            if(null!=maps&&maps.size()>0){
                todoListParameter = JSONObject.toJSONString(maps);
            }
        }

//        todoListParameter = "";
//        if("qingli pei".equals(userId)||"kaixin chang".equals(userId)){
//            todoListParameter="[{\"upDateTime\":\"2022年10月24日\",\"queryUrl\":\""+hdmyDomainUrl+"/authorizedLogin?overseeIssueTodoId=12&toType=1&userId="+userId+"&sessionId="+sessionId+"&t="+t+"\",\"sysId\":\"report\",\"dbName\":\"测试消息发送\",\"upMan\":\"likun yao\"},{\"upDateTime\":\"2022年10月24日\",\"queryUrl\":\""+hdmyDomainUrl+"/authorizedLogin?overseeIssueTodoId=15&toType=1&userId="+userId+"&sessionId="+sessionId+"&t="+t+"\",\"sysId\":\"report\",\"dbName\":\"批量上报3\",\"upMan\":\"likun yao\"}]";
//        }

        String dataString = callbackName + "(";
        if(StringUtil.isNotEmpty(todoListParameter)){
            dataString+=todoListParameter;
        }
        dataString+=")";

        return dataString;
    }

   /**
    * 分页列表查询
    *
    * @param overseeIssueTodo
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "oversee_issue_todo-分页列表查询")
   @ApiOperation(value="oversee_issue_todo-分页列表查询", notes="oversee_issue_todo-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<OverseeIssueTodo>> queryPageList(OverseeIssueTodo overseeIssueTodo,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                        HttpServletRequest req) {
       QueryWrapper<OverseeIssueTodo> queryWrapper = QueryGenerator.initQueryWrapper(overseeIssueTodo, req.getParameterMap());
       Page<OverseeIssueTodo> page = new Page<OverseeIssueTodo>(pageNo, pageSize);
       IPage<OverseeIssueTodo> pageList = overseeIssueTodoService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param overseeIssueTodo
    * @return
    */
   @AutoLog(value = "oversee_issue_todo-添加")
   @ApiOperation(value="oversee_issue_todo-添加", notes="oversee_issue_todo-添加")
   //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_todo:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody OverseeIssueTodo overseeIssueTodo) {
       overseeIssueTodoService.save(overseeIssueTodo);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param overseeIssueTodo
    * @return
    */
   @AutoLog(value = "oversee_issue_todo-编辑")
   @ApiOperation(value="oversee_issue_todo-编辑", notes="oversee_issue_todo-编辑")
   //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_todo:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody OverseeIssueTodo overseeIssueTodo) {
       overseeIssueTodoService.updateById(overseeIssueTodo);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "oversee_issue_todo-通过id删除")
   @ApiOperation(value="oversee_issue_todo-通过id删除", notes="oversee_issue_todo-通过id删除")
   //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_todo:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       overseeIssueTodoService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "oversee_issue_todo-批量删除")
   @ApiOperation(value="oversee_issue_todo-批量删除", notes="oversee_issue_todo-批量删除")
   //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_todo:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.overseeIssueTodoService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "oversee_issue_todo-通过id查询")
   @ApiOperation(value="oversee_issue_todo-通过id查询", notes="oversee_issue_todo-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<OverseeIssueTodo> queryById(@RequestParam(name="id",required=true) String id) {
       OverseeIssueTodo overseeIssueTodo = overseeIssueTodoService.getById(id);
       if(overseeIssueTodo==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(overseeIssueTodo);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param overseeIssueTodo
   */
   //@RequiresPermissions("com.chd.modules.oversee:oversee_issue_todo:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, OverseeIssueTodo overseeIssueTodo) {
       return super.exportXls(request, overseeIssueTodo, OverseeIssueTodo.class, "oversee_issue_todo");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   //@RequiresPermissions("oversee_issue_todo:importExcel")
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return super.importExcel(request, response, OverseeIssueTodo.class);
   }

}
