package com.lotstock.eddid.route.model;
import java.io.Serializable;

/**
 * 
 */
public class TradeProduct implements Serializable {

	private static final long serialVersionUID = -2260388125919493487L;
	private Integer id;
	private String name;//产品名称
	private String code;//产品编号
	private Integer type;//产品类型
	private String detail;//产品描叙
	private Integer source;//策略来源(1:钱坤指)
	private String extra;//附加信息
	private Integer categoryId;//分类ID
	private String platformCode;//平台代码
	private String havePermissionImg;//有权限图片
	private String noPermissionImg;//无权限图片
	private String remark;//备注
	private String link;//链接地址

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getCode () {
        return code;
    }

    public void setCode (String code) {
        this.code = code;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public String getDetail () {
        return detail;
    }

    public void setDetail (String detail) {
        this.detail = detail;
    }

    public Integer getSource () {
        return source;
    }

    public void setSource (Integer source) {
        this.source = source;
    }

    public String getExtra () {
        return extra;
    }

    public void setExtra (String extra) {
        this.extra = extra;
    }

    public Integer getCategoryId () {
        return categoryId;
    }

    public void setCategoryId (Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getPlatformCode () {
        return platformCode;
    }

    public void setPlatformCode (String platformCode) {
        this.platformCode = platformCode;
    }

    public String getHavePermissionImg () {
        return havePermissionImg;
    }

    public void setHavePermissionImg (String havePermissionImg) {
        this.havePermissionImg = havePermissionImg;
    }

    public String getNoPermissionImg () {
        return noPermissionImg;
    }

    public void setNoPermissionImg (String noPermissionImg) {
        this.noPermissionImg = noPermissionImg;
    }

    public String getRemark () {
        return remark;
    }

    public void setRemark (String remark) {
        this.remark = remark;
    }

    public String getLink () {
        return link;
    }

    public void setLink (String link) {
        this.link = link;
    }
}