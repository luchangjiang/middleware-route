package com.lotstock.eddid.route.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lotstock.eddid.route.model.ChooseStockCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChooseCfgUtil {

	@Autowired
	private RedisUtils redisUtils;
	
	/**
	 * 查询选股信息
	 * @param orgCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ChooseStockCfg> queryChooseCfgByOrgCode(String orgCode){
		Map<String,ChooseStockCfg> chooseCfgMap=new HashMap<String,ChooseStockCfg>();
		//从redis 里面获取选股基本配置
		List<Object> objectList = redisUtils.queryHashValue("choose_stock_cfg");
		for(Object object:objectList){
			Map<String, Object> chooseMap = (Map<String, Object>) object;
			if(orgCode.equals(chooseMap.get("orgCode").toString())){
				ChooseStockCfg cfg=new ChooseStockCfg();
				cfg.setCfgId(Integer.parseInt(chooseMap.get("cfgId").toString()));
				cfg.setStockCode(chooseMap.get("stockCode").toString());
				cfg.setStockName(chooseMap.get("stockName").toString());
				cfg.setStatus(Integer.parseInt(chooseMap.get("status").toString()));
				cfg.setOrgCode(chooseMap.get("orgCode").toString());
				cfg.setHavePermissionImg(chooseMap.get("havePermissionImg").toString());
				cfg.setNoPermissionImg(chooseMap.get("noPermissionImg").toString());
				cfg.setRemark(null==chooseMap.get("remark")?"":chooseMap.get("remark").toString());
				cfg.setLink(null==chooseMap.get("link")?"":chooseMap.get("link").toString());
				cfg.setOrderNum(Integer.parseInt(chooseMap.get("orderNum").toString()));
				chooseCfgMap.put(cfg.getStockCode(), cfg);
			}
		}
	    return chooseCfgMap;
	}
	
	/**
	 * 查询选股模型id
	 * @param orgCode
	 * @param strategyCode
	 * @return
	 */
	public Integer queryChooseCfgId(String orgCode,String strategyCode){
		Map<String,ChooseStockCfg> chooseCfgMap=this.queryChooseCfgByOrgCode(orgCode);
		ChooseStockCfg cfg=chooseCfgMap.get(strategyCode);
		return cfg==null?null:cfg.getCfgId();
	}

}
