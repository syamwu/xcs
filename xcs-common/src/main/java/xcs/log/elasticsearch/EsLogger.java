package xcs.log.elasticsearch;

import xcs.log.Logger;
import xcs.log.constant.LoggerType;

public interface EsLogger extends Logger {
    
    void offer(String message) throws Exception;
    
    void info(LoggerType loggerType, String format, Object... args);

    void info(LoggerType loggerType, Thread thread, StackTraceElement st, String format, Object... args);
    
}