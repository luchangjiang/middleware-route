package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ResultModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RefreshScope
@RequestMapping("/fund")
public class FundController extends BaseController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 查询资金账号信息
	private static final String query_account = "/fund/query_account";
	// 查询资金流水
	private static final String query_account_record = "/fund/query_account_record";
	// pc端查询资金流水
	private static final String pc_query_account_record = "/fund/pc_query_account_record";
	
	/**
     * 查询资金账号信息
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_account" ,method = RequestMethod.POST)
    public @ResponseBody
    ResultModel queryAccount(String sessionCode) {
    	if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + query_account + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
    }
    
    /**
     * 查询资金流水
     * @param sessionCode
     * @return
     */
    @RequestMapping(value = "/query_account_record" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryAccountRecord(String sessionCode,int lastId,int count) {
    	if (StringUtils.isBlank(sessionCode)||lastId<0||count<0) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + query_account_record + "?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
    }
    
    /**
     * pc端查询交易流水
     * @param sessionCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/pc_query_account_record" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel pcQueryAccountRecord(String sessionCode,int pageNo,int pageSize) {
    	if (StringUtils.isBlank(sessionCode)||pageNo<1||pageSize<1) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getFundServer() + pc_query_account_record + "?customerId={1}&pageNo={2}&pageSize={3}", ResultModel.class,customerId,pageNo,pageSize);
		}else{
			return re;
		}
    }
    
}
