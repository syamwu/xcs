package com.xchushi.fw.log;

import com.xchushi.fw.log.constant.LoggerType;

public interface XcsLogger {

    void info(String message, Object... args);
    
    void info(Thread thread, StackTraceElement st, String format, Object... args);

    void error(String message);

    void error(String message, Throwable e);

    void offer(String message) throws Exception;

    void append(LoggerType loggerType, Thread thread, StackTraceElement st, String message, Throwable t, Object... args)
            throws Exception;

}
