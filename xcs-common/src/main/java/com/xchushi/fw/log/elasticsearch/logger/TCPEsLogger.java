
package com.xchushi.fw.log.elasticsearch.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.log.constant.EsLoggerConstant;
import com.xchushi.fw.log.constant.LoggerType;
import com.xchushi.fw.log.elasticsearch.EsLogger;
import com.xchushi.fw.log.elasticsearch.MDCBus;
import com.xchushi.fw.log.elasticsearch.changer.Changer;
import com.xchushi.fw.log.elasticsearch.changer.NomalChanger;
import com.xchushi.fw.transfer.sender.Sender;
import com.xchushi.fw.transfer.sender.SenderFactory;

public class TCPEsLogger implements EsLogger {

    private Changer changer;

    private Sender sender;
    
    // private static final String HTTPBULKHEAD = "{\"index\":{}}\n";

    private static Logger logger = SysLoggerFactory.getLogger(TCPEsLogger.class);

    @SuppressWarnings("unused")
    private Class<?> cls;

    public TCPEsLogger(Class<?> cls) {
        this.cls = cls;
        this.changer = NomalChanger.getChanger(null);
        this.sender = SenderFactory.getSender(cls);
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

    @Override
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
            Object... args) throws Exception {
        // TODO Auto-generated method stub
        try {
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String time = sf.format(date);
            Map sendMap = (Map) changer.change(loggerType, thread, st, MDCBus.getMap(), message, t, args);
            if (sendMap == null) {
                logger.warn("sendMap is null");
                sendMap = new LinkedHashMap<>();
                sendMap.put(EsLoggerConstant._MESSAGE, message);
            }
            sendMap.put(EsLoggerConstant.TIME_STAMP, time);
            // String message = HTTPBULKHEAD + JSON.toJSONString(sendMap) +
            // "\n\n";
            offer(JSON.toJSONString(sendMap));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}