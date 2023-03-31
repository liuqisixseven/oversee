package com.chd.modules.system.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chd.common.util.StringUtil;
import com.chd.modules.system.entity.SysAnnouncementSend;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.mapper.SysAnnouncementSendMapper;
import com.chd.modules.system.mapper.SysUserMapper;
import com.chd.modules.system.model.AnnouncementSendModel;
import com.chd.modules.system.service.ISysAnnouncementSendService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户通告阅读标记表
 *
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@Service
public class SysAnnouncementSendServiceImpl extends ServiceImpl<SysAnnouncementSendMapper, SysAnnouncementSend> implements ISysAnnouncementSendService {

	@Resource
	private SysAnnouncementSendMapper sysAnnouncementSendMapper;

	@Autowired
	private SysUserMapper userMapper;

	@Override
	public List<String> queryByUserId(String userId) {
		return sysAnnouncementSendMapper.queryByUserId(userId);
	}

	@Override
	public Page<AnnouncementSendModel> getMyAnnouncementSendPage(Page<AnnouncementSendModel> page,
			AnnouncementSendModel announcementSendModel) {
		Page<AnnouncementSendModel> announcementSendModelPage = page.setRecords(sysAnnouncementSendMapper.getMyAnnouncementSendList(page, announcementSendModel));
//		if(null!=announcementSendModelPage&& CollectionUtils.isNotEmpty(announcementSendModelPage.getRecords())){
//			for(AnnouncementSendModel announcementSendModelS : announcementSendModelPage.getRecords()){
//				if(null!=announcementSendModelS){
//					if(StringUtil.isNotEmpty(announcementSendModelS.getSender())){
//						String realnameBySender = getRealnameBySender(announcementSendModelS.getSender());
//						announcementSendModelS.setSenderRealname(StringUtil.isNotEmpty(realnameBySender)?realnameBySender:announcementSendModelS.getSender());
//					}
//				}
//			}
//		}
		return announcementSendModelPage;
	}

	public String getRealnameBySender(String sender){
		if(StringUtil.isNotEmpty(sender)){
			SysUser sysUser = null;
			List<SysUser> sysUsers = userMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, sender));
			if(CollectionUtils.isNotEmpty(sysUsers)){
				sysUser = sysUsers.get(0);
			}
			if(null==sysUser){
				sysUser = userMapper.selectById(sender);
			}

			if(null!=sysUser){
				return sysUser.getRealname();
			}
		}
		return null;
	}

	public List<SysUser> queryUserListByRead(String anntId) {
		return sysAnnouncementSendMapper.queryUserListByRead(anntId);
	}
}
