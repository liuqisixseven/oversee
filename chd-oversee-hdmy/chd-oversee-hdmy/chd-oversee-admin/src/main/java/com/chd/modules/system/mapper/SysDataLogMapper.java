package com.chd.modules.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.chd.modules.system.entity.SysDataLog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 系统数据日志Mapper接口
 * 
 */
public interface SysDataLogMapper extends BaseMapper<SysDataLog>{
	/**
	 * 通过表名及数据Id获取最大版本
	 * @param tableName
	 * @param dataId
	 * @return
	 */
	public String queryMaxDataVer(@Param("tableName") String tableName,@Param("dataId") String dataId);
	
}
