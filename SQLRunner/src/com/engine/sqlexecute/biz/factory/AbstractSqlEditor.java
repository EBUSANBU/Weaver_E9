package com.engine.sqlexecute.biz.factory;

import com.engine.sqlexecute.entity.PageInfo;

/**
 * sql语句方言化的抽象类
 * @author 陈少鑫
 *
 */
public abstract class AbstractSqlEditor {
	/**
	 *  添加分页语句 
	 * @param sql
	 * @return 修改为适用于特定数据库的sql语句
	 */
	public abstract String addPagination(String sql, PageInfo pageInfo);

	/**
	 *   获取查询条数的sql语句
	 * @param sql
	 * @return 适用于特定数据库的sql语句
	 */
	public abstract String countSql(String sql);
	
	/**
	 *   获取判断是否存在表的sql语句
	 * @param table 表名
	 * @return 适用于特定数据库的sql语句
	 */
	public abstract String ifTable(String table);
	
	
	
	
	/*******************写死，有机会改成根据传入参数动态生成sql*******************/
	/**
	 *     获取插入表的sql语句
	 * @return 适用于特定数据库的sql语句
	 */
	public abstract String insertTable(String sql, String pointid, String type);
	/**
	 *    获取历史表 sql数据的sql语句
	 * @param top 条数
	 * @return 适用于特定数据库的sql语句
	 */
	public abstract String selectTableList(int top);
	
	/**
	 *    获取生成历史表的sql语句
	 * @return 适用于特定数据库的sql语句
	 */
	public abstract String createTable();
}
