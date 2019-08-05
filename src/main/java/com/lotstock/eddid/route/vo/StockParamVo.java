package com.lotstock.eddid.route.vo;

import java.io.Serializable;

public class StockParamVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sessionCode;
	private String markId;
	private String code;
	private String name;
	private Integer customerId;

	/**
	 * List<StockBean> 数组json串
	 */
	private String stocks;
	
	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getStocks() {
		return stocks;
	}

	public void setStocks(String stocks) {
		this.stocks = stocks;
	}
}
