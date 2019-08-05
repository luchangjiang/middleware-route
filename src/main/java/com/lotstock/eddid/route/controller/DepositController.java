package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.util.ResultModel;
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
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;

@RestController
@RefreshScope
@RequestMapping("/deposit")
public class DepositController extends BaseController{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 提现基本信息
	private static final String findCusInfo ="/fund/findCusInfo";
	// 查询客户银行卡信息
	private static final String findBankInfo ="/fund/findBankInfo";
	// 获取提现验证码
	private static final String sendSmsCode="/fund/sendSmsCode";
	// 提现操作
	private static final String depositMoney="/fund/depositMoney";
	
	 /**
     * 提现基本信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/findCusInfo" ,method = RequestMethod.POST)
    public @ResponseBody
    ResultModel findCusInfo(@RequestBody String params) {
    	ResultModel result=new ResultModel();
    	JSONObject subObj= JSON.parseObject(params);
    	JSONObject paramObj=JSONObject.parseObject(subObj.getString("params"));
    	String sessionCode=paramObj.getString("sessionCode");
    	logger.info("findCusInfo param sessionCode:"+sessionCode);
    	if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + findCusInfo + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 查询客户银行卡信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/findBankInfo" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel findBankInfo(@RequestBody String params) {
    	ResultModel result=new ResultModel();
    	JSONObject subObj= JSON.parseObject(params);
    	JSONObject paramObj=JSONObject.parseObject(subObj.getString("params"));
    	String sessionCode=paramObj.getString("sessionCode");
    	logger.info("findBankInfo param sessionCode:"+sessionCode);
    	if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + findBankInfo + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 获取提现验证码
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/sendSmsCode" ,method = RequestMethod.POST)
    public @ResponseBody synchronized ResultModel sendSmsCode(@RequestBody String params) {
    	ResultModel result=new ResultModel();
    	JSONObject subObj= JSON.parseObject(params);
    	JSONObject paramObj=JSONObject.parseObject(subObj.getString("params"));
    	String sessionCode=paramObj.getString("sessionCode");
    	logger.info("sendSmsCode param sessionCode:"+sessionCode);
    	if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + sendSmsCode + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 提现操作
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/depositMoney" ,method = RequestMethod.POST)
    public @ResponseBody synchronized ResultModel depositMoney(@RequestBody String params) {
    	ResultModel result=new ResultModel();
    	JSONObject subObj= JSON.parseObject(params);
    	JSONObject paramObj=JSONObject.parseObject(subObj.getString("params"));
    	String sessionCode=paramObj.getString("sessionCode");
    	String transAmt=paramObj.getString("transAmt");
    	String code=paramObj.getString("code");
    	String username=paramObj.getString("username");
    	String openBank=paramObj.getString("openBank");
    	String cardNo=paramObj.getString("cardNo");
    	String mobile=paramObj.getString("mobile");
    	String idcard=paramObj.getString("idcard");
    	String prov=paramObj.getString("prov");
    	String city=paramObj.getString("city");
    	logger.info("deposity money param sessionCode:"+sessionCode+",transAmt:"+transAmt+",code:"+code+",username:"+username+",openBank:"+openBank+",cardNo:"+cardNo+",mobile:"+mobile+",idcard:"+idcard+",prov:"+prov+",city:"+city);
    	if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(transAmt)||StringUtils.isBlank(code)||StringUtils.isBlank(username)||StringUtils.isBlank(openBank)
    			||StringUtils.isBlank(cardNo)||StringUtils.isBlank(mobile)||StringUtils.isBlank(idcard)||StringUtils.isBlank(prov)||StringUtils.isBlank(city)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + depositMoney + "?customerId={1}&transAmt={2}&smsCode={3}&userName={4}&bankName={5}&bankCard={6}&bankMobile={7}&idCard={8}&prov={9}&city={10}", 
					ResultModel.class,customerId,transAmt,code,username,openBank,cardNo,mobile,idcard,prov,city);
		}else{
			return re;
		}
    }
	

}
