/**
 * Title:        数据库语句执行类
 * Company:      泛微软件
 * @author:      刘煜
 * @version:     1.0
 * create date : 2001-10-23
 * modify log: 
 *
 *
 * Description:  采用 java.sql 中的 CallableStatement 和 Statement 执行数据库操作。
 *               客户端直接调用该类进行数据库操作。不需要考虑数据库链接的建立.
 *
 *               客户端指所有调用该类进行数据库操作的应用程序，不特指用户的客户端。
 *               
 *               RecordSet 执行数据库操作有两种形式，一种为调用存储过程，另一种为直接执行SQL语句
 *               与ConnStatement不同 ，RecordSet 执行SQL语句不分查询和修改，都在一条语句中执行
 *               1、使用默认的链接池执行SQL语句：   
 *                  RecordSet rs = new RecordSet() ;
 *	                rs.executeSql(" select * from TB_Example ") ;
 *	                while( rs.next() ) {
 *                      String thename = rs.getString("name") ;
 *                      ......
 *                  }
 *               2、使用指定的链接池ecologytest执行SQL语句
 *                  RecordSet rs = new RecordSet() ;
 *	                rs.executeSql(" update TB_Example set name = 'the new value' " , "ecologytest" ) ;
 *
 *               3、使用指定的链接池ecologytest执行存储过程 PD_Example_UpdateById
 *                   存储过程PD_Example_UpdateById：
 *                   CREATE  PROCEDURE [PD_Example_UpdateById]
 *                       (@name	varchar(100),
 *                        @id	int,
 *                        @flag integer output,
 *                        @msg varchar(80) output)
 *                   AS
 *                       update TB_Example set name = @name where id = @id
 *
 *                   GO
 *
 *
 *                   RecordSet rs = new RecordSet() ;
 *
 *                   String newname = ....... ;
 *                   String id = ...... ;
 *                   String procpara = newname + Util.getSeparator() + id ;
 *	                 rs.executeProc( "PD_Example_UpdateById" , procpara , "ecologytest" ) ;
 *                  
 *                   procpara 是存储过程的参数值组成的字符串变量，
 *                   多个参数值之间用 weaver.general.Util.getSeparator() 分开
 *
 *               4、在一个客户程序多个执行之间，查询结果可以保留到下一次查询 
 *                  RecordSet rs = new RecordSet() ;
 *	                rs.executeSql(" select * from TB_Example ") ;
 *                  rs.executeSql(" update TB_Example set name = 'the new value '") ;
 *	                while( rs.next() ) {
 *                      String thename = rs.getString("name") ;  //得到修改前查询的值
 *                      ......
 *                  }
 *                  rs.executeSql(" select * from TB_Example ") ;
 *                  while( rs.next() ) {
 *                      String thename = rs.getString("name") ;  //得到修改后查询的值
 *                      ......
 *                  }
 *
 *
 */

package com.engine.sqlexecute.util;


import weaver.conn.*;
import weaver.general.BaseBean;
import weaver.general.GCONST;
import weaver.general.SecurityHelper;
import weaver.general.Util;
import weaver.interfaces.datasource.BaseDataSource;
import weaver.monitor.cache.CacheFactory;
import weaver.monitor.cache.ResultMap;
import weaver.monitor.cache.Util.ConfigMap;
import weaver.servicefiles.DataSourceXML;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class RecordSet_zyz extends BaseBean
{
//当前使用的连接池名称
	private String poolname = null;

/**
 * 构造函数
 *
 */
    public RecordSet_zyz()
    {
        array = new Vector();
        bSuccess = true;
        checksql=true;
 
    }

/**
 * 执行存储过程，采用默认的链接池
 *
 * @param s    存储过程名称
 * @param s1   存储过程参数，多个参数中间用Util.getSeparator()分开
 *
 * @return boolean   如果执行成功，返回true，否则返回false
 */
    public boolean executeProc(String s, String s1)  {
		return executeProc(s, s1,null) ;
	}

/**
 * 执行存储过程，采用指定的链接池
 *
 * @param s    存储过程名称
 * @param s1   存储过程参数，多个参数中间用Util.getSeparator()分开
 * @param poolname    指定的链接池名称，如果为null 则使用默认的链接池 
 *
 * @return boolean   如果执行成功，返回true，否则返回false
 */
    public boolean executeProc(String s, String s1,String poolname)
    {
        long l = 0L;
        long l2 = 0L;
        if(s == null || s.trim().equals("")){
   		 return false;
   	 }
        /*
     	  * 取出缓存
     	  */
     	 ResultMap result = null;
     	 if(ConfigMap.get("iscache","").equals("1")){
     		 if(poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)){
         		 result = CacheFactory.getInstance().getRecordCacheForProc(s, s1);
             	 if(result != null && result.getArray() != null){
             		 this.init();
             		 CacheFactory.getInstance().convertMapToRS(result,this);
             		 bSuccess = true ;
             		 return true;
             	 }
         	 } 
     	 }
     	 
     	 
     	 long time = System.currentTimeMillis();
     	FastConnNode connNode = null;
        
        this.poolname = poolname;
        if(FastConnBuffer.isBuffered()){
            connNode = FastConnBuffer.getConnNode(poolname);
            if(connNode == null){
                if(!getConnection(poolname))
                    return false;
                
                connNode = new FastConnNode(conn, System.currentTimeMillis());
                FastConnBuffer.setConnNode(poolname, connNode);
            }else{
                
                conn = connNode.getConnection();
                
                if(System.currentTimeMillis() - connNode.getStartTime() > FastConnBuffer.RECONNECT_TIME){
                //if(System.currentTimeMillis() - connNode.getStartTime() > 200){  // for test
                    try {
                        //System.out.println("~~~~~~~connect buffer recreate a new connection");  // for test
                        givebackConnection(poolname);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if(!getConnection(poolname))
                        return false;
                    
                    connNode = new FastConnNode(conn, System.currentTimeMillis());
                    FastConnBuffer.setConnNode(poolname, connNode);
                }
            }
            connNode.setExec(true);
            
        }else{
            if(!getConnection(poolname))
                 return false;
        }
        
        databaseType = conn.getDBType() ;
       

        try {

            parseArgument(s1);
            int i = args.length;
            String s2 = "";
            s2 = new StringBuffer("{call ").append(s).append("(").toString(); 
            
            
            if(databaseType.equals("db2")) {
                for(int j = 0; j < i ; j++) {
                    if(j==0) s2 = new StringBuffer(s2).append("?").toString();
                    else s2 = new StringBuffer(s2).append(",?").toString();
                }
                s2 = new StringBuffer(s2).append(")}").toString();
            }
            else {
                for(int j = 0; j < i + 1; j++) s2 = new StringBuffer(s2).append("?,").toString();
                if(databaseType.equals("oracle")) s2 = new StringBuffer(s2).append("?,").toString();
                s2 = new StringBuffer(s2).append("?)}").toString(); 
            }

            CallableStatement callablestatement = conn.prepareCall(s2);
            for(int k = 0; k < i; k++)
            {
                //if(databaseType.equals("sqlserver")) args[k] = (new String(args[k].getBytes("UTF-8"),"ISO8859_1")) ;
                callablestatement.setString(k + 1, args[k]);
            }
            
            if(!databaseType.equals("db2")) {
                callablestatement.registerOutParameter(i + 1, Types.NUMERIC);
                callablestatement.registerOutParameter(i + 2, Types.VARCHAR);
                if(databaseType.equals("oracle"))
                    callablestatement.registerOutParameter(i + 3, -10);
            }

            
            Date beginTime=new Date();
            
            // long l1 = System.currentTimeMillis();
            callablestatement.execute();
            // long l3 = System.currentTimeMillis();
            Date endTime=new Date();
        	ExecuteSqlLogger.log(s+"("+s1+")",beginTime,endTime);
            try
            {
                ResultSet resultset = null;
                if(databaseType.equals("oracle")) {
                    try {
                    	
                    	
                        resultset = (ResultSet)callablestatement.getObject(i + 3);
                    //增加flag,msg输出参数支持,xiaofeng
                        try{
                        flag=callablestatement.getInt(args.length+1);
                            }catch(SQLException e){
                            //ignore
                        }
                        try{
                        msg=callablestatement.getString(args.length+2);
                        }catch(SQLException e){
                            //ignore
                        }
                        }
                    catch(SQLException cursorinvalidate) { resultset = null ; }   
                }
                else
                {
                    while((resultset=callablestatement.getResultSet())==null){
                        if(!callablestatement.getMoreResults() && callablestatement.getUpdateCount()==-1)
                        break;
                    }
                }

                if(resultset!=null){
                  parseResultSet(resultset);
                  resultset.close();
                }
                //增加flag,msg输出参数支持,xiaofeng
                try{
                flag=callablestatement.getInt(args.length+1);
                    }catch(SQLException e){
                            //ignore
                        }
                try{
                msg=callablestatement.getString(args.length+2);
                }catch(SQLException e){
                            //ignore
                        }
            }
            catch(SQLException sqlexception)
            {
                writeLog(sqlexception) ;
            }
            
            callablestatement.close();
            bSuccess = true;
        }
        catch(Exception exception)
        {
            bSuccess = false;
            writeLog(s+" " + s1);
            writeLog(exception) ;
        }
        finally
        {
        	  if(connNode != null){
                	 connNode.setExec(false);
                }
            try
            {
                if(FastConnBuffer.isBuffered()){
                    conn.commit();
                }else{
                    givebackConnection(poolname);
                }
              
            }
            catch(Exception exception2) { }
            lostTime = System.currentTimeMillis()-time;
            if(bSuccess){
            	if (poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)) {
					CacheFactory.getInstance().refreshCacheForProc(s, this, result);
				}
            }
        }
        return bSuccess;
    }

/**
 * 执行SQL语句，采用默认的链接池
 *
 * @param s    SQL语句
 *
 * @return boolean   如果执行成功，返回true，否则返回false
 */	
@Deprecated
 public boolean executeSql(String s)  {
		return executeSql(s , null) ;
	}
 
 /**
  * 新的SQL语句执行方式，可以防止SQL注入
  * @param s
  * @param params
  * @return 
  */
 
 public boolean executeQuery(String s, Object... params){
	 return executeSql(s,true,null,true,params);
 }
 
 public boolean executeUpdate(String s, Object... params){
	 return executeSql(s,false,null,true,params);
 }
 
 public boolean executeSql(String s,boolean isQuerySql, Object... params){
	 return executeSql(s,isQuerySql,null,true,params);
 }
 
 public boolean executeSql(String s,boolean isQuerySql,String poolname, Object... params){
	 return executeSql(s,isQuerySql,poolname,true,params);
 }
 
 private boolean executeSql2(String s,boolean isQuerySql,String poolname, Object... params){
	 return executeSql(s,isQuerySql,poolname,true,params);
 }
 public boolean executeSql(String s,boolean isQuerySql,String poolname,boolean hasnewconn, Object... params){
	 //ConnStatement statement = new ConnStatement();
	 if(s == null || s.trim().equals("")){
		 return false;
	 }
	 /*
	  * 取出缓存
	  */
	 ResultMap resultMap = null;
	 if(ConfigMap.get("iscache","").equals("1")){
		 if(poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)){
			 resultMap = CacheFactory.getInstance().getRecordCache(s,params);
        	 if(resultMap != null && resultMap.getArray() != null ){
        		 this.init();
        		 CacheFactory.getInstance().convertMapToRS(resultMap,this);
        		 bSuccess = true ;
        		 return true;
        	 }
    	 } 
	 }
	
	 long time = System.currentTimeMillis();
	 FastConnNode connNode =null;
	 
	 
	 this.poolname = poolname;
	 //对外部数据源不做任何处理
	 boolean flag = false;
	 //判断是不是本地数据源，只有本地数据源才做处理
	 if(poolname==null||GCONST.getServerName().equals(poolname)||EncodingUtils.containsNativeDB(poolname)||"".equals(poolname)){
		 flag = true;
	 }
	 if(FastConnBuffer.isBuffered()){
         connNode = FastConnBuffer.getConnNode(poolname);
         if(connNode == null){
             if(!getConnection(poolname))
                 return false;
             
             connNode = new FastConnNode(conn, System.currentTimeMillis());
             FastConnBuffer.setConnNode(poolname, connNode);
         }else{
             
             conn = connNode.getConnection();
             
             if(System.currentTimeMillis() - connNode.getStartTime() > FastConnBuffer.RECONNECT_TIME){
             //if(System.currentTimeMillis() - connNode.getStartTime() > 200){  // for test
                 try {
                     //System.out.println("~~~~~~~connect buffer recreate a new connection");  // for test
                     givebackConnection(poolname);
                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
                 if(!getConnection(poolname))
                     return false;
                 
                 connNode = new FastConnNode(conn, System.currentTimeMillis());
                 FastConnBuffer.setConnNode(poolname, connNode);
             }
         }
         connNode.setExec(true);
     }else{
         if(!getConnection(poolname))
              return false;
     }
	 PreparedStatement ps = null;
	 if(flag &&EncodingUtils.encodingStrategy==EncodingUtils.UNICODEENCODING_STRATEGY){
		 s = EncodingUtils.toUNICODE(s);
	 }
	 try{
		 ps = conn.prepareStatement(s);
		  if(params == null||params.length ==0){
			 if(flag&&EncodingUtils.encodingStrategy == EncodingUtils.SQLPARSER_STRATEGY){ 
				 	 Class c=Class.forName("weaver.conn.sqlparser.FormatSQL");
				 	 Method m = c.getDeclaredMethod("parse",String.class);
    				 Object[] result = (Object[]) m.invoke(c.newInstance(), s);
    				 String newsql = result[0].toString();
    				 Vector paramss = (Vector)result[1];
    				 Object[] paramsss = new Object[] { paramss };
    				 //下次将不再满足params == null 不会出现死循环
    				 if( paramsss.length>0){
    					 return executeSql2(newsql,isQuerySql,poolname,hasnewconn,paramss);
    				 }
			 }
		}
		 int i=1;
		 for(Object param : params){
			 if ((param instanceof Vector)) {
		          Vector pars = (Vector)param;
		          for (int j = 0; j < pars.size(); j++) {
		            Object para = pars.get(j);
		            if (((para instanceof Integer)) || ((para instanceof Long))){
				  //需要转码时，将参数先转换为unicode lrf
				    if(flag){
					   if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
						 para = EncodingUtils.toUNICODE(para); 
					  }
				   }
				    ps.setInt(i, Util.getIntValue(""+para));
				  }
		            else if (para == null)
		            	ps.setNull(i, 0);
		            else if ((para instanceof Float))
		            	ps.setFloat(i, Util.getFloatValue(""+para));
		            else if (((para instanceof BigDecimal)) || ((para instanceof Double)))
		            	ps.setBigDecimal(i, (para instanceof BigDecimal) ? (BigDecimal)para : new BigDecimal(Util.getDoubleValue(""+para)));
		            else if ((para instanceof java.sql.Date))
		            	ps.setDate(i, (java.sql.Date)para);
		            else if (((para instanceof String)) || ((para instanceof Character))){
		            	if("null".equals((""+para).trim().toLowerCase())){
		            		ps.setNull(i,Types.NULL);
		            	}else{
						 if(flag){
							 if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
								 para = EncodingUtils.toUNICODE(para); 
							 }
		            	 }
						 ps.setString(i, ""+para);
						}
		            }else if ((para instanceof Clob)){
				 		 String str = "";
						 if(flag){
							 if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
							 str = EncodingUtils.toUNICODE(para);
							 para = EncodingUtils.StringToClob(str, conn);
					 		}
				 		}
			            	ps.setClob(i, (Clob)para);
					}
		            else if ((para instanceof Blob))
		            	ps.setBlob(i, (Blob)para);
		            else {
		            	if(flag){
		            			if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
		            					para=EncodingUtils.toUNICODE(para);
		            			}
		            	}
			            	ps.setObject(i, para);
		            }
		            i++;
		          }
		        }else if ((param instanceof List)) {
		        	List pars = (List)param;
			          for (int j = 0; j < pars.size(); j++) {
			            Object para = pars.get(j);
			            if (((para instanceof Integer)) || ((para instanceof Long))){
							  //需要转码时，将参数先转换为unicode lrf
						    if(flag){
							   if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
								 para = EncodingUtils.toUNICODE(para); 
							  }
						   }
						    ps.setInt(i, Util.getIntValue(""+para));
						  }
			            else if (para == null)
			            	ps.setNull(i, Types.NULL);
			            else if ((para instanceof Float))
			            	ps.setFloat(i, Util.getFloatValue(""+para));
			            else if (((para instanceof BigDecimal)) || ((para instanceof Double)))
			            	ps.setBigDecimal(i, (para instanceof BigDecimal) ? (BigDecimal)para : new BigDecimal(Util.getDoubleValue(""+para)));
			            else if ((para instanceof java.sql.Date))
			            	ps.setDate(i, (java.sql.Date)para);
			            else if (((para instanceof String)) || ((para instanceof Character))){
			            	if("null".equals((""+para).trim().toLowerCase())){
			            		ps.setNull(i,Types.NULL);
			            	}else{
			            		 if(flag){
					 					if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
						 					para = EncodingUtils.toUNICODE(para); 
					 				}
				 				}
			            		ps.setString(i, ""+para);
			            	}
			            }else if ((para instanceof Clob)){
				 		 String str = "";
						 if(flag){
							 if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
								 str = EncodingUtils.toUNICODE(para);
								 para = EncodingUtils.StringToClob(str, conn);
							 }
				 		}
			            	ps.setClob(i, (Clob)para);
						}
			            else if ((para instanceof Blob))
			            	ps.setBlob(i, (Blob)para);
			            else {
			            	if(flag){
			            		if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
			            			para=EncodingUtils.toUNICODE(para);
			            		}
			            	}
			            	ps.setObject(i, para);
			            }
			            i++;
			          }
		        } else {
		          if (((param instanceof Integer)) || ((param instanceof Long))){
				  	 if(flag){
				  		 if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
						 param = EncodingUtils.toUNICODE(param); 
				  		 }
				  	 }
		        	  ps.setInt(i, Util.getIntValue(""+param));
					 }
		          else if (param == null)
		        	  ps.setNull(i, Types.NULL);
		          else if ((param instanceof Float))
		        	  ps.setFloat(i, Util.getFloatValue(""+param));
		          else if (((param instanceof BigDecimal)) || ((param instanceof Double)))
		        	  ps.setBigDecimal(i, (param instanceof BigDecimal) ? (BigDecimal)param : new BigDecimal(Util.getDoubleValue(""+param)));
		          else if ((param instanceof java.sql.Date))
		        	  ps.setDate(i, (java.sql.Date)param);
		          else if (((param instanceof String)) || ((param instanceof Character))){
		        	  if("null".equals((""+param).trim().toLowerCase())){
		            		ps.setNull(i,Types.NULL);
		              }else{
						if(flag){
					 					if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
						 					param = EncodingUtils.toUNICODE(param); 
					 				}
				 				}
		            	ps.setString(i, ""+param);
		            }
		          }else if ((param instanceof Clob)){
				 //oracle clob类型的存储，先将clob转换成String，然后将String转换为unicode编码 ，以String方式入库
				 String str = "";
				 if(flag){
					 if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
						 str = EncodingUtils.toUNICODE(param);
						 param = EncodingUtils.StringToClob(str, conn);
					 }
				 }
		        	  ps.setClob(i, (Clob)param);
				}
		          else if ((param instanceof Blob))
		        	  ps.setBlob(i, (Blob)param);
		          else {
				   if(flag){
					 if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
						 param=EncodingUtils.toUNICODE(param);
					 }
				   }
		        	  ps.setObject(i, param);
		          }
		          i++;
		        }
		 }
		 if(isQuerySql){
			 ps.executeQuery();
			 ResultSet resultset = ps.getResultSet();
			 if(resultset != null) {                        
				parseResultSet(resultset);
	            resultset.close();
			}
		 }else{
			 ps.executeUpdate();
		 }
		 bSuccess = true;
		 return true;
	 }catch(Exception e){
		 String sql = s;
		 for(Object param : params){
			 sql += ","+param;
		 }
		 writeLog(sql);
		 writeLog(e);
		 bSuccess = false;
		 return false;
	 } finally
     {
		 try{
   			 ps.close();
   		 }catch(Exception e){}
         try
         {
        	 if(connNode != null){
            	 connNode.setExec(false);
            }
             if(FastConnBuffer.isBuffered()){
                 conn.commit();
             }else{
                 givebackConnection(poolname);
             }
            
         }
         catch(Exception exception2) { }
         lostTime = System.currentTimeMillis()-time;
         if(bSuccess){
         	if (poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)) {
					CacheFactory.getInstance().refreshCache(s, this, resultMap);
				}
         }
     }
 }
 
 
	 /**
	  * 批量执行SQL语句，带事务处理
	  * @param sql
	  * @param params
	  * @throws Exception
	  */
	public boolean executeBatchSql(String sql, List<List> params) {
		    return executeBatchSql(sql, params, null);
	}
 
	public boolean executeBatchSql(String sql, List<List> params, String poolname) {
		long startTime = new Date().getTime();
		//writeLog("======sql is "+sql+"=============start time is "+new Date()+"======params.size() is " + params.size());
	     if (params == null || params.size() == 0) {
	         return false;
	     }
	    // if (conn == null || conn.isClosed()) {
	         if (!getConnection(poolname))
	             return false;
	    // }
	     // 得到数据库类型
	     databaseType = conn.getDBType();
	     boolean flag = false;
	     if (poolname == null || GCONST.getServerName().equals(poolname) || EncodingUtils.containsNativeDB(poolname) || "".equals(poolname)) {
	         // 对于这种存储过程或预处理方式，若当前为sqlparser模式（默认sqlserevr数据库编码为nvarchar），则不需要实际执行sqlparser，因为存储过程或者预处理方式存储数据没有问题
	         if (EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY) {
	             flag = true;
	             sql = EncodingUtils.toUNICODE(sql);
	         }
	     }
	
	     PreparedStatement pstmt = null;
	     List<Object> paramList = null;
	     try {
	         conn.setAutoCommit(false);
	         pstmt = conn.prepareStatement(sql);
	         boolean hasMoreBatch = false;
	         int k = 0;
	         for (int i = 0; i < params.size(); i++) {
	        	 k++;
	             paramList = params.get(i);
	             // 参数填充
	             for (int j = 0; j < paramList.size(); j++) {
	                 Object param = paramList.get(j);
	                 int parameterIndex = j + 1;
	                 if (param instanceof Integer) {
	                     pstmt.setInt(parameterIndex, (Integer) param);
	                 } else if (param instanceof String) {
	                     if (flag) {
	                         pstmt.setString(parameterIndex, EncodingUtils.toUNICODE((String) param));
	                     } else {
	                         pstmt.setString(parameterIndex, (String) param);
	                     }
	                 } else if (param instanceof Double) {
	                     pstmt.setDouble(parameterIndex, (Double) param);
	                 } else if (param instanceof Float) {
	                     pstmt.setFloat(parameterIndex, (Float) param);
	                 } else if (param instanceof Long) {
	                     pstmt.setLong(parameterIndex, (Long) param);
	                 } else if (param instanceof byte[]){
	                	 pstmt.setBytes(parameterIndex , (byte[]) param);
	                 }else {
	                     if (flag) {
	                         pstmt.setString(parameterIndex, EncodingUtils.toUNICODE((String) param));
	                     } else {
	                         pstmt.setString(parameterIndex, (String) param);
	                     }
	                 }
	             }
	             hasMoreBatch = true;
	             pstmt.addBatch();
	             if(k>=5000){
	            	 pstmt.executeBatch();
	            	 conn.commit();
	            	 pstmt.clearBatch();
	            	 hasMoreBatch = false;
	            	// writeLog("====i is "+i+"====k is "+k+"=====sql is "+sql+"=============commit time is "+new Date());
	            	 k = 0;
	             }
	         }
	         if(hasMoreBatch){
	        	 pstmt.executeBatch();
	        	 conn.commit();
	        	// writeLog("======sql is "+sql+"===hasMoreBatch="+hasMoreBatch+"=========commit time is "+new Date());
	         }
	         return true;
	     } catch (SQLException e) {
	         //writeLog(">>>>执行过程出错");
	         //writeLog(sql);
	        // writeLog(params.toString());
	       //  writeLog(e);
	         //e.printStackTrace();
	         //throw e;
	         return false;
	     } finally {
	    	//清除缓存
				if (poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)) {
					CacheFactory.getInstance().removeCacheForSql(sql);
				}
	    	 
	         try {
	             if (pstmt != null) {
	                 pstmt.close();
	                 // writeLog(">>>>stmt正常关闭");
	             }
	         } catch (SQLException e) {
	             writeLog(">>>>关闭stmt出错");
	             writeLog(e);
	         }
	         try {
	             //自动提交时才归还链接
//	             if (conn.getAutoCommit()) {
//	                 givebackConnection(poolname);
//	                 conn = null;
//	             }
	        	 givebackConnection(poolname);
                 conn = null;
	         } catch (SQLException e) {
	             writeLog(">>>>关闭conn出错");
	             writeLog(e);
	         }
	        // writeLog("======sql is "+sql+"=============end time is "+new Date()+"========execute time is "+(new Date().getTime()-startTime)+"ms");
	     }
	 }



/**
 * 执行SQL语句，采用指定的链接池
 *
 * @param s    SQL语句
 * @param poolname    指定的链接池名称，如果为null 则使用默认的链接池 
 *
 * @return boolean   如果执行成功，返回true，否则返回false
 */
	@Deprecated
     public boolean executeSql(String s ,String poolname)
    {
    	 
    	 if(s == null || s.trim().equals("")){
    		 return false;
    	 }
    	 
    	 /*
    	  * 取出缓存
    	  */
    	 ResultMap resultMap = null;
    	 if(ConfigMap.get("iscache","").equals("1")){
    		 if(poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)){
    			 resultMap = CacheFactory.getInstance().getRecordCache(s);
            	 if(resultMap != null && resultMap.getArray() != null){
            		 this.init();
            		 CacheFactory.getInstance().convertMapToRS(resultMap,this);
            		 bSuccess = true ;
//            		 LOGGER.info("缓存结果查询："+s+"总花费时间："+(System.currentTimeMillis()-time));
            		 return true;
            	 }
        	 } 
    	 }
    	 long time = System.currentTimeMillis();
    	 FastConnNode connNode = null;
    	 
    	 
    	 
    	 this.poolname = poolname;
    	 boolean flag = false;
    	 //首先判断是否本地数据源，只有本地数据源才做编码转换或者sql解析处理
    	 if(poolname==null||GCONST.getServerName().equals(poolname)||EncodingUtils.containsNativeDB(poolname)||"".equals(poolname)){
    		 flag = true;
    	 }
    	 if(flag){
    		 if(EncodingUtils.encodingStrategy == EncodingUtils.SQLPARSER_STRATEGY){
    			 try{
    				 Class c=Class.forName("weaver.conn.sqlparser.FormatSQL");
				 	 Method m = c.getDeclaredMethod("parse",String.class);
    				 Object[] result = (Object[]) m.invoke(c.newInstance(), s);
    				 String newsql = result[0].toString();
    				 Vector params = (Vector)result[1];

    				 if (s.trim().toLowerCase().startsWith("select")) {
    					 return executeQuery(newsql, new Object[] { params });
    				 }
    				 return executeUpdate(newsql, new Object[] { params });
    			 }
    			 catch (Exception e) {
    				 writeLog("=======sql::" + s);
    				 e.printStackTrace();
    			 }
    			 return false;
    		 }else if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
    			 s = EncodingUtils.toUNICODE(s);
    		 }
    	 }
         if(FastConnBuffer.isBuffered()){
             connNode = FastConnBuffer.getConnNode(poolname);
             if(connNode == null){
                 if(!getConnection(poolname))
                     return false;
                 
                 connNode = new FastConnNode(conn, System.currentTimeMillis());
                 FastConnBuffer.setConnNode(poolname, connNode);
             }else{
                 
                 conn = connNode.getConnection();
                 
                 if(System.currentTimeMillis() - connNode.getStartTime() > FastConnBuffer.RECONNECT_TIME){
                 //if(System.currentTimeMillis() - connNode.getStartTime() > 200){  // for test
                     try {
                         //System.out.println("~~~~~~~connect buffer recreate a new connection");  // for test
                         givebackConnection(poolname);
                     } catch (SQLException e) {
                         e.printStackTrace();
                     }
                     if(!getConnection(poolname))
                         return false;
                     
                     connNode = new FastConnNode(conn, System.currentTimeMillis());
                     FastConnBuffer.setConnNode(poolname, connNode);
                 }
             }
             connNode.setExec(true);
         }else{
             if(!getConnection(poolname))
                  return false;
         }

        databaseType = conn.getDBType() ;
       
        
        try
        {   //check sql
            String sql = s;
            String sql1=s;
            if(checksql){
            try{
                sql = Util.replace(sql, "'[^']*'", "", 0);
            }catch(Exception e){
                writeLog("regex parse error:"+sql);
            }
            if (sql.indexOf(";") > -1||sql.indexOf("--")>-1) {
                writeLog("illegal sql statement:" + s);
                return false;
            }
            }

            //check end
            Statement statement = conn.createStatement();
            
            Date beginTime=new Date();            
            statement.execute(s);
            ResultSet resultset = statement.getResultSet();
            
            Date endTime=new Date();
            ExecuteSqlLogger.log(s,beginTime,endTime);
            
			if(resultset != null) {                        // bug mod by liuyu
				parseResultSet(resultset);
	            resultset.close();
			}

            statement.close();
            bSuccess = true;
            setExceptionMessage("SUCCESS");
            // msg="执行成功";
            // flag=0;
        }
        catch(Exception exception)
        {
            setExceptionMessage(exception.getMessage());
            bSuccess = false;
            // msg=exception.getMessage();
            // flag=-1;
            writeLog(s);
            writeLog(exception) ;
        }
        finally
        {
            try
            {
            	 if(connNode != null){
                 	connNode.setExec(false);
                 }
                if(FastConnBuffer.isBuffered()){
                    conn.commit();
                }else{
                    givebackConnection(poolname);
                }
               
            }
            catch(Exception exception2) { }
            if(bSuccess){
            	if (poolname == null || poolname.equals(GCONST.getServerName()) || poolname.equals(DataSourceXML.SYS_LOCAL_POOLNAME)) {
            		lostTime = System.currentTimeMillis()-time;
    				CacheFactory.getInstance().refreshCache(s, this, resultMap);
    			}
            }
        }
        return bSuccess;
    }
     
     /**
      * 根据数据源执行sql
      * @param sql
      * @param datasourceName  数据源名称，此名称为在数据源配置页面配置的名称
      * @return
      */
     public boolean executeSqlWithDataSource(String sql ,String datasourceName){
    	 DataSourceXML dataSourceXML = new DataSourceXML();
 		List pointArrayList = dataSourceXML.getPointArrayList();
 		Hashtable dataHST = dataSourceXML.getDataHST();
 		for(int i=0;i<pointArrayList.size();i++){
 			String pointid = (String)pointArrayList.get(i);
 		    if(pointid.equals("")) continue;
 		    Hashtable detailHST = (Hashtable)dataHST.get(pointid);
 		    if(detailHST!=null){
 				if (pointid.equals(datasourceName)) {
 					String dbtype = Util.null2String((String) detailHST.get("type"));
 					String iscluster = Util.null2String((String) detailHST.get("iscluster"));
 					iscluster = iscluster.equals("") ? "1": iscluster;
 					
 					String url = Util.null2String((String) detailHST.get("url"));
 					String host = Util.null2String((String) detailHST.get("host"));
 					String port = Util.null2String((String) detailHST.get("port"));
 					String DBname = Util.null2String((String) detailHST.get("dbname"));
 					String username = Util.null2String((String) detailHST.get("user"));
 					String password = Util.null2String((String) detailHST.get("password"));
 					String iscode = Util.null2String((String) detailHST.get("iscode"));

 					if ("1".equals(iscode)) {
 						username = SecurityHelper.decrypt("ecology", username);
 						password = SecurityHelper.decrypt("ecology",password);
 					}
 					
 					BaseDataSource baseDataSource = new BaseDataSource();
 			        baseDataSource.setType(dbtype);
 			        baseDataSource.setUrl(url);
 			        baseDataSource.setHost(host);
 			        baseDataSource.setPort(port);
 			        baseDataSource.setDbname(DBname);
 			        
 			        baseDataSource.setUser(username);
 			        baseDataSource.setPassword(password);
 			        baseDataSource.setIscluster(iscluster);
 			        Connection connection = null;
 			        PreparedStatement psm = null;
 			        ResultSet rs1 = null;
 			        boolean hasSqlParse = false;
 			        //weaver_enableMultiLangEncoding。properties配置文件中必须加入本地数据源 在数据源配置的名称
 			        //如果是本地数据源，作如下处理,已经是预处理方式，不需要再考虑sqlserevr模式下的的sqlparser，目前尚不确定在预处理语句中包含 韩文会有什么问题
 			        if(EncodingUtils.containsNativeDB(datasourceName)){
 			        	if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
 			        		sql = EncodingUtils.toUNICODE(sql);
 			        	}else if(EncodingUtils.encodingStrategy == EncodingUtils.SQLPARSER_STRATEGY){
 			        		try{
 			        			Class c=Class.forName("weaver.conn.sqlparser.FormatSQL");
 							 	Method m = c.getDeclaredMethod("parse",String.class);
 			    				Object[] result = (Object[]) m.invoke(c.newInstance(), sql);
 			        			String newsql = result[0].toString();
 			        			Vector params = (Vector)result[1];
 			        			connection = baseDataSource.getConnection();
 			        			psm= connection.prepareStatement(newsql);
 			        			int j = 1;
 			        			for(Object param : params){
 			        				if(param instanceof Integer || param instanceof Long){
 			        					psm.setInt(j,Util.getIntValue(""+param));
 			        				}else if(param instanceof Float){
 			        					psm.setFloat(j,Util.getFloatValue(""+param));
 			        				}else if(param instanceof BigDecimal || param instanceof Double){
 			        					psm.setBigDecimal(j,(param instanceof BigDecimal)?(BigDecimal)param:new BigDecimal(""+param));
 			        				}else if(param instanceof java.sql.Date){
 			        					psm.setDate(j,(java.sql.Date)param);
 			        				}else if(param instanceof String || param instanceof Character){
 			        					psm.setString(j,""+param);
 			        				}else if(param == null){
 			        					//statement.setNull(i);
 			        					psm.setObject(j, param);
 			        				}else if(param instanceof Clob){
 			        					psm.setClob(i,(Clob)param);
 			        				}else if(param instanceof Blob){
 			        					psm.setBlob(i,(Blob)param);
 			        				}else{
 			        					psm.setObject(i,param);
 			        				}
 			        				j++;
 			        			}
 			        		}catch(Exception e){
 			        			writeLog("Serious Error!!! Sqlparser Error!!");
 			        			try {
 			        				psm.close();
 			        				connection.close();
 			        			} catch (SQLException e1) {
 			        				e1.printStackTrace();
 			        			}
 			        		}finally{
 			        			hasSqlParse = true;
 			        		}
 			        	}
 			        }
 			     try {
 			    	 //如果未经过SQlParser解析，初始化ps对象
 			    	 	if(!hasSqlParse){
 			    	 		connection = baseDataSource.getConnection();
 	 			        	psm= connection.prepareStatement(sql);
 			    	 	}
 			        	Date beginTime=new Date();            
  			            rs1 = psm.executeQuery();
  			            if(rs1 != null) {                        // bug mod by liuyu
							parseResultSet(rs1);
	  					}
  			            Date endTime=new Date();
  			            ExecuteSqlLogger.log(sql,beginTime,endTime);
 			        	bSuccess = true;
 					}catch(Exception e){
 						bSuccess = false;
 						
 						writeLog(sql);
 			            writeLog(e) ;
 					}finally{
 						try {
 							if(rs1!=null){
 								rs1.close();
 							}
 							if(psm!=null){
 								psm.close();
 							}
 							if(connection!=null){
 								connection.close();
 							}
 						} catch (SQLException e) {
 							e.printStackTrace();
 						}
 					}
 			    }
 	     
 		    }
 			    
 		}
 		return bSuccess;
     }


/**
 * 执行存储过程，采用默认的链接池
 *
 * @param s    存储过程名称
 * @param s1   存储过程参数，多个参数中间用Util.getSeparator()分开
 *
 * @return boolean   如果执行成功，返回true，否则返回false
 */
    public boolean execute(String s, String s1)
    {
        return executeProc(s, s1);
    }

/**
 * 执行SQL语句，采用默认的链接池
 *
 * @param s    SQL语句
 *
 * @return boolean   如果执行成功，返回true，否则返回false
 */
    public boolean execute(String s)
    {
        return executeSql(s);
    }


/**
 * 获得查询记录的总数
 *
 * @return int   查询记录的总数
 */
    public int getCounts()
    {
        return array.size();
    }

/**
 * 获得查询的字段数
 *
 * @return int   查询记录的字段数
 */
    public int getColCounts()
    {
        return array.isEmpty() ? 0 : ((Object[])array.get(0)).length;
    }


/**
 * 获得查询的字段名数组，数组依照查询顺序排列字段名
 *
 * @return String []   查询的字段名数组
 */
    public String [] getColumnName()
    {
        return columnName;
    }

    public int [] getColumnType()
    {
        return columnType;
    } 

/**
 * 获得查询的指定位置的数据库字段的字段名称
 *
 * @param columnIndex    数据库字段在查询语句中的位置
 *
 * @return String    指定位置的数据库字段的字段名称
 */
    public String  getColumnName(int columnIndex)
    {
        return columnName[columnIndex-1];
    }

	/**
	 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为字符串或者将被转换为字符串类型
	 * 不会去除前后空格
	 * @param columnIndex   数据库字段在查询语句中的位置
	 * @return String   该字段的值
	 */
	public String getStringNoTrim(int columnIndex){
		columnIndex--;
		String s = "";
		if(!array.isEmpty() && curpos >= 0 && curpos < array.size())
		{
			Object as[] = (Object[])array.get(curpos);
			if(columnIndex>= 0 && columnIndex< as.length)
			{
				try{
					s=as[columnIndex].toString();
				}catch(Exception e){ s = ""; }
			}
		}
		s = Util.formatStringIfMultilang(s);
		return s ;

	}

	/**
	 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为字符串或者将被转换为字符串类型
	 * 不会去除前后空格
	 * @param columnname   数据库字段在查询语句中的位置
	 * @return String   该字段的值
	 */
	public String getStringNoTrim(String columnname)
	{

		return getStringNoTrim(getColumnIndex(columnname));

	}
/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为字符串或者将被转换为字符串类型
 *
 * @param columnIndex   数据库字段在查询语句中的位置
 * @return String   该字段的值
 */
    public String getString(int columnIndex)
    {
        columnIndex--;
        String s = "";
        if(!array.isEmpty() && curpos >= 0 && curpos < array.size())
        {
            Object as[] = (Object[])array.get(curpos);
            if(columnIndex>= 0 && columnIndex< as.length)
            {
              try{
                  s=as[columnIndex].toString();

                 // if(databaseType.equals("sqlserver")) s= new String(s.trim().getBytes("ISO8859_1"),"UTF-8") ; 
                  //else
					  s = s.trim() ; 

              }catch(Exception e){ s = ""; }
            }
        } 
        s = Util.formatStringIfMultilang(s);
        return s ;

    }

/**
 * 获得当前记录中某一数据库字段的值，该值为字符串或者将被转换为字符串类型
 *
 * @param columnname   数据库字段的名称
 * @return String   该字段的值
 */
    public String getString(String columnname)
    {

      return getString(getColumnIndex(columnname));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为boolean 类型
 *
 * @param columnIndex   数据库字段在查询语句中的位置
 * @return boolean   该字段的值
 */
    public boolean getBoolean(int columnIndex){
      columnIndex--;
      boolean bool=false;
      if(!array.isEmpty() && curpos>=0 && curpos<=array.size()){
        Object as[]=(Object[])array.get(curpos);
        try{
          bool=((Boolean)as[columnIndex]).booleanValue();
        }catch(Exception e){
          throw new ClassCastException();
        }
      }
      return bool;
    }
 
 /**
 * 获得当前记录中某一数据库字段的值，该值为boolean 类型
 *
 * @param columnName   数据库字段的名称
 * @return boolean   该字段的值
 */
    public boolean getBoolean(String columnName){
      return getBoolean(getColumnIndex(columnName));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为int 类型
 *
 * @param columnIndex   数据库字段在查询语句中的位置
 * @return int   该字段的值
 */
    public int getInt(int columnIndex){
        columnIndex--;
        int integer=-1;
        if(!array.isEmpty() && curpos >= 0 && curpos < array.size())
        {
            Object as[] = (Object[])array.get(curpos);
            if(columnIndex >= 0 && columnIndex < as.length)
            {
                   integer = Util.getIntValue(as[columnIndex].toString(),-1);
            }
        }
        return integer;
    }

/**
 * 获得当前记录中某一数据库字段的值，该值为int 类型
 *
 * @param columnname   数据库字段的名称
 * @return int   该字段的值
 */
    public int getInt(String columnname){
      return getInt(getColumnIndex(columnname));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值以二进制输出流形式输出 InputStream
 *
 * @param columnIndex  数据库字段在查询语句中的位置
 * @return InputStream   该字段的输出
 */
    public InputStream getInputStream(int columnIndex)
    {
      BufferedInputStream bis=null;
      if(!array.isEmpty()&&curpos>=0 && curpos <= array.size())
      {
        Object as[]=(Object[])array.get(curpos);
        if(columnType[columnIndex]==Types.LONGVARCHAR)
        {
          byte inputByte[]=new byte[as[columnIndex].toString().length()];
          inputByte=as[columnIndex].toString().getBytes();

          bis=new BufferedInputStream(new ByteArrayInputStream(inputByte));
        }
      }
      else
      {
        throw new ClassCastException();
      }
      return bis;
    }

/**
 * 获得当前记录中某一数据库字段的值，该值以二进制输出流形式输出 InputStream
 *
 * @param columnName   数据库字段的名称
 * @return InputStream   该字段的输出
 */
    public InputStream getInputStream(String columnName){
      return  getInputStream(getColumnIndex(columnName));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为float 类型
 *
 * @param columnIndex   数据库字段在查询语句中的位置
 * @return float   该字段的值
 */
    public float getFloat(int columnIndex){
      columnIndex--;
      float f=0.0f;
      if(!array.isEmpty() && curpos>=0 && curpos<=array.size()){
        Object as[]=(Object[])array.get(curpos);
        try{
          f=Util.getFloatValue(as[columnIndex].toString(),-1);
        }catch(ClassCastException cce){
          throw new ClassCastException();
        }
      }
      return f;
    }

/**
 * 获得当前记录中某一数据库字段的值，该值为float 类型
 *
 * @param columnName   数据库字段的名称
 * @return float   该字段的值
 */
    public  float getFloat(String columnName){
      return getFloat(this.getColumnIndex(columnName));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值为double 类型
 *
 * @param columnIndex   数据库字段在查询语句中的位置
 * @return double   该字段的值
 */  
    public double getDouble(int columnIndex){
      columnIndex--;
      double d=0.0;
      if(!array.isEmpty() && curpos>=0 && curpos <= array.size()){
        Object as[]=(Object[])array.get(curpos);
        try{
          d=Util.getDoubleValue(as[columnIndex].toString(),-1);
        }catch(ClassCastException cce){
          throw new ClassCastException();
        }
      }
      return d;
    }

/**
 * 获得当前记录中某一数据库字段的值，该值为double 类型
 *
 * @param columnName   数据库字段的名称
 * @return double   该字段的值
 */
    public double getDouble(String columnName){
      return getDouble(getColumnIndex(columnName));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值以日期类型输出
 *
 * @param columnIndex  数据库字段在查询语句中的位置
 * @return Date   该字段的值
 */
    public Date getDate(int columnIndex){
      columnIndex--;
      Date date=null;
      if(!array.isEmpty() && curpos>=0 && curpos<=array.size()){
        Object as[]=(Object[])array.get(curpos);
        try{
          date=(Date)as[columnIndex];
        }catch(Exception e){
          throw new ClassCastException();
        }
       }
       return date;
    }

/**
 * 获得当前记录中某一数据库字段的值，该值以日期类型输出
 *
 * @param columnName   数据库字段的名称
 * @return Date   该字段的值
 */
    public Date getDate(String columnName){
      return getDate(getColumnIndex(columnName));
    }

/**
 * 获得当前记录中指定位置的数据库字段的值，在查询语句中第一个字段的位置为1，该值以二进制输出流形式输出 InputStream
 *
 * @param columnIndex  数据库字段在查询语句中的位置
 * @return InputStream   该字段的输出
 */ 
    public InputStream getBinaryStream(int columnIndex){
      columnIndex--;
      InputStream is=null;
      if(!array.isEmpty() && curpos>=0 && curpos <= array.size()){
        Object[] as=(Object[])array.get(curpos);
        try{
          if(databaseType.equals("oracle")) is=((Blob)as[columnIndex]).getBinaryStream();
          else is = (InputStream)as[columnIndex] ;
        }catch(Exception e){
            writeLog(e) ;
            throw new ClassCastException();
        }
      }
      return is;
    }

/**
 * 获得当前记录中某一数据库字段的值，该值以二进制输出流形式输出 InputStream
 *
 * @param columnName   数据库字段的名称
 * @return InputStream   该字段的输出
 */
    public InputStream getBinaryStream(String columnName){
      return getBinaryStream(getColumnIndex(columnName));
    }
 
/**
 * 获得由存储过程返回的Flag 值，该方法当前请无使用
 *
 * @return int   存储过程返回的Flag 值
 */
    public int getFlag()
    {
        if(flag==0)
            flag=1;
        return flag;
    }

/**
 * 获得由存储过程返回的Msg 信息，该方法当前请无使用
 *
 * @return String   存储过程返回的Msg 信息
 */
    public String getMsg()
    {
        return msg;
    }

/**
 * 将查询结果的游标从当前位置移至第一条记录之前。
 *
 */
	public void beforFirst()
    {
        curpos = -1;
    }

/**
 * 将查询结果的游标从当前位置移至第一条记录。
 *
 * @return boolean   如果第一条记录存在，返回true，否则返回false
 */
    public boolean first()
    {
        if(array.isEmpty())
        {
            return false;
        } else
        {
            curpos = 0;
            return true;
        }
    }

/**
 * 将查询结果的游标从当前位置移至最后一条记录。
 *
 * @return boolean   如果最后一条记录存在，返回true，否则返回false
 */
    public boolean last()
    {
        if(array.isEmpty())
        {
            return false;
        } else
        {
            curpos = array.size() - 1;
            return true;
        }
    }

/**
 * 将查询结果的游标从当前位置移至最后一条记录之后。
 *
 */
	public void afterLast()
    {
        curpos = array.size();
    }

/**
 * 将查询结果的游标从当前位置移至下一个位置，游标最初位于第一条记录之前。
 *
 * @return boolean   如果下一个位置有值，返回true，否则返回false
 */
    public boolean next()
    {
        if(array.isEmpty())
            return false;
        boolean flag1 = true;
        if(curpos < array.size() - 1)
        {
            curpos++;
            return true;
        } else
        {
            return false;
        }
    }

/**
 * 将查询结果的游标从当前位置移至上一个位置。
 *
 * @return boolean   如果上一个位置有值，返回true，否则返回false
 */
    public boolean previous()
    {
        if(array.isEmpty())
            return false;
        if(curpos > 0 && curpos <= array.size())
        {
            curpos--;
            return true;
        } else
        {
            return false;
        }
    }

/**
 * 将查询结果的游标从当前位置移至指定位置，游标第一条记录的位置为1 。
 *
 * @return boolean   如果指定位置有值，返回true，否则返回false
 */
    public boolean absolute(int i)
    {
        if(array.isEmpty())
            return false;
        if(i >= 0 && i < array.size())
        {
            curpos = i;
            return true;
        } else
        {
            return false;
        }
    }


    public String getDBType() {

        return getDBType(null) ;
    }


    public String getDBType(String poolname) {

    	String dbtype = pool.getDbtype();
        return dbtype ;
        
    }
    
    public String getDBTypeByPoolName(String poolname) {
    	
    	WeaverConnection conn = null;
    	try{
    		conn = pool.getConnection(poolname);
    		return conn.getDBType();
    	}finally{
    		pool.returnConnection(poolname, conn);
    	}
        
    }
        

/**
 * 获得查询的字段在字段排列中的位置
 *
 */
	private int getColumnIndex(String columnname){
      for(int i1=0;i1<columnName.length;i1++)
        if(columnName[i1].equalsIgnoreCase(columnname))
          return i1+1;
      return -1;
    }

/**
 * 解析查询结果
 *
 */
	private void parseResultSet(ResultSet resultset) throws Exception{
      init() ;
      ResultSetMetaData resultsetmetadata = resultset.getMetaData();
      int i1 = resultsetmetadata.getColumnCount();
      columnName=new String[i1];
      columnType=new int[i1];
      for(int i2=0;i2<i1;i2++){
        columnName[i2]=resultsetmetadata.getColumnName(i2+1);
        columnType[i2]=resultsetmetadata.getColumnType(i2+1);
      }
     
      int j1;
     for(j1 = 0; resultset.next(); j1++)
     {
         Object as[] = new Object[i1];
        for(int k1 = 1; k1 <= i1; k1++){
            Object tempobj = resultset.getObject(k1) ;
            if( tempobj == null)  as[k1 - 1] = "" ;
            else if(columnType[k1 - 1]==Types.CLOB||columnType[k1 - 1]==Types.NCLOB)
            {
                Clob clob=null;
                try
                {
                	//modified by lrf for E8 将数据库的clob 内容转换为 utf-8字符串
                	if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
                		 as[k1 - 1] = EncodingUtils.toUTF8(tempobj);
                	}else{
                		 as[k1 - 1] = EncodingUtils.ClobToString(tempobj);
                	}
                 
                  /*  clob=(Clob)tempobj ;
                    if(clob!=null) {
                        String readline = "" ;
                        StringBuffer clobStrBuff = new StringBuffer("") ; 
                        BufferedReader clobin = new BufferedReader(clob.getCharacterStream());
                        while ((readline = clobin.readLine()) != null) clobStrBuff = clobStrBuff.append(readline) ;
                        clobin.close() ;
                        as[k1 - 1] = clobStrBuff.toString() ;
                    }
                    else as[k1 - 1] = "";*/
                }catch(Exception se){as[k1 - 1] = "";}
            }else if (columnType[k1 - 1]==Types.BLOB){
            	as[k1 - 1] = tempobj ;
            }
            //modified by lrf for E8 将数据库的字段内容转换为 utf-8字符串
            else{
            	if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
            		as[k1 - 1] = EncodingUtils.toUTF8(tempobj) ;
            	}else{
            		as[k1 - 1] = tempobj ;
            	}
            }
        }
        array.add(as);
      }
      
    }

/**
 * 获取数据库链接
 *
 */
    private boolean getConnection(String poolname)
    {
        try
        {
			if(poolname != null )   conn=pool.getConnection(poolname);
			else conn=pool.getConnection();

			if(conn != null )  return true;
			else return false  ;
        }
        catch(Exception exception)
        {
            writeLog(exception) ;
        }
        return false;
    }

/**
 * 归还数据库链接
 *
 */
    private void givebackConnection(String poolname)
        throws SQLException
    {
        if(conn != null )
      {
            if(poolname != null )   pool.returnConnection(poolname  , conn);
            else  pool.returnConnection(conn);
      }
    }

/**
 * 初始化
 *
 */
    private void init()
    {
        curpos = -1;
        flag = 1;
        msg = "";
        array.clear();
    }

/**
 * 解析存储过程参数
 *
 */
    private void parseArgument(String s)
    {
    	boolean needTransCode = false;
       if(poolname==null||GCONST.getServerName().equals(poolname)||EncodingUtils.containsNativeDB(poolname)||"".equals(poolname)){
    	        if(EncodingUtils.encodingStrategy == EncodingUtils.UNICODEENCODING_STRATEGY){
    	        		needTransCode = true;
    	        }
    	 }
    	int i = 0;
        int j = 0;
        if(s.trim().equals("")){
          args=new String[0];
          return;
        }
        for(j = 0; j < s.length(); j++)
            if(s.charAt(j) == separator)
                i++;

        args = new String[i + 1];
        j = 0;
        i = 0;
        while((j = s.indexOf(separator)) != -1)
        {
        	if(needTransCode){
        		 args[i++] = EncodingUtils.toUNICODE(s.substring(0, j));
        	}else{
        		args[i++] = s.substring(0, j);
        	}
            s = s.substring(j + 1);
        }
        if(needTransCode){
        		 s=EncodingUtils.toUNICODE(s);
        }
        args[i] = s;
    }


    public boolean isChecksql() {
        return checksql;
    }

    public void setChecksql(boolean checksql) {
        this.checksql = checksql;
    }
    public Vector getArray() {
		return array;
	}

	public void setArray(Vector array) {
		this.array = array;
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}

	public void setColumnType(int[] columnType) {
		this.columnType = columnType;
	}

	
	public long getLostTime() {
		return lostTime;
	}

	public void setLostTime(long lostTime) {
		this.lostTime = lostTime;
	}


    public RecordSetData getData(){
        RecordSetData data = new RecordSetData();
        Field[] fields = data.getClass().getDeclaredFields();
        Class clazz = data.getClass();
        Field field = null;
        try {
			field = clazz.getDeclaredField("array");
			field.setAccessible(true);
			field.set(data, array);
			
			field = clazz.getDeclaredField("columnName");
			field.setAccessible(true);
			field.set(data, columnName);
			
			field = clazz.getDeclaredField("columnType");
			field.setAccessible(true);
			field.set(data, columnType);
			
			field = clazz.getDeclaredField("databaseType");
			field.setAccessible(true);
			field.set(data, databaseType);
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return data;
    }

	private long lostTime;
    private int curpos;
    private int flag;
    private String msg;
    public static char separator = Util.getSeparator() ;
    private Vector array;
    private String columnName[];
    private int columnType[];
    private String args[];
    private ConnectionPool pool=ConnectionPool.getInstance();
    private WeaverConnection conn;
    private boolean bSuccess;
    private String databaseType ;
    private String exceptionMessage;
    public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	private boolean checksql;//是否检查sql
}
