package com.lotstock.eddid.route.vo;

import java.io.Serializable;

/**
 * 获取支付单号
 */
public class TradeReqVo implements Serializable {

	private static final long serialVersionUID = 8040688094087617638L;

	/**
	 * 策略CODE
	 */
	private String strategy_code;

	/**
	 * 产品ID
	 */
	private Integer product_id;

	/**
	 * 产品规格Id
	 */
	private Integer product_rule_id;

	private Integer user_id;

	private String sessionCode;

	private String mobile;

	private String orgCode;

	private Integer channel;

	private Double count;

	/**
	 * 支付类型
	 */
	private Integer type;

	public boolean isVali() {
		if (strategy_code == null || product_id == null || product_rule_id == null || user_id == null) {
			return false;
		}
		return true;
	}

	public String getStrategy_code() {
		return strategy_code;
	}

	public void setStrategy_code(String strategy_code) {
		this.strategy_code = strategy_code;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getProduct_rule_id() {
		return product_rule_id;
	}

	public void setProduct_rule_id(Integer product_rule_id) {
		this.product_rule_id = product_rule_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}