package weaversj.x.csxutil.http;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import weaversj.x.csxutil.log.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

public class HttpUtil {
    //final static String PATH = "/ltc/contract/status";
    private final static LogUtil log = LogUtil.getLogger(HttpUtil.class.getName());

    public static Map<String, Object> doPost(String url, Map<String,String> params, Map<String,String> headers) {
        BufferedReader in = null;
        try {
            // 定义HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));

            //设置参数
            if (null != params){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    String value = String.valueOf(params.get(name));
                    nvps.add(new BasicNameValuePair(name, value));

                    // System.out.println(name +"-"+value);
                }
                request.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));//HTTP.UTF_8过时
            }

            //设置请求头
            setHeaders(headers, request);

            HttpResponse response = httpClient.execute(request);
            return getRS(response,true);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> doPost(String url , String json, Map<String,String> headers, boolean jsonStr) {
        StringBuilder msg = new StringBuilder();
        msg.append("<br/>----url：").append(url).append("<br/>----json:").append(json).append("<br/>----headers:").append(headers);
        BufferedReader in = null;
        try {
            // 定义HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));
            //设置参数
            if (null != json){
                StringEntity s = new StringEntity(json);
                s.setContentEncoding("UTF-8");
                s.setContentType("application/json");//发送json数据需要设置contentType
                request.setEntity(s);//HTTP.UTF_8过时
            }

            //设置请求头
            setHeaders(headers, request);

            HttpResponse response = httpClient.execute(request);
            Map<String,Object> rs = getRS(response,!jsonStr);
            msg.append("<br/>----result:").append(rs);
            //System.out.println(msg.toString());
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("<br/>----error:").append(log.ex2String(e));
            return null;
        } finally {
            log.info(msg.toString());
        }
    }

    private static Map<String, Object> getRS(HttpResponse response, boolean isJson) throws IOException {
        BufferedReader in;
        int code = response.getStatusLine().getStatusCode();

        Map<String, Object> r = new HashMap<>();
        r.put("code",code);
        in = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent(), "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        String NL = System.getProperty("line.separator"); //换行符，屏蔽了windows与linux的差异
        while ((line = in.readLine()) != null) {
            sb.append(line).append(NL);
        }
        in.close();
        if (code == HttpStatus.SC_OK && isJson)
            r.put("content",json2Map(sb.toString()));
        else r.put("concent", sb.toString());
        return r;
    }

    private static void setHeaders(Map<String, String> headers, HttpPost request) {
        if (null != headers) {
            for (Iterator iterator = headers.keySet().iterator(); iterator.hasNext(); ) {
                String name = (String) iterator.next();
                String value = String.valueOf(headers.get(name));
                request.setHeader(name, value);
            }
        }
    }

    /**
     * 将json字符串转为Map结构
     * 如果json复杂，结果可能是map嵌套map
     * @param jsonStr 入参，json格式字符串
     * @return 返回一个map
     */
    public static Map<String, Object> json2Map(String jsonStr) {
        Map<String, Object> map = new HashMap<>();
        if(jsonStr != null && !"".equals(jsonStr)){
            //最外层解析
            JSONObject json = JSONObject.fromObject(jsonStr);
            //System.out.println(json);
            for (Object k : json.keySet()) {
                Object v = json.get(k);
                //System.out.println(json.get(k)+ ":"+v);
                //如果内层还是数组的话，继续解析
                if (v instanceof JSONArray) {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    Iterator it = ((JSONArray) v).iterator();
                    while (it.hasNext()) {
                        JSONObject json2 = (JSONObject) it.next();
                        list.add(json2Map(json2.toString()));
                    }
                    map.put(k.toString(), list);
                } else {
                    map.put(k.toString(), v);
                }
            }
            return map;
        }else{
            return null;
        }
    }

    // 这个json2Map有问题
   /* public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println(json2Map("{\"data\":{\"success\":false,\"needSign\":false,\"needWfAssignPersons\":false,\"cancelWriteLog\":true,\"message\":\"数据校验发现错误！\",\"showMessage\":true,\"billCount\":1,\"successPkIds\":[],\"billNos\":{\"880362680907478016\":\"201601-0011\"},\"validateResult\":{\"validateErrors\":[{\"success\":false,\"needSign\":false,\"needWfAssignPersons\":false,\"cancelWriteLog\":false,\"showMessage\":true,\"billCount\":0,\"successPkIds\":[],\"billNos\":{},\"validatorKey\":\"保存\",\"allErrorInfo\":[{\"pkValue\":880362680907478016,\"entityKey\":\"gl_voucher\",\"dataEntityIndex\":0,\"rowIndex\":0,\"subRowIndex\":0,\"title\":\"保存\",\"message\":\"请填写 “凭证类型”\",\"errorCode\":\"errorcode_001\",\"level\":\"Error\",\"errorLevel\":\"Error\"}]}],\"success\":false,\"message\":\"\",\"errorPkIds\":[880362680907478016],\"errorDataIndexs\":[0]},\"allErrorOrValidateInfo\":[{\"pkValue\":880362680907478016,\"entityKey\":\"gl_voucher\",\"dataEntityIndex\":0,\"rowIndex\":0,\"subRowIndex\":0,\"title\":\"保存\",\"message\":\"请填写 “凭证类型”\",\"errorCode\":\"errorcode_001\",\"level\":\"Error\",\"errorLevel\":\"Error\"}],\"runSecond\":0,\"allErrorInfo\":[]},\"success\":false,\"errorCode\":null,\"message\":null}\n"));
    }*/


}
