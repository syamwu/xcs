package xcs.log.elasticsearch.changer;

import java.util.Map;

import xcs.log.constant.LoggerType;

public interface Changer {

    public Object changeInfo(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, Object> threadParams, String format, Object... args) throws Exception;

    public Object changeError(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, Object> threadParams, String message, Throwable t) throws Exception;

}