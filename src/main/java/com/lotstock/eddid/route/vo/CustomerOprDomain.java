package com.lotstock.eddid.route.vo;

public class CustomerOprDomain {
	
	private String sessionCode;

	private Integer userId;
	
	private String oprType;
	
	private String oprNav;
	
	private String cusType;
	
	private String cusVersion;
	
	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public String getOprNav() {
		return oprNav;
	}

	public void setOprNav(String oprNav) {
		this.oprNav = oprNav;
	}

	public String getCusType() {
		return cusType;
	}

	public void setCusType(String cusType) {
		this.cusType = cusType;
	}

	public String getCusVersion() {
		return cusVersion;
	}

	public void setCusVersion(String cusVersion) {
		this.cusVersion = cusVersion;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOprType() {
		return oprType;
	}

	public void setOprType(String oprType) {
		this.oprType = oprType;
	}
	
	
}
