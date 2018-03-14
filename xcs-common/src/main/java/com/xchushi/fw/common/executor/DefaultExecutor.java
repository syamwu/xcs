package com.xchushi.fw.common.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.common.environment.Configure;

@ConfigSetting(prefix = "executor")
public final class DefaultExecutor implements Executor {

    private static ThreadPoolExecutor ex;

    @Override
    public final ThreadPoolExecutor getThreadPoolExecutor(Configure config, Class<?> cls) {
        if (config != null && ex == null) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            ex = new ThreadPoolExecutor(config.getProperty("corePoolSize", Integer.class, cpuCount * 2),
                    config.getProperty("maximumPoolSize", Integer.class, cpuCount * 10),
                    config.getProperty("keepAliveTime", Long.class, 10000l), TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        return ex;
    }

    public final static ThreadPoolExecutor getTPE(Configure config, Class<?> cls) {
        if (ex == null) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            ex = new ThreadPoolExecutor(cpuCount * 2, cpuCount * 10, 10000l, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        return ex;
    }

}
