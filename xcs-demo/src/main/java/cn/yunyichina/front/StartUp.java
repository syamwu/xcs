package cn.yunyichina.front;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import syamwu.xchushi.fw.common.exception.InitException;
import syamwu.xchushi.fw.log.SysLoggerFactory;

public class StartUp {
    
    static Logger logger = SysLoggerFactory.getLogger(StartUp.class);
    
    public static void start(Environment environment){
        start(environment, null);
    }

    public static void start(Environment environment, ThreadPoolExecutor threadPoolExecutor) {
        if (environment == null) {
            throw new InitException("environment can't be null");
        }
        if (threadPoolExecutor == null) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            threadPoolExecutor = new ThreadPoolExecutor(cpuCount * 2, cpuCount * 10, 10000l, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        // ==================eslogger init==================
        SpringConfigure esloggerConfig = new SpringConfigure(environment, "eslogger.");
//        boolean collectSendEnable = esloggerConfig.getProperty("collectSendEnable", Boolean.class, true);
//        if (collectSendEnable) {
//            DefalutCollectSendRunner.initAndStart(esloggerConfig,
//                    HttpSender.getSender(esloggerConfig, threadPoolExecutor), threadPoolExecutor);
//        } else {
//            HttpSender.getSender(esloggerConfig, threadPoolExecutor);
//        }
        //HttpSender.getSender(esloggerConfig, threadPoolExecutor);
        logger.info("=====Initialize eslogger successful=====");
    }

}
