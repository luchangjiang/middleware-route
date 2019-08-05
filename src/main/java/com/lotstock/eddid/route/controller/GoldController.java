package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.util.ResultModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

@RestController
@RefreshScope
@RequestMapping("/gold")
public class GoldController extends BaseController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 查询金币，金豆总额
	private static final String query_ticketPea = "/gold/query_ticketPea";
	// 查询金币明细
	private static final String query_ticket = "/gold/query_ticket";
    // 查询金豆明细
	private static final String query_pea = "/gold/query_pea";
	// 查询金币有效期
	private static final String query_effective = "/gold/query_effective";
	// 金币盈利转资产
	private static final String profit_asset = "/gold/profit_asset";
	// 获取金币面额
	private static final String query_goldcfg = "/gold/query_goldcfg";
	// 马上充值
	private static final String pay_money = "/gold/pay_money";
	// pc端查询金币明细
	private static final String pc_query_ticket = "/gold/pc_query_ticket";
	// pc查询金豆明细
	private static final String pc_query_pea = "/gold/pc_query_pea";
	// pc查询金币有效期
	private static final String pc_query_effective = "/gold/pc_query_effective";
	// 查询打折信息
	private static final String query_goldRecharge_custid="/gold/query_goldRecharge_custid";
	// 金币弹框
	private static final String query_gold_pop="/gold/query_gold_pop";
	//关闭弹框
	private static final String close_gold_pop="/gold/close_gold_pop";
	//点击超链接
	private static final String click_gold_pop="/gold/click_gold_pop";
	
	/**
     * 查询金币，金豆总额
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_ticketPea" ,method = RequestMethod.POST)
    public @ResponseBody
    ResultModel queryTicketPea(String sessionCode) {
    	if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + query_ticketPea + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 查询金币明细
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_ticket" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryTicket(String sessionCode,int lastId,int count) {
    	if (StringUtils.isBlank(sessionCode)||lastId<0||count<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + query_ticket+"?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
    }
    
    /**
     * 查询金豆明细
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_pea" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryPea(String sessionCode,int lastId,int count) {
    	if (StringUtils.isBlank(sessionCode)||lastId<0||count<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + query_pea+"?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
    }
    
    /**
     * 查询金币有效期
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_effective" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryEffective(String sessionCode,int lastId,int count) {
    	if (StringUtils.isBlank(sessionCode)||lastId<0||count<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + query_effective+"?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
    }
    
    /**
     * 金币盈利转资产
     * @param sessionCode
     * @param peaNum
     * @return
     */
    @RequestMapping(value = "/profit_asset" ,method = RequestMethod.POST)
    public @ResponseBody synchronized ResultModel profitAsset(String sessionCode,Integer peaNum) {
    	if (StringUtils.isBlank(sessionCode)||peaNum<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + profit_asset+"?customerId={1}&peaNum={2}", ResultModel.class,customerId,peaNum);
		}else{
			return re;
		}
    }
    
    /**
     * 获取金币面额
     * @return
     */
    @RequestMapping(value = "/query_goldcfg" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryGoldCfg(String sessionCode) {
    	Integer customerId=0;
    	if (StringUtils.isBlank(sessionCode)) {
    		customerId=0;
    	}else{
    		ResultModel re=this.queryCustInfo(sessionCode);
    		if(re.getCode()==0){
    			customerId=(Integer)re.getResult();
    		}else{
    			return re;
    		}
    	}
    	return restTemplate.getForObject(myProps.getGoldServer() + query_goldcfg+"?customerId={1}", ResultModel.class,customerId);
    }
    
    /**
     * 马上充值
     * @param sessionCode
     * @param cfgId
     * @return
     */
    @RequestMapping(value = "/pay_money" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel payMoney(String sessionCode, Integer cfgId) {
    	if (StringUtils.isBlank(sessionCode)||null==cfgId) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + pay_money + "?customerId={1}&cfgId={2}", ResultModel.class,customerId,cfgId);
		}else{
			return re;
		}
    }
    
    /**
     * pc查询金币明细
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/pc_query_ticket" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel pcQueryTicket(String sessionCode,int pageNo,int pageSize) {
    	if (StringUtils.isBlank(sessionCode)||pageNo<1||pageSize<1) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + pc_query_ticket+"?customerId={1}&pageNo={2}&pageSize={3}", ResultModel.class,customerId,pageNo,pageSize);
		}else{
			return re;
		}
    }
    
    /**
     * pc查询金豆明细
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/pc_query_pea" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel pcQueryPea(String sessionCode,int pageNo,int pageSize) {
    	if (StringUtils.isBlank(sessionCode)||pageNo<1||pageSize<1) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + pc_query_pea+"?customerId={1}&pageNo={2}&pageSize={3}", ResultModel.class,customerId,pageNo,pageSize);
		}else{
			return re;
		}
    }
    
    /**
     * pc 查询金币有效期
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/pc_query_effective" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel pcQueryEffective(String sessionCode,int pageNo,int pageSize) {
    	if (StringUtils.isBlank(sessionCode)||pageNo<1||pageSize<1) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + pc_query_effective+"?customerId={1}&pageNo={2}&pageSize={3}", ResultModel.class,customerId,pageNo,pageSize);
		}else{
			return re;
		}
    }
    
    /**
	 * 查询打折信息
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_goldRecharge_custid", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryDisNum(String sessionCode,String type) {
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(type)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + query_goldRecharge_custid+"?customerId={1}&type={2}", ResultModel.class,customerId,type);
		}else{
			return re;
		}
	}
	
	/**
	 * 金币弹框
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_gold_pop")
    public @ResponseBody synchronized ResultModel queryGoldPop(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + query_gold_pop+"?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 关闭弹框
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/close_gold_pop")
    public @ResponseBody synchronized ResultModel closeGoldPop(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + close_gold_pop+"?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 点击超链接
	 * @param customerId
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/click_gold_pop")
    public @ResponseBody synchronized ResultModel clickGoldPop(String sessionCode,Integer type){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getGoldServer() + click_gold_pop+"?customerId={1}&type={2}", ResultModel.class,customerId,type);
		}else{
			return re;
		}
	}

}
