<%@ page import="weaversj.x.workflow.action.SmsAction" %>
<%@ page import="weaversj.x.csxutil.log.LogUtil" %>
<%@ page import="weaver.sms.SmsService" %>
<%@ page import="java.util.UUID" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
    SmsAction smsAction = new SmsAction();
    LogUtil log = LogUtil.getLogger(SmsService.class.getName());
    log.info("-----------send:"+smsAction.send(String.valueOf(UUID.randomUUID()),"13202638769","qweret","亿美"));
    //return smsService.sendSMS(smsId,number,msg);

%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>填写成功</title>
    <style>
        #father {
            width: auto;
            height: auto;
        }

        #son {
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translateX(-50%) translateY(-50%);
        }
    </style>

</head>
<body>

<div id="father">
    <div id="son">您已成功提交申请！</div>
</div>
</body>
</html>