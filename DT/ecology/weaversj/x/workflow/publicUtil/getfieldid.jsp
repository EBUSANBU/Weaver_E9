<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="weaver.conn.RecordSet"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="weaver.general.*" %>
<%@ page import="weaver.upgradetool.wscheck.Util" %>

<%
	String workflowId = Util.null2String(request.getParameter("wid"));
	RecordSet rs = new RecordSet();
	JSONObject ids = new JSONObject();
	String sql = "select a.detailtable FROM WORKFLOW_BILLFIELD A LEFT JOIN WORKFLOW_BASE B ON A.BILLID = B.FORMID WHERE B.ID = "+workflowId+" AND (A.DETAILTABLE != '' or A.DETAILTABLE IS NOT NULL) GROUP BY A.Detailtable order by A.Detailtable";
	if(rs.getDBType().equals("oracle")){
		sql = "select a.detailtable FROM WORKFLOW_BILLFIELD A LEFT JOIN WORKFLOW_BASE B ON A.BILLID = B.FORMID WHERE B.ID = "+workflowId+" AND (A.DETAILTABLE != '' or A.DETAILTABLE IS NOT NULL) GROUP BY A.Detailtable order by length(A.Detailtable ),A.Detailtable ";
	}
	rs.execute(sql);
	
	int index = 1;
	List<String> detailList = new ArrayList<String>();
	while(rs.next()){
		if(!"".equals(Util.null2String(rs.getString(1)))){
			detailList.add(rs.getString(1));
		}
	}
	sql = "select  A.ID,A.FIELDNAME,A.Detailtable FROM WORKFLOW_BILLFIELD A LEFT JOIN WORKFLOW_BASE B ON A.BILLID = B.FORMID WHERE B.ID = "+workflowId+" AND (A.DETAILTABLE IS NULL or A.DETAILTABLE = '')";
	rs.execute(sql);
	JSONObject mid = new JSONObject();
	while(rs.next()){
		mid.put(rs.getString(2), "field" +rs.getString(1));
	}
	ids.put("mid", mid);
	for(String dtabel : detailList){
		JSONObject did = new JSONObject();
		sql = "select  A.ID,A.FIELDNAME,A.Detailtable FROM WORKFLOW_BILLFIELD A LEFT JOIN WORKFLOW_BASE B ON A.BILLID = B.FORMID WHERE B.ID = "+workflowId+" AND A.DETAILTABLE='"+dtabel+"'";
		rs.execute(sql);
		while(rs.next()){
			did.put(rs.getString(2), "field" +rs.getString(1));
		}
		ids.put("did"+index, did);
		index++;
	}
	out.print(ids);
	
	
%>


