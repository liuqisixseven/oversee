package com.chd.oversee.issue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.exception.AssertBizException;
import com.chd.common.exception.BizException;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.MinioUtil;
import com.chd.common.util.StringUtil;
import com.chd.common.util.ZipUtil;
import com.chd.modules.oss.entity.OssFile;
import com.chd.modules.oss.entity.OssFileIssue;
import com.chd.modules.oss.mapper.OssFileIssueMapper;
import com.chd.modules.oss.service.IOssFileService;
import com.chd.modules.oversee.issue.entity.*;
import com.chd.modules.oversee.issue.service.IOverseeIssueService;
import com.chd.modules.oversee.issue.service.impl.OverseeIssueServiceImpl;
import com.chd.modules.system.entity.SysUser;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date: 2022-08-03
 * @Version: V1.0
 */
@Service()
@Transactional()
public class OverseeIssueDocServiceImpl extends OverseeIssueServiceImpl {

    @Autowired
    private IOssFileService ossFileService;

    @Autowired
    OssFileIssueMapper ossFileIssueMapper;

//    @Autowired
//    private IOverseeIssueService overseeIssueService;
    @Value("${chd.minio.minio_url}")
    private String minioUrl;
    @Value(value="${chd.uploadType}")
    private String uploadType;
    //    @Autowired
//    private IOssFileService ossFileService;
    private static final String PROCESS_CATEGORY_ISSUES = "ISSUES";

    /**
     * 后端自动扫描已完成整改问题，进行归档工作
     */
    public void issueToDoc(){
        Map query = Maps.newHashMap();
        query.put("submitState",OverseeConstants.SubmitState.Complete);
        query.put("isDocument",0);
        List<OverseeIssue> data = selectOverseeIssueList(query);
        List<String> ids = data.stream().map(a->{
            return a.getId().toString();
        }).collect(Collectors.toList());
        List<String> files = issueToDoc(ids,"admin");
        ids.stream().forEach(a->{
            OverseeIssue overseeIssue = new OverseeIssue();
            overseeIssue.setIsDocument(1);
            overseeIssue.setId(Long.parseLong(a));
            this.modifySubmitState(overseeIssue);
        });
    }

    private List<String> issueToDoc(List<String> issueIds,String creator){
        String[] dirFiles = new String[0];
        String[] finalDirFiles = dirFiles;
        List<OssFile> ossFiles = new ArrayList<>();
        issueIds.stream().forEach(issueId -> {
            try {
                long id = Long.parseLong(issueId);
                OverseeIssueDetailVo detailVo = getIssueDetailById(id);
//                OverseeIssue redisCacheOverseeIssue = getRedisCacheOverseeIssue(id);
                OssFile minioFile;
                LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(OssFile::getIssueIds,issueId).eq(OssFile::getType,1);
                minioFile = ossFileService.getOne(queryWrapper);
                if(detailVo.getSubmitState()!= OverseeConstants.SubmitState.Complete){
                    ossFileService.delete(minioFile);
                    minioFile=null;
                }
                if(minioFile==null) {
                    minioFile= new OssFile();
                    List<String> files = new ArrayList<>();
                    List<OverseeIssueAppendix> issueAppendices = detailVo.getOverseeIssueAppendixList();
                    issueAppendices.forEach(file -> {
                        files.add(file.getUrl());
                    });
                    List<OverseeIssueFile> fileList = detailVo.getFileList();
                    fileList.forEach(file -> {
                        OverseeIssueFiles tmpFile = (OverseeIssueFiles) file;
                        files.add(tmpFile.getUrl());
                    });
                    if (finalDirFiles.length > 0) {
                        Arrays.stream(finalDirFiles).forEach(file -> {
                            if (file.indexOf("-" + issueId + "-") > 0) {
                                files.add(file);
                            }
                        });
                    }
                    //销号表文档
                    OverseeIssue overseeIssue = new OverseeIssue();
                    overseeIssue.setId(id);
                    XWPFTemplate xwpfTemplate = getCancelANumberWordExport(overseeIssue);
                    String cancalFile = id+"问题销号文件.docx";
                    xwpfTemplate.writeToFile(cancalFile);
                    files.add(cancalFile);
                    String reportTime = DateFormatUtils.format(detailVo.getCreateTime(), "yyyy-MM-dd");
                    String fileName = detailVo.getNum() + "-整改支撑材料-" + reportTime+".zip";
                    //上传oss文件
                    File f = new File(fileName);
                    ZipOutputStream os = (ZipOutputStream) ZipUtil.zipFiles(files, f,null);
//                    InputStream is = new FileInputStream(f);
                    String fileUrl = MinioUtil.upload(f, id+"","issuecancel");
                    //                //保存文件信息
                    minioFile.setFileName(fileName);
                    minioFile.setUrl(fileUrl);
                    minioFile.setIssueIds(issueId + "");
                    minioFile.setType(1);
                    minioFile.setResponsibleUnitOrgName(detailVo.getResponsibleUnitOrg());
                    minioFile.setResponsibleUnitOrgId(detailVo.getResponsibleUnitOrgId());
//                    if(null!=redisCacheOverseeIssue){
//                    }
                    minioFile.setCreateBy(creator);
                    minioFile.setRelativePath(fileUrl.replace(minioUrl, "") + File.pathSeparator + fileName);
                    if (ossFileService.addOrUpdate(minioFile) < 0) {
                        log.error("上传失败,数据保存异常!");
                    }
                }
                ossFiles.add(minioFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //压缩问题列表为一个Zip
        List<String> files = new ArrayList<>();
        ossFiles.forEach(a->{
            files.add(a.getUrl());
        });
        return files;
    }

    @Override
    public File issueToDoc(String issueIds, HttpServletRequest req, HttpServletResponse response) throws Exception {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        AssertBizException.isTrue((issueIds!=null),"未获取到问题id列表！");
        String[] issueIdArr = issueIds.split(",");
        AssertBizException.isTrue(issueIdArr.length<=5,"最多允许5个以内的问题归档！");
        // FIXME: by lhm 2022/12/29
//        Object docPath=paramsMap.get("docPath");

//        if(docPath!=null){
//            dirFiles = new File(docPath.toString()).list();
//        }
        String reportTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String fileName = "整改支撑材料-" + reportTime+".zip";
        File f = new File(fileName);
        List<String> files = issueToDoc(Arrays.asList(issueIdArr),sysUser.getUsername());
        ZipOutputStream os = (ZipOutputStream) ZipUtil.zipFiles(files, f,response);
        os.flush();
        os.close();
        return f;
    }
}
