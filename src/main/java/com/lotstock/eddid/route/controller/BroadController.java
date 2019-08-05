package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;
import com.lotstock.eddid.route.vo.BroadVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RefreshScope
@RequestMapping("/broad")
public class BroadController extends BaseController{

	protected static final Logger logger = Logger.getLogger(BroadController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;

	// 获取实时播报
	private static final String query_broadcast = "/broad/query_broadcast";
	
	/**
	 * 获取实时播报
	 * @author pengj
	 * @param vo { orgCode }
	 * @return
	 */
	@RequestMapping(value = "/query_broadcast")
	public @ResponseBody
    ResultModel queryBroadcast(BroadVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getBroadServer() + query_broadcast + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
}
