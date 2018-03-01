
package xcs.log.elasticsearch.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

import xcs.log.SysLogger;
import xcs.log.constant.EsLoggerConstant;
import xcs.log.constant.LoggerType;
import xcs.log.elasticsearch.EsLogger;
import xcs.log.elasticsearch.MDCBus;
import xcs.log.elasticsearch.changer.Changer;
import xcs.log.elasticsearch.changer.NomalChanger;
import xcs.transfer.sender.HttpSender;
import xcs.transfer.sender.Sender;

public class TCPEsLogger implements EsLogger {

    private Changer changer;

    private Sender sender;
    
    // private static final String HTTPBULKHEAD = "{\"index\":{}}\n";

    private static Logger logger = SysLogger.getLogger(TCPEsLogger.class);

    @SuppressWarnings("unused")
    private Class<?> cls;

    public TCPEsLogger(Class<?> cls) {
        this.cls = cls;
        this.changer = NomalChanger.getChanger(null);
        this.sender = HttpSender.getSender();
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
            info(null, thread, thread.getStackTrace()[2], format, args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void info(LoggerType loggerType, String format, Object... args) {
        try {
            Thread thread = Thread.currentThread();
            info(loggerType, thread, thread.getStackTrace()[2], format, args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void info(LoggerType loggerType, Thread thread, StackTraceElement st, String format, Object... args) {
        try {
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String time = sf.format(date);
            Map sendMap = (Map) changer.changeInfo(loggerType, thread, st, MDCBus.getMap(), format, args);
            if (sendMap == null) {
                logger.warn("sendMap is null");
                sendMap = new LinkedHashMap<>();
                sendMap.put(EsLoggerConstant._MESSAGE, format);
            }
            sendMap.put(EsLoggerConstant.TIME_STAMP, time);
            // String message = HTTPBULKHEAD + JSON.toJSONString(sendMap) +
            // "\n\n";
            offer(JSON.toJSONString(sendMap));
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void error(String message, Throwable e) {
        try {
            Thread thread = Thread.currentThread();
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String time = sf.format(date);
            Map sendMap = (Map) changer.changeError(null, thread, thread.getStackTrace()[2], MDCBus.getMap(), message,
                    e);
            if (sendMap == null) {
                logger.warn("sendMap is null");
                sendMap = new LinkedHashMap<>();
                sendMap.put(EsLoggerConstant._MESSAGE, message);
            }
            sendMap.put(EsLoggerConstant.TIME_STAMP, time);
            offer(JSON.toJSONString(sendMap));
        } catch (Exception t) {
            logger.error(t.getMessage(), t);
        }
    }

}