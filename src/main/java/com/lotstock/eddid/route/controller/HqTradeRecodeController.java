package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;
import com.lotstock.eddid.route.vo.HqTradeRecordVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

/**
 * 行情竞猜
 * @author pengj
 *
 */
@RestController
@RefreshScope
@RequestMapping("/hq_trade_record")
public class HqTradeRecodeController extends BaseController{
	
	protected static final Logger logger = Logger.getLogger(HqTradeRecodeController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 行情竞猜下单
	private static final String order = "/hq_trade_record/order";
	
	// 竞猜状态
	private static final String market_status = "/hq_trade_record/market_status";
	
	// 行情竞猜结果
	private static final String market_result = "/hq_trade_record/market_result";
	
	// 行情点击
	private static final String market_click = "/hq_trade_record/market_click";
	
	// 获取配置股票
	private static final String stockt_config = "/hq_trade_record/stockt_config";
	
	// 金币面额接口
	private static final String query_gold_configure = "/hq_trade_record/query_gold_configure";
	
	// 涨跌幅接口
	private static final String query_choose_area = "/hq_trade_record/query_choose_area";
		
	
	/**
	 * 行情竞猜下单接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/order",method = RequestMethod.POST)
	public @ResponseBody synchronized ResultModel orderTrade(HqTradeRecordVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getHqjcServer() + order + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 竞猜状态
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/market_status",method = RequestMethod.POST)
	public @ResponseBody ResultModel marketStatus(HqTradeRecordVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getHqjcServer() + market_status + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 行情竞猜结果
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/market_result",method = RequestMethod.POST)
	public @ResponseBody ResultModel marketResult(HqTradeRecordVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getHqjcServer() + market_result + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 行情点击
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/market_click",method = RequestMethod.POST)
	public @ResponseBody ResultModel marketClick(HqTradeRecordVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getHqjcServer() + market_click + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 获取配置股票
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/stockt_config",method = RequestMethod.POST)
	public @ResponseBody ResultModel stocktConfig() {
		return restTemplate.getForObject(myProps.getHqjcServer() + stockt_config, ResultModel.class);
	}
	
	/**
	 * 金币面额接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_gold_configure",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryGoldConfigure(HqTradeRecordVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getHqjcServer() + query_gold_configure + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}

	/**
	 * 涨跌幅接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_choose_area",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryChooseArea(HqTradeRecordVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getHqjcServer() + query_choose_area + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
}
