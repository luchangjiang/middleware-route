package com.lotstock.eddid.route.vo;
/**
* @author 作者 wzx
* @version 创建时间：2017年7月26日 下午1:55:17
*/
public class HqTradeRecordVo {
	
	private int userId;	//用户id
	
	private int goldid;	//金币数id
	
	private int shelveid;//涨跌幅id
	
	private String sessionCode;	//用户标识
	
	private int type;	//类型
	
	private String assetId;	//股票id
	
	private String openprice;//开盘价
	
	private String stockName;//股票名称

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGoldid() {
		return goldid;
	}

	public void setGoldid(int goldid) {
		this.goldid = goldid;
	}

	public int getShelveid() {
		return shelveid;
	}

	public void setShelveid(int shelveid) {
		this.shelveid = shelveid;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getOpenprice() {
		return openprice;
	}

	public void setOpenprice(String openprice) {
		this.openprice = openprice;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	
}
