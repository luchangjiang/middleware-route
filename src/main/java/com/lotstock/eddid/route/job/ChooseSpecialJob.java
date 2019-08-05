package com.lotstock.eddid.route.job;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lotstock.eddid.route.util.ChooseStockUtil;
import com.lotstock.eddid.route.util.RedisUtils;
import com.lotstock.eddid.route.util.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ChooseCfgUtil;
import com.lotstock.eddid.route.util.HttpUtils;
import com.lotstock.eddid.route.util.TimeUtil;
import com.lotstock.eddid.route.vo.ChooseResVo;

/**
 * 选股过滤job
 * @author David
 *
 */
@Component
public class ChooseSpecialJob {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private MyProps myProps;
	
	@Autowired
	private ChooseStockUtil chooseStockUtil;
	
	@Autowired
	private ChooseCfgUtil chooseCfgUtil;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private static DecimalFormat  df = new DecimalFormat("######0.00");
	
	//五日之内涨幅条件
	private static final int FIVE_RISE=10;
	
	//十日之内涨幅条件
	private static final int HISTORY_RISE=20;
	
	// 保存运营推送记录
	private static final String save_chooseoperation_record = "/chooseRecord/save_chooseoperation_record";
	// 查询运营推送记录
	private static final String query_chooseoperation_num= "/chooseRecord/query_chooseoperation_num";
	//根据类型查询
	private static final String query_choosetype_num= "/chooseRecord/query_choosetype_num";
	
	//@Scheduled(cron = "${myprops.specialJob}")
	public void tasks() {
		try{
			String orgCode="0001";
			Date date=new Date();
			String curDate=TimeUtil.formatDate(date);
			Date beginTime=TimeUtil.parseTimeFormat(curDate+" 09:30:10");
			//判断时间大于9:30:00
			if(date.getTime()>=beginTime.getTime()){
				logger.info("choose special job start curDate:"+curDate);
				this.queryChooseSpecial(orgCode,curDate);
				logger.info("choose special job end");
			}
		}catch(Exception ce){
			ce.printStackTrace();
			logger.error("choose special job error!");
		}
   }
	
   /**
    * 北京运营针对未订阅的用户推消息逻辑
    * @param orgCode
    * @param curDate
    */
   @SuppressWarnings("unchecked")
   private void queryChooseSpecial(String orgCode,String curDate){
		try{
			Map<String, Object> trdCale=this.queryTrdCale(curDate);
			//当天是交易日
			if((boolean)trdCale.get("isTradeDay")){
				List<ChooseResVo> toDayResVoList=new ArrayList<ChooseResVo>();
				List<ChooseResVo> hisDayResVoList=new ArrayList<ChooseResVo>();
				Set<String> choseDaySet=new HashSet<String>();
				//选股动态存储在redis里面
				List<Object> objectList = redisUtils.queryHashValue("choose_res_vo");
				for(Object object:objectList){
					Map<String, Object> coseMap = (Map<String, Object>) object;
					//判断是当天
					if(curDate.equals(coseMap.get("day").toString())){
						//当日实际涨幅
						Float actualRise=Float.parseFloat(coseMap.get("actualRise").toString());
						if(actualRise>9){
							ChooseResVo toDayresVo=new ChooseResVo();
							//市场id
							toDayresVo.setMarket(Integer.parseInt(coseMap.get("market").toString()));
							//股票代码
							toDayresVo.setCode(coseMap.get("code").toString());
							//股票名称
							toDayresVo.setStockName(coseMap.get("stockName").toString());
							//模型code
							toDayresVo.setStrategyCode(coseMap.get("strategyCode").toString());
							//模型名称
							toDayresVo.setStrategyName(coseMap.get("strategyName").toString());
							//当日实际涨幅
							toDayresVo.setActualRise(String.valueOf(actualRise));
							//日期
							toDayresVo.setDay(coseMap.get("day").toString());
							//时间
							toDayresVo.setTime(coseMap.get("time").toString());
							//收盘价
							toDayresVo.setDayClose(Double.parseDouble(coseMap.get("dayClose").toString()));
							//昨收价
							toDayresVo.setPrevClose(Double.parseDouble(coseMap.get("prevClose").toString()));
							//价格
							toDayresVo.setPrice(coseMap.get("price").toString());
							toDayResVoList.add(toDayresVo);
						}
				    }else{
				    	ChooseResVo hisDayresVo=new ChooseResVo();
				    	//市场id
				    	hisDayresVo.setMarket(Integer.parseInt(coseMap.get("market").toString()));
				    	//股票代码
			    		hisDayresVo.setCode(coseMap.get("code").toString());
						//股票名称
			    		hisDayresVo.setStockName(coseMap.get("stockName").toString());
						//模型code
			    		hisDayresVo.setStrategyCode(coseMap.get("strategyCode").toString());
						//模型名称
			    		hisDayresVo.setStrategyName(coseMap.get("strategyName").toString());
			    		//日期
			    		hisDayresVo.setDay(coseMap.get("day").toString());
			    		//时间
			    		hisDayresVo.setTime(coseMap.get("time").toString());
			    		//10日涨幅
			    		hisDayresVo.setTenRise(coseMap.get("tenRise").toString());
			    		//收盘价
			    		hisDayresVo.setDayClose(Double.parseDouble(coseMap.get("dayClose").toString()));
			    		//昨收价
			    		hisDayresVo.setPrevClose(Double.parseDouble(coseMap.get("prevClose").toString()));
			    		//价格
			    		hisDayresVo.setPrice(coseMap.get("price").toString());
			    		hisDayResVoList.add(hisDayresVo);
			    		choseDaySet.add(hisDayresVo.getDay());
				    }
				}
				//处理今日选股
				Set<String> exitStockCodeSet=this.convertToDayData(orgCode, toDayResVoList, trdCale);
				//处理历史选股
				this.convertHistoryData(orgCode, hisDayResVoList, curDate, choseDaySet,exitStockCodeSet);
			}
		}catch(Exception ce){
			ce.printStackTrace();
			logger.error("choose special info error",ce);
		}
   }
   
   /**
    * 今日选股逻辑
    * @param orgCode
    * @param toDayResVoList
    * @param trdCale
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   private Set<String> convertToDayData(String orgCode,List<ChooseResVo> toDayResVoList,Map<String, Object> trdCale) throws Exception{
	  int type=1;
	  int infocode=30;
	  //用于存储涨停的股票
	  Set<String> exitStockCodeSet=new HashSet<String>();
	  //当日
	  if(toDayResVoList.size()>0){
		  for(ChooseResVo toDayResVo:toDayResVoList){
				//现价
				double dayClose=toDayResVo.getDayClose();
				//昨收价
				double prevClose=toDayResVo.getPrevClose();
				logger.info("当日入选股票:"+toDayResVo.getCode()+",现价:"+dayClose+",昨收价:"+prevClose);
				//判断涨停
				if(dayClose>=this.queryZtPrice(prevClose)){
					String strategyCode=toDayResVo.getStrategyCode();
					int hardenNum=0;
					ResultModel hardenModel=restTemplate.getForObject(myProps.getInitJobServer() + "/chooseRecord/query_choose_harden?stockCode={1}", ResultModel.class,toDayResVo.getCode());
					logger.info("query choose harden num result :"+JSON.toJSONString(hardenModel));
					if(hardenModel.getCode()==0){
						Map<String, Object> map = (Map<String, Object>) hardenModel.getResult();
						hardenNum=Integer.parseInt(map.get("number").toString());
					}
					if(hardenNum==0){
						restTemplate.getForObject(myProps.getInitJobServer()+"/chooseRecord/save_choose_harden?modelCode={1}&modelName={2}&stockCode={3}&stockName={4}&selectDate={5}&selectTime={6}&market={7}&dayClose={8}&prevClose={9}&dayRise={10}&selectPrice={11}", ResultModel.class,strategyCode, toDayResVo.getStrategyName(), toDayResVo.getCode(), toDayResVo.getStockName(),toDayResVo.getDay(),toDayResVo.getTime(),toDayResVo.getMarket(),dayClose,prevClose,toDayResVo.getActualRise(),toDayResVo.getPrice());
					}
					int number=this.commonConvert(toDayResVo.getCode(), type);
					//判断该股票今日是否推送过
					if(number==0){
						Integer codeId=chooseCfgUtil.queryChooseCfgId(orgCode, strategyCode);
						String title="选股提醒";
						String content="来来来！当日入选即涨停！"+toDayResVo.getStrategyName()+"今日入选股票["+toDayResVo.getStockName()+toDayResVo.getCode()+"],猛戳抓更多涨停股！";
						String exdata="{\"id\":"+codeId+", \"c\":\""+toDayResVo.getStrategyCode()+"\"}";
						restTemplate.getForObject(myProps.getInitJobServer()+save_chooseoperation_record+"?modelCode={1}&modelName={2}&stockCode={3}&stockName={4}&title={5}&content={6}&type={7}&selectTime={8}&selectPrice={9}&dayClose={10}&prevClose={11}&riseVal={12}", ResultModel.class,strategyCode, toDayResVo.getStrategyName(), toDayResVo.getCode(), toDayResVo.getStockName(),title,content,type,toDayResVo.getTime(),toDayResVo.getPrice(),dayClose,prevClose,toDayResVo.getActualRise());
						chooseStockUtil.sendChooseSpecialRecord(strategyCode, orgCode, title, content, infocode, exdata);
					}else{
						logger.info("该股票今天已推送，不再进行推送!");
					}
				}
			}
		}
		
		//获取上一个交易日和上上一个交易日
		List<String> lastTrdList=this.queryLastDay(trdCale);
		for(int i=0;i<lastTrdList.size();i++){
			String selectDay=lastTrdList.get(i);
			String strategyCode="";
			String strategyName="";
			String stockCode="";
			String stockName="";
			String selectTime="";
			String selectPrice="";
			int market=0;
			String title="选股提醒";
			String content="";
			ResultModel hardenModel=restTemplate.getForObject(myProps.getInitJobServer() + "/chooseRecord/query_choose_hardenDate?selectDate={1}", ResultModel.class,selectDay);
			logger.info("query choose harden by day result :"+JSON.toJSONString(hardenModel));
			if(hardenModel.getCode()==0){
				List<Object> objList=(List<Object>)hardenModel.getResult();
				if(objList.size()>0){
					for(int j=0;j<objList.size();j++){
						JSONObject jsonObj=JSON.parseObject(JSON.toJSONString(objList.get(j)));
						strategyCode=jsonObj.getString("modelCode");
						strategyName=jsonObj.getString("modelName");
						stockCode=jsonObj.getString("stockCode");
						stockName=jsonObj.getString("stockName");
						selectTime=jsonObj.getString("selectTime");
						selectPrice=jsonObj.getString("selectPrice");
						market=jsonObj.getInteger("market");
						List<String> assetIdList =new ArrayList<String>();
						assetIdList.add(stockCode+"."+market);
						Map<String,Map<String, Double>> closeMap=this.queryHqInfo(assetIdList);
						String assetId=stockCode+"."+market;
						//现价
						double dayClose=closeMap.get(assetId).get("now");
						//昨收价
						double prevClose=closeMap.get(assetId).get("lastclose");
						double rise=(dayClose-prevClose)/prevClose;
						double riseVal=Double.parseDouble(df.format(rise*100));
						logger.info("上一个交易日或上上一个交易日入选股票:"+stockCode+",现价:"+dayClose+",昨收价:"+prevClose+",涨幅:"+riseVal);
						//判断涨停
						if(dayClose>=this.queryZtPrice(prevClose)){
							logger.info("涨停股票code:"+stockCode+",股票名称:"+stockName);
							exitStockCodeSet.add(stockCode);
							if(i==0){
								type=2;
								content="服了吗？连续2天涨停！"+strategyName+TimeUtil.queryDateZh(selectDay)+"入选股票["+stockName+stockCode+"],猛戳抓更多涨停股！";
							}else{
								int secondNum=0;
								//生产bug,要判断第二天是否涨停
								String secondDay=lastTrdList.get(0);
								ResultModel secondModel=restTemplate.getForObject(myProps.getInitJobServer() + query_choosetype_num+"?stockCode={1}&type={2}&selectDate={3}", ResultModel.class,stockCode, 2, secondDay);
								if(secondModel.getCode()==0){
									Map<String, Object> map = (Map<String, Object>) secondModel.getResult();
									secondNum =Integer.parseInt(map.get("number").toString());
								}
								if(secondNum==1){
									type=3;
									content="燃爆了！连续3天涨停！"+strategyName+TimeUtil.queryDateZh(selectDay)+"入选股票["+stockName+stockCode+"],猛戳获独家揭秘！";
								}
							}
							int number=this.commonConvert(stockCode, type);
							//判断该股票是否推送过
							if(number==0){
								Integer codeId=chooseCfgUtil.queryChooseCfgId(orgCode, strategyCode);
								String exdata="{\"id\":"+codeId+", \"c\":\""+strategyCode+"\"}";
								restTemplate.getForObject(myProps.getInitJobServer()+save_chooseoperation_record+"?modelCode={1}&modelName={2}&stockCode={3}&stockName={4}&title={5}&content={6}&type={7}&selectTime={8}&selectPrice={9}&dayClose={10}&prevClose={11}&riseVal={12}", ResultModel.class,strategyCode, strategyName, stockCode, stockName,title,content,type,selectTime,selectPrice,dayClose,prevClose,riseVal);
								chooseStockUtil.sendChooseSpecialRecord(strategyCode, orgCode, title, content, infocode, exdata);
							}else{
								logger.info("该股票今天已推送，不再进行推送!");
							}
						}
					}
				}
			}
		}
		return exitStockCodeSet;
   }
   
   /**
    * 判断每种类型逻辑
    * @param stockCode
    * @param type
    * @return
    */
   @SuppressWarnings("unchecked")
   private int commonConvert(String stockCode,int type){
    	int number=0;
    	//先判断当日该类型的推送次数是否超过2条
		int fist=0;
		ResultModel firstModel=restTemplate.getForObject(myProps.getInitJobServer() + query_chooseoperation_num+"?stockCode={1}&type={2}", ResultModel.class,"",type);
		logger.info("query choose operation today num result :"+JSON.toJSONString(firstModel));
		if(firstModel.getCode()==0){
			Map<String, Object> map = (Map<String, Object>) firstModel.getResult();
			fist=Integer.parseInt(map.get("number").toString());
		}
		if(fist<2){
			if(type==5){
				ResultModel fiveModel=restTemplate.getForObject(myProps.getInitJobServer() + query_choosetype_num+"?stockCode={1}&type={2}&selectDate={3}", ResultModel.class,stockCode,type,"");
				logger.info("query choose operation type num result :"+JSON.toJSONString(fiveModel));
				if(fiveModel.getCode()==0){
					Map<String, Object> map = (Map<String, Object>) fiveModel.getResult();
					number=Integer.parseInt(map.get("number").toString());
				}
			}else{
				ResultModel secondModel=restTemplate.getForObject(myProps.getInitJobServer() + query_chooseoperation_num+"?stockCode={1}&type={2}", ResultModel.class,stockCode,type);
				logger.info("query choose operation stock num result :"+JSON.toJSONString(secondModel));
				if(secondModel.getCode()==0){
					Map<String, Object> map = (Map<String, Object>) secondModel.getResult();
					number=Integer.parseInt(map.get("number").toString());
				}
			}
		}else{
			number=fist;
			logger.info("今天推送选股已达上限，不再进行推送!");
		}
		return number;
   }
   
   /**
    * 历史数据逻辑
    * @param orgCode
    * @param hisDayResVoList
    * @param trdCale
    * @param choseDaySet
    * @throws Exception
    */
   private void convertHistoryData(String orgCode,List<ChooseResVo> hisDayResVoList,String curDate,Set<String> choseDaySet,Set<String> exitStockCodeSet) throws Exception{
	  if(hisDayResVoList.size()>0){
		int type=4;
		int infocode=30;
		List<String> choseDayList=new ArrayList<String>();
		choseDayList.addAll(choseDaySet);
		Collections.sort(choseDayList);
		Collections.reverse(choseDayList);
		List<String> fiveDayList = choseDayList.subList(0, 6);
		//存储10日天数
		List<String> dayList = choseDayList.subList(0, 10);
		List<String> assetIdList =new ArrayList<String>();
		List<ChooseResVo> historyRiseList=new ArrayList<ChooseResVo>();
		if(hisDayResVoList.size()>0){
			for(ChooseResVo historyResVo:hisDayResVoList){
				//取入选10天以内的股票
				if(dayList.contains(historyResVo.getDay())){
					assetIdList.add(historyResVo.getCode()+"."+historyResVo.getMarket());
					historyRiseList.add(historyResVo);
				}
	    	}
		}
    	
		if(historyRiseList.size()>0){
			//通过行情取每支股票的行情
			Map<String,Map<String, Double>> closeMap=this.queryHqInfo(assetIdList);
			for(ChooseResVo historyResVo:historyRiseList){
				String strategyCode=historyResVo.getStrategyCode();
				String title="选股提醒";
				String content="";
				Integer codeId=chooseCfgUtil.queryChooseCfgId(orgCode, strategyCode);
				String exdata="{\"id\":"+codeId+", \"c\":\""+historyResVo.getStrategyCode()+"\"}";
				String assetId=historyResVo.getCode()+"."+historyResVo.getMarket();
				//入选当天的收盘价
				double dayClose=historyResVo.getDayClose();
				//实时价
				double now=closeMap.get(assetId).get("now");
				//加个判断现价为0的为停牌不计算
				if(now>0&&dayClose>0){
					//昨收价
					double lastclose=closeMap.get(assetId).get("lastclose");
					double rise=(now-dayClose)/dayClose;
					double riseVal=Double.parseDouble(df.format(rise*100));
					
					logger.info("query choose history stockCode:"+historyResVo.getCode()+",day:"+historyResVo.getDay()+",dayClose:"+dayClose+",now:"+now+",zhangting:"+Math.round(lastclose*1.1)+",riseVal:"+riseVal);
					//判断不包含今日涨停数据
					if(!exitStockCodeSet.contains(historyResVo.getCode())){
						//入选股票第二个交易日起5天之内有一次涨停，且累计涨幅大于等于10%
						if(now>=this.queryZtPrice(lastclose)){
							type=4;
							if(!curDate.equals(historyResVo.getDay())&&fiveDayList.contains(historyResVo.getDay())&&riseVal>FIVE_RISE){
								int number=this.commonConvert(historyResVo.getCode(), type);
								if(number==0){
									content="必看，入选股票["+historyResVo.getStockName()+historyResVo.getCode()+"]已涨停,累计涨幅高达"+riseVal+"%猛戳抓涨停股！";
									restTemplate.getForObject(myProps.getInitJobServer()+save_chooseoperation_record+"?modelCode={1}&modelName={2}&stockCode={3}&stockName={4}&title={5}&content={6}&type={7}&selectTime={8}&selectPrice={9}&dayClose={10}&prevClose={11}&riseVal={12}", ResultModel.class,strategyCode, historyResVo.getStrategyName(), historyResVo.getCode(), historyResVo.getStockName(),title,content,type,historyResVo.getTime(),historyResVo.getPrice(),now,lastclose,riseVal);
									chooseStockUtil.sendChooseSpecialRecord(strategyCode, orgCode, title, content, infocode, exdata);
								}else{
									logger.info("该股票今天已推送，不再进行推送!");
								}
							}
						}else{
							type=5;
							//入选日期小于等于10天且个股累计涨幅大于等于20%
							if(riseVal>HISTORY_RISE){
								int number=this.commonConvert(historyResVo.getCode(), type);
								if(number==0){
									content="不服来看!"+"累计涨幅达到"+riseVal+"%!"+historyResVo.getStrategyName()+TimeUtil.queryDateZh(historyResVo.getDay())+"入选股票["+historyResVo.getStockName()+historyResVo.getCode()+"]不要再错过牛股！";
									restTemplate.getForObject(myProps.getInitJobServer()+save_chooseoperation_record+"?modelCode={1}&modelName={2}&stockCode={3}&stockName={4}&title={5}&content={6}&type={7}&selectTime={8}&selectPrice={9}&dayClose={10}&prevClose={11}&riseVal={12}", ResultModel.class,strategyCode, historyResVo.getStrategyName(), historyResVo.getCode(), historyResVo.getStockName(),title,content,type,historyResVo.getTime(),historyResVo.getPrice(),now,lastclose,riseVal);
									chooseStockUtil.sendChooseSpecialRecord(strategyCode, orgCode, title, content, infocode, exdata);
								}else{
									logger.info("该股票今天已推送，不再进行推送!");
								}
							}
						}
					}
				}
		    }
		 }
	  }
   }
   
   /**
    * 获取交易日历
    * @param curDateStr
    * @return
    */
   @SuppressWarnings("unchecked")
   private Map<String, Object> queryTrdCale(String curDateStr){
		Object object  = null;
		try {
			String trdkeys=curDateStr+"_ML";
			object = redisUtils.queryHash("stk_trd_cale", trdkeys);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Map<String,Object>)object;
   }
   
   /**
    * 获取上一个交易日,上上一个交易日
    * @param trdCale
    * @return
    */
   private List<String> queryLastDay(Map<String, Object> trdCale){
	   List<String> lastDayList=new ArrayList<String>();
	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
	   String lastTrd = df.format(trdCale.get("lastTrd"));
	   lastDayList.add(lastTrd);
	   Map<String, Object> lastTrdCale=this.queryTrdCale(lastTrd);
	   String lastBeforeTrd=df.format(lastTrdCale.get("lastTrd"));
	   lastDayList.add(lastBeforeTrd);
       return lastDayList;
   }
   
   /**
	 * 获取行情地址
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String queryHqUrl(){
		Object object = redisUtils.queryHash("sys_push_cfg", "0001");
		Map<String, Object> map = (Map<String, Object>) object;
		return map.get("hqUrl").toString();
	}
   
	/**
	 * 获取行情数据
	 * @param assetIdList
	 * @return
	 */
    private Map<String,Map<String, Double>> queryHqInfo(List<String> assetIdList) {
		Map<String,Map<String, Double>> closeMap=new HashMap<String,Map<String, Double>>();
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("reqtype",153);
		jsonObj.put("reqid",1);
		jsonObj.put("session",0);
		JSONObject obj=new JSONObject();
		
		obj.put("pushflag", 0);
		obj.put("getsyminfo", 1);
		JSONArray arraylist = new JSONArray();
		for(String assetId:assetIdList){
			int index=assetId.lastIndexOf(".");
			JSONObject detailObj=new JSONObject();
			detailObj.put("market", Integer.parseInt(assetId.substring(index+1,assetId.length())));
			detailObj.put("code", assetId.substring(0,index));
			arraylist.add(detailObj);
		}
		obj.put("symbol", arraylist);
		jsonObj.put("data", obj);
		logger.info("获取商品行情发送格式:"+jsonObj.toString());
		String url=this.queryHqUrl();
		JSONObject jsonResult=HttpUtils.sendPostReturnJsonObj(url, "utf-8", jsonObj.toString());
		if(null!=jsonResult){
			if("0".equals(jsonResult.get("status").toString())){
				JSONObject data=jsonResult.getJSONObject("data");
				JSONArray jsonArry=data.getJSONArray("symbol");
				if(null!=jsonArry&&jsonArry.size()>0){
					int size=jsonArry.size();
					for(int i=0;i<size;i++){
						JSONObject jsonData=(JSONObject)jsonArry.get(i);
						String assetId=jsonData.getString("code")+"."+jsonData.getInteger("market");
						Double now=0d;
						Double lastclose=0d;
						if(jsonData.containsKey("now") && jsonData.containsKey("lastclose")){
							now=jsonData.getDouble("now");//收盘价
							lastclose=jsonData.getDouble("lastclose");//昨收价
						}
						Map<String, Double> map = new HashMap<String, Double>();
						map.put("now", Double.parseDouble(df.format(now)));
						map.put("lastclose", Double.parseDouble(df.format(lastclose)));
						closeMap.put(assetId, map);
					}
				}
			}else{
				logger.info(jsonResult.get("msg").toString());
			}
		}else{
			logger.info("行情后台服务已停止");
		}
		return closeMap;
	}
    
    /**
     * 查询涨停价
     * @param prevClose
     * @return
     */
    private double queryZtPrice(double prevClose){
    	return Math.round(prevClose*1.1* 100) * 0.01d;
    }

}
