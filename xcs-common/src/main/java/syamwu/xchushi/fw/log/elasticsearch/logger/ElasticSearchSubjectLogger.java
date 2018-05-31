package syamwu.xchushi.fw.log.elasticsearch.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;

import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.Starting;
import syamwu.xchushi.fw.common.environment.Configurable;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.exception.InitException;
import syamwu.xchushi.fw.common.observer.AbstractSubject;
import syamwu.xchushi.fw.common.observer.Observer;
import syamwu.xchushi.fw.common.util.ConfigureUtils;
import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.StartingUtils;
import syamwu.xchushi.fw.config.ConfigureFactory;
import syamwu.xchushi.fw.log.SysLoggerFactory;
import syamwu.xchushi.fw.log.XcsLogger;
import syamwu.xchushi.fw.log.constant.EsLoggerConstant;
import syamwu.xchushi.fw.log.constant.LoggerEntity;
import syamwu.xchushi.fw.log.constant.LoggerEvent;
import syamwu.xchushi.fw.log.constant.LoggerType;
import syamwu.xchushi.fw.log.elasticsearch.changer.Changer;
import syamwu.xchushi.fw.log.elasticsearch.changer.NomalChanger;
import syamwu.xchushi.fw.transfer.runner.CollectSenderObserverRunner;

/**
 * ElasticSearch被观察的主题Logger类,当有日志进入时会通知观察者
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class ElasticSearchSubjectLogger extends AbstractSubject<String> implements XcsLogger, Starting, Configurable {

    private static Logger logger = SysLoggerFactory.getLogger(ElasticSearchSubjectLogger.class);

    private Configure configure;

    private Changer changer;

    private Observer<String> observer;

    private boolean started = false;

    @SuppressWarnings("unused")
    private Class<?> cls;

    private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String TIMEZONE = "GMT";

    public ElasticSearchSubjectLogger() {
    }

    public ElasticSearchSubjectLogger(Class<?> cls) {
        this.cls = cls;
    }

    public ElasticSearchSubjectLogger(Class<?> cls, Changer changer) {
        this.cls = cls;
        this.changer = changer;
    }

    public static XcsLogger getLogger(Class<?> cls, Changer changer) {
        return new ElasticSearchSubjectLogger(cls, changer);
    }

    public static XcsLogger getLogger(Class<?> cls) {
        return new ElasticSearchSubjectLogger(cls);
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
            append(LoggerType.INFO, thread, st, format, null, null, args);
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
            append(LoggerType.ERROR, thread, thread.getStackTrace()[2], message, e, null);
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
                loggerEvent.getT(), loggerEvent.getMDCmap(), loggerEvent.getArgs());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void append(LoggerType loggerType, Thread thread, StackTraceElement st, String message, Throwable t,
            Map<String, String> MDCmap, Object... args) {
        try {
            if (!started) {
                throw new InitException(this.toString() + " don't started!!");
            }
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
            sf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
            String time = sf.format(date);
            Map loggerMap = changer.change(loggerType, thread, st, MDCmap, message, t, args);
            if (loggerMap == null) {
                logger.warn("sendMap is null");
                loggerMap = new LinkedHashMap<>();
                loggerMap.put(EsLoggerConstant._MESSAGE, message);
            }
            loggerMap.put(EsLoggerConstant.TIME_STAMP, time);
            offer(JsonUtils.toJSONString(loggerMap));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void offer(String message) {
        nodifyObservers(message);
    }

    @Override
    public synchronized void start() {
        if (started) {
            throw new InitException(this.toString() + " had started, Can't start it again!!");
        }
        started = true;
        if (configure == null) {
            configure = ConfigureFactory.getConfigure(getClass());
        }
        if (changer == null) {
            changer = new NomalChanger();
            ConfigureUtils.setConfigure(changer, configure, true);
        }
        if(observer == null){
            observer = new CollectSenderObserverRunner();
        }
        if (observer != null) {
            attach(observer);
            ConfigureUtils.setConfigure(observer, configure, false);
            StartingUtils.start(observer, false);
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
