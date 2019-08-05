package com.lotstock.eddid.route.controller;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.util.ResultModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

import javax.servlet.http.HttpServletRequest;

/**
 * pc 特有接口
 * @author David
 *
 */
@RestController
@RefreshScope
@RequestMapping("/pcinfo")
public class PcInfoController extends BaseController{

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyProps myProps;

	// 查询广告信息
	private static final String query_advert = "/pcinfo/query_advert";
	// 获取用户权限
	private static final String query_permiss = "/pcinfo/query_permiss";
	//无权限提示
	private static final String query_tip = "/pcinfo/query_tip";
	//引导升级
	private static final String query_timingMsg = "/pcinfo/query_timingMsg";
	// 查询服务器相关
	private static final String query_serverinfo = "/pcinfo/query_serverinfo";
	// 查询文字导航
	private static final String query_banner = "/pcinfo/query_banner";
	// 获取底部市场
	private static final String query_bottom = "/pcinfo/query_bottom";
	// 获取工具栏配置文件
	private static final String query_toolbar = "/pcinfo/query_toolbar";
	// 用户行为记录
	private static final String saveUserRecord = "/pcinfo/save_userRecord";
	// 查询新股
	private static final String queryStockList = "/pcinfo/queryStockList";
	//个股决策特权
	private static final String fun_permission = "/pcinfo/fun_permission";
	//文件上传
	private static final String file_upLoad="/pcinfo/file_upLoad";
	//获取节假日
	private static final String getCalendar="/pcinfo/getCalendar";
	//获取可交易品种
	private static final String getExchangeCommodity="/pcinfo/getExchangeCommodity";
	//获取热门商品
	private static final String getHotProduct="/pcinfo/getHotProduct";
	//获取期货相关信息
	private static final String getFuturesInfo="/pcinfo/getFuturesInfo";
	//获取新股信息
	private static final String get_new_shares="/pcinfo/get_new_shares";
	//获取可交易合约代码
	private static final String get_deal_code="/pcinfo/get_deal_code";
	//获取港美股板块
	private static final String query_stock_plate="/pcinfo/query_stock_plate";

	/**
	 * 查询广告信息
	 * @param mdl
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_advert" ,method = RequestMethod.POST)
	public @ResponseBody
    ResultModel queryAdvert(String mdl, String orgCode){
	   if (StringUtils.isBlank(mdl)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getPcServer() + query_advert+"?mdl={1}&orgCode={2}", ResultModel.class,mdl,orgCode);
	}

	/**
	 * 获取用户权限
	 * @param sessionCode
	 * @param mdl
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/query_permiss" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryPermiss(String sessionCode,String mdl, int ptype){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(mdl)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getPcServer() + query_permiss+"?customerId={1}&mdl={2}&ptype={3}", ResultModel.class,customerId,mdl,ptype);
		}else{
			return re;
		}
	}

	/**
	 * 无权限提示
	 * @param sessionCode
	 * @param mdl
	 * @return
	 */
	@RequestMapping(value = "/query_tip" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryTip(HttpServletRequest request, String sessionCode,String mdl){
		ResultModel tokenInfo = super.getCusCustomerInfo(request);
		if(tokenInfo.getCode() == -1){
			return tokenInfo;
		}
		JSONObject customerInfo = (JSONObject) tokenInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		return restTemplate.getForObject(myProps.getPcServer() + query_tip+"?customerId={1}&mdl={2}", ResultModel.class,customerId,mdl);
	}

	/**
	 * 引导升级,消息提示
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_timingMsg" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryTimingMsg(HttpServletRequest request, String sessionCode,String sendinterStr,String regfrom){
		ResultModel tokenInfo = super.getCusCustomerInfo(request);
		if(tokenInfo.getCode() == -1){
			return tokenInfo;
		}
		JSONObject customerInfo = (JSONObject) tokenInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		return restTemplate.getForObject(myProps.getPcServer() + query_timingMsg+"?customerId={1}&sendinterStr={2}&regfrom={3}", ResultModel.class,customerId,sendinterStr,regfrom);
	}

	/**
	 * 查询服务器市场相关信息
	 * @param pKey
	 * @param ser
	 * @param bm
	 * @param um
	 * @param lv
	 * @param urm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_serverinfo" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryServerInfo(HttpServletRequest request,String pKey, Integer ser, Integer bm, Integer um, Integer lv, Integer urm, String sessionCode){
		ResultModel result = super.getCusCustomerInfo(request);
		if(result.getCode() == -1){
			return result;
		}else {
			JSONObject customerInfo = (JSONObject) result.getResult();
			int customerId = customerInfo.getInteger("customerId");
			int customerKey = customerInfo.getInteger("customerKey");
			String mobile = customerInfo.getString("mobile");
			String orgCode = "0001";
//			System.out.println("debug-------cinfo is : " + customerInfo);
			return restTemplate.getForObject(myProps.getPcServer() + query_serverinfo+"?pKey={1}&orgCode={2}&ser={3}&bm={4}&um={5}&lv={6}&urm={7}&customerId={8}&customerKey={9}&mobile={10}", ResultModel.class,pKey,orgCode,ser,bm,um,lv,urm,customerId,customerKey,mobile);
		}
	}

	/**
	 * 获取文字导航
	 * @param sessionCode
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_banner" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryBanner(String sessionCode,String orgCode){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getPcServer() + query_banner+"?customerId={1}&orgCode={2}", ResultModel.class,customerId,orgCode);
		}else{
			return re;
		}
	}

	/**
	 * 获取底部市场
	 * @param sessionCode
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_bottom" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryBottom(String sessionCode,String orgCode){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getPcServer() + query_bottom+"?customerId={1}&orgCode={2}", ResultModel.class,customerId,orgCode);
		}else{
			return re;
		}
	}

	/**
	 * 获取工具栏配置文件
	 * @param sessionCode
	 * @param ver
	 * @return
	 */
	@RequestMapping(value = "/query_toolbar" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryToolBar(String sessionCode,String orgCode,String ver){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getPcServer() + query_toolbar+"?customerId={1}&orgCode={2}&ver={3}", ResultModel.class,customerId,orgCode,ver);
		}else{
			return re;
		}
	}

	/**
	 * 用户行为记录
	 * @return
	 */
	@RequestMapping(value = "/save_userRecord" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel saveUserRecord(HttpServletRequest request,String sessionCode,String data){
		ResultModel tokenInfo = super.getCusCustomerInfo(request);
		if(tokenInfo.getCode() == -1){
			return tokenInfo;
		}
		JSONObject customerInfo = (JSONObject) tokenInfo.getResult();
		int customerId = customerInfo.getInteger("customerId");
		//
        MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
        params.add("customerId", customerId);
        params.add("data", data);
        //data 内容太长用post 提交，用get方式不行，这个问题查询了半天，要注意。
        ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getPcServer() + saveUserRecord, params, ResultModel.class);
        ResultModel body = responseEntity.getBody();
        return body;
	}

	/**
	 * 查询新股
	 * @param listedDay
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/queryStockList" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryStockList(String listedDay, Integer pageIndex,String pageSize){
		return restTemplate.getForObject(myProps.getPcServer() + queryStockList+"?listedDays={1}&pageIndex={2}&pageSize={3}", ResultModel.class,listedDay,pageIndex,pageSize);
	}

	/**
	 * 个股决策特权
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/fun_permission" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel funPermission(String sessionCode){
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
			int groupId=Integer.parseInt(map.get("groupId").toString());
			return restTemplate.getForObject(myProps.getPcServer() + fun_permission+"?customerId={1}&groupId={2}", ResultModel.class,customerId,groupId);
		}else{
			return re;
		}
	}

	/**
	 * 文件上传
	 * @param sessionCode
	 * @param file
	 * @param suffix
	 * @param type 1上传 2下载
	 * @return
	 */
	@RequestMapping(value = "/file_upLoad" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel fileUpLoad(String sessionCode,String file,String suffix,int type){
		if (StringUtils.isBlank(sessionCode)||0==type) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }
		if(1==type){
			if(null==file || null==suffix) {
				ResultModel result=new ResultModel();
				result.setCode(-1);
				result.setMessage("参数错误");
				return result;
			}
		}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
	    	params.add("customerId", customerId);
	    	params.add("file", file);
	    	params.add("suffix", suffix);
	    	params.add("type", type);
	    	//file 内容太长用post 提交，用get方式不行，这个问题查询了半天，要注意。
	    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getPcServer() + file_upLoad, params, ResultModel.class);
	    	ResultModel body = responseEntity.getBody();
	    	return body;
		}else{
			return re;
		}
	}

	/**
	 * 获取节假日
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/getCalendar" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel getCalendar(@RequestBody Map<String, String> paramMap){
		ResultModel result=new ResultModel();
		Integer type=-1;
		Integer fontType=1;
		if (StringUtils.isBlank(paramMap.get("type"))) {
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
	    }else{
	    	type=Integer.parseInt(paramMap.get("type").toString());
	    	if (type < 0 || type > 4){
	    		result.setCode(-1);
	    		result.setMessage("类型错误");
	            return result;
	    	}else{
	    		if(null!=paramMap.get("fontType")){
	    			fontType=Integer.parseInt(paramMap.get("fontType").toString());
	    		}
	    		String dateFrom=paramMap.get("dateFrom");
	    		String dateTo=paramMap.get("dateTo");
	    		return restTemplate.getForObject(myProps.getPcServer() + getCalendar+"?type={1}&fontType={2}&dateFrom={3}&dateTo={4}", ResultModel.class,type,fontType,dateFrom,dateTo);
	    	}
	    }
	}

	@RequestMapping(value = "/getExchangeCommodity",method = RequestMethod.POST)
	public @ResponseBody ResultModel getExchangeCommodity(){
		return restTemplate.getForObject(myProps.getPcServer() + getExchangeCommodity,ResultModel.class);
	}

	@RequestMapping(value = "/getHotProduct",method = RequestMethod.POST)
	public @ResponseBody ResultModel getHotProduct(){
		return restTemplate.getForObject(myProps.getPcServer() + getHotProduct,ResultModel.class);
	}

	/**
	 * 获取期货相关信息
	 * @return
	 */
	@RequestMapping(value = "/getFuturesInfo",method = RequestMethod.POST)
	public @ResponseBody ResultModel getHotProduct(int futuresInType){
		return restTemplate.getForObject(myProps.getPcServer() + getFuturesInfo+"?futuresInType={1}",ResultModel.class,futuresInType);
	}

	/**
	 * 获取新股信息
	 * @return
	 */
	@RequestMapping(value = "/get_new_shares",method = RequestMethod.POST)
	public @ResponseBody ResultModel getNewShares(String language){
		return restTemplate.getForObject(myProps.getPcServer() + get_new_shares+"?language={1}",ResultModel.class,language);
	}

	/**
	 * 获取可交易合约代码
	 * @return
	 */
	@RequestMapping(value = "/get_deal_code",method = RequestMethod.POST)
	public @ResponseBody ResultModel getDealCode(String dealCode){
		return restTemplate.getForObject(myProps.getPcServer() + get_deal_code+"?dealCode={1}",ResultModel.class,dealCode);
	}
	
	/**
	 * 获取可交易合约代码
	 * @return
	 */
	@RequestMapping(value = "/query_stock_plate",method = RequestMethod.POST)
	public @ResponseBody ResultModel queryStockPlate(String marketType, String language){
		return restTemplate.getForObject(myProps.getPcServer() + query_stock_plate+"?marketType={1}&language={2}",ResultModel.class,marketType,language);
	}
}
