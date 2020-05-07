package weaversj.x.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import weaver.conn.RecordSet;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaversj.x.csxutil.http.HttpUtil;
import weaversj.x.csxutil.log.LogUtil;
import weaversj.x.workflow.action.log.ActionLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 金蝶凭证集成action
 */
public class CredentialIntegrationAction implements Action {

    /**
     * select workflow_billdetailtable.TABLENAME as dt from workflow_billdetailtable, workflow_bill where workflow_billdetailtable.billid = workflow_bill.ID and workflow_bill.tablename = 'formtable_main_181'
     */

    private static final String APP_TOKEN_URL = "https://yztz2018.test.kdcloud.com/api/getAppToken.do";
    private static final String LOGIN_URL = "https://yztz2018.test.kdcloud.com/api/login.do";
    private static final String PZ_URI = "https://yztz2018.test.kdcloud.com/kapi/sys/gl_voucher/save";
    private static final LogUtil log = LogUtil.getLogger(CredentialIntegrationAction.class.getName());



    private String appId = "asstacttype_add";
    private String appSecuret =  "123456";
    private String tenantid = "yztz";


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecuret() {
        return appSecuret;
    }

    public void setAppSecuret(String appSecuret) {
        this.appSecuret = appSecuret;
    }

    public String getTenantid() {
        return tenantid;
    }

    public void setTenantid(String tenantid) {
        this.tenantid = tenantid;
    }


    @Override
    public String execute(RequestInfo requestInfo) {
//        String wid = requestInfo.getWorkflowid();
        //String rid = requestInfo.getRequestid();
        StringBuilder msg = new StringBuilder();
        msg.append(" <br/>第三方appId: ").append(appId)
                .append(" 第三方app的密码appSecuret: ").append(appSecuret)
                .append(" 租户ID tenantid").append(tenantid);
        try {
            String appToken = getAppToken(msg,requestInfo);
            String errorMSG;
            if (!appToken.startsWith("success_")){
                errorMSG = appToken.substring(appToken.lastIndexOf("_")+1);
                //System.out.println(errorMSG);
                setError(requestInfo, msg, errorMSG);
            } else {
                appToken = appToken.substring(appToken.lastIndexOf("_")+1);
                //System.out.println(appToken);
                msg.append("  <br/>appToken: ").append(appToken);
                String accessToken = getAccessToken("17670742505",appToken,msg,requestInfo);
                if (!accessToken.startsWith("success_")){
                    errorMSG = accessToken.substring(accessToken.lastIndexOf("_")+1);
                    //System.out.println(errorMSG);
                    setError(requestInfo, msg, errorMSG);
                } else {
                    accessToken = accessToken.substring(accessToken.lastIndexOf("_")+1);
                   // System.out.println(accessToken);
                    msg.append("  <br/>accessToken: ").append(accessToken);
                 /* String wid = requestInfo.getWorkflowid();
                    RecordSet rs = new RecordSet();
                    rs.execute("select * from uf_wf_table where lcid="+wid);
                    rs.next();
                    String type = rs.getString("lcm");
                    msg.append(" <br/>获取所属流程语句： ").append("select * from uf_wf_table where lcid=").append(wid).append(" 结果：").append(type);
                    String json = getData(type);*/
                    String json = getData(null);
                    msg.append(" <br/>拆分后的json数据： ").append(json);
                    Map<String,String> header = new HashMap<>();
                    header.put("accessToken", accessToken);

                    msg.append(" <br/>请求地址：").append(PZ_URI).append(" 请求参数 ").append(json).append(" 请求头：").append(header);
                    Map<String, Object> data = HttpUtil.doPost(PZ_URI,json,header,true);

                    msg.append(" <br/>凭证请求后的数据：").append(data);
                }
            }
            return Action.SUCCESS;
        } catch (Exception e){
            msg.append(" <br/>流程提交失败,请求id[").append(requestInfo.getRequestid()).append("],错误信息如下 ");
            msg.append(log.ex2String(e));
            requestInfo.getRequestManager().setMessageid("90001");
            requestInfo.getRequestManager().setMessagecontent("流程提交失败，请求id["+requestInfo.getRequestid()+"],请联系管理员");
            return Action.FAILURE_AND_CONTINUE;
        } finally {
            log.info(msg.toString());
        }
    }

    private String getData(String type) {
        JSONObject js = new JSONObject();
        js.put("orgnum","035");
        js.put("vouchertype","0001");
        js.put("defdate","2019-09-13");
        JSONArray jsonArray = new JSONArray();
        JSONObject js2 = new JSONObject();
        js2.put("creditori", 0);
        js2.put("localrate",1);
        js2.put("debitlocal", 9684);
        js2.put("creditlocal", 0);
        js2.put("debitori", 9684);
        JSONObject js3 = new JSONObject();
        js3.put("number","4001");
        js2.put("account",js3);
        JSONObject js4 = new JSONObject();
        js4.put("number","CNY");
        js2.put("currency",js4);
        js2.put("edescription","摘要xxxx");
        jsonArray.add(js2);
        js.put("entries",jsonArray);
        JSONObject js5 = new JSONObject();
        js5.put("data",js);
        return js5.toJSONString();
    }

    private void setError(RequestInfo requestInfo, StringBuilder msg, String errorMSG) {
        msg.append(" <br/>流程提交失败,请求id[").append(requestInfo.getRequestid()).append("],错误信息如下 ").append(errorMSG);
        requestInfo.getRequestManager().setMessageid("90001");
        requestInfo.getRequestManager().setMessagecontent("流程提交失败，错误信息[" + errorMSG + "]请求id[" + requestInfo.getRequestid() + "],请联系管理员");
    }

    /**
     * 通过http获取appToken
     * @return 状态_信息
     * 如    error_第三方appId或appSecuret不能为空!
     *      success_1012fad2-f8ec-4c0f-91d9-be079a39ff28
     */
    private String getAppToken(StringBuilder msg,RequestInfo requestInfo){
        JSONObject js = new JSONObject();
        js.put("appId", appId);
        js.put("appSecuret", appSecuret);
        js.put("tenantid", tenantid);

        msg.append(" <br/>请求地址：").append(APP_TOKEN_URL).append(" 请求参数 ").append(js.toString());
        Map<String, Object> data = HttpUtil.doPost(APP_TOKEN_URL,js.toString(),null,false);
        Map<String, Object> rdata = (Map<String,Object>) data.get("content");
        String appToken = (String) rdata.get("state");
        if ("success".equalsIgnoreCase(appToken)){
           /* ActionLog.writeLog(requestInfo.getWorkflowid()
                    ,requestInfo.getRequestid(),
                    "",
                    APP_TOKEN_URL,
                    "1",
                    js.toString(),
                    data.toString());*/
            rdata = (Map<String, Object>)(rdata).get("data");
            appToken += "_"+ rdata.get("app_token");
        } else {
            ActionLog.writeLog(requestInfo.getWorkflowid()
                    ,requestInfo.getRequestid(),
                    "",
                    APP_TOKEN_URL,
                    "0",
                    js.toString(),
                    data.toString());
            appToken +=  "_" + rdata.get("errorMsg");
        }
        return appToken;
    }

    /**
     * access_token
     * @return 状态_信息
     * 如    error_参数错误:形式应该为 用户账号、密码、租户代码、登陆类型和accountId。
     *      success_bruXwKp8KygSHSr0zqu4enPgkHlnh7Y7Ir8hYXGRPjfhYfKoWl3tmEJu3XMdCcJoYhnfCvcW78nQqMyYQ4RTDaWWigPLBbo75nhHzMXA4x6RKVhTnZJcHK0r3fQ3KT8l
     */
    private String getAccessToken(String user, String appToken,StringBuilder msg,RequestInfo requestInfo){
        JSONObject js2 = new JSONObject();
        js2.put("user", user);
        //js2.put("tenantid", tenantid);
        //js2.put("usertype", "Mobile");
        js2.put("apptoken",appToken);

        msg.append(" <br/>请求地址：").append(LOGIN_URL).append(" 请求参数 ").append(js2.toString());

        Map<String, Object> data = HttpUtil.doPost(LOGIN_URL, js2.toString(),null,false);
        Map<String, Object> rdata = (Map<String,Object>) data.get("content");
        String token = (String) rdata.get("state");
        if ("success".equalsIgnoreCase(token)){
            rdata = (Map<String, Object>)(rdata).get("data");
           /* ActionLog.writeLog(requestInfo.getWorkflowid()
                    ,requestInfo.getRequestid(),
                    "",
                    LOGIN_URL,
                    "1",
                    js2.toString(),
                    data.toString());*/
            token += "_"+ rdata.get("access_token");
        } else {
            token +=  "_" + rdata.get("errorMsg");
            ActionLog.writeLog(requestInfo.getWorkflowid()
                    ,requestInfo.getRequestid(),
                    "",
                    LOGIN_URL,
                    "0",
                    js2.toString(),
                    data.toString());
        }
        return token;
        //System.out.println("token"+":"+token);
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        CredentialIntegrationAction cia = new CredentialIntegrationAction();
        //System.out.println(cia.getAppToken());
        //System.out.println(cia.getAccessToken("a2edbe16-3774-43e6-a100-daa81f1b31d0"));
        cia.execute(null);
    }

}
