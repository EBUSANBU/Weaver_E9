package com.engine.sqlexecute.service.impl;


import com.engine.core.impl.Service;
import com.engine.sqlexecute.biz.factory.AbstractSqlEditor;
import com.engine.sqlexecute.biz.factory.SQLEditorFactory;
import com.engine.sqlexecute.cmd.ExecuteSqlAndReturnResultCmd;
import com.engine.sqlexecute.cmd.GetDataSourceInfoCmd;
import com.engine.sqlexecute.cmd.GetHistorySqlCmd;
import com.engine.sqlexecute.constant.dbenum.DBTypeEnum;
import com.engine.sqlexecute.service.ISqlRunService;
import com.engine.sqlexecute.util.RecordSet_zyz;

import java.util.Map;

public class SqlRunServiceImpl extends Service implements ISqlRunService {

	/**
	 *  这个建表语句应该放在那个部分？放在service的实现的静态语句块上规范吗
	 */
	static {
		AbstractSqlEditor editor;
		RecordSet_zyz rs = new RecordSet_zyz();
		if ((rs.getDBType()).equals("oracle")) { 
			editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.ORACLE);
		} else {
			editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.SQLSERVER);
		}
		boolean flag = false;
		//获取本地化查询表是否存在语句
		String ifTableSql = editor.ifTable("history_table_view");
		rs.execute(ifTableSql);
		if(rs.next()){
			flag = true;
		}
		if(!flag) {
			//获取本地化生成表语句
			String createTableSql = editor.createTable();
			rs.execute(createTableSql);
		}
	}

	@Override
	public Map<String, Object> getDataSourceInfo(Map<String,Object> params) {
		return commandExecutor.execute(new GetDataSourceInfoCmd(user,params));
	}

	@Override
	public Map<String, Object> getHistorySql(Map<String,Object> params) {
		return commandExecutor.execute(new GetHistorySqlCmd(user,params));
	}

	@Override
	public Map<String, Object> executeSqlAndReturnResult(Map<String,Object> params) {
		return commandExecutor.execute(new ExecuteSqlAndReturnResultCmd(user,params));
	}
}
