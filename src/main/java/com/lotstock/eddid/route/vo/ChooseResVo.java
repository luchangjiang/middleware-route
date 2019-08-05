package com.lotstock.eddid.route.vo;

public class ChooseResVo implements java.io.Serializable {

	private static final long serialVersionUID = 864229760060985807L;

	// 记录id
	private int recid;
	// 市场id
	private int market;
	// 股票代码
	private String code;
	// 股票名称
	private String stockName;
	// 模型code
	private String strategyCode;
	// 模型名称
	private String strategyName;
	// 日期
	private String day;
	// 时间
	private String time;
	// 入选价格
	private String price;
	// 当日实际涨幅
	private String actualRise;
	// 10日涨幅
	private String tenRise;
	//20日涨幅
	private String twentyRise; 
	// 实时价
	private double dayClose;
	// 前一个交易日的收盘价
	private double prevClose;

	public int getMarket() {
		return market;
	}

	public void setMarket(int market) {
		this.market = market;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public int getRecid() {
		return recid;
	}

	public void setRecid(int recid) {
		this.recid = recid;
	}

	public String getStrategyCode() {
		return strategyCode;
	}

	public void setStrategyCode(String strategyCode) {
		this.strategyCode = strategyCode;
	}

	public String getActualRise() {
		return actualRise;
	}

	public void setActualRise(String actualRise) {
		this.actualRise = actualRise;
	}

	public String getTenRise() {
		return tenRise;
	}

	public void setTenRise(String tenRise) {
		this.tenRise = tenRise;
	}

	public double getDayClose() {
		return dayClose;
	}

	public void setDayClose(double dayClose) {
		this.dayClose = dayClose;
	}

	public double getPrevClose() {
		return prevClose;
	}

	public void setPrevClose(double prevClose) {
		this.prevClose = prevClose;
	}

	public String getTwentyRise() {
		return twentyRise;
	}

	public void setTwentyRise(String twentyRise) {
		this.twentyRise = twentyRise;
	}
	
}
