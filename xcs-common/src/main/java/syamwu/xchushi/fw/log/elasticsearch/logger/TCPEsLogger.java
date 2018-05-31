
package syamwu.xchushi.fw.log.elasticsearch.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.MDC;

import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.Starting;
import syamwu.xchushi.fw.common.environment.Configurable;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.exception.InitException;
import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.StartingUtils;
import syamwu.xchushi.fw.log.SysLoggerFactory;
import syamwu.xchushi.fw.log.XcsLogger;
import syamwu.xchushi.fw.log.constant.EsLoggerConstant;
import syamwu.xchushi.fw.log.constant.LoggerEntity;
import syamwu.xchushi.fw.log.constant.LoggerEvent;
import syamwu.xchushi.fw.log.constant.LoggerType;
import syamwu.xchushi.fw.log.elasticsearch.changer.Changer;
import syamwu.xchushi.fw.log.elasticsearch.changer.NomalChanger;
import syamwu.xchushi.fw.transfer.CallBackAble;
import syamwu.xchushi.fw.transfer.sender.Sender;
import syamwu.xchushi.fw.transfer.sender.SenderFactory;

@Deprecated
public class TCPEsLogger implements XcsLogger, CallBackAble, Starting, Configurable {

    private Changer changer;

    private Sender sender;
    
    private boolean started = false;

    private static Logger logger = SysLoggerFactory.getLogger(TCPEsLogger.class);

    private Class<?> cls;
    
    private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    private static final String TIMEZONE = "GMT";

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

    public static XcsLogger getLogger(Class<?> cls, Changer changer, Sender sender) {
        return new TCPEsLogger(cls, changer, sender);
    }

    public static XcsLogger getLogger(Class<?> cls) {
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
        sender.send(message, this);
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
    
    @Override
    public void append(LoggerEntity loggerEntity) {
        Asset.notNull(loggerEntity);
        LoggerEvent loggerEvent = loggerEntity.getValue();
        Asset.notNull(loggerEvent);
        append(loggerEvent.getLoggerType(), loggerEvent.getThread(), loggerEvent.getSt(), loggerEvent.getMessage(),
                loggerEvent.getT(), loggerEvent.getArgs());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void append(LoggerType loggerType, Thread thread, StackTraceElement st, String message, Throwable t,
            Object... args) {
        try {
            if (!started) {
                throw new InitException(this.toString() + " don't started!!");
            }
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
            sf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
            String time = sf.format(date);
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
            sendMap.put(EsLoggerConstant.TIME_STAMP, time);
            offer(JsonUtils.toJSONString(sendMap));
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
    
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
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
        StartingUtils.start(sender, false);
    }

    @Override
    public void stop() {
        if (!started) {
            throw new InitException(this.toString() + " doesn't started, Can't stop it!!");
        }
        started = false;
        if (sender != null && StartingUtils.started(sender)) {
            StartingUtils.stop(sender);
        }
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public void callBack(Object obj) {
        
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        
    }

    @Override
    public void setConfigure(Configure configure) {
        
    }

}