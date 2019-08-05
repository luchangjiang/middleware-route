package com.lotstock.eddid.route.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;

/**
 * 订阅权限
 * 
 * @author David
 *
 */
@Component
public class SubscribeUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyProps myProps;

	// 查询小市场
	private static final String query_small_market = "/pcinfo/query_small_market";
	// 查询香港小市场
	private static final String query_hk_market="/pcinfo/query_hk_market";
	// 根据产品所属系统编码查询
	private static final String queryProductBySysFlag = "/pro/queryProductBySysFlag";
	// 订阅的产品记录
	private static final String queryMyProductList = "/subscribe/queryMyProductList";
	// 订阅的套餐记录
	private static final String queryMyPackageList = "/subscribe/queryMyPackageList";
	// 订阅记录
	private static final String queryMySubList = "/subscribe/queryMySubList";
	// 行情常量
	private static final String productSysFlag = "zb001";
	// 功能常量
	private static final String gnSysFlag = "pay001";
	// 资讯常量
	private static final String zxSysFlag = "zx001";
	//港股特殊市场处理 依次是 认股证，牛熊证
	private List<String> hkMarketList= Arrays.asList("2003","2004");
	// 查询订购的未过期
    private static final String queryActiveCusPro="/gddSub/queryActiveCusPro";
    //批量查询用户id
  	private static final String query_batch_custId="/customer/query_batch_custId";
  	//批量查询用户手机号码
  	private static final String query_batch_custMobile="/customer/query_batch_custMobile";

	/**
	 * 用户权限
	 * @param customerId
	 * @param customerKey
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public JSONObject querySubscribe(int customerId, int customerKey, String mobile) throws Exception{
		JSONObject jsonObject = new JSONObject();
		// 所有小市场
		Map<String, String> firstMap = this.queryBaseMarket(query_small_market);
		// 港股小市场
		Map<String, String> hkMap = this.queryBaseMarket(query_hk_market);
		// 用户订购记录
//		Map<String, List<String>> secondMap = this.queryMyPackage(customerId,customerKey, mobile, productSysFlag);
		Map<String, List<String>> secondMap = this.queryMySubList(customerId,customerKey);
		// 行情权限
		JSONArray hkArray = new JSONArray();
		if (firstMap.size() > 0) {
			for (Entry<String, String> entry : firstMap.entrySet()) {
				JSONObject obj = new JSONObject();
				//0 无数据 1有数据 2需登录
				String subscribe = "0";
				String beginDate="";
				String endDate="";
				String packageUnit="";
				String packageCode="";
				String key = entry.getKey();
				String [] value=entry.getValue().split("_");
				String productName=value[0];
				String type=value[1];
				String parentMarketCode=value[2];
				//判断香港市场
				if(hkMap.containsKey(key)){
					//判断是否是游客
					if(1==customerId){
						if(hkMarketList.contains(key)){
							subscribe = "2";
						}else{
							subscribe = "1";
						}
					}else{
						// 香港股票特殊处理,这里2002代表香港股票
						List<String> dataList=secondMap.get("2002");
						if(dataList != null && dataList.size() > 0){
							String[] strData =dataList.get(dataList.size()-1).split("_");
							beginDate = strData[0];
							endDate = strData[1];
							int startDay = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(beginDate));
							int day = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(endDate));
							subscribe = startDay <= 0 && day > 0  ?"1":"0";
							packageUnit = strData[2];
							packageCode = strData[3];
						}
					}
				}else{
					if(secondMap.containsKey(key)||secondMap.containsKey(parentMarketCode)){
						List<String> dataList = new ArrayList<>();
						List<String> dataList1=secondMap.get(key);
						List<String> dataList2=secondMap.get(parentMarketCode);
						if(dataList1 != null && dataList1.size() > 0) dataList.addAll(dataList1);
						if(dataList2 != null && dataList2.size() > 0) dataList.addAll(dataList2);
						if(dataList != null && dataList.size() > 0){
							String[] strData =dataList.get(dataList.size()-1).split("_");
							beginDate = strData[0];
							endDate = strData[1];
							int startDay = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(beginDate));
							int day = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(endDate));
							subscribe = startDay <= 0 && day > 0  ?"1":"0";
							packageUnit = strData[2];
							packageCode = strData[3];
						}
					} else {
						if(1==customerId){
							//ADR特殊处理
							if("35000".equals(key)){
								subscribe = "2";
							}
						}else{
							subscribe = "0";
						}
					}
				}
				// 国际期货板块过滤
				List<String> ltStr = Arrays.asList("81","82","83","84","85","86");
				if(ltStr.contains(key)){
					continue;
				}
				// 港股热门行业特殊处理
				if("57000".equals(key)){
					subscribe = "1";
					endDate = "2030-12-31";
				}
				obj.put("subscribe", subscribe);
				obj.put("code", key);
				obj.put("name", productName);
				obj.put("type", type);
				obj.put("packageUnit", packageUnit);
				obj.put("beginDate", beginDate);
				obj.put("endDate", endDate);
				obj.put("packageCode", packageCode);
				hkArray.add(obj);
			}
		}
		//AH股游客需要登录
		if(1==customerId){
			JSONObject obj = new JSONObject();
			obj.put("subscribe", "2");
			obj.put("code", "20000");
			obj.put("name", "AH");
			obj.put("type", "1");
			obj.put("packageUnit", "");
			obj.put("beginDate", "");
			obj.put("endDate", "");
			obj.put("packageCode", "");
			hkArray.add(obj);
		}
		jsonObject.put("market", hkArray);

		// 功能权限
		Map<String, String> thirdMap = this.queryProductByFlag(gnSysFlag);
		JSONArray funArray = new JSONArray();
		boolean flag=false;
		if (thirdMap.size() > 0) {
//			Map<String, List<String>> fourthMap = this.queryMyPackage(customerId,customerKey, mobile, gnSysFlag);
			for (Entry<String, String> entry : thirdMap.entrySet()) {
				JSONObject obj = new JSONObject();
				String funCode=entry.getKey();
				//0 无数据 1有数据 2需登录
				String subscribe = "0";
				String beginDate="";
				String endDate="";
				String packageUnit="";
				String packageCode="";
				//判断是否是游客
				if(1==customerId){
					//分时查价,量价图 这两个功能特殊处理游客看延时数据，其他功能需非游客账号登录
					if("fscj".equals(funCode)||"ljt".equals(funCode)){
						subscribe = "1";
					}else{
						subscribe = "2";
					}
				}else{
					if(secondMap.containsKey(funCode)){
						//判断是否是大利市，大利市包含大利市，新大利市，小大利市
						if("dls".equals(funCode)){
							flag=true;
						}
						List<String> dataList=secondMap.get(funCode);
						if(dataList != null && dataList.size() > 0){
							String[] strData =dataList.get(dataList.size()-1).split("_");
							beginDate = strData[0];
							endDate = strData[1];
							int startDay = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(beginDate));
							int day = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(endDate));
							subscribe = startDay <= 0 && day > 0  ?"1":"0";
							packageUnit = strData[2];
							packageCode = strData[3];
						}
						/*List<String> endDateList=fourthMap.get(funCode);
						endDate=endDateList.get(endDateList.size()-1);*/
					}else{
						//分时查价,量价图 这两个功能特殊处理没有订购的看延时数据
						if("fscj".equals(funCode)||"ljt".equals(funCode)){
							subscribe = "1";
						}
					}
				}
				obj.put("subscribe", subscribe);
				obj.put("code", funCode);
				obj.put("name", entry.getValue());
				obj.put("type", "");
				obj.put("packageUnit", packageUnit);
				obj.put("beginDate", beginDate);
				obj.put("endDate", endDate);
				obj.put("packageCode", packageCode);
				funArray.add(obj);
			}
		}
		if(1==customerId){
			this.createGnFun(funArray,"2","yk",false);
		}else{
			this.createGnFun(funArray,"1","yh",flag);
		}
		jsonObject.put("fun", funArray);

		// 资讯权限
		Map<String, String> fiveMap = this.queryProductByFlag(zxSysFlag);
		JSONArray zxArray = new JSONArray();
		if (fiveMap.size() > 0) {
//			Map<String, List<String>> sixMap = this.queryMyPackage(customerId,customerKey, mobile, zxSysFlag);
			for (Entry<String, String> entry : fiveMap.entrySet()) {
				JSONObject obj = new JSONObject();
				String zxCode=entry.getKey();
				//0 无数据 1有数据 2需登录
				String subscribe = "0";
				String beginDate = "";
				String endDate="";
				String packageUnit = "";
				String packageCode="";
				//判断是否是游客
				if(1==customerId){
					subscribe = "2";
					endDate="";
				}else{
					if (secondMap.containsKey(zxCode)) {
						List<String> dataList=secondMap.get(zxCode);
						if(dataList != null && dataList.size() > 0){
							String[] strData =dataList.get(dataList.size()-1).split("_");
							beginDate = strData[0];
							endDate = strData[1];
							int startDay = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(beginDate));
							int day = TimeUtil.getDaySpace(new Date(),TimeUtil.parseDateFormat(endDate));
							subscribe = startDay <= 0 && day > 0  ?"1":"0";
							packageUnit = strData[2];
							packageCode = strData[3];
						}
						/*List<String> endDateList=sixMap.get(zxCode);
						endDate=endDateList.get(endDateList.size()-1);*/
					}
				}
				obj.put("subscribe", subscribe);
				obj.put("code", zxCode);
				obj.put("name", entry.getValue());
				obj.put("type", "");
				obj.put("packageUnit", packageUnit);
				obj.put("beginDate", beginDate);
				obj.put("endDate", endDate);
				obj.put("packageCode", packageCode);
				zxArray.add(obj);
			}
		}
		jsonObject.put("zx", zxArray);
		return jsonObject;
	}

	/**
	 * 获取小市场信息
	 * @return
	 */
	public Map<String, String> queryBaseMarket(String queryUrl) {
		Map<String, String> firstMap = new HashMap<String, String>();
		ResultModel firstModel = restTemplate.getForObject(myProps.getPcServer() + queryUrl, ResultModel.class);
		if (firstModel.getCode() == 0) {
			JSONArray firstArray = JSONArray.parseArray(JSON.toJSONString(firstModel.getResult()));
			int firstSize = firstArray.size();
			if (firstSize > 0) {
				for (int i = 0; i < firstSize; i++) {
					JSONObject firstObj = firstArray.getJSONObject(i);
					String marketCode = firstObj.getString("marketCode");
					String marketName = firstObj.getString("marketName");
					String parentMarketCode=firstObj.getString("parentMarketCode");
					int type=firstObj.getInteger("type");
					firstMap.put(marketCode, marketName+"_"+type+"_"+parentMarketCode);
				}
			}
		}
		return firstMap;
	}

	/**
	 * 查询我的订单
	 * @param customerId
	 * @param customerKey
	 * @param mobile
	 * @param sysFlag
	 * @return
	 */
	private Map<String, List<String>> queryMyOrder(int customerId, int customerKey,String mobile, String sysFlag) {
		Map<String,List<String>> endDateMap=new HashMap<String,List<String>>();
		ResultModel secondModel = restTemplate.getForObject(myProps.getSubscribeServer()+ queryMyProductList+ "?pageSize={1}&customerId={2}&customerKey={3}&customerMobile={4}&queryType={5}&productSysFlag={6}",ResultModel.class, 50, customerId, customerKey, mobile,2, sysFlag);
		logger.info("query subscribeServer hq order result:"+ JSON.toJSONString(secondModel));
		if (secondModel.getCode() == 0) {
			JSONArray secondArray = JSONArray.parseArray(JSON.toJSONString(secondModel.getResult()));
			int secondSize = secondArray.size();
			if (secondSize > 0) {
				for (int i = 0; i < secondSize; i++) {
					JSONObject secondObj = secondArray.getJSONObject(i);
					String productCode = secondObj.getString("sysProductCode");
					Date useEndTime=secondObj.getDate("useEndTime");
					List<String> endDateList=endDateMap.get(productCode);
					if(null==endDateList){
						endDateList=new ArrayList<String>();
						endDateMap.put(productCode, endDateList);
					}
					endDateList.add(TimeUtil.formatDate(useEndTime));
				}
			}
		}
		return endDateMap;
	}

	/**
	 * 查询我订购的套餐
	 * @param customerId
	 * @param customerKey
	 * @param mobile
	 * @param sysFlag
	 * @return
	 */
	private Map<String, List<String>> queryMyPackage(int customerId, int customerKey,String mobile, String sysFlag) {
		Map<String,List<String>> endDateMap=new HashMap<String,List<String>>();
		ResultModel secondModel = restTemplate.getForObject(myProps.getSubscribeServer()+ queryMyPackageList+ "?pageSize={1}&customerId={2}&customerKey={3}&customerMobile={4}&productSysFlag={5}",ResultModel.class, 50, customerId, customerKey, mobile,sysFlag);
		logger.info("query subscribeServer hq order result:"+ JSON.toJSONString(secondModel));
		if (secondModel.getCode() == 0) {
			JSONObject secondObj = JSONObject.parseObject(JSON.toJSONString(secondModel.getResult()));
			JSONArray listArray = JSONArray.parseArray(secondObj.get("list") + "");
			if(listArray.size() > 0){
				for (int j = 0; j < listArray.size(); j++) {
					JSONObject jsonObj = listArray.getJSONObject(j);
					Date useBeginTime=jsonObj.getDate("orderBeginTime");
					Date useEndTime=jsonObj.getDate("orderEndTime");
					String packageUnit = jsonObj.getString("packageUnit");
					JSONArray dataJsonArray = JSONArray.parseArray(jsonObj.getString("data"));
					if(dataJsonArray.size()>0){
						for (int x = 0; x < dataJsonArray.size(); x++) {
							JSONObject dataObj = dataJsonArray.getJSONObject(x);
							String productCode = dataObj.getString("sysProductCode");
							List<String> endDateList=endDateMap.get(productCode);
							if(null==endDateList){
								endDateList=new ArrayList<String>();
								endDateMap.put(productCode, endDateList);
							}
							endDateList.add(TimeUtil.formatDate(useBeginTime) + "_" +TimeUtil.formatDate(useEndTime) + "_"+ packageUnit);
						}
					}
				}
			}
		}
		return endDateMap;
	}
	
	/**
	 * 查询我订购的记录
	 * @param customerId
	 * @param customerKey
	 * @return
	 */
	private Map<String, List<String>> queryMySubList(int customerId, int customerKey) {
		Map<String,List<String>> endDateMap=new HashMap<String,List<String>>();
		ResultModel secondModel = restTemplate.getForObject(myProps.getSubscribeServer()+ queryMySubList+ "?pageSize={1}&customerId={2}&customerKey={3}",ResultModel.class, 50, customerId, customerKey);
		logger.info("query subscribeServer hq order result:"+ JSON.toJSONString(secondModel));
		if (secondModel.getCode() == 0) {
			JSONArray resultArray = JSONArray.parseArray(JSON.toJSONString(secondModel.getResult()));
			if(resultArray != null && resultArray.size() > 0){
				for (int i = 0; i < resultArray.size(); i++) {
					JSONObject resultObj = resultArray.getJSONObject(i);
					String packageUnit = resultObj.getString("packageUnit");
					JSONArray dataJsonArray = JSONArray.parseArray(resultObj.getString("data"));
					if(dataJsonArray.size()>0){
						for (int x = 0; x < dataJsonArray.size(); x++) {
							JSONObject dataObj = dataJsonArray.getJSONObject(x);
							String productCode = dataObj.getString("sysProductCode");
							Date useBeginTime=dataObj.getDate("useBeginTime");
							Date useEndTime=dataObj.getDate("useEndTime");
							String packageCode=dataObj.getString("packageCode");
							List<String> endDateList=endDateMap.get(productCode);
							if(null==endDateList){
								endDateList=new ArrayList<String>();
								endDateMap.put(productCode, endDateList);
							}
							endDateList.add(TimeUtil.formatDate(useBeginTime) + "_" +TimeUtil.formatDate(useEndTime) + "_"+ packageUnit + "_"+ packageCode);
						}
					}
				}
			}
		}
		return endDateMap;
	}
	
	/**
	 * 查询某一类下的产品
	 * @param sysFlag
	 * @return
	 */
	private Map<String, String> queryProductByFlag(String sysFlag) {
		Map<String, String> thirdMap = new HashMap<String, String>();
		ResultModel thirdModel = restTemplate.getForObject(myProps.getSubscribeServer() + queryProductBySysFlag+ "?productSysFlag={1}", ResultModel.class, sysFlag);
		if (thirdModel.getCode() == 0) {
			JSONArray thirdArray = JSONArray.parseArray(JSON.toJSONString(thirdModel.getResult()));
			int thirdSize = thirdArray.size();
			if (thirdSize > 0) {
				for (int i = 0; i < thirdSize; i++) {
					JSONObject thirdObj = thirdArray.getJSONObject(i);
					String productCode = thirdObj.getString("sysProductCode");
					String sysProductName = thirdObj.getString("sysProductName");
					thirdMap.put(productCode, sysProductName);
				}
			}
		}
		return thirdMap;
	}
	
	/**
	 * 特殊功能权限控制
	 * @param funArray
	 * @param subscribe
	 * @param type
	 * @param flag
	 */
	private void createGnFun(JSONArray funArray,String subscribe,String type,boolean flag){
		JSONObject obj1 = new JSONObject();
		obj1.put("subscribe", subscribe);
		obj1.put("code", "jdph");
		obj1.put("name", "阶段排行");
		obj1.put("type", "");
		obj1.put("packageUnit", "");
		obj1.put("beginDate", "");
		obj1.put("endDate", "");
		obj1.put("packageCode", "");
		funArray.add(obj1);
		
		JSONObject obj2 = new JSONObject();
		obj2.put("subscribe", subscribe);
		obj2.put("code", "tl");
		obj2.put("name", "套利");
		obj2.put("type", "");
		obj2.put("packageUnit", "");
		obj2.put("beginDate", "");
		obj2.put("endDate", "");
		obj2.put("packageCode", "");
		funArray.add(obj2);
		
		JSONObject obj3 = new JSONObject();
		obj3.put("subscribe", subscribe);
		obj3.put("code", "scld");
		obj3.put("name", "市场雷达");
		obj3.put("type", "");
		obj3.put("packageUnit", "");
		obj3.put("beginDate", "");
		obj3.put("endDate", "");
		obj3.put("packageCode", "");
		funArray.add(obj3);
		
		JSONObject obj4 = new JSONObject();
		obj4.put("subscribe", subscribe);
		obj4.put("code", "tjyj");
		obj4.put("name", "条件预警");
		obj4.put("type", "");
		obj4.put("packageUnit", "");
		obj4.put("beginDate", "");
		obj4.put("endDate", "");
		obj4.put("packageCode", "");
		funArray.add(obj4);
		
		JSONObject obj5 = new JSONObject();
		obj5.put("subscribe", subscribe);
		obj5.put("code", "tjxgq");
		obj5.put("name", "条件选股器");
		obj5.put("packageUnit", "");
		obj5.put("beginDate", "");
		obj5.put("endDate", "");
		obj5.put("packageCode", "");
		funArray.add(obj5);
		
		JSONObject obj6 = new JSONObject();
		obj6.put("subscribe", subscribe);
		obj6.put("code", "dzxgq");
		obj6.put("name", "定制选股器");
		obj6.put("type", "");
		obj6.put("packageUnit", "");
		obj6.put("beginDate", "");
		obj6.put("endDate", "");
		obj6.put("packageCode", "");
		funArray.add(obj6);
		
		JSONObject obj7 = new JSONObject();
		obj7.put("subscribe", subscribe);
		obj7.put("code", "jrgz");
		obj7.put("name", "今日vcm关注");
		obj7.put("type", "");
		obj7.put("packageUnit", "");
		obj7.put("beginDate", "");
		obj7.put("endDate", "");
		obj7.put("packageCode", "");
		funArray.add(obj7);
		
		//0 无数据 1有数据 2需登录
		String sub="0";
		if("yk".equals(type)){
			sub="2";
		}else{
			if(flag){
			  sub="1";
			}
		}
		JSONObject obj8 = new JSONObject();
		obj8.put("subscribe", sub);
		obj8.put("code", "xindls");
		obj8.put("name", "新大利市");
		obj8.put("type", "");
		obj8.put("packageUnit", "");
		obj8.put("beginDate", "");
		obj8.put("endDate", "");
		obj8.put("packageCode", "");
		funArray.add(obj8);
		
		JSONObject obj9 = new JSONObject();
		obj9.put("subscribe", sub);
		obj9.put("code", "xiaodls");
		obj9.put("name", "小大利市");
		obj9.put("type", "");
		obj9.put("packageUnit", "");
		obj9.put("beginDate", "");
		obj9.put("endDate", "");
		obj9.put("packageCode", "");
		funArray.add(obj9);
	}
	
	/**
	 * 我的特权
	 * @param customerId
	 * @param customerKey
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public JSONArray queryHkPermission(int customerId, int customerKey, String mobile) throws Exception{
		JSONArray jsonArray = new JSONArray();
		Map<String,String> recordMap=new HashMap<String,String>();
		Map<String,List<String>> endDateMap=new HashMap<String,List<String>>();
		//没有过期的订购记录
		ResultModel back= restTemplate.getForObject(myProps.getSubscribeServer() + queryMyProductList+ "?pageSize={1}&customerId={2}&customerKey={3}&customerMobile={4}&queryType={5}&productSysFlag={6}", ResultModel.class,50,customerId,customerKey,mobile,2,productSysFlag);
		JSONArray firstArray=JSONArray.parseArray(JSON.toJSONString(back.getResult()));
		int size=firstArray.size();
		if(size>0){
			for(int i=0;i<size;i++){
				JSONObject jsonObj=firstArray.getJSONObject(i);
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
		
		//所有小市场
		Map<String,String> firstMap=this.queryBaseMarket(query_small_market);
		if(firstMap.size()>0){
			for(Entry<String,String> entry:firstMap.entrySet()){
				JSONObject obj = new JSONObject();
				String beginDate="";
				String endDate="";
				String subscribe="0";
				String key=entry.getKey();
				if(endDateMap.containsKey(key)){
					subscribe="1";
					beginDate=recordMap.get(key);
					List<String> endDateList=endDateMap.get(key);
					endDate=endDateList.get(endDateList.size()-1);
				}else{
					subscribe="0";
					beginDate="";
					endDate="";
				}
				obj.put("subscribe", subscribe);
				obj.put("marketCode", key);
				obj.put("productName", entry.getValue());
				obj.put("beginDate", beginDate);
				obj.put("endDate", endDate);
				jsonArray.add(obj);
			}
		}
		return  jsonArray;
	}

	/**
	 * 订阅倒计时推送
	 * @param sysProductCode
	 * @param orgCode
	 * @param title
	 * @param content
	 * @param infocode
	 * @param exdata
	 */
	@SuppressWarnings("unchecked")
	public void pushSubscribeMsg(String sysProductCode,String orgCode,String title,String content,Integer infocode,String exdata,String productSysFlag){
		ResultModel rmModel= restTemplate.getForObject(myProps.getSubscribeServer() + queryActiveCusPro+ "?productSysFlag={1}&sysProductCode={2}&orgCode={3}", ResultModel.class,productSysFlag,sysProductCode,orgCode);
		if(rmModel.getCode()==0){
			logger.info("订阅中心返回数据:"+JSON.toJSONString(rmModel.getResult()));
			JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(rmModel.getResult()));
			if(jsonArray.size()>0){
				String sysProductName="";
				Map<Integer,List<Integer>> custMap=new HashMap<Integer,List<Integer>>();
				for(int i=0;i<jsonArray.size();i++){
					JSONObject json=jsonArray.getJSONObject(i);
					sysProductName=json.getString("sysProductName");
					Integer customerId=json.getInteger("customerId");
					int day=json.getInteger("remainDays");
					List<Integer> custIdList=custMap.get(day);
					if(null==custIdList){
						custIdList=new ArrayList<Integer>();
						custMap.put(day, custIdList);
					}
					custIdList.add(customerId);
				}
				
				//您订购的产品“s%”,s%天后到期，立即续订请拨打110!
				String detail="";
				for(Entry<Integer,List<Integer>>entry:custMap.entrySet()){
					int day=entry.getKey();
					if(day<=3){
						if(day==0){
							detail=String.format(content, sysProductName, "今");
						}else{
							detail=String.format(content, sysProductName, day);
						}
						logger.info("订阅倒计时天数:"+day+",订阅倒计时发送内容:"+detail);
						
						List<Integer> customerKeyList=entry.getValue();
						String custKeys = JSONArray.toJSONString(customerKeyList);
						MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
						custParams.add("custKeys", custKeys);
						ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custId, custParams, ResultModel.class);
						ResultModel body = responseEntity.getBody();
						logger.info("订阅倒计时用户模块返回数据:"+JSON.toJSONString(body.getResult()));
						
						List<Integer> custIdList=(List<Integer>)body.getResult();
						String userIds="";
						String userIdArr="";
						for(Integer custId:custIdList){
							userIdArr+=custId+",";
						}
						if(userIdArr.length()>0){
							userIds=userIdArr.substring(0,userIdArr.length()-1);
						}
						
						MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
						params.add("title", title);
						params.add("content", detail);
						params.add("typeCode", infocode);
						params.add("exdata", exdata);
						params.add("userIds", userIds);
						params.add("orgCode", orgCode);
						
						//data 内容太长用post 提交
						ResponseEntity<ResultModel> appResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_app", params, ResultModel.class);
						logger.info("订阅倒计时推送app端返回值:"+JSON.toJSONString(appResponseEntity.getBody()));
						
						ResponseEntity<ResultModel> pcResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_pc", params, ResultModel.class);
						logger.info("订阅倒计时推送pc端返回值:"+JSON.toJSONString(pcResponseEntity.getBody()));
						
						//订阅到期发送短信
//						this.sendUserMsg(orgCode, detail, custKeys);
					}
				}
			}
		}
	}
	
	/**
	 * 订阅到期发送短信
	 * @param orgCode
	 * @param detail
	 * @param custKeys
	 */
	@SuppressWarnings("unchecked")
	private void sendUserMsg(String orgCode,String detail,String custKeys){
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		cachedThreadPool.execute(new Runnable() {
			public void run() {
				try {
					MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
					custParams.add("custKeys", custKeys);
					//查询手机号码
					ResponseEntity<ResultModel> responseEntity= restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custMobile, custParams, ResultModel.class);
					ResultModel body = responseEntity.getBody();
					if(body.getCode()==0){
						List<String> custMobileList=(List<String>)body.getResult();
						for(String mobile:custMobileList){
							ResponseEntity<ResultModel> smsResponseEntity = restTemplate.getForEntity("http://smsService/sms/sendSms?mobile="+mobile+"&content="+detail+"&orgCode="+orgCode, ResultModel.class);
							logger.info("订阅到期发送短信:"+JSON.toJSONString(smsResponseEntity.getBody()));
						}
					}
				} catch (Exception ce) {
					ce.printStackTrace();
			   }
			}
		});
	}
	
}

