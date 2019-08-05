package com.lotstock.eddid.route.util;

import java.util.HashMap;
import java.util.Map;

public class Re extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static final String status = "code";
	public static final String message = "message";
	public static final String data = "result";

	public Re() {
		put(status, 0);
		put(message, "success");
	}

	public static Re error() {
		return error(500, "操作异常");
	}

	public static Re error(String msg) {
		return error(500, msg);
	}

	public static Re error(int code, String msg) {
		Re r = new Re();
		r.put(status, code);
		r.put(message, msg);
		return r;
	}

	public static Re ok(String msg) {
		Re r = new Re();
		r.put(message, msg);
		return r;
	}

	public static Re ok(int code, String msg) {
		Re r = new Re();
		r.put(status, code);
		r.put(message, msg);
		return r;
	}

	public static Re ok(Map<String, Object> map) {
		Re r = new Re();
		r.putAll(map);
		return r;
	}

	public static Re ok() {
		return new Re();
	}

	public Re put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Re putData(Object value) {
		super.put(data, value);
		return this;
	}

	public Object getData() {
		return super.get(data);
	}

	public int getCode() {
		return (Integer) this.get(status);
	}

	public Boolean isOk() {
		Integer code = (Integer) this.get(status);
		if (code == 0)
			return true;
		return false;
	}

}