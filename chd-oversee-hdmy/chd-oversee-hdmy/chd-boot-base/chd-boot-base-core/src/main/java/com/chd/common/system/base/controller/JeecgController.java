package com.chd.common.system.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.common.api.CommonAPI;
import com.chd.common.constant.CommonConstant;
import com.chd.common.exception.ServiceException;
import com.chd.common.util.RequestUtils;
import com.chd.common.util.StringUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import com.chd.common.api.vo.Result;
import com.chd.common.system.query.QueryGenerator;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: Controller基类
 * @Author: dangzhenghui@163.com
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public class JeecgController<T, S extends IService<T>> {
    /**issues/2933 JeecgController注入service时改用protected修饰，能避免重复引用service*/
    @Autowired
    protected S service;

    @Value("${chd.path.upload}")
    protected String upLoadPath;

    @Autowired
    protected CommonAPI commonAPI;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;


    protected boolean isSystemRole(){
        boolean isSystemRole = false;
        try{
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Assert.isTrue((sysUser!=null&& StringUtil.isNotEmpty(sysUser.getId())),"未获取到登录信息");
            Set<String> roles = getUserRoles(sysUser.getUsername());
            if(null!=roles&&roles.size()>0){
                if(roles.stream().anyMatch((role)->role.equals(CommonConstant.DEFAULT_SYS_ROLE_CODE))){
                    isSystemRole = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isSystemRole;
    }

    protected Set<String> getUserRoles(String userName){
        Set<String> roles = null;
        try{
            if(StringUtil.isNotEmpty(userName)){
                String redisKey = CommonConstant.USERNAME_ROLES_PREFIX + userName;
                roles = (Set<String>) redisTemplate.opsForValue().get(redisKey);
                if(null==roles||roles.size()<=0){
                    roles = commonAPI.queryUserRoles(userName);
                    if(null!=roles){
                        redisTemplate.opsForValue().set(redisKey,roles, Duration.ofDays(1));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return roles;
    }


    /**
     * 导出excel
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, T object, Class<T> clazz, String title) {
        // Step.1 组装查询条件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id",selectionList);
        }
        if(null!=clazz){
            if(hasField(clazz,"dataType")){
                queryWrapper.eq("data_type",1);
            }
        }
        // Step.2 获取导出数据
        List<T> exportList = service.list(queryWrapper);

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
        ExportParams  exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title);
        exportParams.setImageBasePath(upLoadPath);
        //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    public boolean hasField(Class c, String fieldName){
        if(null!=c){
            Field[] fields = c.getDeclaredFields();
            if(null!=fields&&fields.length>0){
                for (Field f : fields) {
                    if (fieldName.equals(f.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 导出excel
     *
     * @param request
     */
    protected List<T> exportXlsReturnDataList(HttpServletRequest request, T object) {
        // Step.1 组装查询条件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id",selectionList);
        }
        // Step.2 获取导出数据
        List<T> exportList = service.list(queryWrapper);
        return exportList;
    }

    /**
     * 根据每页sheet数量导出多sheet
     *
     * @param request
     * @param object 实体类
     * @param clazz 实体类class
     * @param title 标题
     * @param exportFields 导出字段自定义
     * @param pageNum 每个sheet的数据条数
     * @param request
     */
    protected ModelAndView exportXlsSheet(HttpServletRequest request, T object, Class<T> clazz, String title,String exportFields,Integer pageNum) {
        // Step.1 组装查询条件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // Step.2 计算分页sheet数据
        double total = service.count();
        int count = (int)Math.ceil(total/pageNum);
        //update-begin-author:liusq---date:20220629--for: 多sheet导出根据选择导出写法调整 ---
        // Step.3  过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id",selectionList);
        }
        //update-end-author:liusq---date:20220629--for: 多sheet导出根据选择导出写法调整 ---
        // Step.4 多sheet处理
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <=count ; i++) {
            Page<T> page = new Page<T>(i, pageNum);
            IPage<T> pageList = service.page(page, queryWrapper);
            List<T> exportList = pageList.getRecords();
            Map<String, Object> map = new HashMap<>(5);
            ExportParams  exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title+i,upLoadPath);
            exportParams.setType(ExcelType.XSSF);
            //map.put("title",exportParams);
            //表格Title
            map.put(NormalExcelConstants.PARAMS,exportParams);
            //表格对应实体
            map.put(NormalExcelConstants.CLASS,clazz);
            //数据集合
            map.put(NormalExcelConstants.DATA_LIST, exportList);
            listMap.add(map);
        }
        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.MAP_LIST, listMap);
        return mv;
    }


    /**
     * 根据权限导出excel，传入导出字段参数
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, T object, Class<T> clazz, String title,String exportFields) {
        ModelAndView mv = this.exportXls(request,object,clazz,title);
        mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportFields);
        return mv;
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    protected String getId(T item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected List<T> getUploadExcelData(HttpServletRequest request, Class<T> clazz,ImportParams params) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        List<T> result=new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            try {
                List<T> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                if (CollectionUtils.isNotEmpty(list)) {
                    result.addAll(list);
                }
            }catch (Exception ex){
                throw new ServiceException(ex);
            }
            finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                   log.error("导入Excel获取数据异常",e);
                }
            }
        }
        return result;
    }
    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<T> clazz) {

//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
//        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
//            // 获取上传文件对象
//            MultipartFile file = entity.getValue();
//            ImportParams params = new ImportParams();
//            params.setTitleRows(2);
//            params.setHeadRows(1);
//            params.setNeedSave(true);
            try {
                ImportParams params = new ImportParams();
                params.setTitleRows(2);
                params.setHeadRows(1);
                params.setNeedSave(true);
                List<T> list =getUploadExcelData(request,clazz,params);

//                List<T> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                //update-begin-author:taoyan date:20190528 for:批量插入数据
                long start = System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)) {
                    service.saveBatch(list);
                }
                //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //update-end-author:taoyan date:20190528 for:批量插入数据
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                String msg = e.getMessage();
                log.error(msg, e);
                if(msg!=null && msg.indexOf("Duplicate entry")>=0){
                    return Result.error("文件导入失败:有重复数据！");
                }else{
                    return Result.error("文件导入失败:" + e.getMessage());
                }
                //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
//            } finally {
//                try {
//                    file.getInputStream().close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
//        }
//        return Result.error("文件导入失败！");
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    protected List<T> importExcelReturnDataList(HttpServletRequest request, HttpServletResponse response, Class<T> clazz) {
        ImportParams params = new ImportParams();
        params.setTitleRows(2);
        params.setHeadRows(1);
        params.setNeedSave(true);
        List<T> list =getUploadExcelData(request,clazz,params);
        return list;
    }

    protected Map<String,Object> getSelectMap(HttpServletRequest req,String ...likeFields){
        Map<String, Object> selectMap = Maps.newHashMap();
        Map<String, Object> parameterMap = RequestUtils.conversionObjectMap(req.getParameterMap());
        if(null!=parameterMap){
            if(null!=likeFields&&likeFields.length>0){
                for(String likeField : likeFields){
                    if(StringUtil.isNotEmpty(likeField)){
                        if(null!=parameterMap.get(likeField)){
                            parameterMap.put(likeField, "%" + parameterMap.get(likeField) +"%");
                        }
                    }
                }
            }
            return parameterMap;
        }
        return selectMap;
    }

    protected Map<String,Object> gtZeroVerifyMap(Map<String,Object> selectMap,String ...gtZeroFields){
        if(null!=selectMap){
            if(null!=gtZeroFields&&gtZeroFields.length>0){
                for(String gtZeroField : gtZeroFields){
                    if(StringUtil.isNotEmpty(gtZeroField)){
                        if(null!=selectMap.get(gtZeroField)){
                            Integer issueCategoryId = (Integer) selectMap.get(gtZeroField);
                            if(issueCategoryId.intValue()==0){
                                selectMap.remove(gtZeroField);
                            }
                        }
                    }
                }
            }
        }
        return selectMap;
    }

    protected String likeFieldValue(String fieldValue){
        String likeFieldValue = null;
        if(StringUtil.isNotEmpty(fieldValue)){
            likeFieldValue = ("%" + fieldValue + "%");
        }
        return likeFieldValue;
    }

    protected String eqFieldValue(String fieldValue){
        if(StringUtil.isNotEmpty(fieldValue)){
            return fieldValue;
        }
        return null;
    }

    protected Integer eqFieldValueToInteger(String fieldValue){
        if(StringUtil.isNotEmpty(fieldValue)){
            return Integer.parseInt(fieldValue);
        }
        return null;
    }

    protected Integer eqFieldValueToInteger(Object fieldValue){
        if(StringUtil.isNotEmpty(fieldValue.toString())){
            return Integer.parseInt(fieldValue.toString());
        }
        return null;
    }


}
