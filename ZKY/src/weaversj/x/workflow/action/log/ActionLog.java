package weaversj.x.workflow.action.log;
import org.apache.commons.lang.time.DateFormatUtils;
import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;

import java.util.Date;

/**
 * 用于记录凭证集成第三方接口请求的信息
 */
public class ActionLog {

    /**
     * 将金蝶凭证集成接口访问信息写入建模表单并权限重构
     * @param workflowId 流程id
     * @param requestId 请求id
     * @param workflowNum 流程编号
     * @param url 请求地址
     * @param success 是否成功
     * @param json 请求参数
     * @param result 返回数据
     */
    public static void writeLog(String workflowId,
                                String requestId,
                                String workflowNum,
                                String url,
                                String success,
                                String json,
                                String result){
        RecordSet rs = new RecordSet();
        String sql = "insert into uf_log_record" +
                "(lcid,qqid,lcbh,qqjk,sfcg,fssj,jssj" +
                ",formmodeid,modedatacreatertype,modedatacreatedate,modedatacreatetime,modedatacreater)" +
                "values(" +workflowId+","+requestId+","+workflowNum+","+url+","+success+json+","+result+
                "82,0,"+DateFormatUtils.format(new Date(),"yyyy-MM-dd")
                +","+DateFormatUtils.format(new Date(),"HH:mm:ss")+",0)";
        rs.execute(sql);
        rs.execute("select id from uf_log_record where qqid="+requestId);
        ModeRightInfo modeRightInfo = new ModeRightInfo();
        modeRightInfo.setNewRight(true);
        while (rs.next()){
            modeRightInfo.editModeDataShare(0,82,rs.getInt("id"));
        }
    }


}
