package com.xchushi.fw.log.elasticsearch.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.Starting;
import com.xchushi.fw.common.environment.Configurable;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.observer.AbstractSubject;
import com.xchushi.fw.common.observer.Observer;
import com.xchushi.fw.common.util.ConfigureUtils;
import com.xchushi.fw.common.util.StartingUtils;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.log.constant.EsLoggerConstant;
import com.xchushi.fw.log.constant.LoggerEntity;
import com.xchushi.fw.log.constant.LoggerEvent;
import com.xchushi.fw.log.constant.LoggerType;
import com.xchushi.fw.log.elasticsearch.EsLogger;
import com.xchushi.fw.log.elasticsearch.changer.Changer;
import com.xchushi.fw.log.elasticsearch.changer.NomalChanger;

public class SubjectLogger extends AbstractSubject<String> implements EsLogger, Starting, Configurable {

    private static Logger logger = SysLoggerFactory.getLogger(TCPEsLogger.class);
    
    private Configure configure;

    private Changer changer;

    private Observer<String> observer;

    private boolean started = false;

    @SuppressWarnings("unused")
    private Class<?> cls;

    private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String TIMEZONE = "GMT";

    public SubjectLogger() {
    }

    public SubjectLogger(Class<?> cls) {
        this.cls = cls;
    }

    public SubjectLogger(Class<?> cls, Changer changer) {
        this.cls = cls;
        this.changer = changer;
    }

    public static EsLogger getLogger(Class<?> cls, Changer changer) {
        return new SubjectLogger(cls, changer);
    }

    public static EsLogger getLogger(Class<?> cls) {
        return new SubjectLogger(cls);
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
            offer(JSON.toJSONString(sendMap));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void offer(String message) throws Exception {
        nodifyObservers(message);
    }
    
    @Override
    public synchronized void start() {
        if (started) {
            throw new InitException(this.toString() + " had started, Can't start it again!!");
        }
        started = true;
        if(configure == null){
            configure = ConfigureFactory.getConfigure(getClass());
        }
        if (changer == null){
            changer = new NomalChanger();
            ConfigureUtils.setConfigure(changer, configure, true);
        }
        if (observer != null) {
            attach(observer);
            ConfigureUtils.setConfigure(observer, configure, false);
            StartingUtils.start(observer);
        }
    }

    @Override
    public void stop() {
        if (!started) {
            throw new InitException(this.toString() + " doesn't started, Can't stop it!!");
        }
        started = false;
        if (observer != null) {
            detach(observer);
        }
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public void setConfigure(Configure configure) {
        this.configure = configure;
    }

    public Changer getChanger() {
        return changer;
    }

    public void setChanger(Changer changer) {
        this.changer = changer;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Observer<String> getObserver() {
        return observer;
    }

    public void setObserver(Observer<String> observer) {
        this.observer = observer;
    }

}
