<%@ page import="weaver.conn.RecordSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
    String sql = " SELECT xm from " +
            "formtable_main_4 f1 left join   " +
            "(select xm from View_ProjectFfileSVN where xm is not null and  " +
            "wdzt <>7 group by xm ) f2 on (f1.id = f2.xm)  ";

    RecordSet rs  = new RecordSet();

    rs.execute(sql);
    RecordSet recordSet = new RecordSet();

    recordSet.execute("TRUNCATE table uf_legit_record");

    StringBuilder legitDocIdSB = new StringBuilder();  //合规
    StringBuilder validDocIdSB = new StringBuilder();  //合法
    String ss ="";
    while (rs.next()){
        String xm = rs.getString("xm");
        for (int i = 0; i < 7; i++) {
            sql = "select wdlxgjz from uf_key_config where szjd = " + i;
            recordSet.execute(sql);
            String[] keys = new String[0];

            if (recordSet.next()){
                keys = recordSet.getString("wdlxgjz").split(",");
                ss = recordSet.getString("wdlxgjz");
            }

            Set<String> keySet = new HashSet<>();

            sql = "select wdid, wdmc  from View_ProjectFfileSVN  where wdid is not null and xm = " + xm + " and dj = " + i;
            recordSet.execute(sql);
            while (recordSet.next()) {
                String filename = recordSet.getString("wdmc");
                String fileId = recordSet.getString("wdid");
                //判断文档是否合规
                for (String key :
                        keys) {
                    if (filename.lastIndexOf(key) > -1){
                        //计算文档有效数
                        if (!keySet.contains(key)){
                            keySet.add(key);
                            validDocIdSB.append(fileId).append(',');
                        }
                        legitDocIdSB.append(fileId).append(',');
                        break;
                    }
                }
            }
        }
    }
    StringBuilder s1= new StringBuilder();
    String s2="";
    String legitDocId = legitDocIdSB.length()>0?legitDocIdSB.deleteCharAt(legitDocIdSB.length()-1).toString(): "";
    String validDocId = validDocIdSB.length()>0?validDocIdSB.deleteCharAt(validDocIdSB.length()-1).toString(): "";

    //对合格文档进行更新
    //sql = "update dbo.View_ProjectFfileSVN set sfhg = '是' where wdid in(" +legitDocId+") ";
    String[] ids = legitDocId.split(",");
    StringBuilder sqlSB = new StringBuilder();
    sqlSB.append("INSERT INTO uf_legit_record(wdid,sfhg) VALUES");
    int i = 0;
    for (String s :
            ids) {
        sqlSB.append("(").append(s).append(",'是'),");
        i++;
        if (i > 980){
            sql = sqlSB.deleteCharAt(sqlSB.length()-1).toString();
            s1.append(sql).append(";");
            rs.execute(sql);
            sqlSB = new StringBuilder();
            sqlSB.append("INSERT INTO uf_legit_record(wdid,sfhg) VALUES");
            i = 0;
        }
    }
    if (i <= 980) {
        sql = sqlSB.deleteCharAt(sqlSB.length()-1).toString();
        s1.append(sql).append(";");
        rs.execute(sql);
    }

    //对有效文档进行更新
    ids = validDocId.split(",");
    sqlSB = new StringBuilder();
    sqlSB.append("update uf_legit_record set sfyx = '是' where wdid in(");
    for (String s :
            ids) {
        sqlSB.append(s).append(",");
    }
    if (sqlSB.length() > 0){
        sqlSB.deleteCharAt(sqlSB.length()-1);
        sqlSB.append(')');
        sql = sqlSB.toString();
        rs.execute(sql);
        s2 = sql;
    }
%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>update</title>
</head>
<body>

<div><%=ss%></div>
<div><%=s1.toString()%></div>
<div><%=s2%></div>
</body>
</html>

