
package com.xchushi.fw.log.elasticsearch.logger;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.log.constant.EsLoggerConstant;
import com.xchushi.fw.log.constant.LoggerType;
import com.xchushi.fw.log.elasticsearch.EsLogger;
import com.xchushi.fw.log.elasticsearch.changer.Changer;
import com.xchushi.fw.log.elasticsearch.changer.NomalChanger;
import com.xchushi.fw.transfer.sender.Sender;
import com.xchushi.fw.transfer.sender.SenderFactory;

public class TCPEsLogger implements EsLogger {

    private Changer changer;

    private Sender sender;

    private boolean started = false;

    private static Logger logger = SysLoggerFactory.getLogger(TCPEsLogger.class);

    private Class<?> cls;

    public TCPEsLogger() {
    }

    public TCPEsLogger(Class<?> cls) {
        this.cls = cls;
    }

    public TCPEsLogger(Class<?> cls, Changer changer, Sender sender) {
        this.cls = cls;
        this.changer = changer;
        this.sender = sender;
    }

    public static EsLogger getLogger(Class<?> cls, Changer changer, Sender sender) {
        return new TCPEsLogger(cls, changer, sender);
    }

    public static EsLogger getLogger(Class<?> cls) {
        return new TCPEsLogger(cls);
    }

    @Override
    public void info(String format, Object... args) {
        try {
            Thread thread = Thread.currentThread();
            info(thread, thread.getStackTrace()[2], format, args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void info(Thread thread, StackTraceElement st, String format, Object... args) {
        try {
            append(LoggerType.INFO, thread, st, format, null, args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void offer(String message) throws Exception {
        sender.send(message);
    }

    @Override
    public void error(String message) {
        error(message, null);
    }

    @Override
    public void error(String message, Throwable e) {
        try {
            Thread thread = Thread.currentThread();
            append(LoggerType.ERROR, thread, thread.getStackTrace()[2], message, e);
        } catch (Exception t) {
            logger.error(t.getMessage(), t);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void append(LoggerType loggerType, Thread thread, StackTraceElement st, String message, Throwable t,
            Object... args) {
        if (!started) {
            throw new InitException(this.toString() + " don't started!!");
        }
        try {
            Map threadMap = null;
            try {
                threadMap = MDC.getCopyOfContextMap();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            Map sendMap = (Map) changer.change(loggerType, thread, st, threadMap, message, t, args);
            if (sendMap == null) {
                logger.warn("sendMap is null");
                sendMap = new LinkedHashMap<>();
                sendMap.put(EsLoggerConstant._MESSAGE, message);
            }
            offer(JSON.toJSONString(sendMap));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Changer getChanger() {
        return changer;
    }

    public void setChanger(Changer changer) {
        this.changer = changer;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void start() {
        if (started) {
            throw new InitException(this.toString() + " had started, Can't start it again!!");
        }
        started = true;
        if (changer == null)
            changer = NomalChanger.getChanger(null);
        if (sender == null) {
            sender = SenderFactory.getSender(cls);
        }
        sender.start();
    }

    @Override
    public void stop() {
        if (!started) {
            throw new InitException(this.toString() + " doesn't started, Can't stop it!!");
        }
        started = false;
        if (sender != null && sender.started()) {
            sender.stop();
        }
    }

    @Override
    public boolean started() {
        return started;
    }

}