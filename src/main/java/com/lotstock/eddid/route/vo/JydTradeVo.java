package com.lotstock.eddid.route.vo;

public class JydTradeVo implements java.io.Serializable{

	private static final long serialVersionUID = -8308705954424311141L;
	
	//用户标识
	private String sessionCode;
	
	//用户id
	private int userId;
	
	//手机号码
	private String mobile;

	//机构编码
	private String orgCode;
	
	//类型
	private int type;
	
	//涨跌幅id
	private int shelveid;
	
	//金币面额id
	private int goldid;
	
	//记录id
	private int recordid;
	
	//股票id
	private String assetId;
	
	//用户名称
	private String nickName;
	
	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getShelveid() {
		return shelveid;
	}

	public void setShelveid(int shelveid) {
		this.shelveid = shelveid;
	}

	public int getGoldid() {
		return goldid;
	}

	public void setGoldid(int goldid) {
		this.goldid = goldid;
	}

	public int getRecordid() {
		return recordid;
	}

	public void setRecordid(int recordid) {
		this.recordid = recordid;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
