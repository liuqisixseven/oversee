package com.chd.modules.system.controller;

import com.chd.common.util.StringUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import com.chd.common.api.vo.Result;
import com.chd.common.exception.JeecgBootException;
import com.chd.common.util.CommonUtils;
import com.chd.common.util.MinioUtil;
import com.chd.common.util.oConvertUtils;
import com.chd.modules.oss.entity.OssFile;
import com.chd.modules.oss.service.IOssFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * minio文件上传示例
 *
 */
@Slf4j
@RestController
@RequestMapping("/sys/upload")
public class SysUploadController {


    @Autowired
    private IOssFileService ossFileService;

    @Value("${chd.minio.minio_url}")
    private String minioUrl;

    /**
     * 上传
     * @param request
     */
    @PostMapping(value = "/uploadMinio")
    public Result<Map<String, Object>> uploadMinio(HttpServletRequest request) {
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> map = Maps.newHashMap();
        String bizPath = request.getParameter("biz");

        //LOWCOD-2580 sys/common/upload接口存在任意文件上传漏洞
        boolean flag = oConvertUtils.isNotEmpty(bizPath) && (bizPath.contains("../") || bizPath.contains("..\\"));
        if (flag) {
            throw new JeecgBootException("上传目录bizPath，格式非法！");
        }

        if(oConvertUtils.isEmpty(bizPath)){
            bizPath = "";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件对象
        MultipartFile file = multipartRequest.getFile("file");
        // 获取文件名
        String orgName = file.getOriginalFilename();
        orgName = CommonUtils.getFileName(orgName);
        String fileUrl =  MinioUtil.upload(file,bizPath);
        if(oConvertUtils.isEmpty(fileUrl)){
            return Result.error("上传失败,请检查配置信息是否正确!");
        }
        //保存文件信息
        OssFile minioFile = new OssFile();
        minioFile.setFileName(orgName);
        minioFile.setUrl(fileUrl);
        minioFile.setRelativePath(fileUrl.replace(minioUrl,""));
        if(!ossFileService.save(minioFile)){
            return Result.error("上传失败,数据保存异常!");
        }
        map.put("minioFile",minioFile);
        result.setResult(map);
        result.setMessage(fileUrl);
        result.setSuccess(true);
        return result;
    }


    /**
     * 上传
     * @param request
     */
    @GetMapping(value = "/getMinioById")
    public void getMinioById(HttpServletRequest request,String id) {
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> map = Maps.newHashMap();
        OssFile ossFilebyId = ossFileService.getById(id);
        if(null!=ossFilebyId&& StringUtil.isNotEmpty(ossFilebyId.getUrl())){

        }

    }


    /**
     * 上传
     * @param request
     */
    @PostMapping(value = "/uploadMinioReturnDada")
    public Result<Map<String, Object>> uploadMinioReturnDada(HttpServletRequest request) {
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> map = Maps.newHashMap();
        String bizPath = request.getParameter("biz");

        //LOWCOD-2580 sys/common/upload接口存在任意文件上传漏洞
        boolean flag = oConvertUtils.isNotEmpty(bizPath) && (bizPath.contains("../") || bizPath.contains("..\\"));
        if (flag) {
            throw new JeecgBootException("上传目录bizPath，格式非法！");
        }

        if(oConvertUtils.isEmpty(bizPath)){
            bizPath = "";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件对象
        MultipartFile file = multipartRequest.getFile("file");
        // 获取文件名
        String orgName = file.getOriginalFilename();
        orgName = CommonUtils.getFileName(orgName);
        String fileUrl =  MinioUtil.upload(file,bizPath);
        if(oConvertUtils.isEmpty(fileUrl)){
            return Result.error("上传失败,请检查配置信息是否正确!");
        }
        //保存文件信息
        OssFile minioFile = new OssFile();
        minioFile.setFileName(orgName);
        minioFile.setUrl(fileUrl);
        if(ossFileService.save(minioFile)){
            return Result.error("上传失败,数据保存异常!");
        }
        map.put("minioFile",minioFile);
        result.setResult(map);
        result.setMessage(fileUrl);
        result.setSuccess(true);
        return result;
    }

}
