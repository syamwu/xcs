package com.xchushi.fw.common.executor;

import java.util.concurrent.ThreadPoolExecutor;

import com.xchushi.fw.common.environment.Configure;

public interface Executor {

    ThreadPoolExecutor getThreadPoolExecutor(Configure config, Class<?> cls);

}
