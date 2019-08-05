package com.lotstock.eddid.route.controller;

import java.util.Map;

import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;
import com.lotstock.eddid.route.vo.JydTradeVo;
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
 * 金盈岛小游戏
 * @author David
 *
 */
@RestController
@RefreshScope
@RequestMapping("/jyd_trade")
public class JydTradeController extends BaseController{
	
	protected static final Logger logger = Logger.getLogger(JydTradeController.class);
	
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 金币面额接口
	private static final String query_gold_configure = "/jyd_trade/query_gold_configure";
	
	// 涨跌幅接口
	private static final String query_choose_area = "/jyd_trade/query_choose_area";
		
	// 倒计时接口
	private static final String query_countdown = "/jyd_trade/query_countdown";
		
	// 下单接口
	private static final String order_trade = "/jyd_trade/order_trade";
		
	// 开奖接口
	private static final String open_lottery = "/jyd_trade/open_lottery";
	
	// 交易记录
	private static final String order_detail = "/jyd_trade/order_detail";
		
	// 盈利排行榜
	private static final String query_top = "/jyd_trade/query_top";
		
	/**
	 * 金币面额接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_gold_configure",method = RequestMethod.POST)
	public @ResponseBody
    ResultModel queryGoldConfigure(JydTradeVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getJydServer() + query_gold_configure + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
	/**
	 * 涨跌幅接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_choose_area",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryChooseArea(JydTradeVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getJydServer() + query_choose_area + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
	/**
	 * 倒计时接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_countdown",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryCountDown(JydTradeVo vo) throws Exception{
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getJydServer() + query_countdown + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 下单接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/order_trade",method = RequestMethod.POST)
	public @ResponseBody synchronized ResultModel orderTrade(JydTradeVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getJydServer() + order_trade + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	} 
	
	/**
	 * 开奖接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/open_lottery",method = RequestMethod.POST)
	public @ResponseBody ResultModel openLottery(JydTradeVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getJydServer() + open_lottery + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	} 
	
	/**
	 * 交易记录接口
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/order_detail",method = RequestMethod.POST)
	public @ResponseBody ResultModel orderDetail(JydTradeVo vo) throws Exception{
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setUserId(customerId);
			return restTemplate.getForObject(myProps.getJydServer() + order_detail + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 盈利排行榜
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_top",method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public @ResponseBody ResultModel queryRecordTop2(JydTradeVo vo) throws Exception{
		ResultModel re=this.queryUserInfo(vo.getSessionCode());
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			vo.setUserId(Integer.parseInt(map.get("customerId").toString()));
			vo.setNickName(map.get("nickName").toString());
			return restTemplate.getForObject(myProps.getJydServer() + query_top + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
}
