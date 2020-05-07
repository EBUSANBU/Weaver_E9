package weaversj.x.csxutil.log;

import weaversj.x.csxutil.log.impl.SimpleLog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class LogUtil {

    //LogUtil工具提供的默认实例(废弃)
   // private static final Logger logger = new SimpleLog().getLogger(LogUtil.class.getName());

    private Logger logger;
    /**
     * 获得一个名为name的日志实例
     * @param name
     * @return
     */
    public static LogUtil getLogger(String name){
        return new LogUtil(name);
    }

    private LogUtil(String name){
        LogFactory logFactory = new SimpleLog();
        this.logger =  logFactory.getLogger(name);
    }

    /**
     * LogUtil内置的日志实例，记录一般信息
     * @param msg
     */
    public void info(String msg){
        logger.info(msg);
    }
    /**
     * LogUtil内置的日志实例，记录错误
     * @param msg
     */
    public void error(String msg) {
        logger.severe(msg);
    }
    public void error(Exception e) {
        error(ex2String(e));
    }
    /**
     * LogUtil内置的日志实例，记录警告
     * @param msg
     */
    public void warning(String msg) {
        logger.warning(msg);
    }
    /**
     * 为LogUtil内置的日志实例添加一个处理器
     * @param handler 继承java.util.logging.Handler
     */
    public void addHandler(Handler handler) {
        logger.addHandler(handler);
    }

    public String ex2String(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        LogUtil log = LogUtil.getLogger(LogUtil.class.getName());
        log.info("ceshi");
        log.error("ceshi");
    }

}
