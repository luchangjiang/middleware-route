package com.lotstock.eddid.route.controller.stockconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.R;

@RestController
@RefreshScope
@RequestMapping("/future")
public class FuturesCodeReController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 期货编码转行情编码
	private static final String query_quotes_code = "/future/query_quotes_code";
	// 获取期货代码
	private static final String get_deal_code = "/future/get_deal_code";
	
	/**
	 * 期货编码转行情编码
	 * @return
	 */
	@GetMapping("/query_quotes_code")
	public @ResponseBody R<?> queryQuotesCode(@Param("tradeNoObj")String tradeNoObj){
		return restTemplate.getForObject(myProps.getStockconfigServer() + query_quotes_code+"?tradeNoObj={1}", R.class,tradeNoObj);
	}
	
	/**
	 * 获取期货代码
	 * @return
	 */
	@GetMapping("/get_deal_code")
	public @ResponseBody R<?> getDealCode(String dealCode, int marketCode){
		return restTemplate.getForObject(myProps.getStockconfigServer() + get_deal_code+"?dealCode={1}&marketCode={2}", R.class,dealCode,marketCode);
	}
}
