package com.engine.sqlexecute.cmd;


import com.engine.common.biz.AbstractCommonCommand;
import com.engine.common.entity.BizLogContext;
import com.engine.core.interceptor.CommandContext;
import com.engine.sqlexecute.biz.factory.AbstractSqlEditor;
import com.engine.sqlexecute.biz.factory.SQLEditorFactory;
import com.engine.sqlexecute.constant.dbenum.DBTypeEnum;
import com.engine.sqlexecute.constant.dbenum.SqlTypeEnum;
import com.engine.sqlexecute.util.Base64Util;
import com.engine.sqlexecute.util.RecordSet_zyz;
import com.engine.sqlexecute.util.TransformUtil;
import weaver.general.BaseBean;
import weaver.hrm.User;

import java.util.List;
import java.util.Map;

;

/**
 * 获取历史sql语句 ,只支持获取前top条，不支持分页
 */
public class GetHistorySqlCmd extends AbstractCommonCommand<Map<String,Object>> {


    public GetHistorySqlCmd() {
    }

    public GetHistorySqlCmd(User user, Map<String,Object> params) {
        this.user = user;
        this.params = params;
    }


    @Override
    public BizLogContext getLogContext() {
        return null;
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        Map<String, Object> apiData;
        BaseBean baseBean = new BaseBean();
        RecordSet_zyz rs = new RecordSet_zyz();
        AbstractSqlEditor editor;
        if ((rs.getDBType()).equals("oracle")) {  //本地数据源如果是oracle
            editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.ORACLE);
        } else {
            editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.SQLSERVER);
        }
        baseBean.writeLog("------editor------"+editor);
        String topStr = (String) params.get("top");
        if (topStr == null || topStr.equals("") )
            topStr = "5";
        Integer top = Integer.valueOf(topStr);
        String historySql = editor.selectTableList(top);
        boolean flag = rs.execute(historySql);
        apiData = TransformUtil.RecordSet2WeaResultMsg(rs, flag, SqlTypeEnum.SELECT).getResultMapAll();
        baseBean.writeLog("----------data--"+apiData);
        for(Map<String,Object> map : (List<Map<String, Object>>) ((Map<String,Object>)apiData.get("datas")).get("data")) {
            for(Map.Entry<String, Object> entry : map.entrySet()) {
                map.put(entry.getKey(), Base64Util.decode((String)entry.getValue()));
            }
        }
        baseBean.writeLog("----------data--"+apiData);
        return apiData;

    }
}
