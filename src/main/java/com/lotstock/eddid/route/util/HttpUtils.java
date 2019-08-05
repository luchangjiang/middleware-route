package com.lotstock.eddid.route.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HttpUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class) ;
	
	/**
	 * post发送json格式请求，设置请求头为 application/json
	 * @author liujf 2017年4月13日 下午4:51:08
	 * @param url 
	 * @param charset utf-8
	 * @param jsonData  {}
	 * @return JSONObject json对象data <br>
	 * 		null==>请求出错；<br>
	 * 		其他==>请求正常（可能没有数据返回时，new对象）；<br>
	 */
	public static JSONObject sendPostReturnJsonObj(String url, String charset, String jsonData){
		JSONObject jsonObject = null ;
		String jsonString = sendPostJson(url, charset, jsonData) ;
		if(StringUtils.isBlank(jsonString)){
			return null ;
		}
		jsonObject = JSONObject.parseObject(jsonString) ;
		return jsonObject ;
	}
	
	/**
	 * post发送json格式请求，设置请求头为 application/json
	 * @author liujf 2017年4月13日 下午4:51:08
	 * @param url 
	 * @param charset utf-8
	 * @param parameters  {}
	 * @return json String
	 */
	public static String sendPostJson(String url, String charset, String parameters) {  
        String body = null;  
        if(StringUtils.isBlank(parameters)){
        	parameters = "{}" ;
        }
        if(StringUtils.isBlank(charset)){
        	charset = "utf-8" ;
        }
        try {  
			HttpClient httpClient = new DefaultHttpClient();  
        	HttpPost method = new HttpPost(url); 
        	// 建立一个NameValuePair数组，用于存储欲传送的参数  
        	method.addHeader("Content-type","application/json; charset="+charset);  
        	method.setHeader("Accept", "application/json");  
        	method.setEntity(new StringEntity(parameters, charset));  
        	HttpResponse response = httpClient.execute(method);  
        	int statusCode = response.getStatusLine().getStatusCode();  
        	if (statusCode != HttpStatus.SC_OK) {  
        		logger.error("Method failed:" + response.getStatusLine());  
        	}  
        	body = EntityUtils.toString(response.getEntity());  
        } catch (IOException e) {
        	logger.error("调用接口发生错误",e); 
        	// 网络错误  
        }
        return body;  
    } 
	
	public static void main(String[] args) {
		JSONObject params = new JSONObject();
		params.put("sessionCode", "594e70b1ed5a453c929f4149582cc6ea1");
//		String[] ary = new String[]{"1","2","3"} ;
//		params.put("sysFlag", ary);
		List<String> list = new ArrayList<String>() ;
		list.add("1");
		list.add("3");
		list.add("2");
		params.put("sysFlag", list);
		
		JSONObject paramsJson = new JSONObject();
		paramsJson.put("version", "2.0");
		paramsJson.put("params", params) ;
		String jsonData = JSON.toJSONString(paramsJson) ;
		System.out.println(jsonData);
//		String json = sendPostJson("http://127.0.0.1:9095/customerRest/customerInfo", "UTF-8", jsonData) ;
//		System.out.println(json);
		
	}
}
