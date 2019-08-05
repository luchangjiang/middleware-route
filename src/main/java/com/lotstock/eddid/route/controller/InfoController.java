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
@RequestMapping("/info")
public class InfoController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 新闻列表
	private static final String query_news_list = "/info/query_news_list";
	// 新闻详情
	private static final String query_news_info = "/info/query_news_info";
	// 公告列表
	private static final String query_notices_list = "/info/query_notices_list";
	// 公告详情
	private static final String query_notice_info = "/info/query_notice_info";
	// 研报列表
	private static final String query_reports_list = "/info/query_reports_list";
	// 研报详情
	private static final String query_report_info = "/info/query_report_info";
	// 资金分布
	private static final String fund_design = "/info/fund_design";
	// 近5日资金分布
	private static final String fund_five_design = "/info/fund_five_design";
	
	/**
	 * 新闻列表
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_news_list" ,method = RequestMethod.POST)
	public @ResponseBody
    ResultModel queryNewsList(int page_no, int page_count, String symbols){
		return restTemplate.getForObject(myProps.getInfoServer() + query_news_list+"?page_no={1}&page_count={2}&symbols={3}", ResultModel.class,page_no,page_count,symbols);
	}
	
	/**
	 * 新闻详情
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_news_info" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryNewsInfo(String id, String symbols){
		return restTemplate.getForObject(myProps.getInfoServer() + query_news_info+"?id={1}&symbols={2}", ResultModel.class,id,symbols);
	}
	
	/**
	 * 公告列表
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_notices_list" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryNoticesList(int page_no,int page_count,String symbols){
		return restTemplate.getForObject(myProps.getInfoServer() + query_notices_list+"?page_no={1}&page_count={2}&symbols={3}", ResultModel.class,page_no,page_count,symbols);
	}
	
	/**
	 * 公告详情
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_notice_info" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryNoticeInfo(String id, String symbols){
		return restTemplate.getForObject(myProps.getInfoServer() + query_notice_info+"?id={1}&symbols={2}", ResultModel.class,id,symbols);
	}
	
	/**
	 * 研报列表
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_reports_list" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryReportsList(int page_no,int page_count,String symbols){
		return restTemplate.getForObject(myProps.getInfoServer() + query_reports_list+"?page_no={1}&page_count={2}&symbols={3}", ResultModel.class,page_no,page_count,symbols);
	}
	
	/**
	 * 研报详情
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_report_info" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryReportInfo(String id, String symbols){
		return restTemplate.getForObject(myProps.getInfoServer() + query_report_info+"?id={1}&symbols={2}", ResultModel.class,id,symbols);
	}
	
	/**
	 * 资金分布
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/fund_design" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel fundDesign(String en_prod_code){
		return restTemplate.getForObject(myProps.getInfoServer() + fund_design+"?en_prod_code={1}", ResultModel.class,en_prod_code);
	}

	/**
	 * 近5日资金分布
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/fund_five_design" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel fundFiveDesign(String prod_code){
		return restTemplate.getForObject(myProps.getInfoServer() + fund_five_design+"?prod_code={1}", ResultModel.class,prod_code);
	}
}
