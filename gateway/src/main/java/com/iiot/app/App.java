package com.iiot.app;

import com.iiot.jdbc.BaseInfoCached;
import com.iiot.net.AllQueue;
import com.iiot.net.ImportQueue;
import com.iiot.net.Kafka;
import com.iiot.net.TcpServerIIOT;
import com.iiot.util.ExceptionUtil;
import com.iiot.util.PropertiesUtilLocal;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;

public class App {
    static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        // 设置LOGGER
//        String rootPath=new App().getClass().getResource("/").getFile();
        //当前目录路径
//        String currentPath1=new App().getClass().getResource(".").getFile();
//        System.out.println("根目录：" + rootPath);
//        System.out.println("当前目录路径：" + currentPath1);
        InputStream path = new App().getClass().getClassLoader().getResourceAsStream("conf/log4j.properties");
//        InputStream path = app.getClass().getClassLoader().getResourceAsStream("/opt/gateway/conf/log4j.properties");
        PropertyConfigurator.configure(path);

//        PropertiesUtilLocal.getConfigInternal(); // 目前正在用的写法
        logger.info("begin");


        // 启动数据库资料获取
        BaseInfoCached baseInfo = new BaseInfoCached();
        baseInfo.doStart();
        // 启动数据监听
//        NetIIOT.start();
        TcpServerIIOT.getTcpServer().start();

//        try {
//            ImportQueue.start();
//            AllQueue.onsStart();
//        } catch (Exception e) {
//            logger.error("初始化位置数据缓存失败：" + ExceptionUtil.getStackStr(e));
//            return;
//        }
//
//        new Kafka().start();

        // 退出的时候捕获
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                baseInfo.Stop();
//              NetIIOT.stop();

//                ImportQueue.stop();
//                try {
//                    AllQueue.onsStop();
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                new Kafka().stop();
                TcpServerIIOT.getTcpServer().stop();
            }
        }));

    }

}
