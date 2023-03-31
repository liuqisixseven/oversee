package com.chd.modules.system.mapper;

import java.util.List;
import java.util.Map;

import com.chd.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import com.chd.modules.system.entity.SysAnnouncementSend;
import com.chd.modules.system.model.AnnouncementSendModel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 用户通告阅读标记表
 * 
 * @Date:  2019-02-21
 * @Version: V1.0
 */
public interface SysAnnouncementSendMapper extends BaseMapper<SysAnnouncementSend> {

    /**
     * 通过用户id查询 用户通告阅读标记表
     * @param userId 用户id
     * @return
     */
	public List<String> queryByUserId(@Param("userId") String userId);

	/**
	 * 获取我的消息
	 * @param announcementSendModel
	 * @param page
	 * @return
	 */
	public List<AnnouncementSendModel> getMyAnnouncementSendList(Page<AnnouncementSendModel> page,@Param("announcementSendModel") AnnouncementSendModel announcementSendModel);

	/**
	 * 查询已读的用户列表
	 * @param anntId
	 * @return
	 */
	public List<SysUser> queryUserListByRead(@Param("anntId") String anntId);

	/**
	 *
	 * @param map
	 * @return
	 */
	//todo by apple at 2022/12/26
	public List<SysUser> queryUserListByNotRead(@Param("map") Map<String, Object> map);
}
