package com.chd.modules.system.service;

import com.chd.modules.system.entity.SysDataLog;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 数据日志service接口
 * 
 */
public interface ISysDataLogService extends IService<SysDataLog> {
	
	/**
	 * 添加数据日志
	 * @param tableName
	 * @param dataId
	 * @param dataContent
	 */
	public void addDataLog(String tableName, String dataId, String dataContent);

}
