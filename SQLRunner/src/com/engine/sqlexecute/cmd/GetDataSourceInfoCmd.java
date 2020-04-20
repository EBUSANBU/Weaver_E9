package com.engine.sqlexecute.cmd;

import com.engine.common.biz.AbstractCommonCommand;
import com.engine.common.entity.BizLogContext;
import com.engine.core.interceptor.CommandContext;
import com.engine.sqlexecute.constant.dbenum.SqlTypeEnum;
import com.engine.sqlexecute.util.RecordSet_zyz;
import com.engine.sqlexecute.util.TransformUtil;
import weaver.hrm.User;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取数据源信息
 */
public class GetDataSourceInfoCmd extends AbstractCommonCommand<Map<String,Object>> {

    public GetDataSourceInfoCmd() {
    }
    public GetDataSourceInfoCmd(User user, Map<String,Object> params) {
        this.user = user;
        this.params = params;
    }

    @Override
    public BizLogContext getLogContext() {
        return null;
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        Map<String, Object> apiData = new HashMap<>();

        RecordSet_zyz rs = new RecordSet_zyz();

        String sql = "select pointid, type from datasourcesetting";

        boolean flag = rs.execute(sql);

        apiData = TransformUtil.RecordSet2WeaResultMsg(rs, flag, SqlTypeEnum.SELECT).getResultMapAll();
        return apiData;

    }


}
