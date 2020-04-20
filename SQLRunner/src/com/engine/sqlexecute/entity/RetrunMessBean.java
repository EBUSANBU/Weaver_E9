package com.engine.sqlexecute.entity;

import java.util.List;
import java.util.Map;

public class RetrunMessBean {

	public int code;
	public String msg;
	//public PageInfo pageInfo;
	public int count;
	public List<Map<String,Object>> data;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


	/*
	 * public PageInfo getPageInfo() { return pageInfo; }
	 * 
	 * public void setPageInfo(PageInfo pageInfo) { this.pageInfo = pageInfo; }
	 */
	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
	
	
}
