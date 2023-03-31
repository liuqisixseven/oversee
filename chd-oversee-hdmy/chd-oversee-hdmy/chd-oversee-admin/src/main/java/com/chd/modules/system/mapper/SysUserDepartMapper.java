package com.chd.modules.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.chd.modules.system.entity.SysUser;
import com.chd.modules.system.entity.SysUserDepart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户部门mapper接口
 * 
 */
public interface SysUserDepartMapper extends BaseMapper<SysUserDepart>{

    /**
     * 通过用户id查询部门用户
     * @param userId 用户id
     * @return List<SysUserDepart>
     */
	List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);

	/**
	 *  查询指定部门下的用户 并且支持用户真实姓名模糊查询
	 * @param orgCode
	 * @param realname
	 * @return
	 */
	List<SysUser> queryDepartUserList(@Param("orgCode") String orgCode, @Param("realname") String realname);

	/**
	 * 根据部门查询部门用户
	 * @param page
	 * @param orgCode
	 * @param username
	 * @param realname
	 * @return
	 */
	IPage<SysUser> queryDepartUserPageList(Page<SysUser> page, @Param("orgCode") String orgCode, @Param("username") String username, @Param("realname") String realname);
	IPage<SysUser> queryDepartListUserPageList(Page<SysUser> page, @Param("departIds") String departIds, @Param("username") String username, @Param("realname") String realname);
}
