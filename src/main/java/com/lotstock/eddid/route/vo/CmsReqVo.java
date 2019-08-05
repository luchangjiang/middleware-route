package com.lotstock.eddid.route.vo;

public class CmsReqVo {
	
	private String item;
	//第几页
    private int page=1;
    //一页显示条数
    private int size=10;
    //最后一条记录id
    private int lastId=0;
    
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getLastId() {
		return lastId;
	}
	public void setLastId(int lastId) {
		this.lastId = lastId;
	}
	
}
