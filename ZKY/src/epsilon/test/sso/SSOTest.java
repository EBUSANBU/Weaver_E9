package epsilon.test.sso;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;


public class SSOTest {


    /**
     * 获取token
     */
    public String getToken() {
        String url = "http://120.77.149.125/ssologin/getToken";
        Map<String, String> params = new HashMap<>();
        params.put("appid", "workflowsso");  // appid对应\ecology\WEB-INF\prop\weaverloginclient.properties的配置项
        params.put("loginid", "焦榕昌");
        String s = SSOTest.doPost(url, params);
        System.out.println("token: "+s.trim());
        return s.trim();
    }

    /**
     * 带上token访问ecology页面
     */
    public void visitPage() {
        String token = getToken();
        // String url = "http://192.168.56.1:8080/systeminfo/version.jsp";
         String url = "http://120.77.149.125/systeminfo/version.jsp";
        Map<String, String> params = new HashMap<>();
        params.put("ssoToken", token);
        params.put("workflowid", "2");
        //String s = SSOTest.doPost(url, params);
        //System.out.println(s); // 能成功返回内容
    }

    /**
     * post请求(用于key-value格式的参数)
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map params) {

        BufferedReader in = null;
        try {
            // 定义HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));

            //设置参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));

               // System.out.println(name +"-"+value);
            }
            request.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));//HTTP.UTF_8过时
            System.out.println("请求的方式："+request.getMethod());
            System.out.println("请求的uri："+request.getURI());
            System.out.println("请求的参数："+nvps);
            System.out.println("请求体："+request.getEntity());
            HttpResponse response = httpClient.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator"); //换行符，屏蔽了windows与linux的差异
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }

                in.close();

                return sb.toString();
            } else {   //
                System.out.println("状态码：" + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String doGet(String url){
        BufferedReader in = null;
        try {
            // 定义HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));

            System.out.println("请求的uri："+request.getURI());
            HttpResponse response = httpClient.execute(request);

            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator"); //换行符，屏蔽了windows与linux的差异
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }

                in.close();

                return sb.toString();
            } else {   //
                System.out.println("状态码：" + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, String params) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SSOTest ssoTest = new SSOTest();
        ssoTest.visitPage();

        //doGet("http://192.168.56.1:8080/workflow/request/CreateRequestForward.jsp?ssoToken="+ssoTest.getToken()+"&workflowid=2");
    }

}
