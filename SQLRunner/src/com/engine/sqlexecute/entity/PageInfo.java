package com.engine.sqlexecute.entity;

/**
 * 页的简单信息实体类
 * @author 陈少鑫
 *
 */
public class PageInfo {
	private int pageNum;  //页号
	private int pageSize;  //一页大小
	private int total; //总条数
	
	/**
	 * 获取页号
	 * @return
	 */
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	/**
	 * 获取一页条数
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * 获取总条数
	 * @return
	 */
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	/**
	 * 
	 * @param pageNum 页号
	 * @param pageSize  一页条数
	 * @param total  总条数
	 */
	public PageInfo(int pageNum, int pageSize, int total) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
	}
	public PageInfo(int pageNum, int pageSize) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	
}
