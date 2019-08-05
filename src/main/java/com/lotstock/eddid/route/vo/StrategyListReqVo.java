package com.lotstock.eddid.route.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.lotstock.eddid.route.util.BeanMapUtil;
import com.lotstock.eddid.route.util.Query;
import org.apache.commons.lang.StringUtils;


public class StrategyListReqVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6350472540135225718L;

	private String sessionCode;
	
	private Long customerId;

	private Long customerKey;

	private String orgCode;

	/**
	 * 收益率条数
	 */
	private Integer size;

	/**
	 * 页数
	 */
	private Integer pageIndex;

	/**
	 * 每页条数
	 */
	private Integer pageSize;

	/**
	 * 是否查询收益率
	 */
	private boolean is_rate;

	/**
	 * 是否显示策略统计信息
	 */
	private boolean is_statistics;

	/**
	 * 是否显示今日调仓股数/最近调仓时间
	 */
	private boolean is_trading_today;

	/**
	 * 调仓类型(all:包含开仓&平仓记录,open:仅包含开仓记录,close:仅包含平仓记录)
	 */
	private String trading_type;

	/**
	 * 是否显示完整策略属性
	 */
	private boolean is_full;

	/**
	 * 风险等级（1:进取型、2:稳健型、3:保守型）
	 */
	private Integer risk_grade;

	/**
	 * 投资标的
	 */
	private String inv_target;

	/**
	 * 大盘行情
	 */
	private String market;

	/**
	 * 投资期限
	 */
	private Integer time_limit;

	/**
	 * 是否订阅
	 */
	private Integer subscibe;

	/**
	 * 排序值
	 */
	private Integer sort_rate;

	private String sort;

	private Integer category;

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 * 组装查询策略列表参数
	 * 
	 * @return
	 */
	public Query query(StrategyListReqVo vo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params = BeanMapUtil.beanToMap(vo);

//		Integer pageSize = this.pageSize == null ? 10 : this.pageSize;
//		Integer pageIndex = this.pageIndex == null ? 1 : this.pageIndex;
//		params.put("offset", (pageIndex - 1) * pageSize);
//		params.put("limit", pageSize);

		params.put("risk_grade", this.risk_grade);
		if (StringUtils.isNotBlank(this.inv_target)) {
			params.put("inv_target", this.inv_target.split(","));
		}
		params.put("time_limit", this.time_limit);
		if (StringUtils.isNotBlank(this.market)) {
			params.put("market", this.market.split(","));
		}
		return new Query(params);
	}

	public Long getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(Long customerKey) {
		this.customerKey = customerKey;
	}

	public Integer getSize() {
		return size;
	}

	public boolean isIs_rate() {
		return is_rate;
	}

	public boolean isIs_statistics() {
		return is_statistics;
	}

	public boolean isIs_trading_today() {
		return is_trading_today;
	}

	public String getTrading_type() {
		return trading_type;
	}

	public boolean isIs_full() {
		return is_full;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setIs_rate(boolean is_rate) {
		this.is_rate = is_rate;
	}

	public void setIs_statistics(boolean is_statistics) {
		this.is_statistics = is_statistics;
	}

	public void setIs_trading_today(boolean is_trading_today) {
		this.is_trading_today = is_trading_today;
	}

	public void setTrading_type(String trading_type) {
		this.trading_type = trading_type;
	}

	public void setIs_full(boolean is_full) {
		this.is_full = is_full;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Integer getRisk_grade() {
		return risk_grade;
	}

	public String getInv_target() {
		return inv_target;
	}

	public String getMarket() {
		return market;
	}

	public Integer getTime_limit() {
		return time_limit;
	}

	public Integer getSubscibe() {
		return subscibe;
	}

	public void setRisk_grade(Integer risk_grade) {
		if (risk_grade != null && risk_grade == 0)
			risk_grade = null;
		this.risk_grade = risk_grade;
	}

	public void setInv_target(String inv_target) {
		if (StringUtils.isNotBlank(inv_target) && "0".equals(inv_target))
			inv_target = null;
		this.inv_target = inv_target;
	}

	public void setMarket(String market) {
		if (StringUtils.isNotBlank(market) && "0".equals(market))
			market = null;
		this.market = market;
	}

	public void setTime_limit(Integer time_limit) {
		if (time_limit != null && time_limit == 0)
			time_limit = null;
		this.time_limit = time_limit;
	}

	public void setSubscibe(Integer subscibe) {
		this.subscibe = subscibe;
	}

	public Integer getSort_rate() {
		return sort_rate;
	}

	public void setSort_rate(Integer sort_rate) {
		this.sort_rate = sort_rate;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		if (category != null && category == 0)
			category = null;
		this.category = category;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}
	
}
