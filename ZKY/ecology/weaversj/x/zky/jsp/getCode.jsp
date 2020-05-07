<%@ page import="weaversj.x.workflow.action.SmsAction" %>
<%@ page import="weaversj.x.csxutil.log.LogUtil" %>
<%@ page import="weaver.sms.SmsService" %>
<%@ page import="java.util.UUID" %>
<%@ page import="weaver.general.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
    String code = Util.null2String(request.getParameter("code"));
    String number = Util.null2String(request.getParameter("number"));
    String sign = Util.null2String(request.getParameter("sign"));
    String msg = "您的验证码："+code+" ,请勿泄露给他人！";
    SmsAction smsAction = new SmsAction();
    LogUtil log = LogUtil.getLogger(SmsService.class.getName());
    log.info("参数：msg:"+msg+"---number:"+number+"----sign:"+sign+"-----------send:"+smsAction.send(String.valueOf(UUID.randomUUID()),number,msg,sign));
%>
<%--
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
</html>--%>
