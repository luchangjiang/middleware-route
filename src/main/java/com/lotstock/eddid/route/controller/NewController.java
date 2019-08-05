package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.util.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

@RestController
@RefreshScope
@RequestMapping("/yxg")
public class NewController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 机会情报
	private static final String query_chance = "/yxg/query_chance";
	// 股市震荡
	private static final String query_sign = "/yxg/query_sign";
	
	/**
	 * 机会情报
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_chance" ,method = RequestMethod.POST)
	public @ResponseBody
    ResultModel queryChance(int lastId, int count){
		return restTemplate.getForObject(myProps.getNewServer() + query_chance+"?lastId={1}&count={2}", ResultModel.class,lastId,count);
	}
	
	/**
	 * 股市震荡
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_sign" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel querySign(){
		return restTemplate.getForObject(myProps.getNewServer() + query_sign , ResultModel.class);
	}

}
