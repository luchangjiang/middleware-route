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
@RequestMapping("/fh/star")
public class UsStarStockController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 美股-概念股，明星股
	private static final String getUsStarStock = "/star/getUsStarStock";
	
	/**
	 * 美股-概念股，明星股
	 * @param SECUTYPE  1 明星股   2 中概股
	 * @return
	 */
	@GetMapping("/getUsStarStock")
	public @ResponseBody R<?> getUsStarStock(int secuType){
		return restTemplate.getForObject(myProps.getFhServer() + getUsStarStock+"?secuType={1}", R.class,secuType);
	}
	
}
