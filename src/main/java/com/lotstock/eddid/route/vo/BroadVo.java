package com.lotstock.eddid.route.vo;

import java.io.Serializable;

public class BroadVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3778910575525998350L;

	//会员机构编码
	private String orgCode;
	
	//兼容参数
	private String versionNo;
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
}
