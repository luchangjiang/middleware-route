package com.lotstock.eddid.route.vo;

public class MessageReqVo {
	
	//模型code
	private String strategy_code;
	//消息类型code
	private String infocode;
	//消息标题
	private String title;
	//消息内容
	private String content;
	private String exdata;
	private String url;
	
	public String getStrategy_code() {
		return strategy_code;
	}
	public void setStrategy_code(String strategy_code) {
		this.strategy_code = strategy_code;
	}
	public String getInfocode() {
		return infocode;
	}
	public void setInfocode(String infocode) {
		this.infocode = infocode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExdata() {
		return exdata;
	}
	public void setExdata(String exdata) {
		this.exdata = exdata;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
