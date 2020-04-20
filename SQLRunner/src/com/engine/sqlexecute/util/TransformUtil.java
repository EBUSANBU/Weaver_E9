package com.engine.sqlexecute.util;

import com.cloudstore.eccom.result.WeaResultMsg;
import com.engine.sqlexecute.constant.dbenum.SqlTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformUtil {

    public static WeaResultMsg RecordSet2WeaResultMsg(RecordSet_zyz rs, boolean flag, SqlTypeEnum sqlTypeEnum) {
        WeaResultMsg weaResultMsg = new WeaResultMsg(false);
        List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
        Map<String,Object> result = new HashMap<>();
        if(!flag) {
            weaResultMsg.fail(rs.getExceptionMessage());
        } else {
            if (sqlTypeEnum == SqlTypeEnum.SELECT) {
                String[] fieldName = rs.getColumnName();
                while(rs.next()) {
                    Map<String, Object> datasource = new HashMap<String,Object>();
                    for(int i = 0; i < fieldName.length; i++)
                        datasource.put(fieldName[i], rs.getString(fieldName[i]));
                    dataList.add(datasource);
                }
                result.put("data", dataList);
                result.put("total", rs.getCounts());
                weaResultMsg.setDatas(result);
                weaResultMsg.success();
                //	messBean.setPageInfo(new PageInfo(0, rs.getCounts()));
            } else {
                weaResultMsg.success("success");
            }
        }
        return weaResultMsg;
    }


}
