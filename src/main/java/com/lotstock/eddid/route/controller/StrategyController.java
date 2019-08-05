package com.lotstock.eddid.route.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lotstock.eddid.route.model.CallbackBean;
import com.lotstock.eddid.route.model.TradeProduct;
import com.lotstock.eddid.route.model.TradeRecord;
import com.lotstock.eddid.route.vo.PackageOrderResVo;
import com.lotstock.eddid.route.vo.RouteReqVo;
import com.lotstock.eddid.route.vo.StrategyListReqVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.PayUtils;
import com.lotstock.eddid.route.util.Re;
import com.lotstock.eddid.route.util.RedisUtils;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;
import com.lotstock.eddid.route.util.TimeUtil;

/**
 * 策略
 * @author pengj
 */
@RestController
@RefreshScope
@RequestMapping("/strategy")
public class StrategyController extends BaseController {

	protected static final Logger logger = Logger.getLogger(StrategyController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private PayUtils payUtils;
	
	//分转换为元
	private static final int DEFAULT_AM=100;
		
	// 2.1	策略列表
	private static final String list = "/strategy/list";
	
	// 2.2	策略详细
	private static final String detail = "/strategy/detail";
		
	// 2.3	策略持股分布
	private static final String hold_distribute = "/strategy/hold_distribute";
		
	// 2.4	策略属性枚举
	private static final String attr_enum = "/strategy/attr_enum";
		
	// 2.5	用户订阅策略列表
	private static final String sub_list = "/sub/list";
	
	// 2.6	我的订阅记录
	private static final String orderList = "/sub/orderList";
	
	// 2.8	路由接口
	private static final String route = "/strategy/route";
	
	// 2.7	短信推送开关设置
	private static final String handleSwitch = "/strategy/handleSwitch";
		
	// 2.9	用户是否开启短信推送
	private static final String isSwitch = "/strategy/isSwitch";
	
	// 股多多查询未过期订阅信息
    private static final String queryMySubInfoBySys="/gddSub/queryMySubInfoBySys";
    
    // 股多多我的策略
    private static final String my_strategy="/sub/my_strategy";
    
    // 股多多取消订单（PC）
    private static final String cancleOrderHis="/gddSub/cancleOrderHis";
    
    //策略常量
  	private static final String productSysFlag="cl001";
	
	/**
	 * 策略列表
	 * @param vo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list" ,method = RequestMethod.POST)
	public @ResponseBody
    ResultModel strategyList(StrategyListReqVo vo) throws Exception {
		ResultModel re=new ResultModel();
		if(null!=vo.getSessionCode()){
			re=this.queryUserInfo(vo.getSessionCode());
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				vo.setCustomerId(Long.parseLong(map.get("customerId").toString()));
				vo.setCustomerKey(Long.parseLong(map.get("customerKey").toString()));
				vo.setOrgCode(map.get("orgCode").toString());
				logger.info("策略列表获取用户信息>>>>customerId:"+map.get("customerId").toString()+">>customerKey:"+map.get("customerKey").toString()+
						">>orgCode:"+map.get("orgCode").toString());
			}else{
				return re;
			}
		}
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + list + StrUtils.getParams(BeanUtils.describe(vo)), null, Re.class);
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		if(r.getCode() == 0){
			JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("data").toString());
			for(int x = 0; x<jsonArray.size();x++){
				jsonValues.add(jsonArray.getJSONObject(x));
			}
		}
		List<JSONObject> jsonDataList = new ArrayList<JSONObject>();
		String[] strEnum = new String[]{"long_prudent_3","qkz_shortstock","qkz_stockpool"};
		for(int i = 0;i < strEnum.length;i++){//过滤稳健100，金股池，牛股多多以外的策略
			JSONObject jsonSubData = new JSONObject();
			for(int j = 0;j < jsonValues.size();j++){
				if(!jsonValues.get(j).getString("attribute").isEmpty()){
					JSONObject json_attribute = JSONObject.parseObject(jsonValues.get(j).getString("attribute"));
					if(strEnum[i].equals(json_attribute.getString("strategyCode"))){
						jsonSubData.put("attribute", json_attribute);//策略基本属性
					}
				}
				if(!jsonValues.get(j).getString("statistics").isEmpty()){
					JSONObject json_statistics = JSONObject.parseObject(jsonValues.get(j).getString("statistics"));
					if(strEnum[i].equals(json_statistics.getString("strategy_code"))){
						jsonSubData.put("statistics", json_statistics);//策略静态统计
					}
				}
				/*if(!jsonValues.get(j).getString("lately_trading_time").isEmpty()){
					jsonSubData.put("lately_trading_time", jsonValues.get(j).getString("lately_trading_time"));//最近调仓时间
				}
				
				if(!jsonValues.get(j).getString("rate_index").isEmpty()){
					jsonSubData.put("rate_index", JSONObject.parseObject(jsonValues.get(j).getString("rate_index")));//每日收益率列表
				}
				
				if(!jsonValues.get(j).getString("sub_status").isEmpty()){
					jsonSubData.put("sub_status", JSONObject.parseObject(jsonValues.get(j).getString("sub_status")));//用户订阅状态
				}
				
				if(!jsonValues.get(j).getString("trading_today_count").isEmpty()){
					jsonSubData.put("trading_today_count", jsonValues.get(j).getString("trading_today_count"));//策略今日调仓数量
				}*/
			}
			if(!jsonSubData.isEmpty()){
				jsonDataList.add(jsonSubData);
			}
		}
		//按累计收益率降序
		Collections.sort(jsonDataList, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				JSONObject er1 = o1.getJSONObject("statistics");
				JSONObject er2 = o2.getJSONObject("statistics");
				Double earn_rate_1 = (double)er1.getDouble("earn_rate");
				Double earn_rate_2 = (double)er2.getDouble("earn_rate");
				if (earn_rate_1 < earn_rate_2) {
					return 1;
				}
				if (earn_rate_1 == earn_rate_2) {
					return 0;
				}
				return -1;
			}
		});

		JSONObject jsonData = new JSONObject();
		jsonData.put("data", jsonDataList);
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(jsonData);
		return re;
	}
	
	/**
	 * 策略详细
	 * @param String 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/detail" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel strategyDetail(String strategy_code, String sessionCode, String orgCode, boolean is_full, boolean is_statistics) {
		ResultModel re=new ResultModel();
		Long customerId = null;
		Long customerKey = null;
		if(null!=sessionCode){
			re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				customerId = Long.parseLong(map.get("customerId").toString());
				customerKey = Long.parseLong(map.get("customerKey").toString());
				logger.info("策略详细获取用户信息>>>>customerId:"+customerId+">>customerKey:"+customerKey);
			}else{
				return re;		
			}
		}
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + detail + "?strategy_code={1}&orgCode={2}&customerId={3}&customerKey={4}&is_full={5}&is_statistics={6}",
				null, Re.class,strategy_code,orgCode,customerId,customerKey,is_full,is_statistics);
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(r.getData() == null ? "" : r.getData());
		return re;
	}
	
	/**
	 * 策略持股分布
	 * @param String strategy_code
	 * @return
	 */
	@RequestMapping(value = "/hold_distribute" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel holdDistribute(String strategyCode) {
		ResultModel re= new ResultModel();
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + hold_distribute + "?strategyCode={1}",
				null, Re.class,strategyCode);
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(r.getData() == null ? "" : r.getData());
		return re;
	}
	
	/**
	 * 策略属性枚举
	 * @return
	 */
	@RequestMapping(value = "/attr_enum" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel strategyEnum() {
		ResultModel re= new ResultModel();
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + attr_enum,null, Re.class);
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(r.getData() == null ? "" : r.getData());
		return re;
	}
	
	/**
	 * 用户订阅策略列表
	 * @param String 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sub/list" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel subList(String sessionCode, Integer pageIndex, Integer pageSize,
			boolean isExpired, boolean isPush, Integer soonExpireDays) {
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			Long customerKey = Long.parseLong(map.get("customerKey").toString());
			String orgCode = map.get("orgCode").toString();
			logger.info("用户订阅策略列表获取用户信息>>>>orgCode:"+orgCode+">>customerKey:"+customerKey);
			Re r = restTemplate.postForObject(myProps.getStrategyServer() + sub_list + "?orgCode={1}&customerKey={2}&pageIndex={3}&pageSize={4}&isExpired={5}&isPush={6}&soonExpireDays={7}",
					null, Re.class,orgCode,customerKey,pageIndex,pageSize,isExpired,isPush,soonExpireDays);
			re.setCode(Integer.parseInt(r.get("code").toString()));
			re.setMessage(r.get("message").toString());
			re.setResult(r.getData() == null ? "" : r.getData());
			return re;
		}else {
			return re;
		}
	}
	
	/**
	 * 我的订阅记录
	 * @param vo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sub/orderList" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel orderList(PackageOrderResVo vo) throws Exception {
		ResultModel re=this.queryUserInfo(vo.getSessionCode());
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			vo.setCustomerId(Long.parseLong(map.get("customerId").toString()));
			vo.setCustomerKey(Long.parseLong(map.get("customerKey").toString()));
			vo.setOrgCode(map.get("orgCode").toString());
			vo.setCustomerMobile(map.get("mobile").toString());
			logger.info("我的订阅记录获取用户信息>>>>orgCode:"+map.get("orgCode").toString()+">>customerKey:"+map.get("customerKey").toString()+
					">>customerId:"+map.get("customerId").toString()+">>mobile:"+map.get("mobile").toString());
			Re r = restTemplate.postForObject(myProps.getStrategyServer() + orderList + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			re.setCode(Integer.parseInt(r.get("code").toString()));
			re.setMessage(r.get("message").toString());
			re.setResult(r.getData() == null ? "" : r.getData());
			return re;
		}else {
			return re;
		}
	}
	
	/**
	 * 2.5	短信推送开关设置
	 * @param vo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/handleSwitch" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel handleSwitch(String sessionCode, String strategyCode) throws Exception {
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			Integer customerKey = Integer.parseInt(map.get("customerKey").toString());
			logger.info("短信推送开关设置获取用户信息>>>>customerKey:"+customerKey);
			Re r = restTemplate.postForObject(myProps.getStrategyServer() + handleSwitch + "?customerKey={1}&strategyCode={2}",
					null, Re.class,customerKey,strategyCode);
			re.setCode(Integer.parseInt(r.get("code").toString()));
			re.setMessage(r.get("message").toString());
			re.setResult(r.getData() == null ? "" : r.getData());
			return re;
		}else {
			return re;
		}
	}
	
	/**
	 * 2.6	用户是否开启短信推送
	 * @param vo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/isSwitch" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel isSwitch(String sessionCode, String strategyCode) throws Exception {
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			Integer customerKey = Integer.parseInt(map.get("customerKey").toString());
			logger.info("用户是否开启短信推送获取用户信息>>>>customerKey:"+customerKey);
			Re r = restTemplate.postForObject(myProps.getStrategyServer() + isSwitch + "?customerKey={1}&strategyCode={2}",
					null, Re.class,customerKey,strategyCode);
			re.setCode(Integer.parseInt(r.get("code").toString()));
			re.setMessage(r.get("message").toString());
			re.setResult(r.getData() == null ? "" : r.getData());
			return re;
		}else {
			return re;
		}
	}
	
	/**
	 * 路由接口
	 * @param vo 
	 * @return
	 */
	@RequestMapping(value = "/route" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel route(RouteReqVo vo) throws Exception {
		ResultModel re = new ResultModel();
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		String[] strEnum = new String[]{"long_prudent_3","qkz_shortstock","qkz_stockpool"};
		if(vo.getType() == 2016 ){
			if(r.getCode() == 0){
				JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONObject resultObject = new JSONObject(); 
				for(int i = 0;i < strEnum.length;i++){//过滤稳健100，金股池，牛股多多以外的策略
					if(strEnum[i].equals(jsonObject.getString("strategy_code"))){
						resultObject.put("strategy_id", jsonObject.getInteger("strategy_id"));
						resultObject.put("strategy_code", jsonObject.getString("strategy_code"));
						resultObject.put("strategy_name", jsonObject.getString("strategy_name"));
					}
				}
				re.setCode(Integer.parseInt(r.get("code").toString()));
				re.setMessage(r.get("message").toString());
				re.setResult(resultObject);
				return re;
			}
		}
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(r.getData() == null ? "" : r.getData());
		return re;
	}
	
	/**
	 * 多多金股池排行
	 * @param vo 
	 * @return
	 */
	@RequestMapping(value = "/ranking" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel ranking(RouteReqVo vo) throws Exception {
		ResultModel re = new ResultModel();
		List<TradeRecord> resList = new ArrayList<>();
		int num = vo.getPageSize();
		vo.setType(2002);
		vo.setLimit_type("close");
		vo.setPageIndex(vo.getPageIndex());
		vo.setPageSize(30);
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(Integer.parseInt(r.get("code").toString()) == 0){
			JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("trade_record").toString());
			List<TradeRecord> listRecord = jsonArray.toJavaList(TradeRecord.class);
			//降序
			Collections.sort(listRecord, new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					TradeRecord vo1 = (TradeRecord) o1;
					TradeRecord vo2 = (TradeRecord) o2;
					Double diff = vo2.getEarn_rate() - vo1.getEarn_rate();
					if(diff>0){
						return 1;
					}else{
						return -1;
					}
				}
			});
			for (int i = 0; i < listRecord.size() && i < num; i++) {
				resList.add(listRecord.get(i));
			}
		}
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(resList);
		return re;
	}
	
	/**
	 * 多多配股首页
	 * @param vo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/allotment_index" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel allotment_index(String sessionCode,String orgCode){
		ResultModel re = new ResultModel();
		Integer code = 0;
		String message="成功";
		Long customerId = null;
		Long customerKey = null;
		JSONArray jsonArray = new JSONArray();
		try{
			if(null!=sessionCode){
				ResultModel result=this.queryUserInfo(sessionCode);
				if(result.getCode()==0){
					Map<String, Object> map=(Map<String, Object>)result.getResult();
					customerId = Long.parseLong(map.get("customerId").toString());
					customerKey = Long.parseLong(map.get("customerKey").toString());
					logger.info("多多配股首页获取用户信息>>>>customerId:"+map.get("customerId").toString()+">>customerKey:"+map.get("customerKey").toString());
				}else {
					return result;
				}
			}

			// ====================保守===================
			/*JSONArray defensiveArray = new JSONArray();
			JSONObject long_defensive_3 = new JSONObject();
			JSONObject long_defensive_2 = new JSONObject();
			JSONObject long_defensive_1 = new JSONObject();
			//保守100
			long_defensive_3 = muchIndexData("long_defensive_3",orgCode,customerId,customerKey,restTemplate,myProps,long_defensive_3,2003);
			//保守30
			long_defensive_2 = muchIndexData("long_defensive_2",orgCode,customerId,customerKey,restTemplate,myProps,long_defensive_2,2003);
			//保守05
			long_defensive_1 = muchIndexData("long_defensive_1",orgCode,customerId,customerKey,restTemplate,myProps,long_defensive_1,2003);
			defensiveArray.add(long_defensive_3);
			defensiveArray.add(long_defensive_2);
			defensiveArray.add(long_defensive_1);
			// 保守型排序
			List<JSONObject> jsonValues1 = new ArrayList<JSONObject>();
		    for (int x = 0; x < defensiveArray.size(); x++) {
		        jsonValues1.add(defensiveArray.getJSONObject(x));
		    }
			Collections.sort(jsonValues1, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					Double earn_rate_1 = (double)o1.getDouble("earn_rate");
					Double earn_rate_2 = (double)o2.getDouble("earn_rate");
					if (earn_rate_1 < earn_rate_2) {
						return 1;
					}
					if (earn_rate_1 == earn_rate_2) {
						return 0;
					}
					return -1;
				}
			});

			// ====================进取===================
			JSONArray aggressiveArray = new JSONArray();
			JSONObject long_aggressive_3 = new JSONObject();
			JSONObject long_aggressive_2 = new JSONObject();
			JSONObject long_aggressive_1 = new JSONObject();
			//进取100
			long_aggressive_3 = muchIndexData("long_aggressive_3",orgCode,customerId,customerKey,restTemplate,myProps,long_aggressive_3,2003);
			//进取30
			long_aggressive_2 = muchIndexData("long_aggressive_2",orgCode,customerId,customerKey,restTemplate,myProps,long_aggressive_2,2003);
			//进取05
			long_aggressive_1 = muchIndexData("long_aggressive_1",orgCode,customerId,customerKey,restTemplate,myProps,long_aggressive_1,2003);
			aggressiveArray.add(long_aggressive_3);
			aggressiveArray.add(long_aggressive_2);
			aggressiveArray.add(long_aggressive_1);
			// 进取排序
			List<JSONObject> jsonValues2 = new ArrayList<JSONObject>();
		    for (int y = 0; y < aggressiveArray.size(); y++) {
		    	jsonValues2.add(aggressiveArray.getJSONObject(y));
		    }
			Collections.sort(jsonValues2, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					Double earn_rate_1 = (double)o1.getDouble("earn_rate");
					Double earn_rate_2 = (double)o2.getDouble("earn_rate");
					if (earn_rate_1 < earn_rate_2) {
						return 1;
					}
					if (earn_rate_1 == earn_rate_2) {
						return 0;
					}
					return -1;
				}
			});*/
			
			// ====================稳健===================
			JSONArray prudentArray = new JSONArray();
			JSONObject long_prudent_3 = new JSONObject();
			/*JSONObject long_prudent_2 = new JSONObject();
			JSONObject long_prudent_1 = new JSONObject();*/
			//稳健100
			long_prudent_3 = muchIndexData("long_prudent_3",orgCode,customerId,customerKey,restTemplate,myProps,long_prudent_3,2003);
			/*//稳健30
			long_prudent_2 = muchIndexData("long_prudent_2",orgCode,customerId,customerKey,restTemplate,myProps,long_prudent_2,2003);
			//稳健05
			long_prudent_1 = muchIndexData("long_prudent_1",orgCode,customerId,customerKey,restTemplate,myProps,long_prudent_1,2003);*/
			prudentArray.add(long_prudent_3);
			/*prudentArray.add(long_prudent_2);
			prudentArray.add(long_prudent_1);*/
			// 稳健排序
			List<JSONObject> jsonValues3 = new ArrayList<JSONObject>();
		    for (int z = 0; z < prudentArray.size(); z++) {
		    	jsonValues3.add(prudentArray.getJSONObject(z));
		    }
			Collections.sort(jsonValues3, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					Double earn_rate_1 = (double)o1.getDouble("earn_rate");
					Double earn_rate_2 = (double)o2.getDouble("earn_rate");
					if (earn_rate_1 < earn_rate_2) {
						return 1;
					}
					if (earn_rate_1 == earn_rate_2) {
						return 0;
					}
					return -1;
				}
			});
			
			/*jsonArray.add(jsonValues1.get(0));
			jsonArray.add(jsonValues2.get(0));*/
			jsonArray.add(jsonValues3.get(0));
			
			// 策略排序
			List<JSONObject> jsonValuesResult = new ArrayList<JSONObject>();
		    for (int i = 0; i < jsonArray.size(); i++) {
		    	jsonValuesResult.add(jsonArray.getJSONObject(i));
		    }
			Collections.sort(jsonValuesResult, new Comparator<JSONObject>() {
				public int compare(JSONObject o1, JSONObject o2) {
					Double earn_rate_1 = (double)o1.getDouble("earn_rate");
					Double earn_rate_2 = (double)o2.getDouble("earn_rate");
					if (earn_rate_1 < earn_rate_2) {
						return 1;
					}
					if (earn_rate_1 == earn_rate_2) {
						return 0;
					}
					return -1;
				}
			});
						
			re.setResult(jsonValuesResult);
		}catch (Exception e) {
			e.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query allotment_index error",e);
		}
		re.setCode(code);
		re.setMessage(message);
		return re;
	}
	
	/**
	 * 多多配股
	 * @param vo 
	 * @return
	 */
	@RequestMapping(value = "/allotment" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel allotment(RouteReqVo vo) throws Exception {
		ResultModel re = new ResultModel();
		if(vo.getType() != 2001 || vo.getModelType() != 2003 || StringUtils.isBlank(vo.getStrategy_code())){
			re.setCode(-1);
			re.setMessage("参数有误");
			re.setResult("");
			return re;
		}
		Re r = new Re();
		JSONArray jsonArray = new JSONArray();
		if("long_aggressive".equals(vo.getStrategy_code())){//进取型
			jsonArray = postForObject(vo,restTemplate,myProps,"long_aggressive_3","long_aggressive_2","long_aggressive_1");
		}else if("long_prudent".equals(vo.getStrategy_code())){//稳健型
			jsonArray = postForObject(vo,restTemplate,myProps,"long_prudent_3","long_prudent_2","long_prudent_1");
		}else if("long_defensive".equals(vo.getStrategy_code())){//保守型
			jsonArray = postForObject(vo,restTemplate,myProps,"long_defensive_3","long_defensive_2","long_defensive_1");
		}
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(jsonArray);
		return re;
	}
	
	/**
	 * 多多配股详情
	 * @param vo 
	 * @return
	 */
	@RequestMapping(value = "/allotment_detail" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel allotmentDetail(RouteReqVo vo) throws Exception {
		ResultModel re = new ResultModel();
		Re r = new Re();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonDate_1 = new JSONObject();
		vo.setType(2001);
		vo.setStrategy_code(vo.getStrategy_code());
		String exceed = null;
		BigDecimal bg = null;
		double exceed_hs = 0;
		r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			jsonDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			
			jsonDate_1.put("run_days",  TimeUtil.daysBetween(TimeUtil.parseTimeFormat(jsonDate_1.getString("begin_time").toString()),
					TimeUtil.parseTimeFormat(TimeUtil.formatDateTime(new Date()))));//运行
			
			//保留6位小数
			exceed = String.valueOf(Double.parseDouble(jsonDate_1.get("earn_rate").toString()) - Double.parseDouble(jsonDate_1.get("a_300_earn_rate").toString()));
			bg = new BigDecimal(exceed);  
			exceed_hs = bg.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();  
			jsonDate_1.put("exceed_hs", exceed_hs);//赢超大盘
		}
		
		//近10天的单股最大收益
		if("long_aggressive_1".equals(vo.getStrategy_code()) || "long_aggressive_2".equals(vo.getStrategy_code()) 
				|| "long_aggressive_3".equals(vo.getStrategy_code())){
			vo.setType(2003);
			vo.setCount(10);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				JSONObject getData_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonRates_1 = JSONArray.parseArray(getData_1.get("rates").toString());
				for(int x=0;x<jsonRates_1.size();x++){
					JSONObject job_1 = jsonRates_1.getJSONObject(x);
					JSONObject rates_1 = new JSONObject();
					rates_1.put("date", job_1.get("date").toString());//日期
//					rates_1.put("a_300_earn_rate", Double.parseDouble(job_1.get("a_300_earn_rate").toString()));//沪深300收益率
					rates_1.put("a_300_earn_rate", Double.parseDouble(job_1.get("a_300_earn_day_rate").toString()));//沪深300收益率
					rates_1.put("max_stock_earn_rate", Double.parseDouble(job_1.get("max_stock_earn_rate").toString()));//日单股最大收益率
					
					jsonArray.add(rates_1);
				}
			}
			jsonDate_1.put("mserpy_hs_index", jsonArray);//近10天的单股最大收益
			
			jsonDate_1 = latelyTime(vo,restTemplate,myProps,jsonDate_1);//调仓时间
			
			vo.setModelType(2003);
			jsonDate_1 = modelHsIndex(vo,restTemplate,myProps,jsonDate_1);//模型/大盘收益率
		}
		
		//近6个月的收益率
		if("long_defensive_1".equals(vo.getStrategy_code()) || "long_defensive_2".equals(vo.getStrategy_code()) 
				|| "long_defensive_3".equals(vo.getStrategy_code())){
			vo.setCount(6);//6个月的记录
			vo.setType(2011);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			JSONArray jsonArray_1 = new JSONArray();
			if(r.getCode() == 0){
				JSONObject getData_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonRates_1 = JSONArray.parseArray(getData_1.get("rates").toString());
				for(int x=0;x<jsonRates_1.size();x++){
					JSONObject job_1 = jsonRates_1.getJSONObject(x);
					JSONObject rates_1 = new JSONObject();
					String date = job_1.get("date").toString();
					rates_1.put("date", date.substring(0, date.lastIndexOf("-")));//日期
					rates_1.put("cost", Double.parseDouble(job_1.get("cost").toString()));//成本
					rates_1.put("earn", Double.parseDouble(job_1.get("earn").toString()));//收益
					rates_1.put("earn_rate", Double.parseDouble(job_1.get("earn_rate").toString()));//收益率
					rates_1.put("a_300_earn_rate", Double.parseDouble(job_1.get("a_300_earn_rate").toString()));//同期大盘
					
					//保留6位小数
					exceed = String.valueOf(Double.parseDouble(job_1.get("earn_rate").toString()) - Double.parseDouble(job_1.get("a_300_earn_rate").toString()));
					bg = new BigDecimal(exceed);  
					exceed_hs = bg.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();  
					rates_1.put("exceed_hs", exceed_hs);//赢超大盘
					
					jsonArray_1.add(rates_1);
				}
			}
			jsonDate_1.put("statistics_month_avg", jsonArray_1);
			
			jsonDate_1 = latelyTime(vo,restTemplate,myProps,jsonDate_1);//调仓时间
			
			vo.setModelType(2003);
			jsonDate_1 = modelHsIndex(vo,restTemplate,myProps,jsonDate_1);//模型/大盘收益率
		}
		
		//近1个月的统计结果
		if("long_prudent_1".equals(vo.getStrategy_code()) || "long_prudent_2".equals(vo.getStrategy_code()) 
				|| "long_prudent_3".equals(vo.getStrategy_code())){
			
			vo.setType(2002);
			vo.setLimit_type("close");
			vo.setPageIndex(1);
			vo.setPageSize(200);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonDeparture_1 = JSONArray.parseArray(getDate_1.get("trade_record").toString());
				List<TradeRecord> listRecord_1 = jsonDeparture_1.toJavaList(TradeRecord.class);
				JSONArray jsonArraySort_1 = sort(listRecord_1,4);
				
				jsonDate_1.put("statistics_hold", jsonArraySort_1);
				
				jsonDate_1 = latelyTime(vo,restTemplate,myProps,jsonDate_1);//调仓时间
				
				vo.setModelType(2003);
				jsonDate_1 = modelHsIndex(vo,restTemplate,myProps,jsonDate_1);//模型/大盘收益率
			}
		}
		
		vo.setType(2012);
		r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			JSONObject getPersonDate = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			jsonDate_1.put("valid_strategy_cout", Integer.parseInt(getPersonDate.getString("valid_strategy_cout")));//统计有效的策略数量
			jsonDate_1.put("earn_person_count", Integer.parseInt(getPersonDate.getString("earn_person_count")));//人次获利
		}
		
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(jsonDate_1);
		return re;
	}
	
	/**
	 * 多多配股调用路由
	 * @param vo 
	 * @return
	 */
	private static JSONArray postForObject(RouteReqVo vo,RestTemplate restTemplate,MyProps myProps,String strategyCode_1,
			String strategyCode_2,String strategyCode_3) throws Exception {
		Re r = new Re();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonDate_1 = new JSONObject();
		JSONObject jsonDate_2 = new JSONObject();
		JSONObject jsonDate_3 = new JSONObject();
		if(vo.getType() != null && vo.getType() == 2001){
			vo.setStrategy_code(strategyCode_1);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				jsonDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			}
			
			vo.setStrategy_code(strategyCode_2);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				jsonDate_2 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			}
			
			vo.setStrategy_code(strategyCode_3);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				jsonDate_3 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			}
		}
		
		if(vo.getModelType() != null && vo.getModelType() == 2003){//模型/大盘收益率
			vo.setStrategy_code(strategyCode_1);
			jsonDate_1 = modelHsIndex(vo,restTemplate,myProps,jsonDate_1);
			
			vo.setStrategy_code(strategyCode_2);
			jsonDate_2 = modelHsIndex(vo,restTemplate,myProps,jsonDate_2);
			
			vo.setStrategy_code(strategyCode_3);
			jsonDate_3 = modelHsIndex(vo,restTemplate,myProps,jsonDate_3);
		}
		
		if(vo.getDepartureType() != null && vo.getDepartureType() == 2002){//获利离场数组组合
			vo.setType(vo.getDepartureType());
			vo.setStrategy_code(strategyCode_1);
			vo.setLimit_type("close");
			vo.setPageIndex(1);
			vo.setPageSize(60);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonDeparture_1 = JSONArray.parseArray(getDate_1.get("trade_record").toString());
				List<TradeRecord> listRecord_1 = jsonDeparture_1.toJavaList(TradeRecord.class);
				JSONArray jsonArraySort_1 = sort(listRecord_1,3);
				JSONArray jsonArray_1 = new JSONArray();
				for(int x=0;x<jsonArraySort_1.size();x++){
					JSONObject job_1 = jsonArraySort_1.getJSONObject(x);
					JSONObject departure_1 = new JSONObject();
					departure_1.put("rank", job_1.get("a_rank").toString()+"/"+job_1.get("a_count").toString());
					departure_1.put("name", job_1.get("name").toString());
					departure_1.put("earn_rate", Double.parseDouble((job_1.get("earn_rate").toString())));
					jsonArray_1.add(departure_1);
				}
				jsonDate_1.put("statistics_hold", jsonArray_1);
			}
			
			vo.setStrategy_code(strategyCode_2);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				JSONObject getDate_2 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonDeparture_2 = JSONArray.parseArray(getDate_2.get("trade_record").toString());
				List<TradeRecord> listRecord_2 = jsonDeparture_2.toJavaList(TradeRecord.class);
				JSONArray jsonArraySort_2 = sort(listRecord_2,3);
				JSONArray jsonArray_2 = new JSONArray();
				for(int y=0;y<jsonArraySort_2.size();y++){
					JSONObject job_2 = jsonArraySort_2.getJSONObject(y);
					JSONObject departure_2 = new JSONObject();
					departure_2.put("rank", job_2.get("a_rank").toString()+"/"+job_2.get("a_count").toString());
					departure_2.put("name", job_2.get("name").toString());
					departure_2.put("earn_rate", Double.parseDouble((job_2.get("earn_rate").toString())));
					jsonArray_2.add(departure_2);
				}
				jsonDate_2.put("statistics_hold", jsonArray_2);
			}
			
			vo.setStrategy_code(strategyCode_3);
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				JSONObject getDate_3 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonDeparture_3 = JSONArray.parseArray(getDate_3.get("trade_record").toString());
				List<TradeRecord> listRecord_3 = jsonDeparture_3.toJavaList(TradeRecord.class);
				JSONArray jsonArraySort_3 = sort(listRecord_3,3);
				JSONArray jsonArray_3 = new JSONArray();
				for(int z=0;z<jsonArraySort_3.size();z++){
					JSONObject job_3 = jsonArraySort_3.getJSONObject(z);
					JSONObject departure_3 = new JSONObject();
					departure_3.put("rank", job_3.get("a_rank").toString()+"/"+job_3.get("a_count").toString());
					departure_3.put("name", job_3.get("name").toString());
					departure_3.put("earn_rate", Double.parseDouble((job_3.get("earn_rate").toString())));
					jsonArray_3.add(departure_3);
				}
				jsonDate_3.put("statistics_hold", jsonArray_3);
			}
			
		}
		//调仓时间
		vo.setStrategy_code(strategyCode_1);
		jsonDate_1 = latelyTime(vo,restTemplate,myProps,jsonDate_1);
		
		vo.setStrategy_code(strategyCode_2);
		jsonDate_2 = latelyTime(vo,restTemplate,myProps,jsonDate_2);
		
		vo.setStrategy_code(strategyCode_3);
		jsonDate_3 = latelyTime(vo,restTemplate,myProps,jsonDate_3);
		
		if(vo.getMonthType() != null && vo.getMonthType() == 2011){//月均收益率
			jsonDate_1 = profitRate(vo,restTemplate,myProps,"long_defensive_1",jsonDate_1);
			
			jsonDate_2 = profitRate(vo,restTemplate,myProps,"long_defensive_2",jsonDate_2);
			
			jsonDate_3 = profitRate(vo,restTemplate,myProps,"long_defensive_3",jsonDate_3);
		}
		
		//投入成本
		vo.setStrategy_code(strategyCode_1);
		jsonDate_1 = trCost(vo,restTemplate,myProps,jsonDate_1);
		vo.setStrategy_code(strategyCode_2);
		jsonDate_2 = trCost(vo,restTemplate,myProps,jsonDate_2);
		vo.setStrategy_code(strategyCode_3);
		jsonDate_3 = trCost(vo,restTemplate,myProps,jsonDate_3);
		
		jsonArray.add(jsonDate_1);
		jsonArray.add(jsonDate_2);
		jsonArray.add(jsonDate_3);
		return jsonArray;
	}
	
	//查询月均收益率
	private static JSONObject profitRate(RouteReqVo vo,RestTemplate restTemplate,MyProps myProps,String strategyCode,JSONObject jsonDate) throws Exception {
		Re r = new Re();
		vo.setCount(6);//6个月的记录
		vo.setType(vo.getMonthType());
		vo.setStrategy_code(strategyCode);
		r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			JSONObject getData_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			JSONArray jsonRates_1 = JSONArray.parseArray(getData_1.get("rates").toString());
			JSONArray jsonArray_1 = new JSONArray();
			for(int x=0;x<jsonRates_1.size();x++){
				JSONObject job_1 = jsonRates_1.getJSONObject(x);
				JSONObject rates_1 = new JSONObject();
				String date = job_1.get("date").toString();
				rates_1.put("date", date.substring(0, date.lastIndexOf("-")));
				rates_1.put("earn_rate", Double.parseDouble(job_1.get("earn_rate").toString()));
				jsonArray_1.add(rates_1);
			}
			jsonDate.put("statistics_month_avg", jsonArray_1);
		}
		
		return jsonDate;
	}
	
	//模型/大盘收益率
	private static JSONObject modelHsIndex(RouteReqVo vo,RestTemplate restTemplate,MyProps myProps,JSONObject jsonDate) throws Exception {
		Re r = new Re();
		vo.setCount(180);//半年的记录
		vo.setType(vo.getModelType());
		r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			JSONObject jsonData_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			JSONArray jsonRates_1 = JSONArray.parseArray(jsonData_1.get("rates").toString());
			JSONArray jsonArray_1 = new JSONArray();
			for(int x=0;x<jsonRates_1.size();x++){
				JSONObject job_1 = jsonRates_1.getJSONObject(x);
				JSONObject rates_1 = new JSONObject();
				rates_1.put("date", job_1.get("date").toString());
				rates_1.put("earn_rate", Double.parseDouble(job_1.get("earn_rate").toString()));
				rates_1.put("a_300_earn_rate", Double.parseDouble(job_1.get("a_300_earn_rate").toString()));
				jsonArray_1.add(rates_1);
			}
			jsonDate.put("model_hs_index", jsonArray_1);
		}
		
		return jsonDate;
	}
		
	//调仓时间
	private static JSONObject latelyTime(RouteReqVo vo,RestTemplate restTemplate,MyProps myProps,JSONObject jsonDate) throws Exception {
		Re r = new Re();
		vo.setType(2002);
		vo.setStrategy_code(vo.getStrategy_code());
		vo.setLimit_type("all");
		vo.setPageIndex(1);
		vo.setPageSize(1);
		r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			JSONArray jsonRecord_1 = JSONArray.parseArray(getDate_1.get("trade_record").toString());
			List<TradeRecord> listRecord_1 = jsonRecord_1.toJavaList(TradeRecord.class);
			String time_1 = listRecord_1.get(0).getTime();
			jsonDate.put("lately_trading_time", TimeUtil.timeDiff(time_1, TimeUtil.formatDateTime(new Date())));
		}
		return jsonDate;
	}
	
	//投入成本
	private static JSONObject trCost(RouteReqVo vo,RestTemplate restTemplate,MyProps myProps,JSONObject jsonDate) throws Exception {
		Re r = new Re();
		vo.setType(2005);
		vo.setStrategy_code(vo.getStrategy_code());
		vo.setOrgCode("0001");
		r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			
			jsonDate.put("tr_cost", getDate_1.getInteger("cost"));
		}
		return jsonDate;
	}
		
	//策略交易记录排序
	private static JSONArray sort(List<TradeRecord> listRecord,int num) {
		JSONArray jsonArray = new JSONArray();
		//降序
		Collections.sort(listRecord, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
            	TradeRecord vo1 = (TradeRecord) o1;
            	TradeRecord vo2 = (TradeRecord) o2;
                Double diff = vo2.getEarn_rate() - vo1.getEarn_rate();
                if(diff>0){
                	return 1;
                }else{
                	return -1;
                }
            }
        });
		for (int i = 0; i < listRecord.size() && i < num; i++) {
			jsonArray.add(listRecord.get(i));
        }
		return jsonArray;
	}
	
	//多多配股首页数据
	private static JSONObject muchIndexData(String strategy_code,String orgCode,Long customerId,Long customerKey,
		RestTemplate restTemplate,MyProps myProps,JSONObject jsonObject,Integer type) throws Exception {
		RouteReqVo vo = new RouteReqVo();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		logger.info("strategy_code>>"+strategy_code+"===customerId"+customerId);
		MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
    	params.add("strategy_code", strategy_code);
    	params.add("orgCode", orgCode);
    	params.add("customerId", customerId);
    	params.add("customerKey", customerKey);
    	params.add("is_full", true);
    	params.add("is_statistics", true);
    	ResponseEntity<Re> r = restTemplate.postForEntity(myProps.getStrategyServer() + detail, params, Re.class);
    	Re body = r.getBody();
		logger.info("策略详细>>"+body.getCode());
		if(body.getCode() == 0){
			
			JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(body.getData()));
			JSONObject jsonAttribute = JSONObject.parseObject(getDate_1.get("attribute").toString());
			JSONObject statistics = JSONObject.parseObject(getDate_1.get("statistics").toString());
			if(getDate_1.get("sub_status")==null){
				jsonObject.put("isSubscribe", 0);
				jsonObject.put("remark", "立即订阅(获取最新策略)");
			}else{
				JSONObject subStatus = JSONObject.parseObject(getDate_1.get("sub_status").toString());
				Integer isSubscribe = Integer.parseInt(subStatus.get("is_sub").toString());
				if(isSubscribe != 1){
					jsonObject.put("isSubscribe", isSubscribe);
					jsonObject.put("remark", "立即订阅(获取最新策略)");
				}else{
					String remark = "";
					String end_time = subStatus.getString("end_time").toString();
					Date cusDate = new Date();
					int day = TimeUtil.daysBetween(cusDate,sd.parse(end_time));//现在时间和，套餐结束时间比较；小于0：套餐已到期
					if(day<0){
						remark = "立即订阅(获取最新策略)";
						isSubscribe = 0;//套餐已到期
					} else if(day>0 && day<=10){
						String days = TimeUtil.timeDiff(sd.format(cusDate),
								end_time);
						remark = "策略" + days + "后到期，请立即续订";
					} 
					jsonObject.put("days", day);
					jsonObject.put("isSubscribe", isSubscribe);
					jsonObject.put("remark", remark);
				}
			}
			jsonObject.put("cost", Integer.parseInt(jsonAttribute.get("cost").toString()));//初始资金
			jsonObject.put("riskGrade", jsonAttribute.get("riskGrade").toString());//风险等级
			jsonObject.put("timeLimit", jsonAttribute.get("timeLimit").toString());//投资期限
			jsonObject.put("name", jsonAttribute.get("name").toString());//策略名称
			jsonObject.put("orgCode", jsonAttribute.get("orgCode").toString());//机构code
			jsonObject.put("strategyCode", jsonAttribute.get("strategyCode").toString());//策略code
			vo.setStrategy_code(strategy_code);
			jsonObject = latelyTime(vo,restTemplate,myProps,jsonObject);//调仓时间
			
			jsonObject.put("earn_rate", Double.parseDouble(statistics.get("earn_rate").toString()));//累计收益率
			jsonObject.put("a_300_earn_rate", Double.parseDouble(statistics.get("a_300_earn_rate").toString()));//大盘同期收益率
			jsonObject.put("earn_count_rate", Double.parseDouble(statistics.get("earn_count_rate").toString()));//胜率
			jsonObject.put("income_rate_day", Double.parseDouble(statistics.get("income_rate_day").toString()));//日收益
			jsonObject.put("run_days",  TimeUtil.daysBetween(sd.parse(statistics.getString("begin_time").toString()),
					sd.parse(sd.format(new Date()))));//运行
			jsonObject.put("hold_day_avg_percent", Double.parseDouble(statistics.get("hold_day_avg_percent").toString()));//日均持仓x%
			jsonObject.put("hold_percent", Double.parseDouble(statistics.get("hold_percent").toString()));//持仓
			jsonObject.put("max_retrace_rate", Double.parseDouble(statistics.get("max_retrace_rate").toString()));//回撤
			jsonObject.put("stock_count_cur", Double.parseDouble(statistics.get("stock_count_cur").toString()));//当前持股
			
			// ==== 模型/大盘收益率 =====
			vo.setModelType(type);
			vo.setStrategy_code(strategy_code);
			jsonObject = modelHsIndex(vo,restTemplate,myProps,jsonObject);
		} 
		
		return jsonObject;
	}
	
	/**
	 * 查询策略特权
	 * @param sessionCode
	 * @param productCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_permission", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryPermission(String sessionCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		try{
			if (StringUtils.isBlank(sessionCode)) {
	    		result.setCode(-1);
	    		result.setMessage("参数错误");
	    		return result;
	    	}
			logger.info("query strategy permission sessionCode:"+sessionCode);
			JSONArray arraylist = new JSONArray();
			
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				//机构编码
				String orgCode=map.get("orgCode").toString();
				logger.info("查询策略特权获取用户信息>>>>orgCode:"+orgCode+">>customerKey:"+customerKey);
				//查询策略信息
				Map<String, TradeProduct> productMap=this.queryTradeByOrgCode(orgCode);
				Map<String,String> recordMap=new HashMap<String,String>();
				Map<String,List<String>> endDateMap=new HashMap<String,List<String>>();
				//没有过期的订购记录
				ResultModel back=restTemplate.getForObject(myProps.getSubscribeServer() + queryMySubInfoBySys+ "?sysFlag={1}&customerKey={2}&orgCode={3}", ResultModel.class,productSysFlag,customerKey,orgCode);
				Object queryChooseRecord=back.getResult();
				JSONArray jsonArray=JSON.parseArray(JSON.toJSONString(queryChooseRecord));
				int size=jsonArray.size();
				if(size>0){
					for(int i=0;i<size;i++){
						JSONObject jsonObj=jsonArray.getJSONObject(i);
						String stockCode =jsonObj.getString("sysProductCode");
						Date useBeginTime=jsonObj.getDate("useBeginTime");
						Date useEndTime=jsonObj.getDate("useEndTime");
						if(!recordMap.containsKey(stockCode)){
							recordMap.put(stockCode, TimeUtil.formatDate(useBeginTime));
						}
						List<String> endDateList=endDateMap.get(stockCode);
						if(null==endDateList){
							endDateList=new ArrayList<String>();
							endDateMap.put(stockCode, endDateList);
						}
						endDateList.add(TimeUtil.formatDate(useEndTime));
					}
				}
				for(Entry<String,TradeProduct> entry:productMap.entrySet()){
					String strategyCode=entry.getKey();
					JSONObject obj = new JSONObject();
					TradeProduct product=entry.getValue();
					String stockImg="";
					String beginDate="";
					String endDate="";
					int exit=recordMap.get(strategyCode)==null?0:1;
					if(exit==0){
						stockImg=product.getNoPermissionImg()==null?"":product.getNoPermissionImg();
						beginDate="";
						endDate="";
					}else{
						stockImg=product.getHavePermissionImg()==null?"":product.getHavePermissionImg();
						beginDate=recordMap.get(strategyCode);
						List<String> endDateList=endDateMap.get(strategyCode);
						endDate=endDateList.get(endDateList.size()-1);
					}
					obj.put("strategyCode", strategyCode);
					obj.put("strategyName", product.getName());
					obj.put("strategyImg", stockImg);
					obj.put("isExit", exit);
					obj.put("remark", product.getRemark()==null?"":product.getRemark());
					obj.put("link", product.getLink()==null?"":product.getLink());
					obj.put("validDate", beginDate+" ~ "+endDate);
					arraylist.add(obj); 
				}
				result.setResult(arraylist);
			}else{
				return re;
			}
		}catch(Exception ce){
			code=-1;
			message="失败";
			ce.printStackTrace();
			logger.error("query strategy permission info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 查询策略信息
	 * @param orgCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,TradeProduct> queryTradeByOrgCode(String orgCode){
		Map<String,TradeProduct> tradeCfgMap=new HashMap<String,TradeProduct>();
		//从redis 里面获取选股基本配置
		List<Object> objectList = redisUtils.queryHashValue("trade_product");
		for(Object object:objectList){
			Map<String, Object> productMap = (Map<String, Object>) object;
			if(orgCode.equals(productMap.get("platformCode").toString())){
				TradeProduct product=new TradeProduct();
				product.setId(Integer.parseInt(productMap.get("id").toString()));
				product.setName(productMap.get("name").toString());
				product.setCode(productMap.get("code").toString());
				product.setType(Integer.parseInt(productMap.get("type").toString()));
				product.setDetail(productMap.get("detail").toString());
				product.setSource(Integer.parseInt(productMap.get("source").toString()));
				product.setExtra(productMap.get("extra").toString());
				product.setCategoryId(Integer.parseInt(productMap.get("categoryId").toString()));
				product.setPlatformCode(productMap.get("platformCode").toString());
				product.setHavePermissionImg(productMap.get("havePermissionImg").toString());
				product.setNoPermissionImg(productMap.get("noPermissionImg").toString());
				product.setRemark(productMap.get("remark")==null?"":productMap.get("remark").toString());
				product.setLink(productMap.get("link")==null?"":productMap.get("link").toString());
				tradeCfgMap.put(product.getCode(), product);
			}
		}
	    return tradeCfgMap;
	}
	
	/**
	 * 获取套餐价格
	 * @param productCode
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_strategy_price", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryStrategyPrice(String strategyCode,String orgCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(strategyCode)||StringUtils.isBlank(orgCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		try{
			logger.info("strategy price param strategyCode:"+strategyCode+",orgCode:"+orgCode);
			JSONArray arraylist=payUtils.fullPrice(strategyCode, orgCode, productSysFlag);
			result.setResult(arraylist);
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query strategy price info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 *  支付
	 * @param sessionCode
	 * @param packageCode 套餐编码
	 * @param payType 1：金币支付；2：金豆支付；3：余额支付: 4：收银台支付；
	 * @param orderChannel wx,app,pc
	 * @return
	 */
	@RequestMapping(value = "/strategy_pay", method = RequestMethod.POST)
	public @ResponseBody ResultModel strategyPay(String sessionCode,int packageId,String packageCode,int payType,String orderChannel) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		logger.info("strategy pay param sessionCode:"+sessionCode+",packageCode:"+packageCode+",payType:"+payType+",orderChannel:"+orderChannel);
		try{
			return payUtils.fullPay(sessionCode, packageId, packageCode, payType, orderChannel, productSysFlag);
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query strategy pay info error",ce);
		}
		result.setResult("");
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 支付回调
	 * @param status
	 * @param customerKey
	 * @param extendParams
	 * @param amount
	 * @return
	 */
	@RequestMapping(value = "/pay_back")
	public @ResponseBody String payBack(int status,Integer customerKey,String extendParams,String amount,String subCusProList) throws Exception {
		String result="failed";
    	int code=0;
		Date curDate=new Date();
		int payType=0;
		String packageCode="";
		String remark="购买策略";
		float packageUnitPrice=0f;
		String orderNo=String.valueOf(System.currentTimeMillis());
		int free=0;
		Integer customerId = null;
		String mobile = null;
		String returnParams="";
		String productName="";
		try{
			logger.info("pay back param status:"+status+",customerKey:"+customerKey+",extendParams:"+extendParams+",amount:"+amount+",subCusProList:"+subCusProList);
			if(status==1){
				JSONObject jsonObj=JSON.parseObject(extendParams);
				packageCode=jsonObj.getString("packageCode");
				packageUnitPrice=Float.valueOf(amount)/DEFAULT_AM;
				//这里判断很重要，returnParams 返回为空是通过管理后台订购，然后回调。
				if(jsonObj.containsKey("returnParams")){
					logger.info("strategy back returnParams is value");
					returnParams=jsonObj.getString("returnParams");
					String [] returnParamArr=returnParams.split(",");
					payType=Integer.parseInt(returnParamArr[0]);
					//free 字段的特殊含义是签到活动中免费送给用户付费模型的权限
					free=Integer.parseInt(returnParamArr[1]);
					
					JSONArray jsonarray=JSONArray.parseArray(subCusProList);
					if(jsonarray.size()>0){
						for(int i=0;i<jsonarray.size();i++){
							JSONObject json=jsonarray.getJSONObject(i);
							productName=json.getString("sysProductName");
						}
					}
					remark="购买"+productName+"策略模型";
					Map<Integer,String> map = payUtils.fullPayBack(customerKey, payType, packageCode, packageUnitPrice, free, amount, remark, "cl");
					for(Entry<Integer,String> entry:map.entrySet()){
						customerId=entry.getKey();
						mobile= entry.getValue();
					}
				}
			}
		}catch(Exception ce){
			ce.printStackTrace();
			code=-1;
			result="failed";
			logger.error("pay back info error",ce);
		}finally{
			if(status==1){
				logger.info("callback start");
				List<CallbackBean> callback =  new ArrayList<CallbackBean>();
				JSONArray jsonarray=JSONArray.parseArray(subCusProList);
				if(jsonarray.size()>0){
					for(int i=0;i<jsonarray.size();i++){
						JSONObject json=jsonarray.getJSONObject(i);
						CallbackBean callbackBean = new CallbackBean();
						Date useBeginTime = json.getDate("useBeginTime");
						Date useEndTime = json.getDate("useEndTime");
						String sysProductCode = json.getString("sysProductCode");
						logger.info("subCusProList param useBeginTime:"+useBeginTime+",useEndTime:"+useEndTime+",sysProductCode:"+sysProductCode);
						callbackBean.setSysProductCode(sysProductCode);
						callbackBean.setUseBeginTime(useBeginTime);
						callbackBean.setUseEndTime(useEndTime);
						callback.add(callbackBean);
					}
				}
				String strSubCusProList = JSONObject.toJSONString(callback);
				logger.info("callback param subCusProList:"+strSubCusProList);
				result = restTemplate.getForObject(myProps.getStrategyServer()+"/sub/callback"+"?customerId={1}&customerKey={2}&customerMobile={3}&orderTime={4}&subOrderNo={5}&subCusProList={6}", String.class,customerId, customerKey, mobile, TimeUtil.formatDateTime(curDate), orderNo, strSubCusProList);
				logger.info("策略订购成功,customerId:"+customerId+"===>>productName:"+productName);
				this.sendStrategySms(customerId, productName,productSysFlag);
				logger.info("callback end");
			}
		}
		if(code==0){
			result="success";
		}
		return result;
	}
	
	/**
	 * 获利离场
	 * @param vo 
	 * @return
	 */
	@RequestMapping(value = "/profit_close" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel profitClose(String strategy_code,Integer pageIndex,Integer pageSize) throws Exception {
		ResultModel re = new ResultModel();
		if(StringUtils.isBlank(strategy_code) || pageIndex == null || pageSize == null){
			re.setCode(-1);
			re.setMessage("参数有误");
			re.setResult("");
			return re;
		}
		RouteReqVo vo = new RouteReqVo();
		vo.setType(2002);
		vo.setLimit_type("close");
		vo.setStrategy_code(strategy_code);
		vo.setPageIndex(pageIndex);
		vo.setPageSize(pageSize);
		JSONArray jsonArray = new JSONArray();
		JSONArray jsonArrayRecord = new JSONArray();
		Re r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
				null, Re.class);
		if(r.getCode() == 0){
			JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
			JSONArray jsonDeparture_1 = JSONArray.parseArray(getDate_1.get("trade_record").toString());
			List<TradeRecord> listRecord_1 = jsonDeparture_1.toJavaList(TradeRecord.class);
			// 按收益率排序
			if (!listRecord_1.isEmpty()) {
				Collections.sort(listRecord_1, new Comparator<TradeRecord>() {
					public int compare(TradeRecord o1, TradeRecord o2) {
						if (o1.getEarn_rate() < o2.getEarn_rate()) {
							return 1;
						}
						if (o1.getEarn_rate() == o2.getEarn_rate()) {
							return 0;
						}
						return -1;
					}
				});
			}
			for(int i=0;i<listRecord_1.size();i++){
				jsonArrayRecord.add(listRecord_1.get(i));
			}
			for(int x=0;x<jsonArrayRecord.size();x++){
				JSONObject job_1 = jsonArrayRecord.getJSONObject(x);
				
				Double a_count = Double.parseDouble(job_1.get("a_count").toString());
				Double a_rank = Double.parseDouble(job_1.get("a_rank").toString());
				Double percent = (double) (a_count - a_rank) / a_count;
				job_1.put("win_percent", percent);//超过同期股票比例
				Integer hold_days = TimeUtil.daysBetween(TimeUtil.parseDateFormat(job_1.get("begin_time").toString()),
						TimeUtil.parseDateFormat(job_1.get("time").toString())) + 1;
				job_1.put("hold_days", hold_days);//持股天数
				jsonArray.add(job_1);
			}
		}
		
		re.setCode(Integer.parseInt(r.get("code").toString()));
		re.setMessage(r.get("message").toString());
		re.setResult(jsonArray);
		return re;
	}
	
	/**
	 *  是否订阅
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/isSubscribe", method = RequestMethod.POST)
	public @ResponseBody ResultModel isSubscribe(String sessionCode,String orgCode,String strategy_code) {
		ResultModel result=new ResultModel();
		JSONObject jsonDate = new JSONObject();
		int code=0;
		String message="成功";
		Long customerId = null;
		Long customerKey = null;
		try{
			if(null!=sessionCode){
				ResultModel re=this.queryUserInfo(sessionCode);
				if(re.getCode()==0){
					Map<String, Object> map=(Map<String, Object>)re.getResult();
					customerId = Long.parseLong(map.get("customerId").toString());
					customerKey = Long.parseLong(map.get("customerKey").toString());
					logger.info("是否订阅获取用户信息>>>>customerId:"+customerId+">>customerKey:"+customerKey);
				}else{
					return re;
				}
			}
			jsonDate = muchIndexData(strategy_code,orgCode,customerId,customerKey,restTemplate,myProps,jsonDate,2003);
			if(jsonDate.isEmpty()){
				message="订阅失败";
				code=-1;
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("isSubscribe info error",ce);
		}
		result.setResult(jsonDate);
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 *  今日买入
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/buy_today", method = RequestMethod.POST)
	public @ResponseBody ResultModel buyToday(RouteReqVo vo) {
		ResultModel result=new ResultModel();
		JSONArray jsonList = new JSONArray();
		Re r = new Re();
		int code=0;
		String message="成功";
		try{
			if(StringUtils.isBlank(vo.getStrategy_code()) || vo.getPageIndex() == null || vo.getPageSize() == null 
					|| StringUtils.isBlank(vo.getDay_begin()) || StringUtils.isBlank(vo.getDay_end())){
				result.setResult("");
				result.setCode(-1);
				result.setMessage("参数错误");
			}
			vo.setType(2002);
			vo.setLimit_type("open");
			vo.setStrategy_code(vo.getStrategy_code());
			vo.setPageIndex(vo.getPageIndex());
			vo.setPageSize(vo.getPageSize());
			vo.setDay_begin(vo.getDay_begin());
			vo.setDay_end(vo.getDay_end());
			
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(Integer.parseInt(r.get("code").toString()) == 0){
				JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("trade_record").toString());
				List<TradeRecord> listRecord = jsonArray.toJavaList(TradeRecord.class);
				for(int x=0;x<listRecord.size();x++){
					TradeRecord record = listRecord.get(x);
					JSONObject departure_1 = new JSONObject();
					departure_1.put("strategy_code", vo.getStrategy_code());
					departure_1.put("name", record.getName());
					departure_1.put("code", record.getCode());
					departure_1.put("date", record.getTime());
					departure_1.put("price", record.getCost_price());
					departure_1.put("time", record.getTime());
					departure_1.put("market_id", record.getMarket_id());
					
					RouteReqVo vo_price = new RouteReqVo();
					vo_price.setType(2014);
					vo_price.setStockCode(record.getCode());
					vo_price.setMarket_id(record.getMarket_id());
					vo_price.setStrategy_code(vo.getStrategy_code());
					Double now_price = null;
					Double price_rise_rate = null;
					r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo_price)),
							null, Re.class);
					if(Integer.parseInt(r.get("code").toString()) == 0){
						JSONObject json_price= JSONObject.parseObject(JSON.toJSONString(r.getData()));
						now_price = Double.parseDouble(json_price.getString("price_now").toString());
						price_rise_rate = Double.parseDouble(json_price.getString("price_rise_rate").toString());
					}
					departure_1.put("now_price", now_price);
					departure_1.put("price_rise_rate", price_rise_rate);
					jsonList.add(departure_1);
				}
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("buy_today info error",ce);
		}
		result.setResult(jsonList);
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 *  操盘记录
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/stock_record", method = RequestMethod.POST)
	public @ResponseBody ResultModel stockRecord(RouteReqVo vo) {
		ResultModel result=new ResultModel();
		JSONObject jsonResult =  new JSONObject();
		Re r = new Re();
		int code=0;
		String message="成功";
		try{
			if(StringUtils.isBlank(vo.getStrategy_code()) || vo.getPageIndex() == null || vo.getPageSize() == null 
					|| StringUtils.isBlank(vo.getStockCode()) || vo.getMarket_id() == null){
				result.setResult("");
				result.setCode(-1);
				result.setMessage("参数错误");
			}
			vo.setType(2010);
			vo.setLimit_type("all");
			vo.setStrategy_code(vo.getStrategy_code());
			vo.setPageIndex(vo.getPageIndex());
			vo.setPageSize(vo.getPageSize());
			vo.setStockCode(vo.getStockCode());
			vo.setMarket_id(vo.getMarket_id());
			
			JSONArray jsonList = new JSONArray();
			r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(Integer.parseInt(r.get("code").toString()) == 0){
				JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("trade_record").toString());
				List<TradeRecord> listRecord = jsonArray.toJavaList(TradeRecord.class);
				for(int x=0;x<listRecord.size();x++){
					JSONObject departure_1 = new JSONObject();
					TradeRecord record = listRecord.get(x);
					// 每股收益 = 卖出委托价-成本价
					Double earn = (double) 0;
					if (record.getDirector().equals("S")) {
						earn = record.getPrice() - record.getCost_price();
					}
					departure_1.put("date", record.getTime());
					departure_1.put("director", record.getDirector().equals("B") ? "买入" : "卖出");
					departure_1.put("price", record.getPrice());
					departure_1.put("earn", earn);
					departure_1.put("earn_rate", record.getEarn_rate());
					
					// 个股
					departure_1.put("num", record.getNum());
					departure_1.put("hold_percent", record.getHold_percent());
					
					jsonList.add(departure_1);
				}
				
				jsonResult.put("list", jsonList);
				
				// 最后一次交易记录
				RouteReqVo vo_last = new RouteReqVo();
				vo_last.setType(2010);
				vo_last.setLimit_type("all");
				vo_last.setStrategy_code(vo.getStrategy_code());
				vo_last.setPageIndex(1);
				vo_last.setPageSize(1);
				vo_last.setStockCode(vo.getStockCode());
				vo_last.setMarket_id(vo.getMarket_id());
				
				r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo_last)),
						null, Re.class);
				
				if(Integer.parseInt(r.get("code").toString()) == 0){
					JSONObject jsonLast = JSONObject.parseObject(JSON.toJSONString(r.getData()));
					JSONArray jsonArrayLast = JSONArray.parseArray(jsonLast.get("trade_record").toString());
					List<TradeRecord> listRecordLast = jsonArrayLast.toJavaList(TradeRecord.class);
					if(listRecordLast.size()>0){
						TradeRecord recordLast = listRecordLast.get(0);
						if (recordLast.getDirector().equals("B")) {
							RouteReqVo vo_Hold = new RouteReqVo();
							vo_Hold.setStrategy_code(vo.getStrategy_code());
							vo_Hold.setStockCode(vo.getStockCode());
							vo_Hold.setMarket_id(vo.getMarket_id());
							vo_Hold.setType(2013);
							
							r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo_Hold)),
									null, Re.class);
							if(Integer.parseInt(r.get("code").toString()) == 0){
								JSONObject jsonTradeHold = JSONObject.parseObject(JSON.toJSONString(r.getData()));
								JSONArray jsonArrayTh = JSONArray.parseArray(jsonTradeHold.get("trade_hold").toString());
								JSONObject jsonTh = jsonArrayTh.getJSONObject(0);
								
								jsonResult.put("name", jsonTh.get("name").toString());
								jsonResult.put("code", jsonTh.get("code").toString());
								jsonResult.put("market", jsonTh.get("market_id").toString());
								jsonResult.put("earn_rate", Double.parseDouble(jsonTh.get("earn_rate").toString()));
								jsonResult.put("status", 2);
								
								//牛股
								jsonResult.put("date", jsonTh.get("begin_time").toString());//入场时间
								jsonResult.put("cost_price", Double.parseDouble(jsonTh.get("cost_price").toString()));//入场价
								jsonResult.put("price", Double.parseDouble(jsonTh.get("now_price").toString()));//当前价/离场价
								
								//个股
								Double market_value = Double.parseDouble(jsonTh.get("now_price").toString())*Double.parseDouble(jsonTh.get("vol").toString());
								int hold_days = TimeUtil.daysBetween(TimeUtil.parseTimeFormat(jsonTh.get("begin_time").toString()), new Date());
								jsonResult.put("market_value", market_value);//当前市值
								jsonResult.put("cost", Double.parseDouble(jsonTh.get("hold_used").toString()));//投入成本
								jsonResult.put("hold_days", hold_days);//持仓天数
							}
						} else {
							// (0:获利离场：持仓=0且收益≥0,1:止损离场：持仓=0且收益<0,2:持股中：持仓>0)
							Integer s = 0;
							if (recordLast.getEarn() < 0) {
								s = 1;
							}
							
							jsonResult.put("name", recordLast.getName());
							jsonResult.put("code", recordLast.getCode());
							jsonResult.put("market", recordLast.getMarket_id());
							jsonResult.put("earn_rate", recordLast.getEarn_rate());
							jsonResult.put("status", s);
							
							//牛股
							jsonResult.put("date", recordLast.getBegin_time());//入场时间
							jsonResult.put("cost_price", recordLast.getCost_price());//入场价
							jsonResult.put("price", recordLast.getPrice());//当前价/离场价
							
							//个股
							int hold_days = TimeUtil.daysBetween(TimeUtil.parseTimeFormat(recordLast.getBegin_time()),
									TimeUtil.parseTimeFormat(recordLast.getTime()));
							jsonResult.put("market_value", recordLast.getEarn());//当前市值
							jsonResult.put("cost", recordLast.getHold_used());//投入成本
							jsonResult.put("hold_days", hold_days);//持仓天数
						}
					}
				}
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("stock_record info error",ce);
		}
		result.setResult(jsonResult);
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 我的策略
	 * @param vo 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sub/my_strategy" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel myStrategy(PackageOrderResVo vo) throws Exception {
		ResultModel re=this.queryUserInfo(vo.getSessionCode());
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			Integer customerKey = Integer.parseInt(map.get("customerKey").toString());
			logger.info("我的策略获取用户信息>>>>customerKey:"+customerKey);
			Re r = restTemplate.postForObject(myProps.getStrategyServer() + my_strategy + "?customerKey={1}",
					null, Re.class,customerKey);
			re.setCode(Integer.parseInt(r.get("code").toString()));
			re.setMessage(r.get("message").toString());
			re.setResult(r.getData() == null ? "" : r.getData());
			return re;
		}else {
			return re;
		}
	}
	
	/**
	 * 取消订单（pc）
	 * @param vo 
	 * @return
	 */
	@RequestMapping(value = "/cancleOrderHis" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel cancleOrderHis(String orderCode) throws Exception {
		ResultModel re= new ResultModel();;
		if(StringUtils.isBlank(orderCode)){
			re.setCode(-1);
			re.setMessage("参数有误");
			re.setResult("");
			return re;
		}
		logger.info("取消订单>>>>orderCode:"+orderCode);
		re= restTemplate.getForObject(myProps.getSubscribeServer() + cancleOrderHis + "?orderCode={1}",
				ResultModel.class,orderCode);
		if(re.getCode() == 0 ){
			re.setCode(re.getCode());
			re.setMessage(re.getMessage());
			re.setResult(re.getResult() == null ? "" : re.getResult());
		}
		return re;
	}
	
	/**
	 * 我的策略订购记录
	 * @param sessionCode
	 */
	@RequestMapping(value = "/query_strategy_record", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryOrderDetail(String sessionCode) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		logger.info("strategy query_order_detail order param sessionCode:"+sessionCode);
		return payUtils.queryOrderDetail(sessionCode, productSysFlag);
	}
	
	/**
	 * 牛股多多首页（部分数据）
	 * @param sessionCode
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/super_record", method = RequestMethod.POST)
	public @ResponseBody ResultModel superRecord(String sessionCode) {
		ResultModel result=new ResultModel();
		JSONObject jsonDate = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		int code=0; 
		String message="成功";
		Long customerId = null;
		Long customerKey = null;
		String orgCode = null;
		try{
			Integer isSubscribe = 0;
			if(null!=sessionCode){
				ResultModel re=this.queryUserInfo(sessionCode);
				if(re.getCode()==0){
					Map<String, Object> map=(Map<String, Object>)re.getResult();
					customerId = Long.parseLong(map.get("customerId").toString());
					customerKey = Long.parseLong(map.get("customerKey").toString());
					orgCode = map.get("orgCode").toString();
					logger.info("牛股多多首页获取用户信息>>>>customerId:"+customerId+">>customerKey:"+customerKey);
					Thread.sleep(500);
					jsonDate = muchIndexData("qkz_shortstock",orgCode,customerId,customerKey,restTemplate,myProps,jsonDate,2003);
					if(jsonDate.isEmpty()){
						message="订阅失败";
						code=-1;
					}
					isSubscribe = jsonDate.getInteger("isSubscribe");
				}else{
					return re;
				}
			}
			logger.info("用户是否有订阅==isSubscribe:"+isSubscribe);
			
			RouteReqVo vo = new RouteReqVo();
			vo.setType(2002);
			vo.setLimit_type("open");
			vo.setStrategy_code("qkz_shortstock");
			vo.setPageIndex(1);
			vo.setPageSize(4);
			Re r = restTemplate.postForObject(myProps.getStrategyServer() + route + StrUtils.getParams(BeanUtils.describe(vo)),
					null, Re.class);
			if(r.getCode() == 0){
				JSONObject getDate_1 = JSONObject.parseObject(JSON.toJSONString(r.getData()));
				JSONArray jsonDeparture_1 = JSONArray.parseArray(getDate_1.get("trade_record").toString());
				for(int x=0;x<jsonDeparture_1.size();x++){
					JSONObject job_1 = jsonDeparture_1.getJSONObject(x);
					Integer market_id = job_1.getInteger("market_id");//市场ID
					Double earn_rate = job_1.getDouble(("earn_rate"));//收益率
					String cd = null;
					if(isSubscribe == 0){//没有订阅
						cd = job_1.getString("code").substring(0,2)+"****";//商品代码
					}else{//已订阅
						cd = job_1.getString("code");//商品代码
					}
					job_1.put("market_id", market_id);//市场ID
					job_1.put("code", cd);//商品代码
					job_1.put("earn_rate", earn_rate);//收益率
					jsonArray.add(job_1);
				}
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("super_record info error",ce);
		}
		result.setResult(jsonArray);
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 策略订购成功后发送短信
	 * @param customerId
	 * @param productName
	 */
	private void sendStrategySms(Integer customerId,String productName,String productSysFlag){
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		cachedThreadPool.execute(new Runnable() {  
		    public void run() {  
		     try {
		    	ResultModel smsStrategyModel=restTemplate.getForObject(myProps.getCustomerServer()+"/customer/send_choose_sms"+"?customerId={1}&stockName={2}&productSysFlag={3}", ResultModel.class,customerId,productName,productSysFlag);
		    	logger.info("策略订购成功发送短信返回信息:"+JSON.toJSONString(smsStrategyModel));	
		    	Thread.sleep(1000);
		     } catch (Exception ce) {
		         ce.printStackTrace();
		         logger.error("策略订购成功发送短信线程失败 ",ce);
		     }
		   }
		});
	}
	
}
