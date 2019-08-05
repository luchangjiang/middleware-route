package com.lotstock.eddid.route.controller;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.util.OIDCUtil;
import com.lotstock.eddid.route.util.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 查询
	private static final String query_custInfo = "/customer/query_custInfo";
	//
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 根据sessionCode 获取用户信息
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultModel queryCustInfo(String sessionCode){
		ResultModel body = getInfo(sessionCode);
    	if(body.getCode()==0){
			Map<String, Object> map = (Map<String, Object>) body.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			body.setResult(customerId);
    	}
    	return body;
	}

	@SuppressWarnings("unchecked")
	public ResultModel queryUserInfo(String sessionCode){
		ResultModel body = getInfo(sessionCode);
    	if(body.getCode()==0){
			Map<String, Object> map = (Map<String, Object>) body.getResult();
			body.setResult(map);
    	}
    	return body;
	}

	private ResultModel getInfo(String sessionCode){
		String url = myProps.getCustomerServer() + query_custInfo;
		MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
		params.add("sessionCode", sessionCode);
		ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(url, params, ResultModel.class);
		ResultModel body = responseEntity.getBody();
		return body;
	}

//	public boolean checkToken(String token){
//		String url = myProps.getCheckTokenServer() + "/token/introspection";
//
//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.add("Accept", "*/*");
//		requestHeaders.add("Authorization", "Basic Zm9vOmJhcg==");
//		requestHeaders.add("Cache-Control", "no-cache");
//		requestHeaders.add("Connection", "keep-alive");
//		requestHeaders.add("Content-Type", "application/x-www-form-urlencoded");
//		requestHeaders.add("Host", myProps.getCheckTokenServer().replace("http://", ""));
//		requestHeaders.add("User-Agent", "PostmanRuntime/7.11.0");
//		requestHeaders.add("accept-encoding", "gzip, deflate");
//		requestHeaders.add("content-length", "740");
//		RestTemplate template = new RestTemplate();
//		HttpEntity<String> requestEntity = new HttpEntity<String>(token, requestHeaders);
//		ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, requestEntity, String.class);
//		String str = response.getBody();
//		return true;
//	}



    public ResultModel getCusCustomerInfo(HttpServletRequest request) {
        ResultModel result = new ResultModel();
        String token = request.getHeader("access_token");
        //
        if(token == null || token.equals("")){
            result.setCode(-1);
            result.setMessage("token缺失!");
            return result;
        }else if(token.equals("-1")){
            String url = myProps.getCustomerServer() + "/customer/getGuestCustomerInfo";
            String customerInfoString = restTemplate.getForObject(url, String.class);
            JSONObject customerInfo = JSONObject.parseObject(customerInfoString);
            //
            result.setCode(0);
            result.setResult(customerInfo);
        }else {
            JSONObject userInfo = OIDCUtil.getUserInfo(myProps.getAwsIdpServer(), token);
            if (userInfo != null) {
                logger.info("access token is {}", request.getHeader("access_token"));
                logger.info("userinfo sub is {}", userInfo.get("sub"));
            } else {
                result.setCode(-1);
                result.setMessage("token已失效或者错误!");
                return result;
            }
			/**
			 * get customerKey by eddidId
             */
            String eddidId = userInfo.getString("sub");
            String url = myProps.getCustomerServer() + "/customer/getCustomerByEddidId?eddidId={1}";
            String customerInfoString = restTemplate.getForObject(url, String.class, eddidId);
            JSONObject customerInfo = JSONObject.parseObject(customerInfoString);
            //
//			/**
//			 * 特殊处理86. update in 2019/06/10
//			 */
//			String mobile = customerInfo.getString("mobile");
//			System.out.println("debug-userInfo : "+ JSON.toJSONString(customerInfo));
//			System.out.println("debug-mobile : "+ mobile);
//			if(mobile.startsWith("86")){
//				String tmpMobile = mobile.substring(2);
//				customerInfo.put("mobile",tmpMobile);
//			}
			//
            // add tokenInfo into customerInfo
			customerInfo.put("tokenInfo",userInfo);
            //
            result.setCode(0);
            result.setResult(customerInfo);
        }
        return result;
    }
	
}
