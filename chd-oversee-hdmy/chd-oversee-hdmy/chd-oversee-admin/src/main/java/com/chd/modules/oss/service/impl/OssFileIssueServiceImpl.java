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
import com.chd.modules.oss.service.IOssFileIssueService;
import com.chd.modules.oss.service.IOssFileService;
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
@Service("ossFileIssueService")
public class OssFileIssueServiceImpl extends ServiceImpl<OssFileIssueMapper, OssFileIssue> implements IOssFileIssueService {

	@Autowired
	OssFileIssueMapper ossFileIssueMapper;

}
