package weaversj.x.csxutil.log.impl;

import weaversj.x.csxutil.log.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.*;

public class SimpleLog extends LogFactory {

    private static boolean      useConsoleHandler = true;
    private static boolean      useFileHandler = true;

    //记录的范围 handlerLevel < msg < filterLevel
    //记录的等级
    //private static Level        handlerLevel = Level.INFO;
    //过滤的等级
    //private static Level filterLevel = Level.INFO;

    private static Formatter    formatter = new SimpleFormatter();
    private static String       pattern = "E:\\log-%u";
    private static int          limit = 51200;
    private static int          count = 1;
    private static boolean      append = true;
    private static boolean      useErrorPattern=false;
    private static final String PATH = "weaversj.x.csxutil.properties.log";
    private static String       errorPattern = null;
    //增加handlers是为了所有日志实例共用所有handler
    private static ArrayList<Handler> handlers = new ArrayList<>();
    static {
        init();
    }

    private static void init() {

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(PATH);
            if (bundle.containsKey("useConsoleHandler"))
                useConsoleHandler = "true".compareTo(bundle.getString("useConsoleHandler")) == 0;

            if (bundle.containsKey("useFileHandler")) {
                useFileHandler = "true".compareTo(bundle.getString("useFileHandler")) == 0;
            }

            if (bundle.containsKey("pattern")){
                pattern = (String) bundle.getObject("pattern");
                String dir = pattern.substring(0,pattern.lastIndexOf('/'));
                File fp = new File(dir);
                // 创建目录
                if (!fp.exists()) {
                    fp.mkdirs();// 目录不存在的情况下，创建目录。
                }
            }
            if (bundle.containsKey("useErrorPattern"))
                useErrorPattern = "true".compareTo(bundle.getString("useErrorPattern")) == 0;

            if (bundle.containsKey("errorPattern")){
                errorPattern = (String) bundle.getObject("errorPattern");
                String dir = errorPattern.substring(0,errorPattern.lastIndexOf('/'));
                File fp = new File(dir);
                // 创建目录
                if (!fp.exists()) {
                    fp.mkdirs();// 目录不存在的情况下，创建目录。
                }
            }

            if (bundle.containsKey("limit"))
                limit = Integer.valueOf(bundle.getString("limit"));

            if (bundle.containsKey("count"))
                count = Integer.valueOf(bundle.getString("count"));

            if (bundle.containsKey("append"))
                append = "true".compareTo(bundle.getString("append")) == 0;

            /*
             *  记录的等级
             */
/*
            if (bundle.containsKey("handlerLevel")) {
                handlerLevel = (Level) Class.forName(bundle.getString("handlerLevel")).newInstance();
            }*/


            if (bundle.containsKey("formatter")){
                formatter = (SimpleFormatter) Class.forName(bundle.getString("formatter")).newInstance();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //日志的处理器：输出到控制台
        if (useConsoleHandler)
            handlers.add(new ConsoleHandler());
        //日志的处理器：输出到文件
        if (useFileHandler)
            try {
                handlers.add(createFileHandler(useErrorPattern));
                //System.out.println(useErrorPattern);
                if (useErrorPattern)
                    handlers.add(createErrorFileHandler());
                //System.out.println(handlers);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    private Logger buildLogger(String name) {

        Logger logger = Logger.getLogger(name);
        //日志等级
        logger.setLevel(Level.INFO);
        //日志是否传递给父级
        logger.setUseParentHandlers(false);
        //日志的过滤
        //logger.setFilter();
        for (Handler handler :
             handlers) {
            logger.addHandler(handler);
        }
        return logger;
    }



    private static FileHandler createFileHandler(Boolean outputErrorFile) throws IOException {
        FileHandler fileHandler;
        fileHandler = new FileHandler(pattern, limit, count, append);
        fileHandler.setLevel(Level.INFO);
        //fileHandler.setEncoding("utf8");
        fileHandler.setFormatter(formatter);
        if (outputErrorFile) fileHandler.setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                return record.getLevel().intValue() <= Level.INFO.intValue();
            }
        });
        return fileHandler;
    }

    private static FileHandler createErrorFileHandler() throws IOException {
        FileHandler fileHandler;
        fileHandler = new FileHandler(errorPattern, limit, count, append);
        fileHandler.setLevel(Level.SEVERE);
        //fileHandler.setEncoding("utf8");
        fileHandler.setFormatter(formatter);
        return fileHandler;
    }

    @Override
    public Logger getLogger(String name) {
        return buildLogger(name);
    }



}
