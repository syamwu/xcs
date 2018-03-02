package com.xchushi.common.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xchushi.common.environment.Configure;

public final class DefaultExecutor implements Executor {

    private static ThreadPoolExecutor ex;

    @Override
    public final ThreadPoolExecutor getThreadPoolExecutor(Configure config, Class<?> cls) {
        if (ex == null) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            ex = new ThreadPoolExecutor(cpuCount * 2, cpuCount * 10, 10000l, TimeUnit.MILLISECONDS,
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
