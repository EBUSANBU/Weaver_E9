package weaversj.x.workflow.action;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaversj.x.csxutil.log.LogUtil;

import java.util.*;

public class DocCheckAction implements Action {
    private LogUtil log = LogUtil.getLogger(DocCheckAction.class.getName());
    private String docID;  //附件字段名
    private String level;  //阶段字段名
    private String projectID; //所属项目字段名

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    @Override
    public String execute(RequestInfo requestInfo) {
        StringBuilder msg = new StringBuilder();
        try {
            msg.append(" 附件字段名 ").append(getDocID()).append(" 阶段字段名 ").append(getLevel()).append(" 所属项目字段名 ").append(getProjectID());
            String docIDValue = "";  //文档id对应的表单值
            String leverValue = "";  //所属阶段对应的表单值
            String pojValue = "";  //所属项目对应的表单值
            //取主表数据
            Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();// 主字段名称
                String value = Util.null2String(properties[i].getValue());// 主字段对应的值
                if (name.equalsIgnoreCase(getDocID())) {
                    docIDValue = value;
                } else if(name.equalsIgnoreCase(getProjectID())) {
                    pojValue = value;
                } else if (name.equalsIgnoreCase(getLevel())){
                    leverValue = value;
                }
            }
            msg.append(" 文档id对应的表单值 ").append(docIDValue).append(" 所属阶段对应的表单值 ").append(leverValue).append(" 所属项目字段名 ").append(getProjectID()).append(" 所属项目对应的表单值 ").append(pojValue);

            String sql;
            RecordSet rs = new RecordSet();

            sql = "select wdlxgjz from uf_key_config where szjd = " + leverValue;
            msg.append(" 获取 关键字 语句 ").append(sql);
            rs.execute(sql);
            String[] keys = new String[0];
            if (rs.next()){
                keys = rs.getString("wdlxgjz").split(",");
            }
            msg.append(" 执行后结果 ").append(Arrays.toString(keys));

            StringBuilder legitDocIdSB = new StringBuilder();  //合规
            StringBuilder validDocIdSB = new StringBuilder();  //合法
            int total = 0,legitCnt = 0,validCnt = 0;  //计数
            Set<String> keySet = new HashSet<>();  //已上传有效文档的关键字集合

            sql = "select wdmc from View_ProjectFfileSVN where xm = " + pojValue + " and sfyx = '是' and dj = " + leverValue;
            msg.append(" 获取 已有效的文档 语句 ").append(sql);
            rs.execute(sql);
            while (rs.next()) {
                String name = rs.getString("wdmc");
                for (String key :
                        keys) {
                    if (name.lastIndexOf(key)>-1){
                        keySet.add(key);
                    }
                }
            }
            msg.append(" 已上传有效文档的关键字集合 ").append(keySet);

            sql = "select id,docsubject from DocDetail where id in ("+docIDValue+") ";
            msg.append(" 获取 附件文件名集合 语句 ").append(sql);
            rs.execute(sql);
            total = rs.getCounts();
            while (rs.next()){
                String filename = rs.getString("docsubject");
                String fileId = rs.getString("id");
                msg.append(" 文件名 ").append(filename).append(" 是否合规 ");
                boolean flag = false;
                //判断文档是否合规
                for (String key :
                        keys) {
                    if (filename.lastIndexOf(key) > -1){
                        msg.append(" 合规 ");
                        legitCnt++;
                        //计算文档有效数
                        if (!keySet.contains(key)){
                            keySet.add(key);
                            validCnt++;
                            validDocIdSB.append(fileId).append(',');
                            msg.append(" key ").append(key).append(" 有效 ");
                        } else {
                            msg.append(" 无效 已存在关键字 ").append(key);
                        }
                        legitDocIdSB.append(fileId).append(',');
                        flag = true;
                        break;
                    }
                }
                if (!flag) msg.append(" 不合规 ");
            }

            String legitDocId = legitDocIdSB.length()>0?legitDocIdSB.deleteCharAt(legitDocIdSB.length()-1).toString(): "";
            String validDocId = validDocIdSB.length()>0?validDocIdSB.deleteCharAt(validDocIdSB.length()-1).toString(): "";

            msg.append(" 附件数 ").append(total)
                    .append(" 合规数 ").append(legitCnt).append(" 附件合规id集合 ").append(legitDocId)
                    .append(" 有效数 ").append(validCnt).append(" 附件有效id集合 ").append(validDocId);

            //对合格文档进行更新
            //sql = "update dbo.View_ProjectFfileSVN set sfhg = '是' where wdid in(" +legitDocId+") ";
            String[] ids = legitDocId.split(",");
            StringBuilder sqlSB = new StringBuilder();
            sqlSB.append("INSERT INTO uf_legit_record(wdid,sfhg) VALUES");
            for (String s :
                    ids) {
                sqlSB.append("(").append(s).append(",'是'),");
            }
            sql = sqlSB.deleteCharAt(sqlSB.length()-1).toString();
            msg.append(" 更新文档合规记录表 合格文档 语句 ").append(sql);
            rs.execute(sql);

            //对有效文档进行更新
            if (!"".equals(validDocId)){
                sql = "update uf_legit_record set sfyx = '是' where wdid in(" +validDocId+") ";
                msg.append(" 更新文档合规记录表 有效文档 语句 ").append(sql);
                rs.execute(sql);
            }
            //将此次上传的文档计算文档有效数和有效率，并更新  -- 废弃 ,改为由 视图【View_ProjectFfileSVNGJK】直接提供
            /*sql = "update dbo.View_ProjectFfileSVNGJK set hgwdsl = hgwdsl +"+legitCnt+",wdyxl= wdyxl + "+(validCnt/10)+" where id = " + poj;
            msg.append(" 更新项目文件视图 语句 ").append(sql);
            rs.execute(sql);*/

            // updeteDoc(legitDocId,)
            // return "1";
            return Action.SUCCESS;
        } catch (Exception e){
            msg.append(" 流程提交失败，附件合规校验action执行失败,请求id[").append(requestInfo.getRequestid()).append("],错误信息如下 ");
            msg.append(log.ex2String(e));
            requestInfo.getRequestManager().setMessageid("90001");
            requestInfo.getRequestManager().setMessagecontent("流程提交失败，附件合规校验action执行失败,请求id["+requestInfo.getRequestid()+"],请联系管理员");
            return Action.FAILURE_AND_CONTINUE;
        } finally {
            log.info(msg.toString());
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println(Arrays.toString("计划,启动会".split(",")));
        System.out.println("报告,汇报".split(",")[0]);
        System.out.println(new String[0].length);
        StringBuilder msg = new StringBuilder();
        Set<String> keySet = new HashSet<>();  //已上传有效文档的关键字集合
        if (!keySet.contains("报告")) keySet.add("报告");
        if (!keySet.contains("汇报")) System.out.println("ye");

        keySet.add("ddd");
        msg.append(keySet);
        System.out.println(msg.toString());
        System.out.println("OA门户(1)".lastIndexOf("表")>-1);

    }
}


