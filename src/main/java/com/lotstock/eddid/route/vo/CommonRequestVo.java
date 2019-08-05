package com.lotstock.eddid.route.vo;

/**
 * 公共的请求参数
 * @author liujf 2017年10月23日 上午9:51:46
 */
public class CommonRequestVo extends CommonSerializable{

	private static final long serialVersionUID = -4956964333561168249L;
	
	/**
	 * 公共的扩展请求参数，json格式的字符串
	 * {"KEY1":"VALUE1", "KEY2":"VALUE2"}
	 */
	private String jsonExt ;
	
	private String sessionCode ;
//	private Integer customerId ;
	private Integer customerKey  ;
	private String customerMobile;//订阅人手机号
	private String orgCode;//机构编码
	private Integer orgId;//机构id
	private Integer orgType;//机构类型
	
	private Integer pageIndex = 0; //不传则默认pageIndex=0 ; pageIndex<=0 表示不分页
	private Integer pageSize = 20 ; //pageIndex=0或不传时， 默认查询最新的20
	private String orderBy = "DESC" ; // ASC：升序；  DESC：降序（默认）
	
	
	public String getJsonExt() {
		return jsonExt;
	}

	public void setJsonExt(String jsonExt) {
		this.jsonExt = jsonExt;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

//	public Integer getCustomerId() {
//		return customerId;
//	}
//
//	public void setCustomerId(Integer customerId) {
//		this.customerId = customerId;
//	}

	public Integer getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(Integer customerKey) {
		this.customerKey = customerKey;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getOrgType() {
		return orgType;
	}

	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
	}
}
