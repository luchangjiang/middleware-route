package com.lotstock.eddid.route.controller;

import java.util.Map;

import com.lotstock.eddid.route.util.ResultModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lotstock.eddid.route.config.MyProps;

@RestController
@RefreshScope
@RequestMapping("nr")
public class NrController extends BaseController{
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MyProps myProps;
	
	//首页牛人推荐
	private static final String query_nr_first="/nr/query_nr_first";
	//首页动态查询
	private static final String query_dynamic_first="/nr/query_dynamic_first";
	//上传身份证
	private static final String save_idcard_img="/nr/save_idcard_img";
	//申请入驻
	private static final String save_nr_info="/nr/save_nr_info";
	//根据股多多用户id查
	private static final String query_nr_gddcustid="/nr/query_nr_gddcustid";
	//查询牛人列表
	private static final String query_nr_info="/nr/query_nr_info";
	//根据牛人id查询
	private static final String query_nrbyid="/nr/query_nrbyid";
	// 关注
	private static final String save_nr_concern = "/nr/save_nr_concern";
	//保存点赞信息
	private static final String save_nr_like="/nr/save_nr_like";
	//保存评论信息
	private static final String save_nr_review="/nr/save_nr_review";
	//牛人动态
	private static final String query_dynamic_nrid="/nr/query_dynamic_nrid";
	//查询观点，锦囊 
	private static final String query_nr_kits="/nr/query_nr_kits";
	//预览单条信息
	private static final String query_one_dynamic="/nr/query_one_dynamic";
	//锦囊订购
	private static final String save_nr_kits_record="/nr/save_nr_kits_record";
	//观点打赏
	private static final String save_nr_reward="/nr/save_nr_reward";
	//查询直播内容
	private static final String query_nr_message="/nr/query_nr_message";
	//我的关注
	private static final String query_nr_concern="/nr/query_nr_concern";
	//我的锦囊
	private static final String query_my_kits="/nr/query_my_kits";
	//锦囊订购记录
	private static final String query_kits_record="/nr/query_kits_record";
	//评论列表
	private static final String query_review="/nr/query_review";
	//热门观点或动态
	private static final String query_hot_dynamic="/nr/query_hot_dynamic";
	//查询最新订阅的6条数据
	private static final String query_kits_top="/nr/query_kits_top";
	//查询高手粉丝互动
	private static final String query_nr_msg="/nr/query_nr_msg";
	//用户评论高手
	private static final String query_nr_review="/nr/query_nr_review";
	//用户关注的高手动态
	private static final String query_cust_msg="/nr/query_cust_msg";
	//高手回复用户信息
	private static final String query_cust_reply="/nr/query_cust_reply";
	//直播打赏
	private static final String save_zb_reward="/nr/save_zb_reward";
	//查询金币列表
	private static final String query_ticket_cfg="/nr/query_ticket_cfg";
	//用户关注粉丝数
	private static final String query_cust_gzfs="/nr/query_cust_gzfs";
	//查询视频
	private static final String query_video="/nr/query_video";
	//只查看老师的
	private static final String query_teacher_msg="/nr/query_teacher_msg";
	
	/**
	 * 首页推荐牛人
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value = "/query_nr_first", method = RequestMethod.POST) 
	public @ResponseBody
    ResultModel queryNrFirst(String orgCode, String sessionCode){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nr_first + "?orgCode={1}&customerId={2}", ResultModel.class,orgCode,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 首页动态查询
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_dynamic_first", method = RequestMethod.POST) 
	public ResultModel queryNrMessage(String sessionCode,int lastId,int count){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_dynamic_first + "?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 上传身份证
	 * @param file
	 * @param suffix
	 * @return
	 */
	@RequestMapping(value = "/save_idcard_img", method = RequestMethod.POST) 
	public ResultModel saveIdcardImg(String file,String suffix){
		MultiValueMap<String, Object> params= new LinkedMultiValueMap<String, Object>();
    	params.add("file", file);
    	params.add("suffix", suffix);
    	//file 内容太长用post 提交，用get方式不行，这个问题查询了半天，要注意。
    	ResponseEntity<ResultModel> responseEntity = restTemplate.postForEntity(myProps.getNrServer() + save_idcard_img, params, ResultModel.class);
    	ResultModel body = responseEntity.getBody();
    	return body;
	}
	
	/**
	 * 申请入驻
	 * @param orgCode 机构编码
	 * @param mobile 手机号码
	 * @param nrName  姓名
	 * @param nrNickname 昵称
	 * @param nrIdcard 身份证号码
	 * @param nrPractice 执业编号
	 * @param nrTag 牛人标签
	 * @param nrRemark 简介
	 * @param nrProperity 特点
	 * @param cardPositive 正面
	 * @param cardContrary 反面
	 * @return
	 */
	@RequestMapping(value = "/save_nr_info", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveNrInfo(String orgCode,String mobile,String nrName,String nrNickname,String nrIdcard,String nrPractice,String nrTag,String nrRemark,String nrProperity,String cardPositive,String cardContrary,String nrImage,Integer gddCustId){
		if (StringUtils.isBlank(orgCode)||StringUtils.isBlank(mobile)|| StringUtils.isBlank(nrName)|| StringUtils.isBlank(nrNickname) ||StringUtils.isBlank(nrIdcard)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		return restTemplate.getForObject(myProps.getNrServer() + save_nr_info + "?orgCode={1}&mobile={2}&nrName={3}&nrNickname={4}&nrIdcard={5}&nrPractice={6}&nrTag={7}&nrRemark={8}&nrProperity={9}&cardPositive={10}&cardContrary={11}&nrImage={12}&gddCustId={13}", ResultModel.class, orgCode, mobile, nrName, nrNickname, nrIdcard, nrPractice, nrTag, nrRemark, nrProperity,cardPositive,cardContrary,nrImage,gddCustId);
	}
	
	/**
	 * 根据股多多用户id查
	 * @param gddCustId
	 * @return
	 */
	@RequestMapping(value = "/query_nr_gddcustid", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryNrBymobile(Integer gddCustId){
		return restTemplate.getForObject(myProps.getNrServer() + query_nr_gddcustid + "?gddCustId={1}", ResultModel.class,gddCustId);
	}
	
	/**
	 * 查询牛人列表
	 * @param orgCode
	 * @param sessionCode
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_nr_info", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryNrInfo(String orgCode,String sessionCode,int lastId, int count){
		if (StringUtils.isBlank(sessionCode)||StringUtils.isBlank(orgCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nr_info + "?orgCode={1}&customerId={2}&lastId={3}&count={4}", ResultModel.class,orgCode,customerId,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 根据id查询
	 * @param nrId
	 * @return
	 */
	@RequestMapping(value = "/query_nrbyid", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryNrbyid(Integer nrId,String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nrbyid + "?nrId={1}&customerId={2}", ResultModel.class,nrId,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 关注
	 * @param nrId
	 * @param sessionCode
	 * @param type 1 关注 0取消关注
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_nr_concern", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveNrConcern(Integer nrId,String sessionCode,Integer type){
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
			String mobile=map.get("mobile").toString();
			String nickName=map.get("nickName").toString();
			String iconUrl=map.get("iconUrl").toString();
			return restTemplate.getForObject(myProps.getNrServer() + save_nr_concern + "?nrId={1}&customerId={2}&mobile={3}&nickName={4}&iconUrl={5}&type={6}", ResultModel.class,nrId,customerId,mobile,nickName,iconUrl,type);
		}else{
			return re;
		}
	}
	
	/**
	 * 保存点赞信息
	 * @param nrId
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_nr_like", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveNrLike(Integer dynamicId,String sessionCode){
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
			String mobile=map.get("mobile").toString();
			String nickName=map.get("nickName").toString();
			String iconUrl=map.get("iconUrl").toString();
			return restTemplate.getForObject(myProps.getNrServer() + save_nr_like + "?dynamicId={1}&customerId={2}&mobile={3}&nickName={4}&iconUrl={5}", ResultModel.class,dynamicId,customerId,mobile,nickName,iconUrl);
		}else{
			return re;
		}
	}
	
	/**
	 * 保存评论信息
	 * @param dynamicId
	 * @param sessionCode
	 * @param content
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_nr_review", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveNrReview(Integer dynamicId,String sessionCode,String content){
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
			String mobile=map.get("mobile").toString();
			String nickName=map.get("nickName").toString();
			String iconUrl=map.get("iconUrl").toString();
			return restTemplate.getForObject(myProps.getNrServer() + save_nr_review + "?dynamicId={1}&customerId={2}&mobile={3}&nickName={4}&iconUrl={5}&content={6}", ResultModel.class,dynamicId,customerId,mobile,nickName,iconUrl,content);
		}else{
			return re;
		}
	}
	
	/**
	 * 查询牛人动态信息
	 * @param nrId
	 * @return
	 */
	@RequestMapping(value = "/query_dynamic_nrid", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryDynamicByNrId(Integer nrId,String sessionCode,int lastId, int count){
		if (StringUtils.isBlank(sessionCode)||null==nrId) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_dynamic_nrid + "?nrId={1}&customerId={2}&lastId={3}&count={4}", ResultModel.class,nrId,customerId,lastId,count);
		}else{
			return re;
		}
		
	}
	
	/**
	 * 查询观点，锦囊 
	 * @param nrId
	 * @param dynamicType 1动态 2观点 3锦囊
	 * @return
	 */
	@RequestMapping(value = "/query_nr_kits", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryNrkits(Integer nrId,String sessionCode,Integer dynamicType,int lastId, int count){
		if (StringUtils.isBlank(sessionCode)||null==nrId) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nr_kits + "?nrId={1}&customerId={2}&dynamicType={3}&lastId={4}&count={5}", ResultModel.class,nrId,customerId,dynamicType,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 预览单条信息
	 * @param dynamicId
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_one_dynamic", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryOneDynamic(Integer dynamicId,String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_one_dynamic + "?dynamicId={1}&customerId={1}", ResultModel.class,dynamicId,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 锦囊订购
	 * @param dynamicId
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_nr_kits_record", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveNrKitsRecord(Integer dynamicId,String sessionCode){
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
			String customerMobile=map.get("mobile").toString();
			String customerName=map.get("nickName").toString();
			String iconUrl=map.get("iconUrl").toString();
			return restTemplate.getForObject(myProps.getNrServer() + save_nr_kits_record + "?dynamicId={1}&customerId={2}&customerMobile={3}&customerName={4}&iconUrl={5}", ResultModel.class,dynamicId,customerId,customerMobile,customerName,iconUrl);
		}else{
			return re;
		}
	}
	
	/**
	 * 打赏
	 * @param dynamicId
	 * @param sessionCode
	 * @param goldNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_nr_reward", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveNrAccount(Integer dynamicId,String sessionCode,Integer goldNum){
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
			String customerMobile=map.get("mobile").toString();
			String customerName=map.get("nickName").toString();
			String iconUrl=map.get("iconUrl").toString();
			return restTemplate.getForObject(myProps.getNrServer() + save_nr_reward + "?dynamicId={1}&customerId={2}&goldNum={3}&customerMobile={4}&customerName={5}&iconUrl={6}", ResultModel.class,dynamicId,customerId,goldNum,customerMobile,customerName,iconUrl);
		}else{
			return re;
		}
	}
	
	/**
	 * 查询直播内容
	 * @param nrId
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_nr_message", method = RequestMethod.POST) 
	public ResultModel queryNrMessage(Integer nrId,int lastId,int count){
		return restTemplate.getForObject(myProps.getNrServer() + query_nr_message + "?nrId={1}&lastId={2}&count={3}", ResultModel.class,nrId,lastId,count);
	}

	/**
	 * 我的关注
	 * @param sessionCode
	 * @return
	 */
	@RequestMapping(value = "/query_nr_concern", method = RequestMethod.POST) 
	public @ResponseBody ResultModel saveNrAccount(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
    	ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nr_concern + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 我的锦囊
	 */
	@RequestMapping(value = "/query_my_kits", method = RequestMethod.POST) 
	public ResultModel queryMyKits(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_my_kits + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 锦囊订购记录
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/query_kits_record", method = RequestMethod.POST) 
	public ResultModel queryKitsRecord(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_kits_record + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 评论列表
	 * @param dynamicId
	 * @return
	 */
	@RequestMapping(value = "/query_review", method = RequestMethod.POST) 
	public ResultModel queryReview(Integer dynamicId,int lastId, int count){
		return restTemplate.getForObject(myProps.getNrServer() + query_review + "?dynamicId={1}&lastId={2}&count={3}", ResultModel.class,dynamicId,lastId,count);
	}
	
	/**
	 * 热门观点或热门锦囊
	 * @param dynamicType
	 * @return
	 */
	@RequestMapping(value = "/query_hot_dynamic", method = RequestMethod.POST) 
	public ResultModel queryHotDynamic(Integer dynamicType,Integer dynamicId){
		return restTemplate.getForObject(myProps.getNrServer() + query_hot_dynamic +"?dynamicType={1}&dynamicId={2}", ResultModel.class,dynamicType,dynamicId);
	}
	
	/**
	 * 查询最新订阅的6条数据
	 * @param dynamicId
	 * @return
	 */
	@RequestMapping(value = "/query_kits_top", method = RequestMethod.POST)
	public ResultModel queryKitsTop(Integer dynamicId){
		return restTemplate.getForObject(myProps.getNrServer() + query_kits_top + "?dynamicId={1}", ResultModel.class,dynamicId);
	}
	
	/**
	 * 查询高手粉丝互动
	 * @param gddCustId
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_nr_msg", method = RequestMethod.POST) 
	public ResultModel queryNrMsg(String sessionCode,int lastId,int count){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nr_msg + "?gddCustId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 查询用户评论高手
	 * @param gddCustId
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_nr_review", method = RequestMethod.POST) 
	public ResultModel queryNrReview(String sessionCode,int lastId,int count){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_nr_review + "?gddCustId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 用户关注的高手动态
	 * @param gddCustId
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_cust_msg", method = RequestMethod.POST) 
	public ResultModel queryCustMsg(String sessionCode,int lastId,int count){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_cust_msg + "?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 高手回复用户的信息
	 * @param gddCustId
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_cust_reply", method = RequestMethod.POST) 
	public ResultModel queryCustReply(String sessionCode,int lastId,int count){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_cust_reply + "?customerId={1}&lastId={2}&count={3}", ResultModel.class,customerId,lastId,count);
		}else{
			return re;
		}
	}
	
	/**
	 * 直播打赏
	 * @param nrId
	 * @param sessionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/save_zb_reward", method = RequestMethod.POST) 
	public @ResponseBody synchronized ResultModel saveZbReward(Integer zbId,String sessionCode,Integer goldNum){
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
			String customerMobile=map.get("mobile").toString();
			String customerName=map.get("nickName").toString();
			String iconUrl=map.get("iconUrl").toString();
			return restTemplate.getForObject(myProps.getNrServer() + save_zb_reward + "?zbId={1}&customerId={2}&goldNum={3}&customerMobile={4}&customerName={5}&iconUrl={6}", ResultModel.class,zbId,customerId,goldNum,customerMobile,customerName,iconUrl);
		}else{
			return re;
		}
	}
	
	/**
	 * 根据类型取金币配置
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/query_ticket_cfg", method = RequestMethod.POST) 
	public ResultModel queryTicketCfg(Integer type){
		return restTemplate.getForObject(myProps.getNrServer() + query_ticket_cfg + "?type={1}", ResultModel.class,type);
	}
	
	/**
	 * 查询用户关注，粉丝数
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/query_cust_gzfs", method = RequestMethod.POST) 
	public @ResponseBody ResultModel queryCustGzfs(String sessionCode){
		if (StringUtils.isBlank(sessionCode)) {
    		ResultModel result=new ResultModel();
    		result.setCode(-1);
    		result.setMessage("参数错误");
    		return result;
    	}
		ResultModel re=this.queryCustInfo(sessionCode);
		if(re.getCode()==0){
			int customerId=(Integer)re.getResult();
			return restTemplate.getForObject(myProps.getNrServer() + query_cust_gzfs + "?customerId={1}", ResultModel.class,customerId);
		}else{
			return re;
		}
	}
	
	/**
	 * 访问视频url
	 * @param fileName
	 * @param suffix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query_video/{fileName}", produces = { "text/html;charset=UTF-8" }, method = RequestMethod.GET)
	public @ResponseBody String queryVideo(@PathVariable("fileName")String fileName){
		String url="";
		String title="";
		ResultModel custModel=restTemplate.getForObject(myProps.getNrServer() + query_video + "?actualFileName={1}", ResultModel.class,fileName);
		if(custModel.getCode()==0){
			Map<String, Object> map = (Map<String, Object>) custModel.getResult();
			url=map.get("url")==null?"":map.get("url").toString();
			title=map.get("title")==null?"":map.get("title").toString();
		}
		String str="<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n  <title>"+title+"</title>\n  <style>\n    html {\n      height: 100%;\n    }\n    body {\n      margin: 0;\n      background: black;\n      display: -webkit-box;\n      display: -webkit-flex;\n      display: -ms-flexbox;\n      display: flex;\n      -webkit-box-orient: vertical;\n      -webkit-box-direction: normal;\n      -webkit-flex-direction: column;\n          -ms-flex-direction: column;\n              flex-direction: column;\n      -webkit-box-pack: center;\n      -webkit-justify-content: center;\n          -ms-flex-pack: center;\n              justify-content: center;\n      height: 100%;\n    }\n    video {\n      width: 100%;\n    }\n  </style>\n</head>\n<body>\n  <video autoplay controls=\"controls\" preload=\"auto\">\n    <source src=\""+url+"\">\n    \u4F60\u7684\u6D4F\u89C8\u5668\u4E0D\u652F\u6301\u64AD\u653E\u89C6\u9891\n  </video>\n</body>\n</html>";
		return str;
	}
	
	/**
	 * 只查看老师的内容
	 * @param nrId
	 * @param lastId
	 * @param count
	 * @return
	 */
	@RequestMapping(value = "/query_teacher_msg", method = RequestMethod.POST)
	public @ResponseBody ResultModel queryNrTeacherMsg(Integer nrId,int lastId,int count){
		return restTemplate.getForObject(myProps.getNrServer() + query_teacher_msg + "?nrId={1}&lastId={2}&count={3}", ResultModel.class,nrId,lastId,count);
	}
	
	
}
