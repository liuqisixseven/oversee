package com.chd.common.system.vo;

import java.util.List;

import com.chd.common.aspect.annotation.Dict;
import com.chd.common.util.DateUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户缓存信息
 *
 */
public class SysUserCacheInfo {

	private String sysUserCode;

	private String sysUserName;

	private String sysOrgCode;

	private List<String> sysMultiOrgCode;

	private boolean oneDepart;

	private String hdmyUserId;

	private String hdmyOrgId;


	private String post;


	public boolean isOneDepart() {
		return oneDepart;
	}

	public void setOneDepart(boolean oneDepart) {
		this.oneDepart = oneDepart;
	}

	public String getSysDate() {
		return DateUtils.formatDate();
	}

	public String getSysTime() {
		return DateUtils.now();
	}

	public String getSysUserCode() {
		return sysUserCode;
	}

	public void setSysUserCode(String sysUserCode) {
		this.sysUserCode = sysUserCode;
	}

	public String getSysUserName() {
		return sysUserName;
	}

	public void setSysUserName(String sysUserName) {
		this.sysUserName = sysUserName;
	}

	public String getSysOrgCode() {
		return sysOrgCode;
	}

	public void setSysOrgCode(String sysOrgCode) {
		this.sysOrgCode = sysOrgCode;
	}

	public List<String> getSysMultiOrgCode() {
		return sysMultiOrgCode;
	}

	public void setSysMultiOrgCode(List<String> sysMultiOrgCode) {
		this.sysMultiOrgCode = sysMultiOrgCode;
	}

	public String getHdmyUserId() {
		return hdmyUserId;
	}

	public void setHdmyUserId(String hdmyUserId) {
		this.hdmyUserId = hdmyUserId;
	}

	public String getHdmyOrgId() {
		return hdmyOrgId;
	}

	public void setHdmyOrgId(String hdmyOrgId) {
		this.hdmyOrgId = hdmyOrgId;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}
}
