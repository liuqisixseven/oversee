package com.chd.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.util.CommonUtils;
import com.chd.common.util.StringUtil;
import com.chd.common.util.oss.OssBootUtil;
import com.chd.modules.oss.entity.OssFile;
import com.chd.modules.oss.entity.OssFileIssue;
import com.chd.modules.oss.mapper.OssFileIssueMapper;
import com.chd.modules.oss.mapper.OssFileMapper;
import com.chd.modules.oss.service.IOssFileService;
import com.chd.modules.oversee.hdmy.mapper.MyOrgSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @Description: OSS云存储实现类
 *
 */
@Service("ossFileService")
public class OssFileServiceImpl extends ServiceImpl<OssFileMapper, OssFile> implements IOssFileService {


	@Autowired
	OssFileIssueMapper ossFileIssueMapper;

	@Override
	public void upload(MultipartFile multipartFile) throws IOException {
		String fileName = multipartFile.getOriginalFilename();
		fileName = CommonUtils.getFileName(fileName);
		OssFile ossFile = new OssFile();
		ossFile.setFileName(fileName);
		String url = OssBootUtil.upload(multipartFile,"upload/test");
		//update-begin--Author:scott  Date:20201227 for：JT-361【文件预览】阿里云原生域名可以文件预览，自己映射域名kkfileview提示文件下载失败-------------------
		// 返回阿里云原生域名前缀URL
		ossFile.setUrl(OssBootUtil.getOriginalUrl(url));
		//update-end--Author:scott  Date:20201227 for：JT-361【文件预览】阿里云原生域名可以文件预览，自己映射域名kkfileview提示文件下载失败-------------------
		this.save(ossFile);
	}

	@Override
	public boolean delete(OssFile ossFile) {
		try {
			this.removeById(ossFile.getId());
			OssBootUtil.deleteUrl(ossFile.getUrl());
		}
		catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			return false;
		}
		return true;
	}

	@Override
	public IPage<OssFile> selectOssFilePageVo(Page<?> page, Map<String, Object> map) {
		return baseMapper.selectOssFilePageVo(page,map);
	}

	@Override
	public int addOrUpdate(OssFile ossFile) {
		int addOrUpdat = 0;
		Assert.isTrue((null != ossFile), "请传递ossFile数据");
		try{
			if(StringUtil.isEmpty(ossFile.getCreateBy())){
				ossFile.setCreateBy("admin");
			}

			ossFile.setUpdateBy(ossFile.getCreateBy());

			ossFile.setUpdateTime(new Date());

			if(StringUtil.isEmpty(ossFile.getId())){
				ossFile.setCreateTime(new Date());
				addOrUpdat = baseMapper.insert(ossFile);
			}else {
				addOrUpdat = baseMapper.updateById(ossFile);
			}

			if(addOrUpdat>0){
				OssFileIssue ossFileIssueU = new OssFileIssue();
				ossFileIssueU.setDataType(-1);
				ossFileIssueMapper.update(ossFileIssueU, Wrappers.<OssFileIssue>lambdaUpdate().eq(OssFileIssue::getOssFileId,ossFile.getId()));
				if(StringUtil.isNotEmpty(ossFile.getIssueIds())){
					String[] issueIdArray = ossFile.getIssueIds().split(",");
					if(null!=issueIdArray&&issueIdArray.length>0){
						for(String issueIdSrc : issueIdArray){
							if(StringUtil.isNotEmpty(issueIdSrc)){
								long issueId = Long.parseLong(issueIdSrc);
								//                        保存minioFile和问题关联表
								OssFileIssue ossFileIssue = new OssFileIssue();
								ossFileIssue.setIssueId(issueId);
								ossFileIssue.setOssFileId(ossFile.getId());
								ossFileIssue.setCreateTime(new Date());
								ossFileIssue.setUpdateTime(new Date());
								ossFileIssue.setCreateUserId(ossFile.getCreateBy());
								ossFileIssue.setUpdateUserId(ossFile.getCreateBy());
								int insert = ossFileIssueMapper.insert(ossFileIssue);
								if(insert<=0){
									log.error("minioFile关联关系保存异常!");
								}
							}
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}


		return addOrUpdat;
	}

}
