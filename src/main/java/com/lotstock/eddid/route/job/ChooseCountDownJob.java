package com.lotstock.eddid.route.job;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lotstock.eddid.route.model.ChooseStockCfg;
import com.lotstock.eddid.route.util.ChooseStockUtil;
import com.lotstock.eddid.route.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ChooseCfgUtil;

/**
 * 选股倒计时job 
 * 每天9点推送
 * @author David
 *
 */
@Component
public class ChooseCountDownJob {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private MyProps myProps;
	
	@Autowired
	private ChooseStockUtil chooseStockUtil;

	@Autowired
	private ChooseCfgUtil chooseCfgUtil;
	
	//@Scheduled(cron = "${myprops.chooseCountDownJob}")
	public void tasks() {
		try{
			Date date=new Date();
			java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
			String curDate=df.format(date);
			logger.info("choose count down job start curDate:"+curDate);
			this.queryChooseCountDown();
			logger.info("choose count down job end");
		}catch(Exception ce){
			ce.printStackTrace();
			logger.error("choose count down job error!");
		}
	}
	
	/**
	 * 选股模型到期3/2/1天9点发送消息
	 */
	@SuppressWarnings("unchecked")
	private void queryChooseCountDown(){
		try{
			int infocode=7;
			String title="选股倒计时";
			String content=myProps.getChooseContent();
			
			List<Object> objectList = redisUtils.queryHashValue("sys_push_cfg");
			for(Object object:objectList){
				Map<String, Object> map = (Map<String, Object>) object;
			    String orgCode= map.get("orgCode").toString();
			    
			    Map<String, ChooseStockCfg> chooseMap=chooseCfgUtil.queryChooseCfgByOrgCode(orgCode);
			    for(Entry<String,ChooseStockCfg> entry:chooseMap.entrySet()){
			    	String exdata="{\"id\":"+entry.getValue().getCfgId()+", \"c\":\""+entry.getValue().getStockCode()+"\"}";
			    	logger.info("选股倒计时推送消息 strategy_code:"+entry.getValue().getStockCode()+",infocode:"+infocode+",title:"+title+",content:"+content+",exdata:"+exdata);
			    	chooseStockUtil.pushCountDownMsg(entry.getValue().getStockCode(), orgCode, title, content, infocode, exdata);
			    }
			}
		}catch(Exception ce){
			ce.printStackTrace();
			logger.error("send msg center info error",ce);
		}
	}

}
