package com.lotstock.eddid.route.vo;

/**
 * Created by pengj
 */
public class PacketReqVo extends BaseVo {
	
	private static final long serialVersionUID = 2016975185303517099L;
	
	private int redPacketId;
    private String mobile;
    private int from;
    private String openid;
    private String pwd;
    private String captcha;
    private String url;
    private String orgCode;

    public int getRedPacketId() {
        return redPacketId;
    }

    public void setRedPacketId(int redPacketId) {
        this.redPacketId = redPacketId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
    
    
}
