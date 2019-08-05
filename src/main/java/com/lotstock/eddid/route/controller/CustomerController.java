package com.lotstock.eddid.route.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.lotstock.eddid.route.util.*;
import com.lotstock.eddid.route.vo.ApplyAccountVo;
import com.lotstock.eddid.route.vo.CustomerInfoVo;
import com.lotstock.eddid.route.vo.CustomerOprDomain;
import com.lotstock.eddid.route.vo.StockParamVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

/**
 * 用户信息
 *
 * @author David
 *
 */
@RestController
@RefreshScope
@RequestMapping("/customer")
public class CustomerController extends BaseController{

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyProps myProps;

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private SubscribeUtil subscribeUtil;

	// 发送短信验证码
	private static final String sendSmsCaptcha = "/customer/sendSmsCaptcha";
	// 用户注册
	private static final String register = "/customer/register";
	// 用户登录
	private static final String login = "/customer/login";
	// 查询用户信息
	private static final String query_custInfo = "/customer/query_custInfo";
	// 客户信息修改
	private static final String update_cust = "/customer/update_cust";
	// 修改密码
	private static final String modify_pwd = "/customer/modify_pwd";
	// 重置密码
	private static final String reset_pwd = "/customer/reset_pwd";
	// sessionCode刷新
	private static final String refresh_sessioncode = "/customer/refresh_sessioncode";
	// 退出登录
	private static final String logout = "/customer/logout";
	// 用户头像上传
	private static final String set_headImg = "/customer/set_headImg";
	// 查询banner
	private static final String query_adver = "/customer/query_adver";
	// 首页弹框
	private static final String query_play = "/customer/query_play";
	// 首页五个图标
	private static final String query_first_icon = "/customer/query_first_icon";
	// 明星牛股
	private static final String query_stock_star = "/customer/query_stock_star";
	// 查询最新版本
	private static final String query_lastProductVer = "/customer/query_lastProductVer";
	// 获取数据字典配置
	private static final String query_dataConfByOrg = "/customer/query_dataConfByOrg";
	// 唤醒广告页
	private static final String query_awaken_time = "/customer/query_awaken_time";
	// 意见反馈
	private static final String feedback = "/customer/feedback";
	// 检查sessionCode
	private static final String check_session = "/customer/check_session";
	// 根据openid查询用户手机号
	private static final String query_mobile = "/customer/query_mobile";
	// 注册成功后提示赠送哪个模型
	private static final String query_register_choose = "/customer/query_register_choose";
	// 判断用户是否输入了密码
	private static final String query_visitor = "/customer/query_visitor";
	// 用户输入访问密码
	private static final String input_visitorPwd= "/customer/input_visitorPwd";
	// 根据日期查询
	private static final String query_cust_date="/customer/query_cust_date";
	// 修改用户渠道
	private static final String update_cust_channel="/customer/update_cust_channel";
	// 新增自选股
	private static final String add = "/customer/add";
	// 删除自选股
	private static final String delete = "/customer/delete";
	// 查询自选股
	private static final String query = "/customer/query";
	// 更新自选股
	private static final String update_stock = "/customer/update_stock";
	// 帮助中心
	private static final String queryHelpCfg = "/customer/query_help_cfg";
	// 帮助类型
	private static final String queryHelpList = "/customer/query_help_list";
	// 帮助详情
	private static final String queryHelpDetail = "/customer/query_help_detail";
	// 图片上传
	private static final String save_feedImg= "/customer/save_feedImg";
	//生成验证码
	private static final String identifying_code= "/customer/identifyingCode";
	//申请账号
	private static final String apply_account= "/customer/apply";

	//获取系统配置
	private static final String get_config= "/customer/get_config";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 发送短信验证码
	 * @param mobile
	 * @param orgCode
	 * @param use 1.注册 2.重置密码 3领奖品 4登录
	 * @return
	 */
	@RequestMapping(value = "/sendSmsCaptcha" ,method = RequestMethod.POST)
	public @ResponseBody synchronized ResultModel sendSmsCode(HttpServletRequest request, String mobile, String orgCode, Integer use, String tranche, String language) {
		String ip=request.getRemoteAddr();
		String key="smsNum_"+ip;
		String smsNum = redisUtils.get(key);
		if(null==smsNum||"".equals(smsNum)){
			redisUtils.set(key, 1);
		}else{
			if(Integer.parseInt(smsNum)<30){
				redisUtils.set(key,Integer.parseInt(smsNum)+1,12*3600);
			}else{
				ResultModel result=new ResultModel();
	    		result.setCode(-1);
	    		String msg = "短信发送达到上限，请您稍后再试!";
	    		result.setMessage("zh-Hans".equals(language)?msg: JtFtConvert.convert(msg, 1));
	    		return result;
			}
		}
		return restTemplate.getForObject(myProps.getCustomerServer() + sendSmsCaptcha+"?mobile={1}&orgCode={2}&use={3}&tranche={4}&language={5}", ResultModel.class,mobile,orgCode,use,tranche,language);
	}

	/**
	 * 生成普通验证码
	 * @return
	 */
	@RequestMapping(value = "/identifyingCode" )
	public @ResponseBody ResultModel identifyingCode(){
		return restTemplate.getForObject(myProps.getCustomerServer() + identifying_code,ResultModel.class);
	}

	/**
	 * 用户注册
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/register" ,method = RequestMethod.POST)
	public @ResponseBody synchronized ResultModel register(CustomerInfoVo vo)throws Exception{
		return restTemplate.getForObject(myProps.getCustomerServer() + register + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}

	/**
	 * 申请账号
	 * @return
	 */
	@RequestMapping(value = "/apply" )
	public @ResponseBody ResultModel applyAccount(ApplyAccountVo applyAccountVo) throws Exception {
		return RestUtil.postForObject(myProps.getCustomerServer() + apply_account,applyAccountVo, ResultModel.class);
	}

	/**
	 * 获取系统配置
	 * @return
	 */
	@RequestMapping(value ="/get_config")
	public @ResponseBody ResultModel getConfig() {
		return restTemplate.getForObject(myProps.getCustomerServer() + get_config,ResultModel.class);
	}


	/**
	 * get customerKey
	 */
	@RequestMapping(value = "/getCusCustomerInfo" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel getCustomerId(HttpServletRequest request){
		ResultModel result = super.getCusCustomerInfo(request);
		if(result.getCode() == -1){
			return result;
		}else {
			JSONObject customerInfo = (JSONObject) result.getResult();
			int customerId = customerInfo.getInteger("customerId");
			String mobile = customerInfo.getString("mobile");
			logger.info("customerId is {} , and mobile is {}",customerId,mobile);
			return result;
		}
	}
	/**
	 * 用户登录
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/login" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel login(HttpServletRequest request,CustomerInfoVo vo){
		ResultModel result = super.getCusCustomerInfo(request);
		if(result.getCode() == -1){
			return result;
		}else {
			JSONObject customerInfo = (JSONObject) result.getResult();
			//
			JSONObject c = new JSONObject();
			c.put("sessionCode","");
			c.put("mobile",customerInfo.get("mobile"));
			c.put("customerKey",customerInfo.get("customerKey"));
			c.put("customerId",customerInfo.get("customerId"));
			c.put("nikeName",customerInfo.get("nikeName"));
			c.put("orgCode",customerInfo.get("orgCode"));
			c.put("state",customerInfo.get("state"));
			result.setResult(c);
			return  result;

			//
//			String mobile = customerInfo.getString("mobile");
//			//密码
//			String pwd = customerInfo.getString("originalPwd");
//			//机构编码
//			String orgCode = customerInfo.getString("orgCode");
//			//渠道类型
//			String channelType = vo.getChannelType();
//			//openId
//			String openId = vo.getOpenId();
//			//登录设备号
//			String deviceToken = vo.getDeviceToken();
//			//类型
//			String type = vo.getType();
//			//版本号
//			String versionNumber = vo.getVersionNumber();
//			//验证码
//			String verificationCode = vo.getVerificationCode();
//			//组别
//			String tranche = vo.getTranche();
//			//语言
//			String language = vo.getLanguage();
//			String url = myProps.getCustomerServer() + login + "?mobile={1}&pwd={2}&orgCode={3}&channelType={4}&openId={5}&deviceToken={6}&type={7}&versionNumber={8}&verificationCode={9}&tranche={10}&language={11}";
//
//			return restTemplate.getForObject(url, ResultModel.class, mobile, pwd, orgCode, channelType, openId, deviceToken, type, versionNumber, verificationCode, tranche, language);
		}
	}


	/**
	 * 记录用户操作
	 *
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value ="/recordCustomerOpr", method = RequestMethod.POST)
	public @ResponseBody ResultModel recordCustomerOpr(@RequestBody CustomerOprDomain customerOprDomain) {
		if (StringUtils.isBlank(customerOprDomain.getSessionCode())) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}

		return restTemplate.postForEntity(myProps.getCustomerServer() + "/customer/recordCustomerOpr", customerOprDomain, ResultModel.class).getBody();
	}

	/**
	 * 查询用户信息
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value ="/query_custInfo",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryCustInfo(HttpServletRequest request,String sessionCode,String language) {
		ResultModel result = super.getCusCustomerInfo(request);
		return result;
	}

	/**
	 * 客户信息修改
	 * @param customerId
	 * @param nickName
	 * @param iconUrl
	 * @param gender
	 * @param qq
	 * @param wx
	 * @param email
	 * @return
	 */
	@RequestMapping(value ="/update_cust",method = RequestMethod.POST)
	public @ResponseBody ResultModel updateCustInfo(HttpServletRequest request, Integer customerId, String nickName, String iconUrl, Integer gender, String qq,String wx,String email,String language) {
		ResultModel result = super.getCusCustomerInfo(request);
		if(result.getCode() == -1){
			return result;
		}else {
			JSONObject customerInfo = (JSONObject) result.getResult();
			int cusId = customerInfo.getInteger("customerId");
			if(cusId == 1){
				//游客,直接退出
				result.setMessage("成功");
				result.setResult(null);
				return result;
			}else {
				return restTemplate.getForObject(myProps.getCustomerServer() + update_cust+"?customerId={1}&nickName={2}&iconUrl={3}&gender={4}&qq={5}&wx={6}&email={7}&language={8}", ResultModel.class,cusId,nickName,iconUrl,gender,qq,wx,email,language);
			}
		}
	}

	/**
	 * todo 如果是对接会员中心需要更新密码到Eddid
	 * 修改密码
	 * @param customerId
	 * @param pwd
	 * @param pwdNew
	 * @return
	 */
	@RequestMapping(value ="/modify_pwd",method = RequestMethod.POST)
    public @ResponseBody ResultModel modifyPwd(HttpServletRequest request, Integer customerId, String pwd, String pwdNew,String language) {
		ResultModel result = new ResultModel();
		result.setCode(0);
		return result;
	}

	/**
	 * todo 如果是对接会员中心需要更新密码到Eddid
	 * 重置密码
	 * @param mobile
	 * @param orgCode
	 * @param pwd 新密码
	 * @param verificationCode 验证码
	 * @return
	 */
	@RequestMapping(value ="/reset_pwd",method = RequestMethod.POST)
	public @ResponseBody ResultModel resetPwd(String mobile, String orgCode,String pwd, String verificationCode,String language) {
		ResultModel result = new ResultModel();
		result.setCode(0);
		return result;
	}

	/**
	 * sessionCode刷新
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value ="/refresh_sessioncode",method = RequestMethod.POST)
	public @ResponseBody ResultModel refreshSessioncode(String sessionCode,String language) {
		ResultModel result = new ResultModel();
		result.setCode(0);
		return result;
	}

	/**
	 * 退出登录
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value ="/logout",method = RequestMethod.POST)
	public @ResponseBody ResultModel logout(String sessionCode,String language) {
		ResultModel result = new ResultModel();
		result.setCode(0);
		return result;
	}

	/**
	 * 用户头像上传
	 * @param customerId
	 * @param file
	 * @param suffix
	 * @return
	 */
	@RequestMapping(value ="/set_headImg",method = RequestMethod.POST)
	public @ResponseBody ResultModel setHeadImg(HttpServletRequest request, Integer customerId,String file,String suffix,String language) {
		if (StringUtils.isBlank(file)||StringUtils.isBlank(suffix)) {
			ResultModel result=new ResultModel();
			result.setCode(-1);
			String msg = "参数错误";
			result.setMessage("zh-Hans".equals(language)?msg:JtFtConvert.convert(msg, 1));
			return result;
		}
		//
		ResultModel tokenInfo = super.getCusCustomerInfo(request);
		if(tokenInfo.getCode() == -1){
			return tokenInfo;
		}
		JSONObject customerInfo = (JSONObject) tokenInfo.getResult();
		int cusId = customerInfo.getInteger("customerId");
		//
		MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
    	params.add("customerId", cusId);
    	params.add("file", file);
    	params.add("suffix", suffix);
    	params.add("language", language);
    	//file 内容太长用post 提交，用get方式不行，这个问题查询了半天，要注意。
    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + set_headImg, params, ResultModel.class);
    	ResultModel body = responseEntity.getBody();
    	return body;
	}

	/**
	 * 查询banner
	 * @param mobile
	 * @param orgCode
	 * @param adverType
	 * @return
	 */
	@RequestMapping(value = "/query_adver", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryAdver(String mobile, String orgCode, Integer adverType) {
		return restTemplate.getForObject(myProps.getCustomerServer() + query_adver+"?mobile={1}&orgCode={2}&adverType={3}", ResultModel.class,mobile,orgCode,adverType);
	}

	/**
	 * 首页弹框
	 * @param mobile
	 * @param orgCode
	 * @param adverType
	 * @return
	 */
	@RequestMapping(value = "/query_play" , method = RequestMethod.POST)
	public @ResponseBody ResultModel queryPlay(String mobile, String orgCode,String deviceToken) {
		if (StringUtils.isBlank(orgCode)||StringUtils.isBlank(mobile)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getCustomerServer() + query_play+"?mobile={1}&orgCode={2}&deviceToken={3}", ResultModel.class,mobile,orgCode,deviceToken);
	}

	/**
	 * 首页五个图标
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_first_icon" , method = RequestMethod.POST)
	public @ResponseBody ResultModel queryFirstIcon(String orgCode) {
		if (StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getCustomerServer() + query_first_icon +"?orgCode={1}", ResultModel.class,orgCode);
	}

	/**
	 * 查询明星牛股
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/query_stock_star" , method = RequestMethod.POST)
	public @ResponseBody ResultModel queryStockStar(String type) {
		if (StringUtils.isBlank(type)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getCustomerServer() + query_stock_star+"?type={1}", ResultModel.class,type);
	}

	/**
	 * 查询最新版本
	 * @param productKey
	 * @param childProductKey
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_lastProductVer" , method = RequestMethod.POST)
	public @ResponseBody ResultModel queryLastProductVer(String productKey,String childProductKey,String orgCode,String language) {
		return restTemplate.getForObject(myProps.getCustomerServer() + query_lastProductVer+"?productKey={1}&childProductKey={2}&orgCode={3}&language={4}", ResultModel.class,productKey,childProductKey,orgCode,language);
	}

	/**
	 * 获取数据字典配置
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_dataConfByOrg" , method = RequestMethod.POST)
	public @ResponseBody ResultModel queryDataCfgByKey(String orgCode,String clientType,String versionCheck) {
		return restTemplate.getForObject(myProps.getCustomerServer() + query_dataConfByOrg+"?orgCode={1}&clientType={2}&versionCheck={3}", ResultModel.class,orgCode,clientType,versionCheck);
	}

	/**
	 * 唤醒广告页
	 * @return
	 */
	@RequestMapping(value = "/query_awaken_time" , method = RequestMethod.POST)
	public @ResponseBody ResultModel queryAwakenTime(String orgCode) {
		if (StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getCustomerServer() + query_awaken_time +"?orgCode={1}", ResultModel.class,orgCode);
	}

	/**
	 * 用户意见反馈
	 * @param customerId
	 * @param adviceType
	 * @param adviceContent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/feedback" , method = RequestMethod.POST)
	public @ResponseBody ResultModel feedback(HttpServletRequest request,String sessionCode,Integer adviceType,String adviceContent,String advicePic,String orgCode) {
		ResultModel result = super.getCusCustomerInfo(request);
		if(result.getCode() == -1){
			return restTemplate.getForObject(myProps.getCustomerServer() + feedback +"?customerId={1}&adviceType={2}&adviceContent={3}&advicePic={4}&orgCode={5}", ResultModel.class,null,adviceType,adviceContent,advicePic,orgCode);
		}else {
			JSONObject customerInfo = (JSONObject) result.getResult();
			int customerId = customerInfo.getInteger("customerId");
			return restTemplate.getForObject(myProps.getCustomerServer() + feedback +"?customerId={1}&adviceType={2}&adviceContent={3}&advicePic={4}&orgCode={5}", ResultModel.class,customerId,adviceType,adviceContent,advicePic,orgCode);
		}
	}

	/**
	 * 检查sessionCode
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value ="/check_session",method = RequestMethod.POST)
	public @ResponseBody ResultModel checkSession(String sessionCode) {
//		return restTemplate.getForObject(myProps.getCustomerServer() + check_session+"?sessionCode={1}", ResultModel.class,sessionCode);
		ResultModel result = new ResultModel();
		result.setCode(0);
		return result;
	}

	/**
	 * 根据openid查询用户手机号
	 * @param openId
	 * @return
	 */
	@RequestMapping(value = "/query_mobile", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryMobileByOpenId(String openId) {
		if (StringUtils.isBlank(openId)) {
			ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getCustomerServer() + query_mobile+"?openId={1}", ResultModel.class,openId);
	}

	/**
	 * 注册成功后提示赠送哪个模型
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_register_choose", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryRegisterChoose(String orgCode) {
		if (StringUtils.isBlank(orgCode)) {
			ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getCustomerServer() + query_register_choose+"?orgCode={1}", ResultModel.class,orgCode);
	}

	/**
	 * 判断用户是否输入了密码
	 * @param customerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value ="/query_visitor")
	public @ResponseBody ResultModel queryVisitor(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map = (Map<String, Object>) re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			return restTemplate.getForObject(myProps.getCustomerServer() + query_visitor + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}

	/**
	 * 用户输入访问密码
	 * @param customerId
	 * @param pwd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/input_visitorPwd")
	public @ResponseBody ResultModel inputVisitorPwd(String sessionCode,String pwd){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(pwd)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map = (Map<String, Object>) re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			return restTemplate.getForObject(myProps.getCustomerServer() + input_visitorPwd + "?customerId={1}&pwd={2}", ResultModel.class,customerId,pwd);
		}else{
			return re;
		}
	}

	/**
	 * 根据日期查询
	 * @param queryDate
	 * @return
	 */
	@RequestMapping(value = "/query_cust_date",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryCustByDate(@RequestBody Map<String, Object> paramMap){
		String queryDate=paramMap.get("queryDate").toString();
		ResultModel re=restTemplate.getForObject(myProps.getCustomerServer() + query_cust_date + "?queryDate={1}", ResultModel.class,queryDate);
		return re;
	}

	/**
	 * 修改用户来源
	 * @param array
	 * @return
	 */
	@RequestMapping(value = "/update_cust_channel",method = RequestMethod.POST)
	public @ResponseBody ResultModel updateCustChannel(@RequestBody Map<String, Object> paramMap){
		ResultModel result=new ResultModel();
		int status=0;
		String message="成功";
		try{
			String data=JSONObject.toJSONString(paramMap.get("data"));
			JSONArray array=JSONArray.parseArray(data);
			for(int i=0;i<array.size();i++){
				JSONObject json=array.getJSONObject(i);
				String channel=json.getString("channel");
				String mobile=json.getString("mobile");
				restTemplate.getForObject(myProps.getCustomerServer() + update_cust_channel + "?channel={1}&mobile={2}", ResultModel.class,channel,mobile);
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			status=-1;
			logger.error("update cust channel error",ce);
		}
		result.setCode(status);
		result.setMessage(message);
		return result;
	}

	/**
	 * 查询权限
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_subscribe" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel querySubscribe(HttpServletRequest request,String sessionCode, String language){
		JSONObject jsonObject = new JSONObject();
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		int langCode = !"zh-Hans".equals(language) ? 1 : 0;
		try{
			ResultModel tokenCustomerInfo = super.getCusCustomerInfo(request);
			if(tokenCustomerInfo.getCode() == -1){
				return tokenCustomerInfo;
			}else {
				JSONObject customerInfo = (JSONObject) tokenCustomerInfo.getResult();
				int customerId = customerInfo.getInteger("customerId");
				int customerKey = customerInfo.getInteger("customerKey");
				String mobile = customerInfo.getString("mobile");
				jsonObject = subscribeUtil.querySubscribe(customerId, customerKey, mobile);
			}
		}catch(Exception ce){
			code=-1;
			message="失败";
			ce.printStackTrace();
		}
		result.setCode(code);
		result.setMessage(message);
		result.setResult(JSONObject.parseObject(JtFtConvert.convert(JSON.toJSONString(jsonObject), langCode)));
		return result;
	}

	/**
	 * 新增自选
	 * @param sessionCode
	 * @param markId
	 * @param code
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	public @ResponseBody ResultModel add(HttpServletRequest request, String sessionCode, String markId, String code, String name) {
		ResultModel result = new ResultModel();
		String message="成功";
		//
		ResultModel tokenCustomerInfo = super.getCusCustomerInfo(request);
		if(tokenCustomerInfo.getCode() == -1){
			return tokenCustomerInfo;
		}
		//
		JSONObject customerInfo = (JSONObject) tokenCustomerInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		String orgCode = customerInfo.getString("orgCode");
		ResultModel rm = restTemplate.getForObject(myProps.getCustomerServer() + add +"?customerId={1}&markId={2}&code={3}&name={4}&orgCode={5}", ResultModel.class,customerId,markId,code,name,orgCode);
		if(rm.getCode()==0){
			message="添加自选成功";
		}else{
			message="添加自选失败";
		}
		result.setCode(rm.getCode());
		result.setMessage(message);
		result.setResult(rm.getResult() == null ? "" : rm.getResult());
		return result;
	}

	/**
	 * 删除自选
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/delete",method = RequestMethod.POST)
	public @ResponseBody ResultModel delete(HttpServletRequest request, StockParamVo vo) throws Exception {
		ResultModel result = new ResultModel();
		String message="成功";
		//
		ResultModel tokenCustomerInfo = super.getCusCustomerInfo(request);
		if(tokenCustomerInfo.getCode() == -1){
			return tokenCustomerInfo;
		}
		//
		JSONObject customerInfo = (JSONObject) tokenCustomerInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		String orgCode = customerInfo.getString("orgCode");
		//
		String stocks = vo.getStocks();
		MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
		params.add("orgCode", orgCode);
		/**
		 * customerId为200283测试通过，移植的数据在部分表可能缺失部分东西
		 */
		params.add("customerId", customerId);
		params.add("stocks", stocks);
		//file 内容太长用post 提交，用get方式不行，这个问题查询了半天，要注意。
		ResponseEntity<ResultModel> responseEntity=restTemplate.postForEntity(myProps.getCustomerServer() + delete, params, ResultModel.class);
		ResultModel rm = responseEntity.getBody();
		if(rm.getCode()==0){
			message="删除自选成功";
		}else{
			message="删除自选失败";
		}
		result.setCode(rm.getCode());
		result.setMessage(message);
		result.setResult(rm.getResult() == null ? "" : rm.getResult());
		return result;
	}

	/**
	 * 查询自选
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query",method = RequestMethod.POST)
	public @ResponseBody ResultModel query(HttpServletRequest request, String sessionCode) {
		ResultModel tokenCustomerInfo = super.getCusCustomerInfo(request);
		if(tokenCustomerInfo.getCode() == -1){
			return tokenCustomerInfo;
		}
		//
		JSONObject customerInfo = (JSONObject) tokenCustomerInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		String orgCode = customerInfo.getString("orgCode");
		ResultModel rm = restTemplate.getForObject(myProps.getCustomerServer() + query +"?customerId={1}&orgCode={2}", ResultModel.class,customerId,orgCode);
		rm.setResult(rm.getResult() == null ? "" : rm.getResult());
		return rm;
	}

	/**
	 * 覆盖更新自选
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update_stock",method = RequestMethod.POST)
	public @ResponseBody ResultModel updateStock(HttpServletRequest request, StockParamVo vo) throws Exception {
		ResultModel tokenCustomerInfo = super.getCusCustomerInfo(request);
		if(tokenCustomerInfo.getCode() == -1){
			return tokenCustomerInfo;
		}
		//
		JSONObject customerInfo = (JSONObject) tokenCustomerInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		String orgCode = customerInfo.getString("orgCode");
		ResultModel rm = restTemplate.getForObject(myProps.getCustomerServer() + update_stock + "?customerId={1}&stocks={2}&orgCode={3}", ResultModel.class,customerId,vo.getStocks(),orgCode);
		rm.setResult(rm.getResult() == null ? "" : rm.getResult());
		return rm;
	}

	/**
	 * 行情权限
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_hk_permission", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryHkPermission(String sessionCode) {
		ResultModel result=new ResultModel();
		int code=0;
		String message="成功";
		if (StringUtils.isBlank(sessionCode)) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		logger.info("query hk permission param sessionCode:"+sessionCode);
		JSONArray jsonArray = new JSONArray();
		try{
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				int customerId=Integer.parseInt(map.get("customerId").toString());
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				String mobile=map.get("mobile").toString();
				jsonArray=subscribeUtil.queryHkPermission(customerId, customerKey, mobile);
			}else{
				return re;
			}
		}catch(Exception ce){
			ce.printStackTrace();
			message="失败";
			code=-1;
			logger.error("query hk permission  info error",ce);
		}
		result.setCode(code);
		result.setMessage(message);
		result.setResult(jsonArray);
		return result;
	}

	/**
	 * 帮助中心
	 * @param helpType
	 * @param helpName
	 * @return
	 */
	@RequestMapping(value = "/query_help_cfg", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryHelpCfg(String language) {
		return restTemplate.getForObject(myProps.getCustomerServer() + queryHelpCfg + "?language={1}", ResultModel.class,language);
	}

	/**
	 * 搜素帮助中心数据
	 * @return
	 */
	@RequestMapping(value = "/query_help_list", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryHelpList(String helpName, int pageIndex, int pageSize, String language) {
		return restTemplate.getForObject(myProps.getCustomerServer() + queryHelpList + "?helpName={1}&pageIndex={2}&pageSize={3}&pageSize={4}", ResultModel.class,helpName,pageIndex,pageSize,language);
	}

	/**
	 * 帮助详情
	 * @return
	 */
	@RequestMapping(value = "/query_help_detail", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryHelpDetail(int helpId, String language) {
		return restTemplate.getForObject(myProps.getCustomerServer() + queryHelpDetail + "?helpId={1}&language={2}", ResultModel.class,helpId,language);
	}

	/**
	 * 用户头像上传
	 * @param customerId
	 * @param file
	 * @param suffix
	 * @return
	 */
	@RequestMapping(value ="/save_feedImg",method = RequestMethod.POST)
	public @ResponseBody ResultModel saveFeedImg(String file,String suffix,String language) {
		if (StringUtils.isBlank(file)||StringUtils.isBlank(suffix)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		String msg = "参数错误";
    		result.setMessage("zh-Hans".equals(language)?msg:JtFtConvert.convert(msg, 1));
    		return result;
    	}
		MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
    	params.add("file", file);
    	params.add("suffix", suffix);
    	params.add("language", language);
    	//file 内容太长用post 提交，用get方式不行，这个问题查询了半天，要注意。
    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getCustomerServer() + save_feedImg, params, ResultModel.class);
    	ResultModel body = responseEntity.getBody();
    	return body;
	}


}
