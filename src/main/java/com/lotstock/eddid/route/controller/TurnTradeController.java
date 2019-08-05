package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;
import com.lotstock.eddid.route.vo.PacketReqVo;
import com.lotstock.eddid.route.vo.TurnTableInnerReqVo;
import com.lotstock.eddid.route.vo.TurnTableOuterReqVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * 大转盘
 * @author pengj
 *
 */
@RestController
@RefreshScope
@RequestMapping("/turnTable")
public class TurnTradeController extends BaseController{
	
	protected static final Logger logger = Logger.getLogger(TurnTradeController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	// 内部打开转盘页
	private static final String innerEnter = "/turnTable/innerEnter";
	
	// 内部点击转转盘
	private static final String innerStart = "/turnTable/innerStart";
		
	// 内部点击我的中奖纪录
	private static final String innerMyRecord = "/turnTable/innerMyRecord";
		
	// 外部打开转盘页
	private static final String outerEnter = "/turnTable/outerEnter";
		
	// 外部转盘页点击抽奖
	private static final String outerStart = "/turnTable/outerStart";
	
	// 播报
	private static final String turnTableBroadcast = "/turnTable/turnTableBroadcast";
		
	// 外部转盘点击注册
	private static final String turnTableRegister = "/turnTable/turnTableRegister";
	
	// 外部红包页面打开
	private static final String packetEnter = "/turnTable/packetEnter";
		
	// 点击拆红包
	private static final String packetOpen = "/turnTable/packetOpen";
		
	// 外部红包注册
	private static final String packetRegister = "/turnTable/packetRegister";
		
	// 获取验证码
	private static final String sendCaptcha = "/turnTable/sendCaptcha";
		
	/**
	 * 内部打开转盘页
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/innerEnter",method = RequestMethod.POST)
	public @ResponseBody
    ResultModel innerEnter(TurnTableInnerReqVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setCustomerId(customerId);
			return restTemplate.getForObject(myProps.getTurnServer() + innerEnter + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 内部点击转转盘
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/innerStart",method = RequestMethod.POST)
	public @ResponseBody ResultModel innerStart(TurnTableInnerReqVo vo) throws Exception {
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setCustomerId(customerId);
			return restTemplate.getForObject(myProps.getTurnServer() + innerStart + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 内部点击我的中奖纪录
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/innerMyRecord",method = RequestMethod.POST)
	public @ResponseBody ResultModel innerMyRecord(TurnTableInnerReqVo vo) throws Exception{
		ResultModel re=this.queryCustInfo(vo.getSessionCode());
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			vo.setCustomerId(customerId);
			return restTemplate.getForObject(myProps.getTurnServer() + innerMyRecord + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
		}else{
			return re;
		}
	}
	
	/**
	 * 外部打开转盘页
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/outerEnter",method = RequestMethod.POST)
	public @ResponseBody ResultModel outerEnter(TurnTableOuterReqVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getTurnServer() + outerEnter + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	} 
	
	/**
	 * 外部转盘页点击抽奖
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/outerStart",method = RequestMethod.POST)
	public @ResponseBody ResultModel outerStart(TurnTableOuterReqVo vo) throws Exception {
		return restTemplate.getForObject(myProps.getTurnServer() + outerStart + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	} 
	
	/**
	 * 播报
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/turnTableBroadcast",method = RequestMethod.POST)
	public @ResponseBody ResultModel turnTableBroadcast() throws Exception{
		return restTemplate.getForObject(myProps.getTurnServer() + turnTableBroadcast, ResultModel.class);
	}
	
	/**
	 * 外部转盘点击注册
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/turnTableRegister",method = RequestMethod.POST)
	public @ResponseBody ResultModel turnTableRegister(TurnTableOuterReqVo vo) throws Exception{
		return restTemplate.getForObject(myProps.getTurnServer() + turnTableRegister + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
	/**
	 * 外部红包页面打开
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/packetEnter",method = RequestMethod.POST)
	public @ResponseBody ResultModel packetEnter(PacketReqVo vo) throws Exception{
		return restTemplate.getForObject(myProps.getTurnServer() + packetEnter + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
	/**
	 * 点击拆红包
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/packetOpen",method = RequestMethod.POST)
	public @ResponseBody ResultModel packetOpen(PacketReqVo vo) throws Exception{
		return restTemplate.getForObject(myProps.getTurnServer() + packetOpen + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
	/**
	 * 外部红包注册
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/packetRegister",method = RequestMethod.POST)
	public @ResponseBody ResultModel packetRegister(PacketReqVo vo) throws Exception{
		return restTemplate.getForObject(myProps.getTurnServer() + packetRegister + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
	/**
	 * 获取验证码
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/sendCaptcha",method = RequestMethod.POST)
	public @ResponseBody ResultModel sendCaptcha(PacketReqVo vo) throws Exception{
		return restTemplate.getForObject(myProps.getTurnServer() + sendCaptcha + StrUtils.getParams(BeanUtils.describe(vo)), ResultModel.class);
	}
	
}
