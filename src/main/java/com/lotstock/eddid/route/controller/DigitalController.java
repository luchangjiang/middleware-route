package com.lotstock.eddid.route.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

import net.sf.jxls.transformer.XLSTransformer;

@RestController
@RefreshScope
@RequestMapping("/stock")
public class DigitalController extends BaseController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 查询股票
	private static final String query_stock = "/stock/query_stock";
	// 获取买卖盘初始数据
	private static final String get_buySell_init = "/stock/get_buySell_init";
	// 获取买卖盘分析数据
	private static final String get_buySell = "/stock/get_buySell";
	// 异动榜
	private static final String change_list = "/stock/change_list";
	// 导出
	private static final String do_export = "/stock/do_export";
	
	/**
	 * 查询股票
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_stock" ,method = RequestMethod.POST)
	public @ResponseBody
    ResultModel queryStock(String code, String language){
		return restTemplate.getForObject(myProps.getDigitalServer() + query_stock+"?code={1}&language={2}", ResultModel.class,code,language);
	}
	
	/**
	 * 获取买卖盘初始数据
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/get_buySell_init" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel getBuySellInit(){
		return restTemplate.getForObject(myProps.getDigitalServer() + get_buySell_init, ResultModel.class);
	}
	
	/**
	 * 获取买卖盘初始数据
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get_buySell" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel getBuySell(String code, String language, Integer days,String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			return restTemplate.getForObject(myProps.getDigitalServer() + get_buySell+"?code={1}&language={2}&customerId={3}&days={4}", ResultModel.class,code,language,customerId,days);
		}else{
			return re;
		}
	}
	
	/**
	 * 异动榜
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/change_list" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel getBuySell(String language, String sessionCode, int pageStart, int pageSize, String sort, String sortType){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			return restTemplate.getForObject(myProps.getDigitalServer() + change_list +"?language={1}&customerId={2}&pageStart={3}&pageSize={4}&sort={5}&sortType={6}", 
					ResultModel.class,language,customerId,pageStart,pageSize,sort,sortType);
		}else{
			return re;
		}
	}
	
	/**
	 * 导出
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/do_export")
	public @ResponseBody ResultModel doExport(String code, String language,String sessionCode, Integer days, HttpServletResponse response) throws Exception{
		ResultModel result=new ResultModel();
		if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			List<?> list = restTemplate.getForObject(myProps.getDigitalServer() + do_export+"?code={1}&language={2}&customerId={3}&days={4}", 
					List.class,code,language,customerId,days);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=" + 
					new String(("成交列表"+" "+ TimeUtil.formatDate(new Date(),"yyyyMMdd")+".xls").getBytes("GBK"), "ISO8859-1"));
			// 导出
			try (
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("excelTemplates/DealList.xls"); OutputStream out = response.getOutputStream()) {
				if(is == null){
					throw new Exception("找不到模板文件");
				}
				Map<String, Object> exportData = new HashMap<String, Object>();
				exportData.put("dataList", list);
				HSSFWorkbook workbook = new HSSFWorkbook(is);
		        XLSTransformer transformer = new XLSTransformer();
		        transformer.transformWorkbook(workbook, exportData);
		        workbook.write(out);
		        result.setCode(0);
		        result.setMessage("成功");
			} catch (Exception e) {
				result.setCode(-1);
				result.setMessage("失败");
				e.printStackTrace();
			}
			return result;
		}else{
			return re;
		}
	}
	
	/**
	 * 查经纪人-经纪人席位排名
	 */
	@RequestMapping("/stockBroker/queryStockTradeByBroker")
	public ResultModel queryStockTradeByBroker(String brokerName, String stockCode, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryStockTradeByBroker?stockCode={1}&brokerName={2}&days={3}&language={4}", ResultModel.class, stockCode, brokerName, days, language);
	}
	
	/**
	 * 查经纪人-股票阶段排名
	 */
	@RequestMapping("/stockBroker/queryAllStockTradeByBroker")
	public ResultModel queryAllStockTradeByBroker(String brokerName, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
			result.setCode(-1);
			result.setMessage("参数错误");
			return result;
		}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryAllStockTradeByBroker?brokerName={1}&days={2}&language={3}", ResultModel.class, brokerName, days, language);
	}
	
	/**
	 * 查股票-经纪人席位排名
	 */
	@RequestMapping("/stockBroker/queryBrokerTradeByStock")
	public ResultModel queryBrokerTradeByStock(String stockCode, String brokerName, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryBrokerTradeByStock?stockCode={1}&brokerName={2}&days={3}&language={4}", ResultModel.class, stockCode, brokerName, days, language);
	}
	
	/**
	 * 查股票-经济阶段排名
	 */
	@RequestMapping("/stockBroker/queryAllBrokerTradeByStock")
	public ResultModel queryAllBrokerTradeByStock(String stockCode, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
			result.setCode(-1);
			result.setMessage("参数错误");
			return result;
		}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryAllBrokerTradeByStock?stockCode={1}&days={2}&language={3}", ResultModel.class, stockCode, days, language);
	}
	
	@RequestMapping("/stockBroker/queryStockBrokerTrade")
	public ResultModel queryStockBrokerTrade(String brokerName, String stockCode, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryStockBrokerTrade?brokerName={1}&stockCode={2}&days={3}&language={4}", ResultModel.class, brokerName, stockCode, days, language);
	}
	
	@RequestMapping("/stockBroker/queryAllStockTrade")
	public ResultModel queryAllStockTrade(String brokerName, String stockCode, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
			result.setCode(-1);
			result.setMessage("参数错误");
			return result;
		}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryAllStockTrade?brokerName={1}&stockCode={2}&days={3}&language={4}", ResultModel.class, brokerName, stockCode, days, language);
	}
	
	@RequestMapping("/stockBroker/queryAllBrokerTrade")
	public ResultModel queryAllBrokerTrade(String brokerName, String stockCode, Integer days, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
			result.setCode(-1);
			result.setMessage("参数错误");
			return result;
		}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryAllBrokerTrade?brokerName={1}&stockCode={2}&days={3}&language={4}", ResultModel.class, brokerName, stockCode, days, language);
	}
	
	@RequestMapping("/stockBroker/queryAllStockList")
	public ResultModel queryAllStockList(String stock, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryAllStockList?stock={1}&language={2}", ResultModel.class, stock, language);
	}
	
	
	@RequestMapping("/stockBroker/queryAllBrokerList")
	public ResultModel queryAllBrokerList(String broker, String sessionCode, String language) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
			result.setCode(-1);
			result.setMessage("参数错误");
			return result;
		}
		ResultModel re = this.queryUserInfo(sessionCode);
		if (re.getCode() != 0) {
			return re;
		}
		
		return restTemplate.getForObject(myProps.getDigitalServer() + "/stockBroker/queryAllBrokerList?broker={1}&language={2}", ResultModel.class, broker, language);
	}
}
