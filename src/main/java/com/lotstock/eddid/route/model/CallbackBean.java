package com.lotstock.eddid.route.model;

import java.io.Serializable;
import java.util.Date;

public class CallbackBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6338767630524353131L;

	/**
	 * 产品编码
	 */
	private String sysProductCode;

	/**
	 * 订阅有效期开始时间
	 */
	private Date useBeginTime;

	/**
	 * 订阅有效期结束时间
	 */
	private Date useEndTime;

	public String getSysProductCode() {
		return sysProductCode;
	}

	public Date getUseBeginTime() {
		return useBeginTime;
	}

	public Date getUseEndTime() {
		return useEndTime;
	}

	public void setSysProductCode(String sysProductCode) {
		this.sysProductCode = sysProductCode;
	}

	public void setUseBeginTime(Date useBeginTime) {
		this.useBeginTime = useBeginTime;
	}

	public void setUseEndTime(Date useEndTime) {
		this.useEndTime = useEndTime;
	}

}
