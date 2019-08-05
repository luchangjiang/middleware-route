package com.lotstock.eddid.route.controller;

import java.util.List;
import java.util.Map;

import com.lotstock.eddid.route.util.ChooseStockUtil;
import com.lotstock.eddid.route.util.RedisUtils;
import com.lotstock.eddid.route.util.ResultModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RefreshScope
@RequestMapping("/strategyTransaction")
public class StrategyTransactionController {
	
	protected static final Logger logger = Logger.getLogger(StrategyTransactionController.class);

	@Autowired
	private ChooseStockUtil chooseStockUtil;
	
	@Autowired
	private RedisUtils redisUtils;
	
	/**
	 * c++策略平台选出股票后推送给app和pc端
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/transaction_route_dispatch", method = RequestMethod.POST)
	public @ResponseBody
    ResultModel transaction_route_dispatch(@RequestBody Map<String, Object> paramMap) {
		ResultModel result=new ResultModel();
		int status=0;
		String message="成功";
		try{
			//{"businessType":1,"data":{"strategy_code":"","strategyName":"","time":" +0800","sig":[{"exchange":"","market":0,"code":"","name":"","is_buy":0,"is_open":0,"count":0,"price":"0.00","price_type":0}]}}
			int infocode=4;
			String data=JSONObject.toJSONString(paramMap.get("data"));
			logger.info("接收c++策略中心参数 data:"+data);
			JSONObject jsonObj=JSONObject.parseObject(data);
			String strategyCode=jsonObj.getString("strategy_code");
			String strategyName=jsonObj.getString("strategyName");
			String title="策略模型："+strategyName;
			String content="";
			String exdata="";
			JSONArray array=jsonObj.getJSONArray("sig");
			if(array.size()>0){
				for(int i=0;i<array.size();i++){
					JSONObject json=array.getJSONObject(i);
					String code=json.getString("code");
					String name=json.getString("name");
					String price=json.getString("price");
					//1买 2卖
					int is_buy=json.getInteger("is_buy");
					//1开仓 2平仓
					//int is_open=json.getInteger("is_open");
					//1限价委托 2市价委托
					//int price_type=json.getInteger("price_type");
					String buy=is_buy==1?"买入":"卖出";
					String remark=is_buy==1?"立即买入":"快速离场";
					content+=name+"("+code+")"+"提示"+buy+"，"+buy+"价"+price+"元"+"，紧跟节奏"+remark+"!";
					exdata="{\"id\":\""+infocode+"\", \"c\":\""+strategyCode+"\"}";
				}
			}
			
			logger.info("接收c++策略中心参数 strategy_code:"+strategyCode+",infocode:"+infocode+",title:"+title+",content:"+content+",exdata:"+exdata);
			
			List<Object> objectList = redisUtils.queryHashValue("sys_push_cfg");
			for(Object object:objectList){
				Map<String, Object> map = (Map<String, Object>) object;
			    String orgCode= map.get("orgCode").toString();
			    chooseStockUtil.pushStrategyMsg(strategyCode, orgCode, title, content, infocode, exdata);
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			status=-1;
			logger.error("send_msg info error",ce);
		}
		result.setCode(status);
		result.setMessage(message);
		return result;
	}
	
	
}
