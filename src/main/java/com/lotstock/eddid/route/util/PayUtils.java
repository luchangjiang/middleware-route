package com.lotstock.eddid.route.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.controller.BaseController;

/**
 * 支付通用方法
 * @author David
 *
 */
@Service
public class PayUtils extends BaseController{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyProps myProps;
	
	@Autowired
	private RedisUtils redisUtils;
	
	//分转换为元
	private static final int DEFAULT_AM=100;
	
	// 获取套餐价格
	private static final String query_choose_price = "/subscribe/queryPackageListByProCodes";
	
	// 支付
	private static final String choose_pay = "/pay/createOrder";
	
	// 股多多根据套餐id查询套餐信息
    private static final String queryPackageById ="/gddSub/queryPackageById";
    
    // 股多多查询未过期订阅信息
    private static final String querySubOrderHis="/gddSub/querySubOrderHis";
    
	// 根据用户key 查询用户id
    private static final String query_custKey = "/customer/query_custKey";
    
    // 查询金币，金豆总额
 	private static final String query_ticketPea = "/gold/query_ticketPea";
 	
	// 消费金币
	private static final String consume_goldTicket = "/gold/consume_goldTicket";
	
	// 消费金豆
	private static final String consume_goldPea = "/gold/consume_goldPea";
	
	// 查询资金账号信息
	private static final String query_account = "/fund/query_account";
	
	// 减少余额
	private static final String consume_custAccount = "/fund/consume_custAccount";
	
	/**
	 * 获取套餐价格
	 * @param productCode
	 * @param orgCode
	 * @return
	 */
	public JSONArray fullPrice(String productCode,String orgCode,String productSysFlag)throws Exception{
		JSONArray arraylist = new JSONArray();
		ResultModel re=restTemplate.getForObject(myProps.getSubscribeServer() + query_choose_price+ "?productSysFlag={1}&productCodes={2}&orgCode={3}&packageCombType={4}", ResultModel.class,productSysFlag,productCode,orgCode,1);
		logger.info("query subscribe price result:"+JSON.toJSONString(re));
		if(null!=re){
	 		String key=productSysFlag+"_"+productCode;
	 		JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(re.getResult()));
	 		JSONObject subObj= jsonObj.getJSONObject(key);
	 		Date curDate=new Date();
	 		if(subObj.containsKey("subPackList")){
	 			JSONArray subPackList= subObj.getJSONArray("subPackList");
	 			for(int i=0;i<subPackList.size();i++){
	 				JSONObject json=subPackList.getJSONObject(i);
	 				//套餐是否免费 1：免费 0：收费
	 				int free=Integer.parseInt(json.getString("packageIsFree"));
	 				//套餐周期
	 				String packageUnit=json.getString("packageUnit");
	 				if(free==0&&!"天".equals(packageUnit)){
	 					JSONObject obj = new JSONObject();
	 					//套餐名称
	 					obj.put("packageName", packageUnit+"套餐");
	 					//套餐id
	 					obj.put("packageId", json.getInteger("id"));
	 					//套餐编码
	 					obj.put("packageCode", json.getString("packageCode"));
	 					//套餐时长
	 					obj.put("packageDelayNum", json.getInteger("packageDelayNum"));
	 					//套餐周期
	 					obj.put("packageUnit", json.getString("packageUnit"));
	 					//价格
	 					obj.put("packageUnitPrice", Float.valueOf(json.getInteger("packageUnitPrice"))/DEFAULT_AM);
	 					//打折优惠期限,开始时间
	 					Date beginDate=json.getDate("discountBeginTime");
	 					//打折优惠期限,结束时间
	 					Date endDate=json.getDate("discountEndTime");
	 					if(null!=beginDate&&null!=endDate){
	 						if(curDate.getTime()>=beginDate.getTime()&&curDate.getTime()<=endDate.getTime()){
	 							//打折优惠价
	 		 					obj.put("discountUnitPrice", Float.valueOf(json.getInteger("discountUnitPrice"))/DEFAULT_AM);
	 						}else{
	 							obj.put("discountUnitPrice", 0f);
	 						}
	 					}
	 					//支付方式  统一订阅 1：虚拟币支付；2：余额支付；3：收银台支付； 股多多需要转换成  1：金币支付；2：金豆支付；3：余额支付: 4：收银台支付；
	 					String type="";
	 					String payType=json.getString("packagePayType");
	 					if(payType.contains("1")){
	 						type="1,2,";
	 					}
	 					if(payType.contains("2")){
	 						type+="3,";
	 					}
	 					if(payType.contains("3")){
	 						type+="4,";
	 					}
	 					obj.put("packagePayType",type.substring(0,type.length()-1));
	 					obj.put("remark","打折优惠,马上抢入吧!");
	 					arraylist.add(obj);
	 				}
	 			}
	 		}
		}
		return arraylist;
	}
	
	/**
	 * 支付
	 * @param sessionCode
	 * @param packageId
	 * @param packageCode
	 * @param payType 1：金币支付；2：金豆支付；3：余额支付: 4：收银台支付；
	 * @param orderChannel
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultModel fullPay(String sessionCode, int packageId,String packageCode,int payType,String orderChannel,String productSysFlag)throws Exception{
		ResultModel result=new ResultModel();
		int packageOrderNum=1;
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			//用户中心用户表主键用户Id
			int customerKey=Integer.parseInt(map.get("customerKey").toString());
			//订阅人手机号
			String customerMobile=map.get("mobile").toString();
			//机构编码
			String orgCode=map.get("orgCode").toString();
			//创建人id
			int createById=customerId;
			String createByNickname="";
			//创建人昵称
			if(null==map.get("nickName")||"".equals(map.get("nickName"))){
				createByNickname=customerMobile.substring(7);
			}else{
				createByNickname=map.get("nickName").toString();
			}
			Float packageUnitPrice=0f;
			//根据套餐id查询套餐信息
			ResultModel packageResult=restTemplate.getForObject(myProps.getSubscribeServer() + queryPackageById+ "?packageId={1}", ResultModel.class,packageId);
			if(null!=packageResult){
				Date curDate=new Date();
				JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(packageResult.getResult()));
				packageUnitPrice=Float.valueOf(jsonObj.getInteger("packageUnitPrice"))/DEFAULT_AM;
				//打折优惠期限,开始时间
				Date beginDate=jsonObj.getDate("discountBeginTime");
				//打折优惠期限,结束时间
				Date endDate=jsonObj.getDate("discountEndTime");
				if(null!=beginDate&&null!=endDate){
					if(curDate.getTime()>=beginDate.getTime()&&curDate.getTime()<=endDate.getTime()){
						//打折优惠价
						packageUnitPrice=Float.valueOf(jsonObj.getInteger("discountUnitPrice"))/DEFAULT_AM;
					}
				}
			}
			//统一支付 payTypeFinalUse 1：虚拟币支付；2：余额支付；3：收银台支付；
			int payTypeFinalUse=1;
			if(payType==1||payType==2){
				payTypeFinalUse=1;
				int ticket=0,ticketRate=0;
				int pea=0,peaRate=0;
				// 获取金币金豆总额
				ResultModel chceckModel=restTemplate.getForObject(myProps.getGoldServer() + query_ticketPea + "?customerId={1}", ResultModel.class,customerId);
				if(chceckModel.getCode()==0){
					Map<String, Object> ticketMap=(Map<String, Object>)chceckModel.getResult();
					ticket=Integer.parseInt(ticketMap.get("ticket").toString());
				    pea=Integer.parseInt(ticketMap.get("pea").toString());
				    ticketRate=Integer.parseInt(ticketMap.get("ticketRate").toString());
				    peaRate=Integer.parseInt(ticketMap.get("peaRate").toString());
				}
    			//金币购买
    			if(payType==1){
    				logger.info("金币购买 customerId:"+customerId+",ticket:"+ticket+",packageUnitPrice:"+packageUnitPrice+",actualticket:"+packageUnitPrice*ticketRate+",productSysFlag:"+productSysFlag);
    				if(ticket<(packageUnitPrice*ticketRate)){
    					result.setCode(-1);
    		    		result.setMessage("金币不足");
    		    		return result;
    				}
    			}
    			//金豆购买
				if(payType==2){
					logger.info("金豆购买 customerId:"+customerId+",pea:"+ticket+",packageUnitPrice:"+packageUnitPrice+",actualpea:"+packageUnitPrice*peaRate+",productSysFlag:"+productSysFlag);
					if(pea<(packageUnitPrice*peaRate)){
						result.setCode(-1);
    		    		result.setMessage("金豆不足");
    		    		return result;
    				}
				}
			}else if(payType==3){
				payTypeFinalUse=2;
				// 获取账号余额
				ResultModel moneyModel=restTemplate.getForObject(myProps.getFundServer() + query_account + "?customerId={1}", ResultModel.class,customerId);
				if(moneyModel.getCode()==0){
					Map<String, Object> ticketMap=(Map<String, Object>)moneyModel.getResult();
					Float profit=Float.parseFloat(ticketMap.get("profit").toString());
					logger.info("余额购买 customerId:"+customerId+",profit:"+profit+",packageUnitPrice:"+packageUnitPrice+",productSysFlag:"+productSysFlag);
					if(profit<packageUnitPrice){
						result.setCode(-1);
    		    		result.setMessage("余额不足");
    		    		return result;
					}
				}
			}else{
				payTypeFinalUse=3;
			}
			ResultModel bak= restTemplate.getForObject(myProps.getSubscribeServer() + choose_pay+"?customerId={1}&orderFromSysFlag={2}&packageCode={3}&packageOrderNum={4}&payTypeFinalUse={5}&orderChannel={6}&customerKey={7}&customerMobile={8}&orgCode={9}&createById={10}&createByNickname={11}&packageId={12}&returnParams={13}&remark={14}", ResultModel.class,customerId,productSysFlag,packageCode,packageOrderNum,payTypeFinalUse,orderChannel,customerKey,customerMobile,orgCode,createById,createByNickname,packageId,payType+",1","正常购买");
			logger.info("统一订阅中心 result back:"+JSON.toJSONString(bak));
			if(payTypeFinalUse==3){
				return bak;
			}else{
				result.setCode(bak.getCode());
				result.setMessage(bak.getMessage());
				return result;
			}
		}else{
			return re;
		}
	}
	
	/**
	 * 支付回调
	 * @param customerKey
	 * @param payType
	 * @param packageCode
	 * @param packageUnitPrice
	 * @param free 0免费 1收费
	 * @param amount
	 * @param remark
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer,String> fullPayBack(Integer customerKey,int payType,String packageCode,float packageUnitPrice,int free,String amount,String remark,String modelType)throws Exception{
		Map<Integer,String> customerMap=new HashMap<Integer,String>();
		int customerId=0;
		String mobile="";
		//根据用户key查询用户id
		ResultModel custModel=restTemplate.getForObject(myProps.getCustomerServer() + query_custKey + "?customerKey={1}", ResultModel.class,customerKey);
		if(custModel.getCode()==0){
			Map<String, Object> custMap=(Map<String, Object>)custModel.getResult();
			customerId=Integer.parseInt(custMap.get("customerId").toString());
			mobile=custMap.get("mobile").toString();
		}
		int ticketRate=0;
		int peaRate=0;
		//获取金币金豆总额
		ResultModel chceckModel=restTemplate.getForObject(myProps.getGoldServer() + query_ticketPea + "?customerId={1}", ResultModel.class,customerId);
		logger.info("查询金币金豆总额:"+JSON.toJSONString(chceckModel));
		if(chceckModel.getCode()==0){
			Map<String, Object> ticketMap=(Map<String, Object>)chceckModel.getResult();
		    ticketRate=Integer.parseInt(ticketMap.get("ticketRate").toString());
		    peaRate=Integer.parseInt(ticketMap.get("peaRate").toString());
		}
		//金币支付
		if(payType==1){
			int ticket=Math.round(packageUnitPrice*ticketRate);
			Integer opeatorType=7;
			if("cl".equals(modelType)){
				opeatorType=6;
			}else{
				opeatorType=7;
			}
			logger.info("消费金币数量 customerId:"+customerId+",ticket:"+ticket);
			ResultModel ticketModel=restTemplate.getForObject(myProps.getGoldServer() + consume_goldTicket + "?customerId={1}&ticket={2}&opeatorType={3}&remark={4}", ResultModel.class,customerId,ticket,opeatorType,remark);
			logger.info("消费金币:"+JSON.toJSONString(ticketModel));
		}
		//金豆支付
		if(payType==2){
			int pea=Math.round(packageUnitPrice*peaRate);
			Integer opeatorType=4;
			if("cl".equals(modelType)){
				opeatorType=3;
			}else{
				opeatorType=4;
			}
			logger.info("消费金豆数量 customerId:"+customerId+",pea:"+pea);
			ResultModel peaModel=restTemplate.getForObject(myProps.getGoldServer() + consume_goldPea + "?customerId={1}&pea={2}&opeatorType={3}&remark={4}", ResultModel.class,customerId,pea,opeatorType,remark);
			logger.info("消费金豆:"+JSON.toJSONString(peaModel));
		}
		//余额支付
		if(payType==3){
			if(free==1){
				Integer opeatorType=4; 
				logger.info("消费余额数量 customerId:"+customerId+",amount:"+Integer.parseInt(amount));
				ResultModel moneyModel=restTemplate.getForObject(myProps.getFundServer() + consume_custAccount + "?customerId={1}&opType={2}&amount={3}&fee={4}&remark={5}", ResultModel.class,customerId,opeatorType,Integer.parseInt(amount),0,remark);
				logger.info("余额支付:"+JSON.toJSONString(moneyModel));
			}
		}
		customerMap.put(customerId, mobile);
		return customerMap;
	}
	
	/**
	 * 查询订购记录
	 * @param sessionCode
	 * @param productSysFlag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultModel queryOrderDetail(String sessionCode,String productSysFlag){
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		try{
			JSONArray arraylist = new JSONArray();
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				//用户中心用户表主键用户Id
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				ResultModel back=restTemplate.getForObject(myProps.getSubscribeServer() + querySubOrderHis+ "?sysFlag={1}&customerId={2}", ResultModel.class,productSysFlag,customerKey);
				if(back.getCode()==0){
					JSONArray arrays=JSONArray.parseArray(JSON.toJSONString(back.getResult()));
					int size=arrays.size();
					if(size>0){
						for(int i=0;i<size;i++){
							JSONObject json=arrays.getJSONObject(i);
				 			JSONObject obj = new JSONObject();
				 			obj.put("sysProductName", json.getString("packageName"));
				 			obj.put("orderStatus", json.getString("orderStatus"));//0 待支付 1支付成功 2支付失败
				 			obj.put("orderType", json.getString("orderType"));//1 订阅 2续订
				 			obj.put("orderNo", json.getString("orderCode"));//订单号
				 			obj.put("orderTime", TimeUtil.formatDate(json.getDate("createTime")));
				 			obj.put("period", TimeUtil.formatDate(json.getDate("orderBeginTime"))+"~"+TimeUtil.formatDate(json.getDate("orderEndTime")));
				 			Date beginDate=json.getDate("orderBeginTime");
				 			Date endDate=json.getDate("orderEndTime");
				 			int day=0;
				 			day=TimeUtil.daysBetween(beginDate, endDate);
				 			obj.put("payMoney", Double.valueOf(json.getString("pay_moneyFinal"))/DEFAULT_AM);
				 			obj.put("remark", json.getString("remark"));
				 			obj.put("day", day+1);
				 			arraylist.add(obj);
						}
					}
					result.setResult(arraylist);
				}else{
					return back;
				}
			}else{
				return re;
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query query_order_detail info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		return result;
	}

}
