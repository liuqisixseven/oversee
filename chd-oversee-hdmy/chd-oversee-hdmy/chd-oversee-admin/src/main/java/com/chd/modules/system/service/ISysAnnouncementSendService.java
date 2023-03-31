package com.chd.modules.system.service;

import java.util.List;

import com.chd.modules.system.entity.SysAnnouncementSend;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.model.AnnouncementSendModel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 用户通告阅读标记表
 * 
 * @Date:  2019-02-21
 * @Version: V1.0
 */
public interface ISysAnnouncementSendService extends IService<SysAnnouncementSend> {

    /**
     * 通过用户id查询用户通告阅读标记
     * @param userId 用户id
     * @return
     */
	public List<String> queryByUserId(String userId);
	
	/**
	 * 获取我的消息
	 * @param announcementSendModel
     * @param page 当前页数
	 * @return
	 */
	public Page<AnnouncementSendModel> getMyAnnouncementSendPage(Page<AnnouncementSendModel> page,AnnouncementSendModel announcementSendModel);

	public List<SysUser> queryUserListByRead(String anntId);
}
