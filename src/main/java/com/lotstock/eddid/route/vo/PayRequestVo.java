package com.lotstock.eddid.route.vo;

/**
 * 订阅支付请求参数
 * @author liujf 2017年10月25日 下午2:26:54
 */
public class PayRequestVo extends CommonRequestVo{
	
	private static final long serialVersionUID = -3142158133805617905L;
	
	//客户端传递的请求参数；非必填
	private String frontPageUrl ;//前端跳转地址，
	
	//业务请求参数；必填
	private Integer packageId;//套餐ID
	private String packageCode;//套餐编码（没有编码用id代替）
	private Integer packageOrderNum = 0;//套餐订阅数量（如：3 天 /月/年）
	private String payTypeFinalUse;//订阅者选择的	支付方式：[1：虚拟币支付；2：余额支付；3：收银台支付；混合支付时，多个逗号隔开]
	private String orderChannel;//订购渠道（wx | app | pc | sys【内部】）
	
	private String orderFromSysFlag;//订阅来源系统编码[pay001：支付系统；cl001：策略；xg001：选股；zb001：指标；]
	private Integer createById  ;//创建人ID,[后台用户ID或者注册用户ID]
	private String createByNickname;//创建人昵称
	//业务请求参数；非必填
	private String orderBeginTime;//套餐开始日期
	private String returnParams;//子系统下单后，回调时子系统需要返回的特定内容字段
	private String remark;//下单备注
	
	public String getFrontPageUrl() {
		return frontPageUrl;
	}
	public void setFrontPageUrl(String frontPageUrl) {
		this.frontPageUrl = frontPageUrl;
	}
	public Integer getPackageId() {
		return packageId;
	}
	public void setPackageId(Integer packageId) {
		this.packageId = packageId;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public Integer getPackageOrderNum() {
		return packageOrderNum;
	}
	public void setPackageOrderNum(Integer packageOrderNum) {
		this.packageOrderNum = packageOrderNum;
	}
	public String getPayTypeFinalUse() {
		return payTypeFinalUse;
	}
	public void setPayTypeFinalUse(String payTypeFinalUse) {
		this.payTypeFinalUse = payTypeFinalUse;
	}
	public String getOrderChannel() {
		return orderChannel;
	}
	public void setOrderChannel(String orderChannel) {
		this.orderChannel = orderChannel;
	}
	public String getOrderFromSysFlag() {
		return orderFromSysFlag;
	}
	public void setOrderFromSysFlag(String orderFromSysFlag) {
		this.orderFromSysFlag = orderFromSysFlag;
	}
	public Integer getCreateById() {
		return createById;
	}
	public void setCreateById(Integer createById) {
		this.createById = createById;
	}
	public String getCreateByNickname() {
		return createByNickname;
	}
	public void setCreateByNickname(String createByNickname) {
		this.createByNickname = createByNickname;
	}
	public String getOrderBeginTime() {
		return orderBeginTime;
	}
	public void setOrderBeginTime(String orderBeginTime) {
		this.orderBeginTime = orderBeginTime;
	}
	public String getReturnParams() {
		return returnParams;
	}
	public void setReturnParams(String returnParams) {
		this.returnParams = returnParams;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "PayRequestVo [frontPageUrl=" + frontPageUrl + ", packageId=" + packageId
				+ ", packageCode=" + packageCode + ", packageOrderNum=" + packageOrderNum
				+ ", payTypeFinalUse=" + payTypeFinalUse + ", orderChannel="
				+ orderChannel + ", orderFromSysFlag=" + orderFromSysFlag
				+ ", createById=" + createById + ", createByNickname=" + createByNickname
				+ ", orderBeginTime=" + orderBeginTime 
				+ "]";
	}

}
