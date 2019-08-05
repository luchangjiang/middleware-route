package com.lotstock.eddid.route.vo;

import java.io.Serializable;

/**
 * Created by tians_000 on 2017/8/3.
 */
public class BaseVo implements Serializable{
	private static final long serialVersionUID = 8186632246784660912L;
	private Integer customerId;
    private String sessionCode;
    private String openid;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
