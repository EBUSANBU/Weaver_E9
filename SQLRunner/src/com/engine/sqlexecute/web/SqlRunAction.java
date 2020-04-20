package com.engine.sqlexecute.web;


import com.alibaba.fastjson.JSONObject;
import com.engine.common.util.ParamUtil;
import com.engine.common.util.ServiceUtil;
import com.engine.sqlexecute.constant.dbenum.DBTypeEnum;
import com.engine.sqlexecute.constant.dbenum.SqlTypeEnum;
import com.engine.sqlexecute.entity.PageInfo;
import com.engine.sqlexecute.service.ISqlRunService;
import com.engine.sqlexecute.service.impl.SqlRunServiceImpl;
import com.weaver.general.BaseBean;
import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class SqlRunAction {

	@GET
	@Path("/getHistorySql")
	@Produces({MediaType.TEXT_PLAIN})
	public String getHistorySql(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		Map<String, Object> apidatas = new HashMap<String, Object>();
		try {
			//获取当前用户
			User user = HrmUserVarify.getUser(request, response);
			apidatas.putAll(getService(user).getHistorySql(ParamUtil.request2Map(request)));
			apidatas.put("api_status", true);
		} catch (Exception e) {
			e.printStackTrace();
			new BaseBean().writeLog(e);
			apidatas.put("api_status", false);
			apidatas.put("api_errormsg", "catch exception : " + e.getMessage());
		}
		return JSONObject.toJSONString(apidatas);
	}


	@GET
	@Path("/getDataSourceInfo")
	@Produces({MediaType.TEXT_PLAIN})
	public String getDataSourceInfo(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		Map<String, Object> apidatas = new HashMap<String, Object>();
		try {
			//获取当前用户
			User user = HrmUserVarify.getUser(request, response);
			apidatas.putAll(getService(user).getDataSourceInfo(ParamUtil.request2Map(request)));
			apidatas.put("api_status", true);
		} catch (Exception e) {
			e.printStackTrace();
			new BaseBean().writeLog(e);
			apidatas.put("api_status", false);
			apidatas.put("api_errormsg", "catch exception : " + e.getMessage());
		}
		return JSONObject.toJSONString(apidatas);
	}
	@POST
	@Path("/executeSql")
	@Produces({MediaType.TEXT_PLAIN})
	public String executeSql(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		PageInfo pageInfo = new PageInfo(Integer.valueOf(request.getParameter("pageNum")),Integer.valueOf(request.getParameter("pageSize")));
		String sql = request.getParameter("sql");
		SqlTypeEnum sqlTypeEnum = getSqlType(sql.trim().substring(0,6));
		
		DBTypeEnum dbType = getDBType(request.getParameter("dbs"));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		params.put("sqlTypeEnum", sqlTypeEnum);
		params.put("dbType", dbType);
		params.put("sql",sql);
		params.put("isCols", request.getParameter("isCols"));
		params.put("pointId", request.getParameter("pointId"));

		Map<String, Object> apidatas = new HashMap<String, Object>();
		try {
			//获取当前用户
			User user = HrmUserVarify.getUser(request, response);
			apidatas.putAll(getService(user).executeSqlAndReturnResult(params));
			apidatas.put("api_status", true);
		} catch (Exception e) {
			e.printStackTrace();
			new BaseBean().writeLog(e);
			apidatas.put("api_status", false);
			apidatas.put("api_errormsg", "catch exception : " + e.getMessage());
		}
		return JSONObject.toJSONString(apidatas);
	}
	
	private DBTypeEnum getDBType(String dbs) {
		dbs = dbs.toUpperCase();
		DBTypeEnum dbType;
		if (dbs.matches("^SQLSERVER[\\s\\S]*")) {
			dbType = DBTypeEnum.SQLSERVER;
		} else if(dbs.matches("^ORACLE[\\s\\S]*")) {
			dbType = DBTypeEnum.ORACLE;
		} else if(dbs.matches("^MYSQL[\\s\\S]*")) {
			dbType = DBTypeEnum.MYSQL;
		} else if(dbs.matches("^DB2[\\s\\S]*")) {
			dbType = DBTypeEnum.DB2;
		} else if(dbs.matches("^SYBASE[\\s\\S]*")) {
			dbType = DBTypeEnum.SYBASE;
		} else if(dbs.matches("^INFORMIX[\\s\\S]*")) {
			dbType = DBTypeEnum.INFORMIX;
		} else if(dbs.matches("^HANA[\\s\\S]*")) {
			dbType = DBTypeEnum.HANA;
		} else {
			dbType = DBTypeEnum.LOCAL;
		}
		return dbType;
	}
	
	private SqlTypeEnum getSqlType(String sql) {
		SqlTypeEnum sqlTypeEnum;
		if ("select".equalsIgnoreCase(sql)) {
			sqlTypeEnum = SqlTypeEnum.SELECT;
		} else if("delete".equalsIgnoreCase(sql)) {
			sqlTypeEnum = SqlTypeEnum.DELETE;
		} else if("insert".equalsIgnoreCase(sql)) {
			sqlTypeEnum = SqlTypeEnum.INSERT;
		} else if("update".equalsIgnoreCase(sql)) {
			sqlTypeEnum = SqlTypeEnum.UPDATE;
		} else 
			sqlTypeEnum = SqlTypeEnum.CREATE;
		new BaseBean().writeLog(sqlTypeEnum);
		return sqlTypeEnum;
	}

	private ISqlRunService getService(User user) {
		return (SqlRunServiceImpl) ServiceUtil.getService(SqlRunServiceImpl.class, user);

	}
}
