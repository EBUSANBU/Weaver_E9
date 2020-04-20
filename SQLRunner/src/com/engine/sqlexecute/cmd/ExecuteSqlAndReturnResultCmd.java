package com.engine.sqlexecute.cmd;

import com.engine.common.biz.AbstractCommonCommand;
import com.engine.common.entity.BizLogContext;
import com.engine.core.interceptor.CommandContext;
import com.engine.sqlexecute.biz.factory.AbstractSqlEditor;
import com.engine.sqlexecute.biz.factory.SQLEditorFactory;
import com.engine.sqlexecute.constant.dbenum.DBTypeEnum;
import com.engine.sqlexecute.constant.dbenum.SqlTypeEnum;
import com.engine.sqlexecute.entity.PageInfo;
import com.engine.sqlexecute.util.Base64Util;
import com.engine.sqlexecute.util.RecordSet_zyz;
import com.engine.sqlexecute.util.TransformUtil;
import weaver.hrm.User;

import java.util.List;
import java.util.Map;

/**
 * 执行sql并返回信息
 */
public class ExecuteSqlAndReturnResultCmd extends AbstractCommonCommand<Map<String,Object>> {
    public ExecuteSqlAndReturnResultCmd() {
    }
    public ExecuteSqlAndReturnResultCmd(User user, Map<String,Object> params) {
        this.user = user;
        this.params = params;
    }

    @Override
    public BizLogContext getLogContext() {
        return null;
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        // TODO Auto-generated method stub
        AbstractSqlEditor editor;
        DBTypeEnum dbType = (DBTypeEnum) params.get("dbType");
        SqlTypeEnum sqlTypeEnum = (SqlTypeEnum) params.get("sqlTypeEnum");
        String sql = (String) params.get("sql");
        PageInfo pageInfo = (PageInfo) params.get("pageInfo");
        String pointId = (String) params.get("pointId");
        boolean isCols = (boolean) params.get("isCols");

        Map<String, Object> apiData;

        RecordSet_zyz rs = new RecordSet_zyz();
        if (dbType == DBTypeEnum.LOCAL) {   //如果是本地数据源
            if ((rs.getDBType()).equals("oracle")) {
                editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.ORACLE);
            } else {
                editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.SQLSERVER);
            }

            if(sqlTypeEnum == SqlTypeEnum.SELECT) {

                String lsql = editor.addPagination(sql, pageInfo); //这句话其实没有用处，以后再补充完整，在数据库分页，而不是在代码层面分页
                //new BaseBean().writeLog(lsql);
                boolean flag = rs.execute(lsql);
                apiData = TransformUtil.RecordSet2WeaResultMsg(rs, flag, sqlTypeEnum).getResultMapAll();

                //在代码层面分页
                pagination(pageInfo, apiData);

                if(!isCols && pageInfo.getPageNum() == 1)  //如果不是查询表头且查询第一页，则插入历史sql语句表里
                    insertIntoHistory(sql, pointId, dbType.toString());
            } else {
                boolean flag = rs.execute(sql);

                apiData = TransformUtil.RecordSet2WeaResultMsg(rs, flag, sqlTypeEnum).getResultMapAll();
                insertIntoHistory(sql, pointId, dbType.toString());
            }

            return apiData;
        }
        System.out.println(pointId);
        //非本地数据源
        editor = SQLEditorFactory.getSqlEditor(dbType);

        if(sqlTypeEnum == SqlTypeEnum.SELECT) {

            String lsql = editor.addPagination(sql, pageInfo); //这句话其实没有用处，以后再补充完整，在数据库分页，而不是在代码层面分页
            //new BaseBean().writeLog(lsql);
            boolean flag = rs.executeSqlWithDataSource(lsql, pointId);

            apiData = TransformUtil.RecordSet2WeaResultMsg(rs, flag, sqlTypeEnum).getResultMapAll();

            //在代码层面分页
            pagination(pageInfo, apiData);

            if(!isCols && pageInfo.getPageNum() == 1)  //如果不是查询表头且查询第一页，则插入历史sql语句表里
                insertIntoHistory(sql, pointId, dbType.toString());
        } else {
            boolean flag = rs.executeSqlWithDataSource(sql, pointId);

            apiData = TransformUtil.RecordSet2WeaResultMsg(rs, flag, sqlTypeEnum).getResultMapAll();
            insertIntoHistory(sql, pointId, dbType.toString());
        }

        return apiData;

    }

    //在代码层面分页
    private void pagination(PageInfo pageInfo, Map<String,Object> apiData) {
        int fromIndex,toIndex;
        fromIndex = (pageInfo.getPageNum()-1)*pageInfo.getPageSize();
        toIndex = pageInfo.getPageNum()*pageInfo.getPageSize();
        List<Map<String,Object>> dataList = (List<Map<String, Object>>) ((Map<String,Object>)apiData.get("datas")).get("data");
        toIndex = toIndex > dataList.size()?dataList.size():toIndex;
        ((Map<String,Object>)apiData.get("datas")).put("data",dataList.subList(fromIndex, toIndex));
    }

    //使用本地的数据源
    private void insertIntoHistory(String sql, String pointId,String dbType) {

        AbstractSqlEditor editor;
        RecordSet_zyz rs = new RecordSet_zyz();
        if ((rs.getDBType()).equals("oracle")) {
            editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.ORACLE);
        } else {
            editor = SQLEditorFactory.getSqlEditor(DBTypeEnum.SQLSERVER);
        }

        //获取本地化插入表语句
        String insertTableSql = editor.insertTable(Base64Util.encode(sql), Base64Util.encode(pointId), Base64Util.encode(dbType));
        rs.execute(insertTableSql);
    }
}
