package com.xchushi.fw.log;

import com.xchushi.fw.common.Starting;
import com.xchushi.fw.log.constant.LoggerEntity;

/**
 * 基础logger类，描述了logger的基本行为
 * 
 * @author: SamJoker
 * @date: 2018-03-09
 */
public interface XcsLogger extends Starting {

    /**
     * 保存info级别的日志
     * 
     * @param message
     *            日志信息
     * @param args
     *            日志附带参数
     */
    void info(String message, Object... args);

    /**
     * 保存info级别的日志
     * 
     * @param thread
     *            输出日志信息的线程
     * @param st
     *            输出日志信息的线程栈，用以查找输出日志的类和方法
     * @param format
     *            日志信息
     * @param args
     *            日志附带的参数
     */
    void info(Thread thread, StackTraceElement st, String format, Object... args);

    /**
     * 保存error级别的日志
     * 
     * @param message  日志信息
     */
    void error(String message);

    /**
     * 保存error级别的日志，并传入异常
     * 
     * @param message  日志信息
     * @param e  异常
     */
    void error(String message, Throwable e);
    
    /**
     * 保存日志<br>
     * 
     * loggerEvent->loggerType  日志级别<br>
     * loggerEvent->thread  保存日志的线程<br>
     * loggerEvent->st  输出日志信息的线程栈，用以查找输出日志的类和方法<br>
     * loggerEvent->message  日志信息<br>
     * loggerEvent->t  异常<br>
     * loggerEvent->args  日志附带的参数<br>
     * entityType  使用nomal<br>
     */
    void append(LoggerEntity loggerEntity);

}
