package com.lotstock.eddid.route.model;

import java.io.Serializable;

/**
 * 交易记录返回数据
 * @author XIONGWEI
 *
 */
public class TradeRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hold_id;
	private String director;
	private String trade_type;
	private String code;
	private Integer market_id;
	private String name;
	private Double price;
	private Integer num;
	private String time;
	private Double fee;
	private Double hold_percent;
	private Integer additionid;
	private Double cost_price;
	private Double earn;
	private Double earn_rate;
	private Integer a_rank;
	private Integer a_count;
	private String begin_time;
	private Double remain_vol;
	private Double hold_used;
	
	/**
	 * 建议仓位
	 */
	private Double weight;
    /**
     * 持仓天数
     */
	private Integer hold_day;
	
	public String getHold_id() {
		return hold_id;
	}
	public void setHold_id(String hold_id) {
		this.hold_id = hold_id;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getMarket_id() {
		return market_id;
	}
	public void setMarket_id(Integer market_id) {
		this.market_id = market_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	public Double getHold_percent() {
		return hold_percent;
	}
	public void setHold_percent(Double hold_percent) {
		this.hold_percent = hold_percent;
	}
	public Integer getAdditionid() {
		return additionid;
	}
	public void setAdditionid(Integer additionid) {
		this.additionid = additionid;
	}
	public Double getCost_price() {
		return cost_price;
	}
	public void setCost_price(Double cost_price) {
		this.cost_price = cost_price;
	}
	public Double getEarn() {
		return earn;
	}
	public void setEarn(Double earn) {
		this.earn = earn;
	}
	public Double getEarn_rate() {
		return earn_rate;
	}
	public void setEarn_rate(Double earn_rate) {
		this.earn_rate = earn_rate;
	}
	public Integer getA_rank() {
		return a_rank;
	}
	public void setA_rank(Integer a_rank) {
		this.a_rank = a_rank;
	}
	public Integer getA_count() {
		return a_count;
	}
	public void setA_count(Integer a_count) {
		this.a_count = a_count;
	}
	public String getBegin_time() {
		return begin_time;
	}
	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}
	public Double getRemain_vol() {
		return remain_vol;
	}
	public void setRemain_vol(Double remain_vol) {
		this.remain_vol = remain_vol;
	}
	public Double getHold_used() {
		return hold_used;
	}
	public void setHold_used(Double hold_used) {
		this.hold_used = hold_used;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Integer getHold_day() {
		return hold_day;
	}
	public void setHold_day(Integer hold_day) {
		this.hold_day = hold_day;
	}

}
