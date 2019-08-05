package com.lotstock.eddid.route.util;

/**
 * json转换固定格式
 * @author David
 *
 */
public class ResultModel {
	
	public int code=1;
    public String message;
    public Object result;
     
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}

}


