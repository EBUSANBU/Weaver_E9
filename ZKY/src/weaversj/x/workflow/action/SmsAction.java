package weaversj.x.workflow.action;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.sms.SmsService;
import weaver.sms.system.emay.YMSmsServiceImpl;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaversj.x.csxutil.log.LogUtil;

import java.util.UUID;

public class SmsAction implements Action {

    private static LogUtil log = LogUtil.getLogger(SmsService.class.getName());

    private String url,serialnum,key,password,isRegist;
    private String sign;
    private String fieldNumber;
    private String fieldMsg;

    public String getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(String fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    public String getFieldMsg() {
        return fieldMsg;
    }

    public void setFieldMsg(String fieldMsg) {
        this.fieldMsg = fieldMsg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String execute(RequestInfo requestInfo) {

        String number = "";
        String msg = "";
        Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
        for (int i = 0; i < properties.length; i++) {

            String name = properties[i].getName();// 主字段名称
            if (name.equalsIgnoreCase(this.getFieldNumber())){
                number =  Util.null2String(properties[i].getValue());// 主字段对应的值
            } else if (name.equalsIgnoreCase(this.getFieldMsg())){
                msg =  Util.null2String(properties[i].getValue());// 主字段对应的值
            }
        }

        String error = "=====》参数信息如下,手机号码："+number+"  短信内容:"+msg+"  签名:"+this.getSign() + "手机号码对于字段：" + this.getFieldNumber()+"短信内容对于字段："+this.getFieldMsg();

        if ( "".equalsIgnoreCase(number)
                ||"".equalsIgnoreCase(msg)
                ||"".equalsIgnoreCase(sign) ){
            log.error(error);
            requestInfo.getRequestManager().setMessageid("90001");
            requestInfo.getRequestManager().setMessagecontent("流程提交失败，手机号码、短信内容、签名不能为空！");
            return Action.FAILURE_AND_CONTINUE;
        }

        if (send(String.valueOf(UUID.randomUUID()),number,msg,this.getSign())){
            return Action.SUCCESS;
        } else {
            requestInfo.getRequestManager().setMessageid("90001");
            requestInfo.getRequestManager().setMessagecontent("短信发送失败，终止流程提交！");
            log.error("短信发送失败，查看ecology大日志这天的日志。"+error);
            return Action.FAILURE_AND_CONTINUE;
        }
    }

    public boolean send(String smsId,String number,String msg, String sign){
        SmsService smsService = null;
        try {
            smsService = setSmsPropertis();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            log.error(e);
            return false;
        }
        if (smsService instanceof YMSmsServiceImpl){
            YMSmsServiceImpl ymSms = (YMSmsServiceImpl) smsService;
            ymSms.setSerialnum(serialnum);
            ymSms.setPassword(password);
            ymSms.setKey(key);
            ymSms.setIsRegist(isRegist);
            ymSms.setUrl(url);
            ymSms.setSign(sign);
            log.info("-------AddSerial------"+ymSms.getAddSerial()
                    +"---------IsRegist----"+ymSms.getIsRegist()
                    +"-------Key------"+ymSms.getKey()
                    +"----Password---------"+ymSms.getPassword()
                    +"------Postion-------"+ymSms.getPostion()
                    +"--------Serialnum-----"+ymSms.getSerialnum()
                    +"----Sign---------"+ymSms.getSign()
                    +"-----Url--------"+ymSms.getUrl());
            return ymSms.sendSMS(smsId,number,msg);
        } else {
            log.error("使用的短信服务商的实现类为："+smsService.getClass().getName());
            return false;
        }

    }
    private SmsService setSmsPropertis( ) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        SmsService smsService = null;
        RecordSet var1 = new RecordSet();
        var1.execute("select val,prop from smspropertis");
        while (var1.next()){
            if ("ecology_sms_class".equalsIgnoreCase(var1.getString("PROP"))){
                smsService = (SmsService) Class.forName(var1.getString("val")).newInstance();
            } else if ("url".equalsIgnoreCase(var1.getString("PROP"))){
                url = var1.getString("val");
            } else if ("serialnum".equalsIgnoreCase(var1.getString("PROP"))){
                serialnum = var1.getString("val");
            } else if ("key".equalsIgnoreCase(var1.getString("PROP"))){
                key = var1.getString("val");
            } else if ("password".equalsIgnoreCase(var1.getString("PROP"))){
                password = var1.getString("val");
            } else if ("isRegist".equalsIgnoreCase(var1.getString("PROP"))){
                isRegist = var1.getString("val");
            } else {
                log.warning("此配置项未使用："+var1.getString("PROP")+" : "+var1.getString("val"));
            }
        }
        return smsService;
    }

}
