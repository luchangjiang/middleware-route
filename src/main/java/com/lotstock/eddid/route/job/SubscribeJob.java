package com.lotstock.eddid.route.job;

import java.util.Date;

import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.SubscribeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;

/**
 * 订阅到期job 每天10点查询推送
 * 
 * @author pengj
 *
 */
@Component
public class SubscribeJob {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyProps myProps;

	@Autowired
	private SubscribeUtil subscribeUtil;
	
	@Scheduled(cron = "${myprops.subscribeJob}")
	public void tasks() {
		try {
			Date date = new Date();
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String curDate = df.format(date);
			logger.info("订阅到期job start curDate:" + curDate);
			this.querySubscribeCountDown();
			logger.info("订阅到期job end:"+ curDate);
		} catch (Exception ce) {
			ce.printStackTrace();
			logger.error("订阅到期job error!");
		}
	}

	/**
	 * 策略到期提醒
	 */
	private void querySubscribeCountDown() {
		try {
			int infocode = 500;
			String title = "订阅到期提醒";
			String content = myProps.getSubscribeContent();
			String strFlag = "zb001,pay001,zx001";
			String[] productSysFlags = strFlag.split(",");
			for(String productSysFlag:productSysFlags){
				ResultModel rtModel= restTemplate.getForObject(myProps.getSubscribeServer() + "/pro/queryProductBySysFlag"+ "?productSysFlag={1}", ResultModel.class,productSysFlag);
				if(rtModel.getCode()==0){
					JSONArray jsonArray=JSONArray.parseArray(JSON.toJSONString(rtModel.getResult()));
					if(jsonArray.size() > 0){
						for(int i=0;i<jsonArray.size();i++){
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							String sysProductCode = jsonObject.getString("sysProductCode");
							String orgCode = jsonObject.getString("orgCode");
							
							String exdata = "{\"id\":\""+infocode+"\", \"c\":\""+sysProductCode+"\"}";
							logger.info("订阅倒计时推送消息sysProductCode :" + sysProductCode + ",infocode:" + infocode + "title:" + title
									+ ",content:" + content + ",exdata:" + exdata + ",orgCode" + orgCode);
							subscribeUtil.pushSubscribeMsg(sysProductCode, orgCode, title, content, infocode, exdata, productSysFlag);
						}
					}
				}
			}
		} catch (Exception ce) {
			ce.printStackTrace();
			logger.error("subscribe push error", ce);
		}
	}
}
