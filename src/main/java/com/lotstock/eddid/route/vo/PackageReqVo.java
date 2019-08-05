package com.lotstock.eddid.route.vo;

/**
 *查询（产品/套餐）列表参数
 * @author liujf 2017年10月23日 上午10:09:14
 */
public class PackageReqVo extends CommonRequestVo {

	private static final long serialVersionUID = 521477213062131466L;
	//查询套餐列表参数
	private Integer packageIsFree ;	//收费类型查询，默认不传查询所有  1：免费；0：收费（默认0）
	private String productCode ;	//产品编码 默认不传查询所有
	private Integer packageCombType ;	//套餐组合类型 默认为1 (0：组合捆绑套餐； 1：单个独立套餐 )
	private String productSysFlag ; 
	private String productCodes ;	//查询多个产品的独立套餐信息
	
	//查询套餐内的产品列表参数
	private String packageCode ;//套餐编码
	
	public Integer getPackageIsFree() {
		return packageIsFree;
	}
	public void setPackageIsFree(Integer packageIsFree) {
		this.packageIsFree = packageIsFree;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Integer getPackageCombType() {
		return packageCombType;
	}
	public void setPackageCombType(Integer packageCombType) {
		this.packageCombType = packageCombType;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public String getProductCodes() {
		return productCodes;
	}
	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}
	public String getProductSysFlag() {
		return productSysFlag;
	}
	public void setProductSysFlag(String productSysFlag) {
		this.productSysFlag = productSysFlag;
	}
	
}