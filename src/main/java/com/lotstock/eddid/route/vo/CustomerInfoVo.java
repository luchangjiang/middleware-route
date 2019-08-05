package com.lotstock.eddid.route.vo;

public class CustomerInfoVo {
	
	//手机号码
	private String mobile;
	//密码
	private String pwd;
	//验证码
	private String verificationCode;
	//机构编码
	private String orgCode;
	//渠道类型
	private String channelType;
	//分享者id
	private String fromCus;
	//昵称
	private	String nickName;
	//头像地址
	private String iconUrl;
	//下载来源
	private	String downSource;
	//openId
	private String openId;
	//类型
	private String type;
	//性别 1：男，0：女
	private int gender=0;
	//qq
	private String qq;
	//wx
	private String wx;
	//email
	private String email;
	//注册ip
	private String registerIp;
	//注册设备
	private String registerDevice;
	//登录设备号
	private String deviceToken;
	//版本号
	private String versionNumber;
	//组别 1大陆 0香港
	private String tranche;
	
	private String language;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getFromCus() {
		return fromCus;
	}
	public void setFromCus(String fromCus) {
		this.fromCus = fromCus;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getDownSource() {
		return downSource;
	}
	public void setDownSource(String downSource) {
		this.downSource = downSource;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getWx() {
		return wx;
	}
	public void setWx(String wx) {
		this.wx = wx;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRegisterIp() {
		return registerIp;
	}
	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}
	public String getRegisterDevice() {
		return registerDevice;
	}
	public void setRegisterDevice(String registerDevice) {
		this.registerDevice = registerDevice;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	public String getTranche() {
		return tranche;
	}
	public void setTranche(String tranche) {
		this.tranche = tranche;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
