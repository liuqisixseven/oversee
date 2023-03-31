package com.chd.modules.oss.service;

import java.io.IOException;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oss.entity.OssFile;
import com.chd.modules.oversee.issue.entity.MorphologicalCategories;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: OOS云存储service接口
 *
 */
public interface IOssFileService extends IService<OssFile> {

    /**
     * oss文件上传
     * @param multipartFile
     * @throws IOException
     */
	void upload(MultipartFile multipartFile) throws IOException;

    /**
     * oss文件删除
     * @param ossFile OSSFile对象
     * @return
     */
	boolean delete(OssFile ossFile);

    IPage<OssFile> selectOssFilePageVo(Page<?> page, Map<String, Object> map);

    int addOrUpdate(OssFile ossFile);


}
