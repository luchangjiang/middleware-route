package com.lotstock.eddid.route.vo;

import org.apache.commons.lang.StringUtils;

/**
 * 订阅回调参数
 * 
 * @author XIONGWEI
 *
 */
public class CallbackResVo {
	private Integer customerId;

	private Integer customerKey;

	private String customerMobile;

	/**
	 * 订阅时间
	 */
	private String orderTime;

	/**
	 * 订阅中心关联key
	 */
	private String subOrderNo;

	private String subCusProList;

	public boolean isValid() {
		if (/* customerId == null || */customerKey == null || StringUtils.isBlank(orderTime)
				|| StringUtils.isBlank(subCusProList) || StringUtils.isBlank(subOrderNo)
				|| StringUtils.isBlank(customerMobile)) {
			return false;
		}
		return true;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public Integer getCustomerKey() {
		return customerKey;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public String getSubCusProList() {
		return subCusProList;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public void setCustomerKey(Integer customerKey) {
		this.customerKey = customerKey;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public void setSubCusProList(String subCusProList) {
		this.subCusProList = subCusProList;
	}

	public String getSubOrderNo() {
		return subOrderNo;
	}

	public void setSubOrderNo(String subOrderNo) {
		this.subOrderNo = subOrderNo;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

}
