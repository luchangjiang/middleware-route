package com.lotstock.eddid.route.util;

/**
 * @author ：River
 * @date ：Created in 6/3/2019 2:52 PM
 * @description：result model for eddid
 * @modified By：
 * @version: $
 */
public class ResultModel2 {

    public int code=1;
    public String msg;
    public Object data;

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

}