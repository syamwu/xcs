package syamwu.xchushi.fw.common.executor;

import java.util.concurrent.ThreadPoolExecutor;

import syamwu.xchushi.fw.common.environment.Configure;

public interface Executor {

    ThreadPoolExecutor getThreadPoolExecutor(Configure config, Class<?> cls);

    ThreadPoolExecutor getThreadPoolExecutor(Class<?> cls);

}
