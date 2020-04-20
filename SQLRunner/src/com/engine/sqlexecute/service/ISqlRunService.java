package com.engine.sqlexecute.service;


import java.util.Map;

public interface ISqlRunService {
	/**
	 * 获取数据源信息
	 * @return
	 */
	Map<String, Object> getDataSourceInfo(Map<String, Object> params);


	/**
	 * 获取历史sql语句 ,只支持获取前top条，不支持分页
	 * @param top  条数
	 * @return
	 */
	Map<String, Object> getHistorySql(Map<String, Object> params);


	/**
	 * 执行sql并返回信息
	 * @param sql  传入的sql语句
	 * @param pageInfo  分页信息
	 * @param pointId   数据源标识
	 * @param sqlTypeEnum  语句执行类型
	 * @param dbType   数据源类型
	 * @param isCols 是否查询表头
	 * @return
	 */
	Map<String, Object> executeSqlAndReturnResult(Map<String, Object> params);
	
}
