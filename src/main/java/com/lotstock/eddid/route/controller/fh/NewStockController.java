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
@RequestMapping("/fh/newstock")
public class NewStockController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 上市新股
	private static final String listing = "/newstock/listing";
	
	/**
	 * 上市新股
	 * @param vo
	 * @return
	 */
	@GetMapping("/listing")
	public @ResponseBody R<?> getListing(String marketType, String language){
		return restTemplate.getForObject(myProps.getFhServer() + listing+"?marketType={1}&language={2}", R.class,marketType,language);
	}
	
}
