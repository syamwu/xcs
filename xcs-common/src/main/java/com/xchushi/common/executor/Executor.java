package com.xchushi.common.executor;

import java.util.concurrent.ThreadPoolExecutor;

import com.xchushi.common.environment.Configure;

public interface Executor {

    ThreadPoolExecutor getThreadPoolExecutor(Configure config, Class<?> cls);

}
