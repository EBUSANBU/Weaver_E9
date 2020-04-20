package com.engine.sqlexecute.biz.factory;


import com.engine.sqlexecute.biz.factory.impl.*;
import com.engine.sqlexecute.constant.dbenum.DBTypeEnum;

/*
 * 获取AbstractSqlEditor工厂类
 */
public class SQLEditorFactory {
	
	public static AbstractSqlEditor getSqlEditor(DBTypeEnum DBType) {
		switch( DBType ) {
			case DB2:
				return new DB2SqlEditor();
			case HANA:
				return new HANASqlEditor();
			case INFORMIX:
				return new INFORMIXSqlEditor();
			case MYSQL:
				return new MYSQLSqlEditor();
			case ORACLE:
				return new ORACLESqlEditor();
			case SQLSERVER:
				return new SQLSERVERSqlEditor();
			default:
				return new SYBASESqlEditor();
		}
	}
}
