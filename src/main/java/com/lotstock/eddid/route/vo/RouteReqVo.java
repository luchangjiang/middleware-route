package com.lotstock.eddid.route.vo;

public class RouteReqVo {

	/**
	 * 接口类型（RouteType）
	 */
	private Integer type;

	/**
	 * 策略编码
	 */
	private String strategy_code;

	private String orgCode;
	
	/**
	 * 是否显示策略统计信息
	 */
	private boolean is_statistics;

	/**
	 * 指定开平仓种类 (all:包含开仓&平仓记录,open:仅包含开仓记录,close:仅包含平仓记录,clear :仅包含清仓(彻底平仓)记录(预留)商品)
	 */
	private String limit_type;

	private Integer pageIndex;

	private Integer pageSize;

	private String day_begin;

	private String day_end;

	private Integer count;

	/**
	 * 最少收益率
	 */
	private Double min_earn_rate;

	private String params;

	/**
	 * 股票代码
	 */
	private String stockCode;
	
    /**
     * 是否显示持仓图表信息
     */
	private boolean is_chart;
	
    /**
     * 策略补充记录ID
     */
	private Long recordId;
	
	/**
     * 模型类型
     */
	private Integer modelType;
	
	/**
     * 月均Type
     */
	private Integer monthType;
	
	private String endDate;

	private Integer day;
	
	private Integer market_id;
	
	/**
     * 获利离场类型
     */
	private Integer departureType;

	public Integer getType() {
		return type;
	}

	public String getStrategy_code() {
		return strategy_code;
	}

	public boolean isIs_statistics() {
		return is_statistics;
	}

	public String getLimit_type() {
		return limit_type;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getCount() {
		return count;
	}

	public String getDay_begin() {
		return day_begin;
	}

	public String getDay_end() {
		return day_end;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setStrategy_code(String strategy_code) {
		this.strategy_code = strategy_code;
	}

	public void setIs_statistics(boolean is_statistics) {
		this.is_statistics = is_statistics;
	}

	public void setLimit_type(String limit_type) {
		this.limit_type = limit_type;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public void setDay_begin(String day_begin) {
		this.day_begin = day_begin;
	}

	public void setDay_end(String day_end) {
		this.day_end = day_end;
	}

	public Double getMin_earn_rate() {
		return min_earn_rate;
	}

	public void setMin_earn_rate(Double min_earn_rate) {
		this.min_earn_rate = min_earn_rate;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public boolean isIs_chart() {
		return is_chart;
	}

	public void setIs_chart(boolean is_chart) {
		this.is_chart = is_chart;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Integer getModelType() {
		return modelType;
	}

	public void setModelType(Integer modelType) {
		this.modelType = modelType;
	}

	public Integer getDepartureType() {
		return departureType;
	}

	public void setDepartureType(Integer departureType) {
		this.departureType = departureType;
	}

	public Integer getMonthType() {
		return monthType;
	}

	public void setMonthType(Integer monthType) {
		this.monthType = monthType;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getMarket_id() {
		return market_id;
	}

	public void setMarket_id(Integer market_id) {
		this.market_id = market_id;
	}

	
}
