<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="weaversj.x.sso.GetToken" %>
<%@ page import="weaversj.x.csxutil.log.LogUtil" %>
<%@ page import="weaver.general.Util" %>
<%@ page import="weaver.conn.RecordSet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%

    LogUtil log = LogUtil.getLogger("getTokenJsp");
    String action = Util.null2String(request.getParameter("action"));
    String want = Util.null2String(request.getParameter("want"));
    String name="";
    String workflowid = Util.null2String(request.getParameter("wid"));
    String requestid = Util.null2String(request.getParameter("rid"));
    String isPc = Util.null2String(request.getParameter("isPc"));

    if ( "add".equalsIgnoreCase(action) && !"".equals(want) ){
        String sql = "select id,yqmc from uf_yqjcsz where szgs="+want;
        RecordSet rs = new RecordSet();
        rs.execute(sql);
        if (rs.next()){
            want = rs.getString("id");
            name = rs.getString("yqmc");
        } else {
            log.error("getToken.jsp------want："+want+" 执行的sql："+sql);
            out.write("http://120.77.149.125/login/Login.jsp");
        }
    }
    String wfpc = "http://120.77.149.125/spa/workflow/static4form/index.html?ssoToken=";
    String wfpcC = "#/main/workflow/req?";
    String wfmb ="http://120.77.149.125/spa/workflow/forwardMobileForm.html?ssoToken=";
    String create = "iscreate=1&workflowid="+workflowid+"&want="+want+"&name="+name;
    String doing = "requestid="+requestid;
    if ("add".equalsIgnoreCase(action)){
        String token = getString();
        String url;
        if ("true".equalsIgnoreCase(isPc)) url = wfpc+token+wfpcC+create;
        else url = wfmb+token+"&"+create;
        log.info("getToken.jsp ---------------url:" + url);
        out.write(url);
    } else if ("doing".equalsIgnoreCase(action)){
        String token = getString();
        String url;
        if ("true".equalsIgnoreCase(isPc)) url = wfpc+token+wfpcC+doing;
        else url = wfmb+token+"&"+doing;
        log.info("getToken.jsp ---------------url:" + url);
        out.write(url);
    } else {
        log.error("getToken.jsp------action："+action+" 传值错误");
        out.write("http://120.77.149.125/login/Login.jsp");
    }

%><%!
    private String getString() {
        String url = "http://120.77.149.125/ssologin/getToken";
        Map<String, String> params = new HashMap<>();
        params.put("appid", "workflowsso");  // appid对应\ecology\WEB-INF\prop\weaverloginclient.properties的配置项
        params.put("loginid", "王洋");
        String token = GetToken.doPost(url, params);
        return token.trim();
    }
%>