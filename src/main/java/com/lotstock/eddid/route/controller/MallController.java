package com.lotstock.eddid.route.controller;

import java.util.Map;

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
@RequestMapping("/mall")
public class MallController extends BaseController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	//领券中心
	private static final String  query_voucherinfo="/mall/query_voucherinfo";
	//保存领券
	private static final String  save_voucherinfo="/mall/save_voucherinfo";
	//领取码用户领券
	private static final String  save_voucher_code="/mall/save_voucher_code";
	//个人领券
	private static final String  query_myvoucherinfo="/mall/query_myvoucherinfo";
	
	//邀请好友得大礼(活动列表)
	private static final String query_welfareinfo = "/mall/query_welfareinfo";
	//邀请好友得大礼 (单条活动详情)
	private static final String query_one_fare = "/mall/query_one_fare";
	
	/**
     * 领券中心
     * @return
     */
    @RequestMapping(value = "/query_voucherinfo" ,method = RequestMethod.POST)
    public @ResponseBody
    ResultModel queryVoucherinfo(String orgCode) {
    	if (StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	return restTemplate.getForObject(myProps.getMallServer() + query_voucherinfo+"?orgCode={1}" , ResultModel.class, orgCode);
    }
    
	/**
	 * 保存领券
	 * @param vouchId
	 * @param customerId
	 * @param mobile
	 * @return
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_voucherinfo" )
	public @ResponseBody ResultModel saveVoucherinfo(String sessionCode,int vouchId){
    	if (StringUtils.isBlank(sessionCode)||vouchId<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			String mobile=map.get("mobile").toString();
			return restTemplate.getForObject(myProps.getMallServer() + save_voucherinfo + "?vouchId={1}&customerId={2}&mobile={3}", ResultModel.class,vouchId,customerId,mobile);
		}else{
			return re;
		}
	}
	
	/**
	 * 领取码用户领券
	 * @param sessionCode
	 * @param captcha
	 * @return
	 */
	@RequestMapping(value = "/save_voucher_code" )
	public @ResponseBody ResultModel saveMyVoucherByCode(String sessionCode,String captcha){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(captcha)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getMallServer() + save_voucher_code + "?customerId={1}&captcha={2}", ResultModel.class,customerId,captcha);
		}else{
			return re;
		}
	}
	
	/**
	 * 个人领券
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_myvoucherinfo" )
	public @ResponseBody ResultModel queryMyVoucher(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getMallServer() + query_myvoucherinfo + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
     * 邀请好友得大礼(活动列表)
     * @return
     */
    @RequestMapping(value = "/query_welfareinfo" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryWelfareInfo() {
    	return restTemplate.getForObject(myProps.getMallServer() + query_welfareinfo , ResultModel.class);
    }
    
    /**
     * 邀请好友得大礼(单条活动详情)
     * @param sessionCode
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/query_one_fare" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryOneFare(String sessionCode,int welId) {
    	if (StringUtils.isBlank(sessionCode)||welId<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryUserInfo(sessionCode);
		if(re.getCode()==0){
			Map<String, Object> map=(Map<String, Object>)re.getResult();
			int customerId=Integer.parseInt(map.get("customerId").toString());
			String mobile=map.get("mobile").toString();
			return restTemplate.getForObject(myProps.getMallServer() + query_one_fare + "?welId={1}&customerId={2}&mobile={3}", ResultModel.class,welId,customerId,mobile);
		}else{
			return re;
		}
    }
    

}
