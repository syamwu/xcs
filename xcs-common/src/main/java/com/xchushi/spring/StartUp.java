package com.xchushi.spring;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import com.xchushi.common.exception.InitException;
import com.xchushi.log.SysLogger;
import com.xchushi.log.elasticsearch.config.EsLoggerConfig;
import com.xchushi.spring.environment.SpringConfigure;
import com.xchushi.transfer.executor.CollectSendExecutor;
import com.xchushi.transfer.sender.HttpSender;

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
