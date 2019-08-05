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
@RequestMapping("/fh/finance")
public class FinanceController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 财务-资产负债表
	private static final String balance_info = "/finance/balance_info";
	// 财务-综合损益表
	private static final String income_info = "/finance/income_info";
	// 财务-现金流量表
	private static final String cash_info = "/finance/cash_info";
	
	/**
	 * 财务-资产负债表
	 * @param vo
	 * @return
	 */
	@GetMapping("/balance_info")
	public @ResponseBody R<?> getBalanceInfo(String stockCode, String reportType, String marketType){
		return restTemplate.getForObject(myProps.getFhServer() + balance_info+"?stockCode={1}&reportType={2}&marketType={3}", R.class,stockCode,reportType,marketType);
	}
	
	/**
	 * 财务-综合损益表
	 * @param vo
	 * @return
	 */
	@GetMapping("/income_info")
	public @ResponseBody R<?> getIncomeInfo(String stockCode, String reportType, String marketType){
		return restTemplate.getForObject(myProps.getFhServer() + income_info+"?stockCode={1}&reportType={2}&marketType={3}", R.class,stockCode,reportType,marketType);
	}
	
	/**
	 * 财务-现金流量表
	 * @param vo
	 * @return
	 */
	@GetMapping("/cash_info")
	public @ResponseBody R<?> getCashInfo(String stockCode, String reportType, String marketType){
		return restTemplate.getForObject(myProps.getFhServer() + cash_info+"?stockCode={1}&reportType={2}&marketType={3}", R.class,stockCode,reportType,marketType);
	}
}
