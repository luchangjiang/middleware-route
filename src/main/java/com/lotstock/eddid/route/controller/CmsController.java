package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.vo.CmsReqVo;
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
@RequestMapping("/cmsInfo")
public class CmsController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// app 首页3条
	private static final String query_first = "/cmsInfo/query_first";
	// app 资讯查询
	private static final String query_info = "/cmsInfo/query_info";
	// pc资讯查询
	private static final String search = "/cmsInfo/search";
	
	/**
	 * 首页显示
	 * @return
	 */
	@RequestMapping(value = "/query_first" ,method = RequestMethod.POST)
    public @ResponseBody
    ResultModel queryFirst() {
		return restTemplate.getForObject(myProps.getCmsServer() + query_first, ResultModel.class);
	}
	
	/**
	 * app特有
	 * @return
	 */
	@RequestMapping(value = "/query_info" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel queryInfo(CmsReqVo cmsReqVo) {
		String item=cmsReqVo.getItem();
		int lastId=cmsReqVo.getLastId();
		int size=cmsReqVo.getSize();
		return restTemplate.getForObject(myProps.getCmsServer() + query_info +"?item={1}&lastId={2}&size={3}", ResultModel.class,item,lastId,size);
	}
	
	/**
	 * pc资讯查询
	 * @return
	 */
	@RequestMapping(value = "/search" ,method = RequestMethod.POST)
    public @ResponseBody ResultModel Search(CmsReqVo cmsReqVo) {
		String item=cmsReqVo.getItem();
		int page=cmsReqVo.getPage();
		int size=cmsReqVo.getSize();
		return restTemplate.getForObject(myProps.getCmsServer() + search +"?item={1}&page={2}&size={3}", ResultModel.class,item,page,size);
	}

}
