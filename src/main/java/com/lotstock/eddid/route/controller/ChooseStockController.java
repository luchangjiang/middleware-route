package com.lotstock.eddid.route.controller;

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

import com.lotstock.eddid.route.model.ChooseStockCfg;
import com.lotstock.eddid.route.vo.ChooseResVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ChooseStockUtil;
import com.lotstock.eddid.route.util.PayUtils;
import com.lotstock.eddid.route.util.RedisUtils;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.TimeUtil;

@RestController
@RefreshScope
@RequestMapping("/choose")
public class ChooseStockController extends BaseController{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyProps myProps;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private PayUtils payUtils;
	
	@Autowired
	private ChooseStockUtil chooseStockUtil;
	
	//分转换为元
	private static final int DEFAULT_AM=100;
	
	//选股常量
	private static final String productSysFlag="xg001";
	
	/** 选股app 接口 开始 ***********************************/
	// 股多多app 首页
	private static final String app_home="/app/newhome";
	// 模型介绍 战法逻辑
	private static final String describe = "/stock/query_mode_logic";
	// 当日选股
	private static final String today = "/app/today";
	// 历史推荐
	private static final String history = "/app/newhistory";
	// 策略评测
	private static final String evaluate = "/app/evaluate";
	/** 选股app 接口 结束 *************************************/
	
	/** 选股pc 接口 开始 *************************************/
	// pc 选股首页
	private static final String pc_home = "/stock/home";
	// 查询所有分类
	private static final String query_all_types = "/stock/query_all_types";
	// 查询分类下的选股模型
	private static final String query_modes = "/stock/query_modes";
	// 查询模型的当日选股数
	private static final String query_mode_num="/stock/query_mode_num";
	// 模型选股结果
	private static final String query_result = "/stock/query_result";
	// 战绩回顾
	private static final String query_review_result = "/stock/query_review_result";
	// 历史战绩
	private static final String query_histroy_result = "/stock/query_histroy_result";
	// pc模型效果
	private static final String chart = "/stock/chart";
	/** 选股pc接口 结束 *************************************/
	
	// 获取用户订购的产品
    private static final String queryCusOrderProductInfo = "/subscribe/queryCusOrderProductInfo";
    // 股多多根据系统编码查询
    private static final String querySubProductInfo= "/gddSub/querySubProductInfo";
    // 股多多查询未过期订阅信息
    private static final String queryMySubInfoBySys="/gddSub/queryMySubInfoBySys";
    // 股多多用户订阅数
    private static final String querySubProCount="/gddSub/querySubProCount";
    // 每日金股
  	private static final String activity = "/app/activity";
    // 每日金股每日浏览数量
 	private static final String query_choose_count = "/customer/query_choose_count";
 	// 开通选股
 	private static final String  open_choose_model="/customer/open_choose_model";
	
	/**
	 * 股多多app 首页
	 * @return
	 */
 	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_appfirst", method = RequestMethod.POST)
	public @ResponseBody
    ResultModel queryAppFirst(String orgCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		JSONArray jsonArray = new JSONArray();
		String org="0001";
		try{
			if (StringUtils.isNotBlank(orgCode)) {
				org=orgCode;
	    	}
			Map<String,Integer> exitMap=new HashMap<String,Integer>();
			//用户订阅数
			ResultModel subCountResult=restTemplate.getForObject(myProps.getSubscribeServer() + querySubProCount+ "?sysFlag={1}&orgCode={2}", ResultModel.class,productSysFlag,orgCode);
			if(subCountResult.getCode()==0){
				logger.info("query choose cust number :"+subCountResult.getResult());
				Map<String,Integer> subMap=(Map<String,Integer>)subCountResult.getResult();
				for(Entry<String,Integer> entry:subMap.entrySet()){
					String mapKey=entry.getKey().replace(orgCode+"_", "");
					exitMap.put(mapKey, entry.getValue());
				}
			}
			//查询选股信息
			Map<String, ChooseStockCfg> chooseMap=this.queryChooseCfgByOrgCode(org);
			ResultModel firstModel=restTemplate.getForObject(myProps.getChooseServer() + app_home, ResultModel.class);
			if(firstModel.getCode()==0){
				JSONArray firstArray=JSONArray.parseArray(JSON.toJSONString(firstModel.getResult()));
				if(firstArray.size()>0){
					for(int i=0;i<firstArray.size();i++){
						JSONObject jsonObj=firstArray.getJSONObject(i);
						String modeCode=jsonObj.getString("modeCode");
						if(chooseMap.containsKey(modeCode)){
							JSONObject obj = new JSONObject();
							obj.put("modeId", jsonObj.getString("modeId"));
							obj.put("modeCode", modeCode);
							obj.put("modeName", jsonObj.getString("modeName"));
							obj.put("dayRise", jsonObj.getString("dayRise"));
							obj.put("winRate", jsonObj.getString("winRate"));
							obj.put("selectFlag", jsonObj.getString("selectFlag"));
							obj.put("techQuota", jsonObj.getString("techQuota"));
							obj.put("num", jsonObj.getInteger("num"));
							obj.put("day", jsonObj.getString("day"));
							obj.put("dataFlag", jsonObj.getBoolean("dataFlag"));
							obj.put("orderNum", chooseMap.get(modeCode).getOrderNum()+(exitMap.get(modeCode)==null?0:exitMap.get(modeCode)));
							JSONArray detailArray=jsonObj.getJSONArray("v");
							JSONArray arrays=new JSONArray();
							for(int j=0;j<detailArray.size();j++){
								JSONObject detail=detailArray.getJSONObject(j);
								JSONObject detailObj = new JSONObject();
								detailObj.put("name", detail.getString("name"));
								detailObj.put("code", detail.getString("code"));
								detailObj.put("market", detail.getInteger("market"));
								detailObj.put("time", detail.getString("time"));
								detailObj.put("dayRise", detail.getDouble("dayRise"));
								detailObj.put("price", detail.getDouble("price"));
								arrays.add(detailObj);
							}
							obj.put("v", arrays);
							jsonArray.add(obj);
						}
					}
				}
			}
		}catch(Exception ce){
			code=-1;
			message="失败";
			ce.printStackTrace();
			logger.error("query app first error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		result.setResult(jsonArray);
		return result;
	}
	
	/**
	 * 模型介绍 战法逻辑
	 * @param modeCode 模型code
	 * @param orgCode 机构编码
	 * @return
	 */
	@RequestMapping(value = "/describe", method = RequestMethod.POST)
	public @ResponseBody ResultModel describe(String modeCode,String orgCode) {
		if (StringUtils.isBlank(modeCode)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		if(!"0001".equals(orgCode)){
			orgCode="0001";
		}
		return restTemplate.getForObject(myProps.getChooseServer() + describe+ "?modeCode={1}&orgCode={1}", ResultModel.class, modeCode,orgCode);
	}
	
	/**
	 * 当日选股
	 * @param modeCode
	 * @param queryDate
	 * @return
	 */
	@RequestMapping(value = "/today", method = RequestMethod.POST)
	public @ResponseBody ResultModel today(String modeCode,String queryDate) {
		if (StringUtils.isBlank(modeCode)||StringUtils.isBlank(queryDate)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + today+"?modeCode={1}&queryDate={2}", ResultModel.class,modeCode,queryDate);
	}
	
	/**
	 * 历史推荐
	 * @param modeCode
	 * @param queryDate
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/history", method = RequestMethod.POST)
	public @ResponseBody ResultModel today(String modeCode,String queryDate,int count) {
		if (StringUtils.isBlank(modeCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + history+"?modeCode={1}&queryDate={2}&count={3}", ResultModel.class,modeCode,queryDate,count);
	}
	
	/**
	 * 策略评测
	 * @param modeCode 模型code
	 * @return
	 */
	@RequestMapping(value = "/evaluate", method = RequestMethod.POST)
	public @ResponseBody ResultModel evaluate(String modeCode) {
		if (StringUtils.isBlank(modeCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + evaluate+ "?modeCode={1}", ResultModel.class, modeCode);
	}
	
	/**
	 * pc 选股首页
	 * @param typeCode 类型编码
	 * @return
	 */
	@RequestMapping(value = "/query_pcfirst", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryPcFirst(String typeCode) {
		if (StringUtils.isBlank(typeCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + pc_home+ "?typeCode={1}", ResultModel.class, typeCode);
	}
	
	/**
	 * 查询所有分类
	 * @return
	 */
	@RequestMapping(value = "/query_all_types", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryAllType() {
		return restTemplate.getForObject(myProps.getChooseServer() + query_all_types, ResultModel.class);
	}
	
	/**
	 * 查询分类下的选股模型
	 * @param typeCode
	 * @return
	 */
	@RequestMapping(value = "/query_modes", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryModes(String typeCode) {
		return restTemplate.getForObject(myProps.getChooseServer() + query_modes+"?typeCode={1}", ResultModel.class, typeCode);
	}
	
	/**
	 * 查询模型的当日选股数
	 * @param typeCode
	 * @return
	 */
	@RequestMapping(value = "/query_mode_num", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryModeNum(String typeCode) {
		return restTemplate.getForObject(myProps.getChooseServer() + query_mode_num+"?typeCode={1}", ResultModel.class, typeCode);
	}
	
	/**
	 * 模型选股结果
	 * @param modeCode 模型编码
	 * @param queryDate 查询日期
	 * @return
	 */
	@RequestMapping(value = "/query_result", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryResult(String modeCode,String queryDate) {
		if (StringUtils.isBlank(modeCode)||StringUtils.isBlank(queryDate)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + query_result+ "?modeCode={1}&queryDate={2}", ResultModel.class, modeCode,queryDate);
	}
	
	/**
	 * 战绩回顾
	 * @param modeCode 模型编码
	 * @param reviewType 回顾类型 0-- 5日回顾 1-- 10日回顾   2-- 30日回顾
	 * @return
	 */
	@RequestMapping(value = "/query_review_result", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryReviewResult(String modeCode,String reviewType) {
		if (StringUtils.isBlank(modeCode)||StringUtils.isBlank(reviewType)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + query_review_result+ "?modeCode={1}&reviewType={2}", ResultModel.class, modeCode,reviewType);
	}
	
	/**
	 * 历史战绩
	 * @param modeCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_histroy_result", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryHistroyResult(String modeCode,String sessionCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(modeCode)||StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		JSONObject obj = new JSONObject();
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			int customerKey=Integer.parseInt(map.get("customerKey").toString());
			Object queryChooseRecord=null;
			ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + querySubProductInfo+ "?sysFlag={1}&sysProduceCode={2}", ResultModel.class,productSysFlag,modeCode); 
			if(chooseCodeModel.getCode()==0){
				JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(chooseCodeModel.getResult()));
				String chooseCode=jsonObj.getString("productCode");
				ResultModel back=restTemplate.getForObject(myProps.getSubscribeServer() + queryCusOrderProductInfo+ "?customerId={1}&customerKey={2}&productCode={3}", ResultModel.class,customerId,customerKey,chooseCode);
				logger.info("choose cfg back "+JSON.toJSONString(back));
			    queryChooseRecord=back.getResult();
			}
			
			String curDate= TimeUtil.formatDate(new Date());
			ResultModel historyModel=restTemplate.getForObject(myProps.getChooseServer() + query_histroy_result+ "?modeCode={1}", ResultModel.class, modeCode);
			logger.info("historyModel result:"+JSON.toJSONString(historyModel));
			if(historyModel.getCode()==0){
				JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(historyModel.getResult()));
				//胜率
				obj.put("winRate", jsonObj.getString("winRate"));
				//5日
				obj.put("fiveRiseAvg", jsonObj.getString("fiveRiseAvg"));
				//10日
				obj.put("tenRiseAvg", jsonObj.getString("tenRiseAvg"));
				//20日
				obj.put("twentyRiseAvg", jsonObj.getString("twentyRiseAvg"));
				//30日
				obj.put("thirtyRiseAvg", jsonObj.getString("thirtyRiseAvg"));
				JSONArray jsonArray=jsonObj.getJSONArray("v");
				JSONArray arrays=new JSONArray();
				for(int i=0;i<jsonArray.size();i++){
					JSONObject detail=jsonArray.getJSONObject(i);
					JSONObject detailObj = new JSONObject();
					detailObj.put("seqNum", detail.getString("seqNum"));
					detailObj.put("stocktCode", detail.getString("stocktCode"));
					detailObj.put("stocktName", detail.getString("stocktName"));
					detailObj.put("newPrice", detail.getString("newPrice"));
					detailObj.put("price", detail.getString("price"));
					detailObj.put("time", detail.getString("time"));
					detailObj.put("industry", detail.getString("industry"));
					detailObj.put("dayRise", detail.getString("dayRise"));
					detailObj.put("five_rise", detail.getString("five_rise"));
					detailObj.put("twenty_rise", detail.getString("twenty_rise"));
					detailObj.put("circulatingStock", detail.getString("circulatingStock"));
					detailObj.put("circulatingValue", detail.getString("circulatingValue"));
					detailObj.put("peRatio", detail.getString("peRatio"));
					detailObj.put("marketId", detail.getString("marketId"));
					detailObj.put("actualRise", detail.getString("actualRise"));
					String queryDate=detail.getString("time").substring(0, 10);
					if(null==queryChooseRecord){
						if(!curDate.equals(queryDate)){
							arrays.add(detailObj);
						}
					}else{
						arrays.add(detailObj);
					}
				}
				obj.put("v", arrays);
			}
		}
		result.setCode(code);
		result.setMessage(message);
		result.setResult(obj);
		return result;
	}

	/**
	 * 选股模型效果
	 * @param modeCode
	 * @return
	 */
	@RequestMapping(value = "/chart", method = RequestMethod.POST)
	public @ResponseBody ResultModel chart(String modeCode) {
		if (StringUtils.isBlank(modeCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getChooseServer() + chart+ "?modeCode={1}", ResultModel.class, modeCode);
	}
	
	/**
	 * 是否订阅
	 * @param sessionCode
	 * @param productCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_choosecfg", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryChooseCfg(String sessionCode,String productCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		String status="";
		String remark="";
		String subscribe="";
		int day=0;
		int hour=0;
		int minute=0;
		try{
			JSONObject obj = new JSONObject();
			if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(productCode)) {
				result.setCode(-1);
				result.setMessage("参数错误");
	    		return result;
	    	}
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				int customerId=Integer.parseInt(map.get("customerId").toString());
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				//机构编码
				String orgCode=map.get("orgCode").toString();
				Object queryChooseRecord=null;
				ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + querySubProductInfo+ "?sysFlag={1}&sysProduceCode={2}", ResultModel.class,productSysFlag,productCode); 
				if(chooseCodeModel.getCode()==0){
					JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(chooseCodeModel.getResult()));
					String chooseCode=jsonObj.getString("productCode");
					ResultModel back=restTemplate.getForObject(myProps.getSubscribeServer() + queryCusOrderProductInfo+ "?customerId={1}&customerKey={2}&productCode={3}", ResultModel.class,customerId,customerKey,chooseCode);
					logger.info("choose cfg back "+JSON.toJSONString(back));
				    queryChooseRecord=back.getResult();
				}
				//从redis 里面获取选股基本配置
				Object object = redisUtils.queryHash("choose_stock_cfg", orgCode+"-"+productCode);
				if(null==object){
					result.setCode(-1);
					result.setMessage("选股不存在");
		    		return result;
				}
				Map<String, Object> chooseMap = (Map<String, Object>) object;
				//0免费 1付费
				int chooseStatus=Integer.parseInt(chooseMap.get("status").toString());
				//状态：0免费 1付费
				if(1==chooseStatus){
					status="money";
				}else{
					status="free";
				}
				//当前日期下用户没有付费或是付费后到期
				if(null==queryChooseRecord){
					//状态：0免费 1付费
					if(1==chooseStatus){
						remark="立即订阅";
					}else{
						remark="免费订阅";
					}
					subscribe="false";
				}else{
					subscribe="true";
					logger.info("choose cfg pay is not end");
					JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(queryChooseRecord));
					logger.info("queyre subscribe result :"+JSON.toJSONString(queryChooseRecord));
					Date endDtae=jsonObj.getDate("useEndTime");
					int days=TimeUtil.getDaySpace(TimeUtil.now(), endDtae);
					if(days<=10){
						if(days==0){
							day=0;
							List<Integer> hourMiuList=chooseStockUtil.convertCodeResult(endDtae);
							hour=hourMiuList.get(0);
							minute=hourMiuList.get(1);
						}else{
							day=days;
							hour=0;
							minute=0;
						}
						remark="立即续订";
					}else{
						remark="";
					}
				}
				obj.put("subscribe", subscribe);
				obj.put("status", status);
				obj.put("remark", remark);
				obj.put("day", day);
				obj.put("hour", hour);
				obj.put("minute", minute);
				obj.put("productCode", productCode);
				result.setResult(obj);
		  }else{
			 return re;
		  }
		}catch(Exception ce){
			code=-1;
			message="失败";
			ce.printStackTrace();
			logger.error("query choose cfg info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}

	/**
	 * 获取套餐价格
	 * @param productCode
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_choose_price", method = RequestMethod.POST)
	public @ResponseBody ResultModel  queryChoosePrice(String productCode,String orgCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(productCode)||StringUtils.isBlank(orgCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		try{
			logger.info("choose price param productCode:"+productCode+",orgCode:"+orgCode);
			JSONArray arraylist=payUtils.fullPrice(productCode, orgCode, productSysFlag);
			result.setResult(arraylist);
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query choose price info error",ce);
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
	@RequestMapping(value = "/choose_pay", method = RequestMethod.POST)
	public @ResponseBody ResultModel  choosePay(String sessionCode,int packageId,String packageCode,int payType,String orderChannel) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		logger.info("choose pay param sessionCode:"+sessionCode+",packageCode:"+packageCode+",payType:"+payType+",orderChannel:"+orderChannel);
		try{
			return payUtils.fullPay(sessionCode, packageId, packageCode, payType, orderChannel, productSysFlag);
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query choose pay info error",ce);
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
	public @ResponseBody String  PayBack(int status,Integer customerKey,String extendParams,String amount,String subCusProList) {
		String result="failed";
    	int code=0;
		Date curDate=new Date();
		Date endDate=curDate;
		int customerId=0;
		int payType=0;
		String packageCode="";
		String remark="购买选股模型";
		float packageUnitPrice=0f;
		String orderNo=String.valueOf(System.currentTimeMillis());
		int free=0;
		String productName="";
		String returnParams="";
		try{
			logger.info("pay back param status:"+status+",customerKey:"+customerKey+",extendParams:"+extendParams+",amount:"+amount+",subCusProList:"+subCusProList);
			if(status==1){
				JSONObject jsonObj=JSON.parseObject(extendParams);
				packageCode=jsonObj.getString("packageCode");
				packageUnitPrice=Float.valueOf(amount)/DEFAULT_AM;
				//这里判断很重要，returnParams 返回为空是通过管理后台订购，然后回调。
				if(jsonObj.containsKey("returnParams")){
					logger.info("pay back returnParams is value");
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
							endDate=json.getDate("useEndTime");
						}
					}
					remark="购买"+productName+"选股模型";
					Map<Integer,String> customerMap=payUtils.fullPayBack(customerKey, payType, packageCode, packageUnitPrice, free, amount, remark ,"xg");
					for(Entry<Integer,String> entry:customerMap.entrySet()){
						customerId=entry.getKey();
					}
					logger.info("pay back choose productName:"+productName);
				}
			}
		}catch(Exception ce){
			ce.printStackTrace();
			code=-1;
			result="failed";
			logger.error("pay back info error",ce);
		}finally{
			if(status==1&&!"".equals(returnParams)){
				logger.info("pay back success finally");
				this.saveSubLocalData(customerId, orderNo, productName, curDate, endDate, payType, amount);
				//this.sendChooseSms(customerId, productName,productSysFlag);
			}
		}
		if(code==0){
			result="success";
		}
		return result;
	}
	
	/**
	 * 保存数据至本地库
	 * @param customerId
	 * @param orderNo
	 * @param packageCode
	 * @param curDate
	 * @param endDate
	 * @param payType
	 * @param amount
	 */
	private void saveSubLocalData(int customerId, String orderNo, String packageCode, Date curDate, Date endDate, int payType,String amount){
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		cachedThreadPool.execute(new Runnable() {  
		    public void run() {  
		     try {
		    	restTemplate.getForObject(myProps.getInitJobServer()+"/chooseRecord/save_reocrd"+"?customerId={1}&orderNo={2}&stockCode={3}&startDate={4}&endDate={5}&type={6}&amount={7}", ResultModel.class,customerId, orderNo, packageCode, curDate, endDate, payType, Integer.parseInt(amount));
		    	Thread.sleep(1000);
		     } catch (Exception ce) {
		         ce.printStackTrace();
		         logger.error("选股订购记录保存本地线程失败 ",ce);
		     }
		   }
		});
	}
	
	/**
	 * 选股订购成功后发送短信
	 * @param customerId
	 * @param productName
	 */
	private void sendChooseSms(Integer customerId,String productName,String productSysFlag){
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		cachedThreadPool.execute(new Runnable() {  
		    public void run() {  
		     try {
		    	ResultModel smsChooseModel=restTemplate.getForObject(myProps.getCustomerServer()+"/customer/send_choose_sms"+"?customerId={1}&stockName={2}&productSysFlag={3}", ResultModel.class,customerId,productName,productSysFlag);
		    	logger.info("选股订购成功发送短信返回信息:"+JSON.toJSONString(smsChooseModel));	
		    	Thread.sleep(1000);
		     } catch (Exception ce) {
		         ce.printStackTrace();
		         logger.error("选股订购成功发送短信线程失败 ",ce);
		     }
		   }
		});
	}
	
	/**
	 * 选股免费订阅
	 * @param sessionCode
	 * @param packageCode
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/free_order", method = RequestMethod.POST)
	public @ResponseBody ResultModel freeOrdere(String sessionCode,String productCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		try{
			if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(productCode)) {
	    		result.setCode(-1);
	    		result.setMessage("参数错误");
	    		return result;
	    	}
			logger.info("choose free order param sessionCode:"+sessionCode+",productCode:"+productCode);
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> custMap = (Map<String, Object>) re.getResult();
				int customerId=Integer.parseInt(custMap.get("customerId").toString());
				String orgCode=custMap.get("orgCode").toString();
				//查询选股信息
				Map<String,ChooseStockCfg> chooseMap=this.queryChooseCfgByOrgCode(orgCode);
				String productName=chooseMap.get(productCode).getStockName();
				ResultModel custModel=restTemplate.getForObject(myProps.getCustomerServer()+open_choose_model +"?customerId={1}&stockCode={2}&stockName={3}&productSysFlag={4}&openType={5}&remark={6}", ResultModel.class,customerId,productCode,productName,"xg001","2","订购免费模型");
		    	logger.info("免费订阅开通选股权限返回信息:"+JSON.toJSONString(custModel));	
			}else{
				return re;
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("free choose order info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 查询选股权限
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
			logger.info("query choose permission sessionCode:"+sessionCode);
			JSONArray arraylist = new JSONArray();
			
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				int customerId=Integer.parseInt(map.get("customerId").toString());
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				//机构编码
				String orgCode=map.get("orgCode").toString();
				//查询选股信息
				Map<String,ChooseStockCfg> chooseMap=this.queryChooseCfgByOrgCode(orgCode);
				Map<String,Integer> exitMap=new HashMap<String,Integer>();
				
				//用户订阅数
				ResultModel subCountResult=restTemplate.getForObject(myProps.getSubscribeServer() + querySubProCount+ "?sysFlag={1}&orgCode={2}", ResultModel.class,productSysFlag,orgCode);
				if(subCountResult.getCode()==0){
					logger.info("query choose cust number :"+subCountResult.getResult());
					Map<String,Integer> subMap=(Map<String,Integer>)subCountResult.getResult();
					for(Entry<String,Integer> entry:subMap.entrySet()){
						String mapKey=entry.getKey().replace(orgCode+"_", "");
						exitMap.put(mapKey, entry.getValue());
					}
				}
				
				Map<String,String> recordMap=new HashMap<String,String>();
				Map<String,List<String>> endDateMap=new HashMap<String,List<String>>();
				//没有过期的订购记录
				ResultModel back=restTemplate.getForObject(myProps.getSubscribeServer() + queryMySubInfoBySys+ "?sysFlag={1}&customerKey={2}&orgCode={3}", ResultModel.class,productSysFlag,customerKey,orgCode);
				JSONArray jsonArray=JSON.parseArray(JSON.toJSONString(back.getResult()));
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
				for(Entry<String,ChooseStockCfg> entry:chooseMap.entrySet()){
					String stockCode=entry.getKey();
					JSONObject obj = new JSONObject();
					ChooseStockCfg cfg=entry.getValue();
					String stockImg="";
					String beginDate="";
					String endDate="";
					int exit=recordMap.get(stockCode)==null?0:1;
					if(exit==0){
						stockImg=cfg.getNoPermissionImg()==null?"":cfg.getNoPermissionImg();
						beginDate="";
						endDate="";
					}else{
						stockImg=cfg.getHavePermissionImg()==null?"":cfg.getHavePermissionImg();
						beginDate=recordMap.get(stockCode);
						List<String> endDateList=endDateMap.get(stockCode);
						endDate=endDateList.get(endDateList.size()-1);
					}
					obj.put("stockCode", stockCode);
					obj.put("stockId", cfg.getCfgId());
					obj.put("isExit", exit);
					obj.put("orderNum", cfg.getOrderNum()+(exitMap.get(stockCode)==null?0:exitMap.get(stockCode)));
					obj.put("status", cfg.getStatus()==0?"free":"money");
					obj.put("stockName", cfg.getStockName());
					obj.put("stockImg", stockImg);
					obj.put("remark", cfg.getRemark()==null?"":cfg.getRemark());
					obj.put("link", cfg.getLink()==null?"":cfg.getLink());
					obj.put("validDate", beginDate+" ~ "+endDate);
					
					ResultModel shareResult=restTemplate.getForObject(myProps.getActicityServer() + "/activity/query_share_info"+ "?customerId={1}&stockCode={2}", ResultModel.class,customerId,stockCode);
					if(shareResult.getCode()==0){
						Map<String, Object> shareMap=(Map<String, Object>)shareResult.getResult();
						obj.put("ticket", Integer.parseInt(shareMap.get("ticket").toString()));
						obj.put("isShare", Integer.parseInt(shareMap.get("isShare").toString()));
					}
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
			logger.error("query permission info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 查询选股信息
	 * @param orgCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,ChooseStockCfg> queryChooseCfgByOrgCode(String orgCode){
		Map<String,ChooseStockCfg> chooseCfgMap=new HashMap<String,ChooseStockCfg>();
		//从redis 里面获取选股基本配置
		List<Object> objectList = redisUtils.queryHashValue("choose_stock_cfg");
		for(Object object:objectList){
			Map<String, Object> chooseMap = (Map<String, Object>) object;
			if(orgCode.equals(chooseMap.get("orgCode").toString())){
				ChooseStockCfg cfg=new ChooseStockCfg();
				cfg.setCfgId(Integer.parseInt(chooseMap.get("cfgId").toString()));
				cfg.setStockCode(chooseMap.get("stockCode").toString());
				cfg.setStockName(chooseMap.get("stockName").toString());
				cfg.setStatus(Integer.parseInt(chooseMap.get("status").toString()));
				cfg.setOrgCode(chooseMap.get("orgCode").toString());
				cfg.setHavePermissionImg(chooseMap.get("havePermissionImg").toString());
				cfg.setNoPermissionImg(chooseMap.get("noPermissionImg").toString());
				cfg.setRemark(null==chooseMap.get("remark")?"":chooseMap.get("remark").toString());
				cfg.setLink(null==chooseMap.get("link")?"":chooseMap.get("link").toString());
				cfg.setOrderNum(Integer.parseInt(chooseMap.get("orderNum").toString()));
				chooseCfgMap.put(cfg.getStockCode(), cfg);
			}
		}
	    return chooseCfgMap;
	}
	
	/**
	 * 我的选股首页
	 * @param sessionCode
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_order", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryOrder(String sessionCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		try{
			String curDateStr=TimeUtil.formatDate(new Date());
			JSONArray arraylist = new JSONArray();
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				//机构编码
				String orgCode=map.get("orgCode").toString();
				//查询选股信息
				Map<String,ChooseStockCfg> chooseMap=this.queryChooseCfgByOrgCode(orgCode);
				Map<String,List<ChooseResVo>> resVoMap=new HashMap<String,List<ChooseResVo>>();
				//选股动态存储在redis里面
				List<Object> objectList = redisUtils.queryHashValue("choose_res_vo");
				for(Object object:objectList){
					Map<String, Object> coseMap = (Map<String, Object>) object;
					if(curDateStr.equals(coseMap.get("day").toString())){
						ChooseResVo resVo=new ChooseResVo();
						resVo.setCode(coseMap.get("code").toString());
						resVo.setStockName(coseMap.get("stockName").toString());
						resVo.setPrice(coseMap.get("price").toString());
						resVo.setTime(coseMap.get("time").toString());
						String strategyCode=coseMap.get("strategyCode").toString();
						List<ChooseResVo> resVoList=resVoMap.get(strategyCode);
						if(null==resVoList){
							resVoList=new ArrayList<ChooseResVo>();
							resVoMap.put(strategyCode, resVoList);
						}
						resVoList.add(resVo);
				    }
				}
				 
				Map<String,Date> custRecordMap=new HashMap<String,Date>();
				//没有过期的订购记录
				ResultModel back=restTemplate.getForObject(myProps.getSubscribeServer() + queryMySubInfoBySys+ "?sysFlag={1}&customerKey={2}&orgCode={3}", ResultModel.class,productSysFlag,customerKey,orgCode);
				JSONArray jsonArray=JSON.parseArray(JSON.toJSONString(back.getResult()));
				int size=jsonArray.size();
				if(size>0){
					for(int i=0;i<size;i++){
						JSONObject jsonObj=jsonArray.getJSONObject(i);
						String stockCode =jsonObj.getString("sysProductCode");
						Date useEndTime=jsonObj.getDate("useEndTime");
						custRecordMap.put(stockCode, useEndTime);
					}
				}
				int mapSize=custRecordMap.size();
				if(mapSize>0){
					for(Entry<String,Date> entry:custRecordMap.entrySet()){
						String stockCode=entry.getKey();
						JSONObject obj = new JSONObject();
						obj.put("stockId", chooseMap.get(stockCode).getCfgId());
						obj.put("stockCode", stockCode);
						obj.put("stockName", chooseMap.get(stockCode).getStockName());
						obj.put("date", TimeUtil.formatDate(entry.getValue()));
						List<ChooseResVo> resVoList=resVoMap.get(stockCode);
						if(null!=resVoList&&resVoList.size()>0){
						    this.sortChooseResVoByTime(resVoList);
							ChooseResVo resVo=resVoList.get(0);
							if(null!=resVo){
								obj.put("dynamic", "【"+resVo.getStockName()+resVo.getCode()+"】被入选，入选价"+resVo.getPrice());
								obj.put("dynamicTime", resVo.getTime());
							}else{
								obj.put("dynamic", "暂无入选动态");
								obj.put("dynamicTime", "");
							}
						}else{
							obj.put("dynamic", "暂无入选动态");
							obj.put("dynamicTime", "");
						}
						arraylist.add(obj);
					}
				}
				result.setResult(arraylist);
			}else{
				return re;
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query query_order info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 按照时间逆序排序
	 * @param objList
	 */
	private void sortChooseResVoByTime(List<ChooseResVo> infoList){ 
		Comparator<ChooseResVo> com = new Comparator<ChooseResVo>() { 
		public int compare(ChooseResVo obj1, ChooseResVo obj2){ 
            if (obj1.getTime().compareTo(obj2.getTime())>0 ){
				return -1;
			}else{
				return 1;
			}
		 } 
		}; 
		Collections.sort(infoList, com); 
	}
	
	/**
	 * 我的选股订购记录
	 * @param sessionCode
	 */
	@RequestMapping(value = "/query_choose_record", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryOrderDetail(String sessionCode) {
		if (StringUtils.isBlank(sessionCode)) {
			ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		logger.info("choose query_order_detail order param sessionCode:"+sessionCode);
		return payUtils.queryOrderDetail(sessionCode, productSysFlag);
	}
	
	/**
	 * c++选股平台选出股票后推送给app和pc端
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/send_msg", method = RequestMethod.POST)
	public @ResponseBody ResultModel sendMsg(@RequestBody Map<String, String> paramMap) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		try{
			String strategyCode=paramMap.get("strategy_code").toString();
			int infocode=Integer.parseInt(paramMap.get("infocode"));
			String title=paramMap.get("title");
			String content=paramMap.get("content");
			String exdata=paramMap.get("exdata");
			logger.info("接收选股中心参数 strategy_code:"+strategyCode+",infocode:"+infocode+",title:"+title+",content:"+content+",exdata:"+exdata);
			
			ResultModel chooseModel=restTemplate.getForObject(myProps.getInitJobServer() + "/chooseRecord/query_choosepush_num"+ "?modelCode={1}", ResultModel.class,strategyCode);
    		logger.info("query choose num result :"+JSON.toJSONString(chooseModel));
    		int number=0;
			if(chooseModel.getCode()==0){
				Map<String, Object> map = (Map<String, Object>) chooseModel.getResult();
				number=Integer.parseInt(map.get("number").toString());
			}
			
			if(number<3){
				restTemplate.getForObject(myProps.getInitJobServer()+"/chooseRecord/save_choosepush_record"+"?modelCode={1}&infoCode={2}&title={3}&content={4}", ResultModel.class,strategyCode, infocode, title, content);
				List<Object> objectList = redisUtils.queryHashValue("sys_push_cfg");
				for(Object object:objectList){
					Map<String, Object> map = (Map<String, Object>) object;
				    String orgCode= map.get("orgCode").toString();
				    chooseStockUtil.pushMsg(strategyCode, orgCode, title, content, infocode, exdata);
				}
			}else{
				logger.info("该模型今天推送次数已达到3次，不在进行推送!");
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("send_msg info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 每日金股
	 * @return
	 */
	@RequestMapping(value = "/activity", method = RequestMethod.POST)
	public @ResponseBody ResultModel activity() {
		return restTemplate.getForObject(myProps.getChooseServer() + activity, ResultModel.class);
	}
	
	/**
	 * 每日金股每日浏览数量
	 * @return
	 */
	@RequestMapping(value = "/query_choose_count", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryChooseCount() {
		return restTemplate.getForObject(myProps.getCustomerServer() + query_choose_count, ResultModel.class);
	}
	
	/**
	 * 查询免费的选股模型
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_free", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryFree(String orgCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(orgCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		logger.info("query choose free param orgCode:"+orgCode);
		try{
			List<ChooseStockCfg> cfgList = new ArrayList<ChooseStockCfg>();
			//查询选股信息
			Map<String,ChooseStockCfg> chooseMap=this.queryChooseCfgByOrgCode(orgCode);
			for(Entry<String,ChooseStockCfg> entry:chooseMap.entrySet()){
				ChooseStockCfg cfg=entry.getValue();
				if(cfg.getStatus()==0){
					cfgList.add(cfg);
				}
			}
			result.setResult(cfgList);
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query choose free info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
	
}
