package com.lotstock.eddid.route.controller.subsrcibe;

import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.controller.BaseController;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;
import com.lotstock.eddid.route.vo.PackageReqVo;
import com.lotstock.eddid.route.vo.PayRequestVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@RefreshScope
@RequestMapping("/subscribe")
public class SubsrcibeController extends BaseController {

	protected static final Logger logger = Logger.getLogger(SubsrcibeController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 套餐列表
	private static final String queryPackageList = "/subscribe/queryPackageList";
	// 订购
	private static final String createOrder = "/pay/createOrder";
	// 更新套餐点击次数
	private static final String updateClickNum="/gddSub/updateClickNum";
	
	/**
	 * 套餐列表
	 * @return
	 */
	@RequestMapping("/subscribe/queryPackageList")
	public @ResponseBody ResultModel queryQuotesCode(PackageReqVo bean)throws Exception {
		return restTemplate.getForObject(myProps.getSubscribeServer() + queryPackageList + StrUtils.getParams(BeanUtils.describe(bean)), ResultModel.class);
	}
	
	/**
	 * 订购
	 * @return
	 */
	@RequestMapping("/pay/createOrder")
	public @ResponseBody ResultModel getDealCode(HttpServletRequest request, PayRequestVo bean)throws Exception {
		ResultModel re = super.getCusCustomerInfo(request);
		if (re.getCode() != 0) {
			return re;
		}else{
			JSONObject customerInfo = (JSONObject) re.getResult();
			String orgCode = customerInfo.getString("orgCode");
			String mobile = customerInfo.getString("mobile");
			int customerKey = customerInfo.getInteger("customerKey");
			bean.setCustomerKey(customerKey);
			bean.setCustomerMobile(mobile);
			bean.setOrgCode(orgCode);
			return restTemplate.getForObject(myProps.getSubscribeServer() + createOrder + StrUtils.getParams(BeanUtils.describe(bean)), ResultModel.class);
		}
	}

	/**
	 * 更新套餐点击次数
	 */
	@RequestMapping(value = "/updateClickNum", method = RequestMethod.GET)
	public @ResponseBody
	ResultModel updateClickNum(HttpServletRequest request, String packageCode){
		ResultModel re = super.getCusCustomerInfo(request);
		if (re.getCode() == -1) {
			return re;
		}
		if (StringUtils.isBlank(packageCode)) {
			re.setCode(-1);
			re.setMessage("参数有误");
			re.setResult("");
			return re;
		}
		logger.info("更新套餐点击次数>>>>packageCode:" + packageCode);
		if (re.getCode() == 0) {
			JSONObject customerInfo = (JSONObject) re.getResult();
			int customerId = customerInfo.getInteger("customerId");
			int customerKey = customerInfo.getInteger("customerKey");
			String url = myProps.getSubscribeServer();
			url += updateClickNum + "?customerId={1}&packageCode={2}";
			re = restTemplate.getForObject(url, ResultModel.class, customerKey, packageCode);
			if (re.getCode() == 0) {
				re.setCode(re.getCode());
				re.setMessage(re.getMessage());
				re.setResult(re.getResult());
			}
		}
		return re;
	}
}
