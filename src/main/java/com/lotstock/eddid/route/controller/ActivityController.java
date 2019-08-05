package com.lotstock.eddid.route.controller;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.Re;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.vo.StockParamVo;

@RestController
@RefreshScope
@RequestMapping("/activity")
public class ActivityController extends BaseController{

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 签到头部信息
	private static final String clock_top = "/activity/clock_top";
	// 每日签到
	private static final String clock_in = "/activity/clock_in";
	// 牛股记录
	private static final String query_signHis = "/activity/query_signHis";
	// 分享
	private static final String save_share="/activity/save_share";
	
	// 获取金蛋信息
	private static final String intEnter = "/activity/intEnter";
	// 砸金蛋
	private static final String smashEggs = "/activity/smashEggs";
	// 砸金蛋中奖记录
	private static final String winRecord = "/activity/winRecord";
	// 砸金蛋播报
	private static final String broadcastEggs = "/activity/broadcastEggs";
	
	//可翻牌次数
	private static final String query_prize_status="/activity/query_prize_status";
	//翻牌动作
	private static final String flip_cards="/activity/flip_cards";
	//查询中奖播报
	private static final String query_prize_record="/activity/query_prize_record";
	//查询用户的中奖纪录
	private static final String query_prize_cust="/activity/query_prize_cust";
	//游客领取奖品
	private static final String pull_down_visitor="/activity/pull_down_visitor";
	//保存用户分享app 记录
	private static final String save_share_info="/activity/save_share_info";
	//判断游客是否领券奖品
	private static final String query_visitor_record="/activity/query_visitor_record";
	//根据用户id查询
	private static final String query_sign_record="/activity/query_sign_record";
	//分享App得金币
	private static final String share_app="/activity/share_app";
	//进入和讯活页面
	private static final String init_sales="/activity/init_sales";
	//马上抢购
	private static final String buy_sales="/activity/buy_sales";
	
	/**
     * 签到头部信息
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/clock_top" ,method = RequestMethod.POST)
    public @ResponseBody synchronized ResultModel clockTop(String sessionCode) {
    	if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getActicityServer() + clock_top + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 每日签到
     * @param sessionCode
     * @return
     */
	@SuppressWarnings("unchecked")
    @RequestMapping(value = "/clock_in" ,method = RequestMethod.POST)
    public @ResponseBody synchronized ResultModel clockIn(String sessionCode) {
    	if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			String orgCode=map.get("orgCode").toString();
			return restTemplate.getForObject(myProps.getActicityServer() + clock_in + "?customerId={1}&orgCode={2}", ResultModel.class,customerId,orgCode);
		}else{
			return re;
		}
    }
    
    /**
     * 牛股记录
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_signHis" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel querySignHis(String sessionCode) {
    	if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getActicityServer() + query_signHis + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 保存分享记录
     * @param sessionCode
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/save_share" ,method = RequestMethod.POST)
    public @ResponseBody synchronized ResultModel saveShare(String sessionCode,String stockCode) {
    	if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			String orgCode=map.get("orgCode").toString();
			//用户中心用户表主键用户Id
			int customerKey=Integer.parseInt(map.get("customerKey").toString());
			return restTemplate.getForObject(myProps.getActicityServer() + save_share + "?customerId={1}&customerKey={2}&orgCode={3}&stockCode={4}", ResultModel.class,customerId,customerKey,orgCode,stockCode);
		}else{
			return re;
		}
    }
		
    /**
     * 获取金蛋信息
     * @param sessionCode
     * @return
     */
	@RequestMapping(value = "/intEnter",method = RequestMethod.POST)
	public @ResponseBody ResultModel add(String sessionCode) {
		ResultModel result = new ResultModel();
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			Re r = restTemplate.getForObject(myProps.getActicityServer() + intEnter +"?customerId={1}", Re.class,customerId);
			result.setCode(Integer.parseInt(r.get("code").toString()));
			result.setMessage(r.get("message").toString());
			result.setResult(r.getData() == null ? "" : r.getData());
			return result;
		}else{
			return re;
		}
	}

	/**
     * 砸金蛋
     * @param sessionCode
     * @return
     */
	@RequestMapping(value = "/smashEggs",method = RequestMethod.POST)
	public @ResponseBody ResultModel delete(StockParamVo vo) {
		ResultModel result = new ResultModel();
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			Re r = restTemplate.getForObject(myProps.getActicityServer() + smashEggs + "?customerId={1}", Re.class,customerId);
			result.setCode(Integer.parseInt(r.get("code").toString()));
			result.setMessage(r.get("message").toString());
			result.setResult(r.getData() == null ? "" : r.getData());
			return result;
		}else{
			return re;
		}
	}

	/**
     * 砸金蛋中奖记录
     * @param sessionCode
     * @return
     */
	@RequestMapping(value = "/winRecord",method = RequestMethod.POST)
	public @ResponseBody ResultModel query(String sessionCode) {
		ResultModel result = new ResultModel();
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			Re r = restTemplate.getForObject(myProps.getActicityServer() + winRecord +"?customerId={1}", Re.class,customerId);
			result.setCode(Integer.parseInt(r.get("code").toString()));
			result.setMessage(r.get("message").toString());
			result.setResult(r.getData() == null ? "" : r.getData());
			return result;
		}else{
			return re;
		}
	}
	
	/**
     * 砸金蛋播报
     * @param sessionCode
     * @return
     */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/broadcastEggs",method = RequestMethod.POST)
	public @ResponseBody ResultModel updateStock(StockParamVo vo) {
		ResultModel result = new ResultModel();
		ResultModel re=this.queryUserInfo(vo.getSessionCode());
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			Integer customerId=Integer.parseInt(map.get("customerId").toString());
			String customerName = map.get("nickName").toString();
			String mobile = map.get("mobile").toString();
			Re r = restTemplate.getForObject(myProps.getActicityServer() + broadcastEggs + "?customerId={1}&customerName={2}&mobile={3}", Re.class,customerId,customerName,mobile);
			result.setCode(Integer.parseInt(r.get("code").toString()));
			result.setMessage(r.get("message").toString());
			result.setResult(r.getData() == null ? "" : r.getData());
			return result;
		}else{
			return re;
		}
	}
	
	/**
	 * 查询状态
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_prize_status")
	public @ResponseBody synchronized ResultModel queryPrizeStatus(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getActicityServer() + query_prize_status + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 翻牌动作
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/flip_cards")
	public @ResponseBody synchronized ResultModel flipCards(String sessionCode){
		Integer customerId=0;
		String mobile="";
		if(StringUtils.isBlank(sessionCode)){
			return restTemplate.getForObject(myProps.getActicityServer() + flip_cards + "?customerId={1}&mobile={2}", ResultModel.class,customerId,mobile);
		}else{
			ResultModel re=this.queryUserInfo(sessionCode);
			if(re.getCode()==0){
				Map<String, Object> map=(Map<String, Object>)re.getResult();
				customerId=Integer.parseInt(map.get("customerId").toString());
				mobile=map.get("mobile").toString();
				return restTemplate.getForObject(myProps.getActicityServer() + flip_cards+"?customerId={1}&mobile={2}", ResultModel.class,customerId,mobile);
			}else{
				return re;
			}
		}
	}
	
	/**
	 * 查询中奖播报
	 * @return
	 */
	@RequestMapping(value = "/query_prize_record")
	public @ResponseBody ResultModel queryPrizeRecord(){
		return restTemplate.getForObject(myProps.getActicityServer() + query_prize_record, ResultModel.class);
	}
	
	/**
	 * 查询用户的中奖纪录
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_prize_cust")
	public @ResponseBody ResultModel queryPrizeRecordByCustId(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getActicityServer() + query_prize_cust + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 游客领取奖品
	 * @param orgCode
	 * @param mobile
	 * @param verificationCode
	 * @param channelType
	 * @param recordId
	 * @return
	 */
	@RequestMapping(value = "/pull_down_visitor")
	public @ResponseBody synchronized ResultModel pullDownVisitor(String orgCode,String mobile, String verificationCode,String channelType,int recordId,String fromCus,String openId){
		if (StringUtils.isBlank(orgCode)||StringUtils.isBlank(mobile)||StringUtils.isBlank(verificationCode)||StringUtils.isBlank(channelType)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getActicityServer() + pull_down_visitor + "?orgCode={1}&mobile={2}&verificationCode={3}&channelType={4}&recordId={5}&fromCus={6}&openId={7}", ResultModel.class,orgCode,mobile,verificationCode,channelType,recordId,fromCus,openId);
	}
	
	/**
	 * 保存用户分享app 记录
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/save_share_info")
	public @ResponseBody synchronized ResultModel saveCustShare(Integer customerId){
		return restTemplate.getForObject(myProps.getActicityServer() + save_share_info + "?customerId={1}", ResultModel.class,customerId);
	}
	
	/**
	 * 判断游客是否领券奖品
	 * @param recordId
	 * @return
	 */
	@RequestMapping(value = "/query_visitor_record")
	public @ResponseBody ResultModel queryVisitorByRecordId(int recordId){
		return restTemplate.getForObject(myProps.getActicityServer() + query_visitor_record + "?recordId={1}", ResultModel.class,recordId);
	}
	
	/**
	 * 根据用户id查询签到信息
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/query_sign_record")
	public @ResponseBody ResultModel queryVisitorByRecordId(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getActicityServer() + query_sign_record + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 分享App得金币
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/share_app")
	public @ResponseBody synchronized ResultModel shareApp(String sessionCode, Integer shareType, String stockCode, Integer shareSign){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			String orgCode=map.get("orgCode").toString();
			//用户中心用户表主键用户Id
			if(shareType == 3){//选股分享
				int customerKey=Integer.parseInt(map.get("customerKey").toString());
				return restTemplate.getForObject(myProps.getActicityServer() + save_share + "?customerId={1}&customerKey={2}&orgCode={3}&stockCode={4}", ResultModel.class,customerId,customerKey,orgCode,stockCode);
			}else{
				return restTemplate.getForObject(myProps.getActicityServer() + share_app + "?customerId={1}&shareType={2}&shareSign={3}", ResultModel.class,customerId,shareType,shareSign);
			}
		}else{
			return re;
		}
	}
	
	/**
	 * 进入和讯活页面
	 * @return
	 */
	@RequestMapping(value = "/init_sales")
	public @ResponseBody ResultModel initSales(String openid){
		return restTemplate.getForObject(myProps.getActicityServer() + init_sales + "?openid={1}", ResultModel.class,openid);
	}
	
	/**
	 * 马上抢购
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/buy_sales")
	public @ResponseBody ResultModel buySales(String openid){
		return restTemplate.getForObject(myProps.getActicityServer() + buy_sales + "?openid={1}", ResultModel.class,openid);
	}
}