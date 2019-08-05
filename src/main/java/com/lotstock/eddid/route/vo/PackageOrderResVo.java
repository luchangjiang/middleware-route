package com.lotstock.eddid.route.vo;

public class PackageOrderResVo {

	private Integer pageIndex;
	private Integer pageSize;
	/**
	 * 排序方式 ASC：升序； DESC：降序（默认）
	 */
	private String orderBy;

	private Long customerId;
	private Long customerKey;
	private String customerMobile;
	private String orgCode;

	/**
	 * 0：所有； 1：套餐订阅完成，支付成功； 5：套餐订阅未完成
	 */
	private Integer orderStatus;

	/**
	 * 0：所有； 1：订阅 ； 2：续订
	 */
	private Integer orderType;

	/**
	 * 套餐内产品归属 系统编码
	 */
	private String productSysFlag;

	/**
	 * 0：组合捆绑套餐； 1：单个独立套餐； 不传则查询所有
	 */
	private Integer packageCombType;
	
	private String sessionCode;

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Long getCustomerKey() {
		return customerKey;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public String getProductSysFlag() {
		return productSysFlag;
	}

	public Integer getPackageCombType() {
		return packageCombType;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setCustomerKey(Long customerKey) {
		this.customerKey = customerKey;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public void setProductSysFlag(String productSysFlag) {
		this.productSysFlag = productSysFlag;
	}

	public void setPackageCombType(Integer packageCombType) {
		this.packageCombType = packageCombType;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	
}
