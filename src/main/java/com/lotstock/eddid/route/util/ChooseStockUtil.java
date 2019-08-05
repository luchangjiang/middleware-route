package com.lotstock.eddid.route.util;

import java.util.ArrayList;
import java.util.Calendar;
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

@Component
public class ChooseStockUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 股多多查询订购的未过期
    private static final String queryActiveCusPro="/gddSub/queryActiveCusPro";
    
	//选股常量
	private static final String productSysFlag="xg001";
	
	//策略常量
  	private static final String clSysFlag="cl001";
	
	//批量查询用户id
	private static final String query_batch_custId="/customer/query_batch_custId";
	
	//查询所有的用户key
	private static final String query_batch_custkey="/customer/query_batch_custkey";
	
	//批量查询用户手机号码
	private static final String query_batch_custMobile="/customer/query_batch_custMobile";
	
	//获取h5服务器地址
	private static final String query_datacfg_orgCode="/customer/query_datacfg_orgCode";
	
	/**
	 * 根据结束时间返回
	 * @param endDate
	 * @return
	 */
	public List<Integer> convertCodeResult(Date endDate){
		List<Integer> hourMiuList=new ArrayList<Integer>();
		try{
			if(null!=endDate){
				java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String endTimeStr=plusDay(endDate, 1)+" 00:00:00";
				Date endTime=df.parse(endTimeStr);
				Date curDate=new Date();
				int seconds=TimeUtil.getTimeDelta(curDate, endTime);
				int hour=seconds/3600;
				int minute=seconds%3600/60;
				hourMiuList.add(hour);
				hourMiuList.add(minute);
			}
		}catch(Exception ce){
			ce.printStackTrace();
		}
	    return hourMiuList;
	}
	
	/**
	 * 日期加减
	 * @param endDate
	 * @param day
	 * @return
	 */
	public String plusDay(Date endDate,int day)throws Exception {
		java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(endDate);
		rightNow.add(Calendar.DAY_OF_YEAR,day);
		Date dt=rightNow.getTime();
		String reStr = df.format(dt);
		return reStr;
	}
	
	/**
	 * 推送消息
	 * @param strategyCode
	 * @param orgCode
	 * @param title
	 * @param content
	 * @param infocode
	 * @param exdata
	 */
	@SuppressWarnings("unchecked")
	public void pushMsg(String strategyCode,String orgCode,String title,String content,Integer infocode,String exdata){
		List<Integer> custSet=new ArrayList<Integer>();
		ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + queryActiveCusPro+ "?productSysFlag={1}&sysProductCode={2}&orgCode={3}", ResultModel.class,productSysFlag,strategyCode,orgCode);
		if(chooseCodeModel.getCode()==0){
			logger.info("机构:"+orgCode+",订阅中心返回数据:"+JSON.toJSONString(chooseCodeModel.getResult()));
			JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(chooseCodeModel.getResult()));
			if(jsonArray.size()>0){
				for(int i=0;i<jsonArray.size();i++){
					JSONObject json=jsonArray.getJSONObject(i);
					custSet.add(json.getInteger("customerId"));
				}
				int size=custSet.size();
				int pageSize=800;
				int num=size%pageSize==0?size/pageSize:size/pageSize+1;
				
				List<Integer> customerKeyList=null;
				for(int i=1;i<=num;i++){
					int pageStart=(i-1)*pageSize;
					logger.info("每页开始值:"+pageStart);
					if(i==num){
						customerKeyList=custSet.subList(pageStart, size);
					}else{
						customerKeyList=custSet.subList(pageStart, i*pageSize);
					}
					
					String custKeys = JSONArray.toJSONString(customerKeyList);
					MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
					custParams.add("custKeys", custKeys);
			    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custId, custParams, ResultModel.class);
			    	ResultModel body = responseEntity.getBody();
			    	logger.info("机构:"+orgCode+",用户模块返回已订阅数据:"+JSON.toJSONString(body.getResult()));
			    	
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
			    	params.add("content", content);
			    	params.add("typeCode", infocode);
			    	params.add("exdata", exdata);
			    	params.add("userIds", userIds);
			    	params.add("orgCode", orgCode);
			    	
			    	//data 内容太长用post 提交
			    	ResponseEntity<ResultModel> appResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_app", params, ResultModel.class);
			    	logger.info("推送app端返回值:"+JSON.toJSONString(appResponseEntity.getBody()));
			    	
			    	ResponseEntity<ResultModel> pcResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_pc", params, ResultModel.class);
			    	logger.info("推送pc端返回值:"+JSON.toJSONString(pcResponseEntity.getBody()));
				}
			}
		}
	}
	
	/**
	 * 选股倒计时
	 * @param strategyCode
	 * @param orgCode
	 * @param title
	 * @param content
	 * @param infocode
	 * @param exdata
	 */
	 @SuppressWarnings("unchecked")
	 public void pushCountDownMsg(String strategyCode,String orgCode,String title,String content,Integer infocode,String exdata){
		ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + queryActiveCusPro+ "?productSysFlag={1}&sysProductCode={2}&orgCode={3}&remainDays={4}", ResultModel.class,productSysFlag,strategyCode,orgCode,3);
		if(chooseCodeModel.getCode()==0){
			logger.info("选股倒计时机构:"+orgCode+",订阅中心返回数据:"+JSON.toJSONString(chooseCodeModel.getResult()));
			JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(chooseCodeModel.getResult()));
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
				
				Map<String,Integer> strategyMap=this.queryNumByStrategyCode();
				int num=strategyMap.get(strategyCode);
				String detail="";
				for(Entry<Integer,List<Integer>>entry:custMap.entrySet()){
					int day=entry.getKey();
					if(day==0){
						String remark=String.format(content, sysProductName, "今", "10%", num);
						detail="最后15小时"+remark.replace("后", "");
					}else{
						detail=String.format(content, sysProductName, day, "10%", num);
					}
					logger.info("选股倒计时天数:"+day+",选股倒计时发送内容:"+detail);
					
					List<Integer> customerKeyList=entry.getValue();
					String custKeys = JSONArray.toJSONString(customerKeyList);
					MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
					custParams.add("custKeys", custKeys);
			    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custId, custParams, ResultModel.class);
			    	ResultModel body = responseEntity.getBody();
			    	logger.info("选股倒计时机构:"+orgCode+",用户模块返回数据:"+JSON.toJSONString(body.getResult()));
			    	
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
			    	logger.info("选股倒计时推送app端返回值:"+JSON.toJSONString(appResponseEntity.getBody()));
			    	
			    	ResponseEntity<ResultModel> pcResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_pc", params, ResultModel.class);
			    	logger.info("选股倒计时推送pc端返回值:"+JSON.toJSONString(pcResponseEntity.getBody()));
			    	
			    	//当天过期发送短信
			    	if(day==0){
			    		//取消发送短信,给短信通道省点钱
			    		//this.sendUserMsg(orgCode, detail, custKeys);
			    	}
				}
			}
		}
	}
	
	/**
	 * 当天过期的选股模型短信提示
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
							logger.info("选股当天过期发送短信:"+JSON.toJSONString(smsResponseEntity.getBody()));
						}
					}
				} catch (Exception ce) {
					ce.printStackTrace();
			   }
			}
		});
	}
	
	/**
	 * 策略的推送
	 * @param strategyCode
	 * @param orgCode
	 * @param title
	 * @param content
	 * @param infocode
	 * @param exdata
	 */
	@SuppressWarnings("unchecked")
	public void pushStrategyMsg(String strategyCode,String orgCode,String title,String content,Integer infocode,String exdata){
		List<Integer> custSet=new ArrayList<Integer>();
		ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + queryActiveCusPro+ "?productSysFlag={1}&sysProductCode={2}&orgCode={3}", ResultModel.class,clSysFlag,strategyCode,orgCode);
		if(chooseCodeModel.getCode()==0){
			logger.info("机构:"+orgCode+",订阅中心返回数据:"+JSON.toJSONString(chooseCodeModel.getResult()));
			JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(chooseCodeModel.getResult()));
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=jsonArray.getJSONObject(i);
				custSet.add(json.getInteger("customerId"));
			}
			int size=custSet.size();
			int pageSize=800;
			int num=size%pageSize==0?size/pageSize:size/pageSize+1;
			
			List<Integer> customerKeyList=null;
			for(int i=1;i<=num;i++){
				int pageStart=(i-1)*pageSize;
				logger.info("每页开始值:"+pageStart);
				if(i==num){
					customerKeyList=custSet.subList(pageStart, size);
				}else{
					customerKeyList=custSet.subList(pageStart, i*pageSize);
				}
				
				String custKeys = JSONArray.toJSONString(customerKeyList);
				MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
				custParams.add("custKeys", custKeys);
		    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custId, custParams, ResultModel.class);
		    	ResultModel body = responseEntity.getBody();
		    	logger.info("机构:"+orgCode+",用户模块返回数据:"+JSON.toJSONString(body.getResult()));
		    	
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
		    	params.add("content", content);
		    	params.add("typeCode", infocode);
		    	params.add("exdata", exdata);
		    	params.add("userIds", userIds);
		    	params.add("orgCode", orgCode);
		    	
		    	//data 内容太长用post 提交
		    	ResponseEntity<ResultModel> appResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_app", params, ResultModel.class);
		    	logger.info("推送app端返回值:"+JSON.toJSONString(appResponseEntity.getBody()));
		    	
		    	ResponseEntity<ResultModel> pcResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_pc", params, ResultModel.class);
		    	logger.info("推送pc端返回值:"+JSON.toJSONString(pcResponseEntity.getBody()));
			}
		}
	}
	
	/**
	 * 运营推送数据
	 * @param strategyCode
	 * @param orgCode
	 * @param title
	 * @param content
	 * @param infocode
	 * @param exdata
	 */
	@SuppressWarnings("unchecked")
	public void sendChooseSpecialRecord(String strategyCode,String orgCode,String title,String content,Integer infocode,String exdata){
		List<Integer> custSet=new ArrayList<Integer>();
		ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + queryActiveCusPro+ "?productSysFlag={1}&sysProductCode={2}&orgCode={3}", ResultModel.class,productSysFlag,strategyCode,orgCode);
		if(chooseCodeModel.getCode()==0){
			logger.info("运营推送消息机构:"+orgCode+",订阅中心返回数据:"+JSON.toJSONString(chooseCodeModel.getResult()));
			JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(chooseCodeModel.getResult()));
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=jsonArray.getJSONObject(i);
				custSet.add(json.getInteger("customerId"));
			}
		}
		
		String h5ServerUrl="";
		//查询h5地址
		ResultModel dataCfgModel= restTemplate.getForObject(myProps.getCustomerServer() + query_datacfg_orgCode+ "?orgCode={1}&isCheckPass={2}&dataKey={3}", ResultModel.class,orgCode,1,"H5_SERVER");
		if(dataCfgModel.getCode()==0){
			List<Object> objList=(List<Object>)dataCfgModel.getResult();
			for(int j=0;j<objList.size();j++){
				JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(objList.get(j)));
				logger.info("查询用户模块返回数据:"+JSON.toJSONString(objList.get(j)));
				h5ServerUrl=jsonObj.getString("dataValue");
			}
		}
		logger.info("h5服务器地址:"+h5ServerUrl);
		
		//查询所有用户的key,然后删掉已订阅的用户key
		ResultModel custModel= restTemplate.getForObject(myProps.getCustomerServer() + query_batch_custkey+ "?orgCode={1}", ResultModel.class,orgCode);
		if(custModel.getCode()==0){
			List<Integer> custKeyList=(List<Integer>)custModel.getResult();
			custKeyList.removeAll(custSet);
			logger.info("运营推送消息未订阅用户id:"+JSON.toJSONString(custKeyList));
			
			int size=custKeyList.size();
			int pageSize=800;
			int num=size%pageSize==0?size/pageSize:size/pageSize+1;
			
			List<Integer> customerKeyList=null;
			for(int i=1;i<=num;i++){
				int pageStart=(i-1)*pageSize;
				if(i==num){
					customerKeyList=custKeyList.subList(pageStart, size);
				}else{
					customerKeyList=custKeyList.subList(pageStart, i*pageSize);
				}
				
				String custKeys = JSONArray.toJSONString(customerKeyList);
				MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
				custParams.add("custKeys", custKeys);
		    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custId, custParams, ResultModel.class);
		    	ResultModel body = responseEntity.getBody();
		    	logger.info("运营推送消息用户模块返回用户id:"+JSON.toJSONString(body.getResult()));
		    	
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
		    	params.add("content", content);
		    	params.add("typeCode", infocode);
		    	params.add("exdata", exdata);
		    	params.add("userIds", userIds);
		    	params.add("orgCode", orgCode);
		    	params.add("url", h5ServerUrl+"/stock_mode_detail.html?modeCode="+strategyCode);
		    	
		    	//data 内容太长用post 提交
		    	ResponseEntity<ResultModel> appResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_app", params, ResultModel.class);
		    	logger.info("推送app端返回值:"+JSON.toJSONString(appResponseEntity.getBody()));
		    	
		    	ResponseEntity<ResultModel> pcResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_pc", params, ResultModel.class);
		    	logger.info("推送pc端返回值:"+JSON.toJSONString(pcResponseEntity.getBody()));
			}
		}
	}
	
	/**
	 * 模型超过一定比例的股票数量
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Integer> queryNumByStrategyCode(){
		Map<String,Integer> strategyMap=new HashMap<String,Integer>();
		Integer tenNum=0;
		//选股动态存储在redis里面
		List<Object> objectList = redisUtils.queryHashValue("choose_res_vo");
		for(Object object:objectList){
			Map<String, Object> coseMap = (Map<String, Object>) object;
			String strategyCode=coseMap.get("strategyCode").toString();
			//20日涨幅
	    	Float twentyRise=Float.parseFloat(coseMap.get("twentyRise").toString());
	    	if(twentyRise>10){
	    		Integer num=strategyMap.get(strategyCode);
	    		if(null==num){
	    			tenNum=1;
	    		}else{
	    			tenNum+=1;
	    		}
	    		strategyMap.put(strategyCode, tenNum);
	    	}
		}
		return strategyMap;
	}

	/**
	 * 策略倒计时
	 * @param strategyCode
	 * @param orgCode
	 * @param title
	 * @param content
	 * @param infocode
	 * @param exdata
	 */
	 @SuppressWarnings("unchecked")
	 public void pushStrategyCountDown(String strategyCode,String orgCode,String title,String content,Integer infocode,String exdata, double earnRate){
		ResultModel chooseCodeModel= restTemplate.getForObject(myProps.getSubscribeServer() + queryActiveCusPro+ "?productSysFlag={1}&sysProductCode={2}&orgCode={3}&remainDays={4}", ResultModel.class,clSysFlag,strategyCode,orgCode,3);
		if(chooseCodeModel.getCode()==0){
			logger.info("机构:"+orgCode+",订阅中心返回数据:"+JSON.toJSONString(chooseCodeModel.getResult()));
			String sysProductName="";
			Map<Integer,List<Integer>> custMap=new HashMap<Integer,List<Integer>>();
			JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(chooseCodeModel.getResult()));
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
			
			String detail="";
			for(Entry<Integer,List<Integer>>entry:custMap.entrySet()){
				int day=entry.getKey();
				if(day==0){
					detail="最后8小时续订充值最高享8折优惠！"+String.format(content, sysProductName,"即将",earnRate+"%");
				}else{
					detail="续订充值最高享8折优惠！"+String.format(content, sysProductName,"将于"+day+"天后",earnRate+"%");
				}
				logger.info("策略倒计时天数:"+day+",发送内容:"+detail);
				
				List<Integer> customerKeyList=entry.getValue();
				String custKeys = JSONArray.toJSONString(customerKeyList);
				MultiValueMap<String, String> custParams= new LinkedMultiValueMap<String, String>();
				custParams.add("custKeys", custKeys);
		    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + query_batch_custId, custParams, ResultModel.class);
		    	ResultModel body = responseEntity.getBody();
		    	logger.info("机构:"+orgCode+",用户模块返回数据:"+JSON.toJSONString(body.getResult()));
		    	
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
		    	logger.info("推送app端返回值:"+JSON.toJSONString(appResponseEntity.getBody()));
		    	
		    	ResponseEntity<ResultModel> pcResponseEntity = restTemplate.postForEntity(myProps.getMessageServer() + "/message/sendmsg_pc", params, ResultModel.class);
		    	logger.info("推送pc端返回值:"+JSON.toJSONString(pcResponseEntity.getBody()));
			}
		}
	}
}
