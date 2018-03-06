package com.xchushi.fw.log.elasticsearch.changer;

import java.util.Map;

import com.xchushi.fw.log.constant.LoggerType;

public interface Changer {

    Object change(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, Object> threadParams, String message, Throwable t,Object... args) throws Exception;

}