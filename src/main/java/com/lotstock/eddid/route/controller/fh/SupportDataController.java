package com.lotstock.eddid.route.controller.fh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.R;

@RestController
@RefreshScope
@RequestMapping("/fh/support")
public class SupportDataController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 财务数据支持
	private static final String listing = "/support/getSupportData";
	
	/**
	 * 财务数据支持
	 * @param vo
	 * @return
	 */
	@GetMapping("/getSupportData")
	public @ResponseBody R<?> getListing(String marketType, String stockCode){
		return restTemplate.getForObject(myProps.getFhServer() + listing+"?marketType={1}&stockCode={2}", R.class,marketType,stockCode);
	}
	
}
