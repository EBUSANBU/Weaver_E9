package com.engine.sqlexecute.biz.factory.impl;



import com.engine.sqlexecute.biz.factory.AbstractSqlEditor;
import com.engine.sqlexecute.entity.PageInfo;

public class ORACLESqlEditor extends AbstractSqlEditor {
	@Override
	public String createTable() {
		// TODO Auto-generated method stub
				return "CREATE TABLE history_table_view(id number(8) primary key not  null ,sqltext text, pointid varchar2(1024), type varchar2(100))";
	}
	@Override
	public String countSql(String sql) {
		// TODO Auto-generated method stub
		return sql;
	}
	@Override
	public String addPagination(String sql, PageInfo pageInfo) {
		// TODO Auto-generated method stub
		return sql;
	}

	@Override
	public String ifTable(String table) {
		// TODO Auto-generated method stub
		return "select 1 from user_tables where table_name ='"+table+"'";
	}

	@Override
	public String insertTable(String sql, String pointid, String type) {
		// TODO Auto-generated method stub
		return "insert into history_table_view(sqltext,pointid,type) values('"+sql+ "','"+ pointid +"','"+ type +"')";
	}

	@Override
	public String selectTableList(int top) {
		// TODO Auto-generated method stub
		return "select sqltext,pointid,type from history_table_view  where  rownum <= "+top+" order by id desc";
	}


}
