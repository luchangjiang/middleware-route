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

/**
 * 消息
 * @author David
 *
 */
@RestController
@RefreshScope
@RequestMapping("/message")
public class MsgInfoController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 保存设备信息
	private static final String save_device = "/message/save_device";
	// 查询消息汇总列表
	private static final String query_msgRecord = "/message/query_msgRecord";
	// 查询消息列表
	private static final String query_msgInfo = "/message/query_msgInfo";
	// 查询一条消息，后台标记为已读
	private static final String query_oneMsg = "/message/query_oneMsg";
	// 删除消息
	private static final String del_msg = "/message/del_msg";
	
	/**
	 * 保存设备信息
	 * @param userId
	 * @param deviceToken 
	 * @param type 1ios 2android
	 * @param pushType 默认为信鸽，1为个推
	 * @return
	 */
	@RequestMapping(value = "/save_device" ,method = RequestMethod.POST)
	public @ResponseBody
    ResultModel saveDeviceInfo(Integer customerId, String deviceToken, String type, Integer pushType) {
		return restTemplate.getForObject(myProps.getMessageServer() + save_device +"?customerId={1}&deviceToken={2}&type={3}&pushType={4}", ResultModel.class,customerId,deviceToken,type,pushType);
	}
	
	/**
	 * 查询消息汇总列表
	 * @param customerId
	 * @param  language  zh-Hans  简体 zh-Hant  繁体
	 * @return
	 */
	@RequestMapping(value = "/query_msgRecord" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryMsgRecord(Integer customerId,String language) {
		String lang="";
		if (StringUtils.isBlank(language)) {
			lang="zh-Hant";
    	}else{
    		lang=language;
    	}
		return restTemplate.getForObject(myProps.getMessageServer() + query_msgRecord +"?customerId={1}&language={2}", ResultModel.class,customerId,lang);
	}
	
	/**
	 * 查询消息列表
	 * @param customerId
	 * @param type
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_msgInfo" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryMsgInfo(Integer customerId, String type,String lastId, String count,String language) {
		String lang="";
		if (StringUtils.isBlank(language)) {
			lang="zh-Hant";
    	}else{
    		lang=language;
    	}
		return restTemplate.getForObject(myProps.getMessageServer() + query_msgInfo +"?customerId={1}&type={2}&lastId={3}&count={4}&language={5}", ResultModel.class,customerId,type,lastId,count,lang);
	}

	/**
	 * 查询一条消息，后台标记为已读
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/query_oneMsg" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel queryOneMsg(Integer customerId,String infoId,String language) {
		String lang="";
		if (StringUtils.isBlank(language)) {
			lang="zh-Hant";
    	}else{
    		lang=language;
    	}
		return restTemplate.getForObject(myProps.getMessageServer() + query_oneMsg +"?customerId={1}&infoId={2}&language={3}", ResultModel.class,customerId,infoId,lang);
	}

	/**
	 * 删除消息
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/del_msg" ,method = RequestMethod.POST)
	public @ResponseBody ResultModel delMsg(Integer customerId,String infos) {
		return restTemplate.getForObject(myProps.getMessageServer() + del_msg +"?customerId={1}&infos={2}", ResultModel.class,customerId,infos);
	}

}
