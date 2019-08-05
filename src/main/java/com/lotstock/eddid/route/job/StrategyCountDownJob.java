package com.lotstock.eddid.route.job;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lotstock.eddid.route.util.ChooseStockUtil;
import com.lotstock.eddid.route.util.R;
import com.lotstock.eddid.route.util.RedisUtils;
import com.lotstock.eddid.route.util.StrUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.vo.RouteReqVo;

/**
 * 策略倒计时job 每天10点查询推送
 * 
 * @author David
 *
 */
@Component
public class StrategyCountDownJob {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private MyProps myProps;

	@Autowired
	private ChooseStockUtil chooseStockUtil;

	//@Scheduled(cron = "${myprops.strategyJob}")
	public void tasks() {
		try {
			Date date = new Date();
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String curDate = df.format(date);
			logger.info("策略倒计时 job start curDate:" + curDate);
			//this.queryChooseCountDown();
			logger.info("策略倒计时 job end");
		} catch (Exception ce) {
			ce.printStackTrace();
			logger.error("策略倒计时 job error!");
		}
	}

	/**
	 * 策略到期提醒
	 */
	@SuppressWarnings("unchecked")
	private void queryChooseCountDown() {
		try {
			int infocode = 6;
			String title = "策略倒计时";
			String content = myProps.getStrategyContent();

			List<Object> objectList = redisUtils.queryHashValue("trade_product");
			for (Object object : objectList) {
				Map<String, Object> map = (Map<String, Object>) object;
				String code = map.get("code").toString();
				String orgCode = map.get("platformCode").toString();
				RouteReqVo vo = new RouteReqVo();
				vo.setType(2001);
				vo.setStrategy_code(code);
				R r = restTemplate.postForObject(
						myProps.getStrategyServer() + "/strategy/route" + StrUtils.getParams(BeanUtils.describe(vo)),
						null, R.class);
				if (r.getCode() == 0) {
					DecimalFormat df = new DecimalFormat("######0.00");
					JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(r.getData()));
					double earnRate = Double.parseDouble(df.format(jsonObject.getDouble("earn_rate")*100));
					String exdata = "{\"id\":\""+infocode+"\", \"c\":\""+code+"\"}";
					logger.info("策略倒计时推送消息 strategy_code:" + code + ",infocode:" + infocode + "title:" + title
							+ ",content:" + content + ",exdata:" + exdata);
					chooseStockUtil.pushStrategyCountDown(code, orgCode, title, content, infocode, exdata, earnRate);
				}
			}
		} catch (Exception ce) {
			ce.printStackTrace();
			logger.error("send msg center info error", ce);
		}
	}

}
