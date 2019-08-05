package com.lotstock.eddid.route.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置文件自定义属性和值
 * @author David
 *
 */
@Component
@ConfigurationProperties(prefix = "myprops")
public class MyProps {
	//
	private String awsIdpServer;
	// 用户模板
	private String customerServer;
	// 资金模板
	private String fundServer;
	// 金币模块
	private String goldServer;
	// cms模块
	private String cmsServer;
	// 消息模块
	private String messageServer;
	// 新闻模块
	private String newServer;
	// 金赢岛模块
	private String jydServer;
	// 行情竞猜模块
	private String hqjcServer;
	// pc特有模块
    private String pcServer;
    // 播报模块
    private String broadServer;
    // 自选股模块
    private String selfStockServer;
    // 策略模块
    private String strategyServer;
    // 智能选股模块
    private String chooseServer;
    // 统一订阅模块
    private String subscribeServer;
    // 活动模块
    private String acticityServer;
    // 初始化job模块
    private String initJobServer;
    // 兑换商城模块
    private String mallServer;
    // 大转盘模块
    private String turnServer;
    // 选股倒计时job
    private String chooseCountDownJob;
    // 选股倒计时内容
    private String chooseContent;
    // 运营消息推送job
    private String specialJob;
    // 策略倒计时job
    private String strategyJob;
    // 策略倒计时发送内容
    private String strategyContent;
    // 牛人模块
    private String nrServer;
    // 资讯模块
    private String infoServer;
    // 股票分析统计模块
    private String digitalServer;
	// token验证地址
	private String checkTokenServer;
    /**
     * 订阅推送job
     */
    private String subscribeJob;
    
    /**
     * 订阅推送发送内容
     */
    private String subscribeContent;

	/**
	 * F10 7400
	 */
	private String fhServer;
	
	/**
	 * 股票配置 7500
	 */
	private String stockconfigServer;

	public String getStockconfigServer() {
		return stockconfigServer;
	}

	public void setStockconfigServer(String stockconfigServer) {
		this.stockconfigServer = stockconfigServer;
	}

	public String getFhServer() {
		return fhServer;
	}

	public void setFhServer(String fhServer) {
		this.fhServer = fhServer;
	}

	public String getAwsIdpServer() {
		return awsIdpServer;
	}

	public void setAwsIdpServer(String awsIdpServer) {
		this.awsIdpServer = awsIdpServer;
	}

	public String getCheckTokenServer() {
		return checkTokenServer;
	}

	public void setCheckTokenServer(String checkTokenServer) {
		this.checkTokenServer = checkTokenServer;
	}

	public String getCustomerServer() {
		return customerServer;
	}
	public void setCustomerServer(String customerServer) {
		this.customerServer = customerServer;
	}
	public String getFundServer() {
		return fundServer;
	}
	public void setFundServer(String fundServer) {
		this.fundServer = fundServer;
	}
	public String getGoldServer() {
		return goldServer;
	}
	public void setGoldServer(String goldServer) {
		this.goldServer = goldServer;
	}
	public String getCmsServer() {
		return cmsServer;
	}
	public void setCmsServer(String cmsServer) {
		this.cmsServer = cmsServer;
	}
	public String getMessageServer() {
		return messageServer;
	}
	public void setMessageServer(String messageServer) {
		this.messageServer = messageServer;
	}
	public String getNewServer() {
		return newServer;
	}
	public void setNewServer(String newServer) {
		this.newServer = newServer;
	}
	public String getJydServer() {
		return jydServer;
	}
	public void setJydServer(String jydServer) {
		this.jydServer = jydServer;
	}
	public String getHqjcServer() {
		return hqjcServer;
	}
	public void setHqjcServer(String hqjcServer) {
		this.hqjcServer = hqjcServer;
	}
	public String getPcServer() {
		return pcServer;
	}
	public void setPcServer(String pcServer) {
		this.pcServer = pcServer;
	}
	public String getBroadServer() {
		return broadServer;
	}
	public void setBroadServer(String broadServer) {
		this.broadServer = broadServer;
	}
	public String getSelfStockServer() {
		return selfStockServer;
	}
	public void setSelfStockServer(String selfStockServer) {
		this.selfStockServer = selfStockServer;
	}
	public String getStrategyServer() {
		return strategyServer;
	}
	public void setStrategyServer(String strategyServer) {
		this.strategyServer = strategyServer;
	}
	public String getChooseServer() {
		return chooseServer;
	}
	public void setChooseServer(String chooseServer) {
		this.chooseServer = chooseServer;
	}
	public String getSubscribeServer() {
		return subscribeServer;
	}
	public void setSubscribeServer(String subscribeServer) {
		this.subscribeServer = subscribeServer;
	}
	public String getActicityServer() {
		return acticityServer;
	}
	public void setActicityServer(String acticityServer) {
		this.acticityServer = acticityServer;
	}
	public String getInitJobServer() {
		return initJobServer;
	}
	public void setInitJobServer(String initJobServer) {
		this.initJobServer = initJobServer;
	}
	public String getMallServer() {
		return mallServer;
	}
	public void setMallServer(String mallServer) {
		this.mallServer = mallServer;
	}
	public String getChooseCountDownJob() {
		return chooseCountDownJob;
	}
	public void setChooseCountDownJob(String chooseCountDownJob) {
		this.chooseCountDownJob = chooseCountDownJob;
	}
	public String getChooseContent() {
		return chooseContent;
	}
	public void setChooseContent(String chooseContent) {
		this.chooseContent = chooseContent;
	}
	public String getTurnServer() {
		return turnServer;
	}
	public void setTurnServer(String turnServer) {
		this.turnServer = turnServer;
	}
	public String getSpecialJob() {
		return specialJob;
	}
	public void setSpecialJob(String specialJob) {
		this.specialJob = specialJob;
	}
	public String getStrategyJob() {
		return strategyJob;
	}
	public void setStrategyJob(String strategyJob) {
		this.strategyJob = strategyJob;
	}
	public String getStrategyContent() {
		return strategyContent;
	}
	public void setStrategyContent(String strategyContent) {
		this.strategyContent = strategyContent;
	}
	public String getNrServer() {
		return nrServer;
	}
	public void setNrServer(String nrServer) {
		this.nrServer = nrServer;
	}
	public String getInfoServer() {
		return infoServer;
	}
	public void setInfoServer(String infoServer) {
		this.infoServer = infoServer;
	}
	public String getDigitalServer() {
		return digitalServer;
	}
	public void setDigitalServer(String digitalServer) {
		this.digitalServer = digitalServer;
	}
	public String getSubscribeJob() {
		return subscribeJob;
	}
	public void setSubscribeJob(String subscribeJob) {
		this.subscribeJob = subscribeJob;
	}
	public String getSubscribeContent() {
		return subscribeContent;
	}
	public void setSubscribeContent(String subscribeContent) {
		this.subscribeContent = subscribeContent;
	}

	/**
	 * Ayers Server
	 */
	private String ayersServer;

	public String getAyersServer() {
		return ayersServer;
	}

	public void setAyersServer(String ayersServer) {
		this.ayersServer = ayersServer;
	}
}