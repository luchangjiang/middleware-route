package com.lotstock.eddid.route.vo;

import java.math.BigDecimal;
import java.util.Date;

public class AnalysisRecVo {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 市场code
     */
    private Integer market;

    /**
     * 买卖类型 1内盘 2外盘 0其它
     */
    private Integer flag;

    private String time;

    /**
     * 价钱
     */
    private BigDecimal price;

    /**
     * 成交量
     */
    private Integer volume;

    private Date creat_time;
    
    private Integer buyNum;
    
    private Integer sellNum;
    
    private Integer buySellNum;
    
    private Integer otherNum;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取股票代码
     *
     * @return code - 股票代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置股票代码
     *
     * @param code 股票代码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取股票名称
     *
     * @return name - 股票名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置股票名称
     *
     * @param name 股票名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取市场code
     *
     * @return market - 市场code
     */
    public Integer getMarket() {
        return market;
    }

    /**
     * 设置市场code
     *
     * @param market 市场code
     */
    public void setMarket(Integer market) {
        this.market = market;
    }

    /**
     * 获取买卖类型 1内盘 2外盘 0其它
     *
     * @return flag - 买卖类型 1内盘 2外盘 0其它
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * 设置买卖类型 1内盘 2外盘 0其它
     *
     * @param flag 买卖类型 1内盘 2外盘 0其它
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 获取价钱
     *
     * @return price - 价钱
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置价钱
     *
     * @param price 价钱
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取成交量
     *
     * @return volume - 成交量
     */
    public Integer getVolume() {
        return volume;
    }

    /**
     * 设置成交量
     *
     * @param volume 成交量
     */
    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    /**
     * @return creat_time
     */
    public Date getCreat_time() {
        return creat_time;
    }

    /**
     * @param creat_time
     */
    public void setCreat_time(Date creat_time) {
        this.creat_time = creat_time;
    }

	public Integer getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(Integer buyNum) {
		this.buyNum = buyNum;
	}

	public Integer getSellNum() {
		return sellNum;
	}

	public void setSellNum(Integer sellNum) {
		this.sellNum = sellNum;
	}

	public Integer getBuySellNum() {
		return buySellNum;
	}

	public void setBuySellNum(Integer buySellNum) {
		this.buySellNum = buySellNum;
	}

	public Integer getOtherNum() {
		return otherNum;
	}

	public void setOtherNum(Integer otherNum) {
		this.otherNum = otherNum;
	}
    
}