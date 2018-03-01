package xcs.spring;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import xcs.common.exception.InitException;
import xcs.log.SysLogger;
import xcs.log.elasticsearch.config.EsLoggerConfig;
import xcs.spring.environment.SpringConfigure;
import xcs.transfer.executor.CollectSendExecutor;
import xcs.transfer.sender.HttpSender;

public class StartUp {
    
    static Logger logger = SysLogger.getLogger(StartUp.class);
    
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
        EsLoggerConfig.initConfig(environment);
        String uri = esloggerConfig.getProperty("index", "test_syslog") + "/" + esloggerConfig.getProperty("type", "test");
        boolean collectSendEnable = esloggerConfig.getProperty("collectSendEnable", Boolean.class, true);
        if (collectSendEnable) {
            CollectSendExecutor.initAndStart(esloggerConfig,
                    HttpSender.getSender(uri, esloggerConfig, threadPoolExecutor), threadPoolExecutor);
        } else {
            HttpSender.getSender(uri, esloggerConfig, threadPoolExecutor);
        }
        logger.info("=====Initialize eslogger successful=====");
    }

}
