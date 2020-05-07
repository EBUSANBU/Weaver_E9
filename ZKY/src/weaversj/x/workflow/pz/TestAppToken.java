package weaversj.x.workflow.pz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import weaversj.x.csxutil.http.HttpUtil;


public class TestAppToken {

	public static void main(String[] args){
		String appId = "asstacttype_add";
		String appSecuret =  "123456";
		String tenantid = "yztz";
		String appTokenUrl = "https://yztz2018.test.kdcloud.com/api/getAppToken.do";
		String loginUrl = "https://yztz2018.test.kdcloud.com/api/login.do";
		String appToken = "";

		String token = "";
		String pzUril = "https://yztz2018.test.kdcloud.com/kapi/sys/gl_voucher/save";

		try {

            JSONObject js = new JSONObject();
            js.put("appId", appId);
            js.put("appSecuret", appSecuret);
            js.put("tenantid", tenantid);

			Map<String, Object> data = HttpUtil.doPost(appTokenUrl,js.toString(),null,false);
            appToken = (String) ((Map<String, Object>)((Map<String,Object>)data.get("content")).get("data")).get("app_token");
			//System.out.println("app_token"+":"+appToken);



			JSONObject js2 = new JSONObject();
			js2.put("user", "17670742505");
			js2.put("tenantid", tenantid);
			js2.put("usertype", "Mobile");
			js2.put("apptoken",appToken);

			data = HttpUtil.doPost(loginUrl, js2.toString(),null,false);

			token = (String) ((Map<String, Object>)((Map<String,Object>)data.get("content")).get("data")).get("access_token");
			//System.out.println("token"+":"+token);

			Map<String,String> header = new HashMap<>();
			header.put("accessToken", token);

           // data = HttpUtil.doPost(pzUril,"{\"data\":"+getData()+"}",header);
            data = HttpUtil.doPost(pzUril,getData2(),header,true);
			//System.out.println("登录接口返回信息"+":"+data);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getData2(){
	/*	JSONObject js = new JSONObject();
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
		return js5.toJSONString();*/
	return null;
	}


	public static String getData(){
		int maxRowCount = 1;

		Random itemRnd = new Random();
		// 核算组织的随机定义
		String[] orgNumbers = new String[] { "051", "035", "045", "013" };
		int orgIndex = itemRnd.nextInt(4);
		String orgNum = "\"" + orgNumbers[orgIndex] + "\"";

		// 凭证类型的随机定义
		String[] vouchertypeNumbers = new String[] { "0001", "0002", "0003" };
		int vouchertypeIndex = itemRnd.nextInt(3);
		String vouchertypeNum = "\"" + vouchertypeNumbers[vouchertypeIndex] + "\"";

		// 业务日期随机生成
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate;
		Date endDate;

		long lStartDate = 0;
		long lEndDate = 0;
		long deltaDate = 0;

		try {
			startDate = format.parse("2019-01-01");
			endDate = format.parse("2019-12-31");

			lStartDate = startDate.getTime();
			lEndDate = endDate.getTime();
			deltaDate = lEndDate - lStartDate;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		String vchDate = "\"" + format.format(new Date(lStartDate + (long) (Math.random() * deltaDate))) + "\"";

		// 不带核算维度的科目
		String[] acctNumbers = new String[] { "1001.01", "1002.01.01.02", "1602.05", "1131", "6001", "6401", "6603", "4001", "2001", "2206" };
		// 核算维度1名字
		String assist1 = "客户";
		// 带核算维度1的科目
		String[] acctNumbersAssist1 = new String[] { "1121", "1122" };
		String[] assist1Numbers = new String[] { "CUST000001" };
		// 核算维度2名字
		String assist2 = "供应商";
		// 带核算维度2的科目
		String[] acctNumbersAssist2 = new String[] { "2201", "2202.01", "2202.02", "2209.01", "2209.02" };
		String[] assist2Numbers = new String[] { "SUP000003" };

		StringBuilder enries = new StringBuilder();
		enries.append("[");
		for (int j = 0; j < maxRowCount; j++) {

			if (j > 0) {
				enries.append(",");
			}

			String twoEntries = "";
			for (int i = 0; i < 2; i++) {
				int accounttype = itemRnd.nextInt(3);
				String accountnum = "";
				String assgrpStr = "";
				int accountindex = 0;
				switch (accounttype) {
					case 1:
						accountindex = itemRnd.nextInt(acctNumbersAssist1.length);
						accountnum = acctNumbersAssist1[accountindex];
						int assist1Index = itemRnd.nextInt(assist1Numbers.length);
						assgrpStr = "\"assgrp\": {\"" + assist1 + "\": {\"number\": \"" + assist1Numbers[assist1Index] + "\"}}," ;
						break;
					case 2:
						accountindex = itemRnd.nextInt(acctNumbersAssist2.length);
						accountnum = acctNumbersAssist2[accountindex];
						int assist2Index = itemRnd.nextInt(assist2Numbers.length);
						assgrpStr = "\"assgrp\": {\"" + assist2 + "\": {\"number\": \"" + assist2Numbers[assist2Index] + "\"}}," ;
						break;
					case 0:
						accountindex = itemRnd.nextInt(acctNumbers.length);
						accountnum = acctNumbers[accountindex];
						break;
				}
				int money = itemRnd.nextInt(10000) + 1;
				String accountStr = "\"account\": {\"number\": \"" + accountnum + "\"},";
				if (i == 0) {
					twoEntries += "{\"edescription\": \"摘要xxxx\"," + accountStr + assgrpStr
							+ "\"currency\": {\"number\": \"CNY\"}," + "\"debitori\": " + money + ","
							+ "\"creditori\": 0," + "\"localrate\": 1," + "\"debitlocal\": " + money + ","
							+ "\"creditlocal\": 0},";
				} else {
					twoEntries += "{\"edescription\": \"摘要yyyy\"," + accountStr + assgrpStr
							+ "\"currency\": {\"number\": \"CNY\"}," + "\"debitori\": 0," + "\"creditori\": " + money
							+ "," + "\"localrate\": 1," + "\"debitlocal\": 0," + "\"creditlocal\": " + money + "}";
				}
			}
			enries.append(twoEntries);
		}
		enries.append("]");

		JSONObject js3 = new JSONObject();
		js3.put("orgnum", orgNum);
		js3.put("defdate", vchDate);
		js3.put("vouchertype", vouchertypeNum);
		js3.put("entries",enries.toString());
		return js3.toString();
	}
}
