package com.lotstock.eddid.route.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 选股配置表
 */
public class ChooseStockCfg implements Serializable {

	private static final long serialVersionUID = -2260388125919493487L;
	private Integer cfgId;//选股配置id
	private String stockCode;//商品编码
	private String stockName;//商品名称
	private Integer status = 1;//状态：0免费 1付费
	private Date createTime;//创建时间
	private Date updateTime;//更新时间
	private String orgCode = "0001";//机构编码
	private String havePermissionImg;//有权限图片
	private String noPermissionImg;//无权限图片
	private String remark;//备注
	private String link;//链接地址
	private Integer orderNum = 0;//订阅人数

    public Integer getCfgId () {
        return cfgId;
    }

    public void setCfgId (Integer cfgId) {
        this.cfgId = cfgId;
    }

    public String getStockCode () {
        return stockCode;
    }

    public void setStockCode (String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName () {
        return stockName;
    }

    public void setStockName (String stockName) {
        this.stockName = stockName;
    }

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }

    public Date getCreateTime () {
        return createTime;
    }

    public void setCreateTime (Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime () {
        return updateTime;
    }

    public void setUpdateTime (Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOrgCode () {
        return orgCode;
    }

    public void setOrgCode (String orgCode) {
        this.orgCode = orgCode;
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

    public Integer getOrderNum () {
        return orderNum;
    }

    public void setOrderNum (Integer orderNum) {
        this.orderNum = orderNum;
    }
}