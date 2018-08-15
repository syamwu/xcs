package syamwu.xchushi.easylog.logback;

import java.io.IOException;
import java.util.Map;

import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import syamwu.xchushi.easylog.XcsLogger;
import syamwu.xchushi.easylog.constant.LoggerEntity;
import syamwu.xchushi.easylog.constant.LoggerEvent;
import syamwu.xchushi.easylog.constant.LoggerType;
import syamwu.xchushi.easylog.factory.XcsLoggerConfigueFactory;
import syamwu.xchushi.easylog.proxy.EasyLogProxy;
import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.util.ConfigureUtils;
import syamwu.xchushi.fw.common.util.LifeCycleUtils;
import syamwu.xchushi.fw.factory.AbstractFactory;
import syamwu.xchushi.fw.factory.FactoryProxy;
import syamwu.xchushi.fw.proxy.InstanceProxy;

/**
 * 与logback结合，输出日志数据
 * 
 * @author: syam_wu
 * @date: 2018-03-09
 */
public class XcsLogbackAppender extends AppenderBase<LoggingEvent> {

    /* 配置文件路径 */
    protected String fileName;
    
    /**
     * 工厂代理类
     */
    protected InstanceProxy instanceProxy;

    /* 统一配置接口 */
    protected Configure config;

    /* Logger日志接口 */
    protected XcsLogger xcsLogger;

    protected boolean isInit = false;

    public XcsLogbackAppender() {
        super.addFilter(new XcsLoggerFilter());
    }

    @Override
    protected void append(LoggingEvent eventObject) {
        try {
            StackTraceElement[] sts = eventObject.getCallerData();
            Thread thread = Thread.currentThread();
            LoggerType loggerType = exchangeLevel(eventObject.getLevel());
            Throwable t = eventObject.getThrowableProxy() == null ? null
                    : ((ThrowableProxy) eventObject.getThrowableProxy()).getThrowable();
            Map<String, String> threadMap = null;
            try {
                threadMap = MDC.getCopyOfContextMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            xcsLogger.append(new LoggerEntity(new LoggerEvent(loggerType, thread, sts[0], eventObject.getMessage(), t,
                    threadMap, eventObject.getArgumentArray())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * logback在创建完成Appender之后会调用该方法，用以初始化Appender
     * 
     * @author syam_wu
     */
    @Override
    public void start() {
        started = true;
        try {
            initXcsLogbackLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Appender:
     * 1.获取工厂对象代理类
     * 2.通过代理类获取配置工厂，通过配置工厂生产配置对象；
     * 3.通过代理类获取logger工厂，通过logger工厂生产；
     * 4.为logger分配配置对象，并启动logger
     * 
     * @throws IOException
     */
    private void initXcsLogbackLogger() throws IOException {
        if (!isInit) {
            isInit = true;
            if (instanceProxy == null) {
                instanceProxy = EasyLogProxy.getInstance();
            }
            if(config == null){
                AbstractFactory<Configure> configFactory = FactoryProxy.getFactory(Configure.class, instanceProxy, true);
                if (configFactory instanceof XcsLoggerConfigueFactory) {
                    config = ((XcsLoggerConfigueFactory) configFactory).getFileConfigue(getClass(), fileName);
                } else {
                    config = configFactory.getInstance(getClass());
                }
            }
            AbstractFactory<XcsLogger> loggerFactory = FactoryProxy.getFactory(XcsLogger.class, instanceProxy, true);
            if (xcsLogger == null) {
                xcsLogger = loggerFactory.getInstance(XcsLogbackAppender.class);
            }
            if (xcsLogger != null && config != null) {
                ConfigureUtils.setConfigure(xcsLogger, config, false);
            }
            startLogger(xcsLogger);
        } else {
            isInit = true;
        }
    }

    /**
     * 转换level枚举类
     * 
     * @param level
     * @return
     */
    protected LoggerType exchangeLevel(Level level) {
        Asset.notNull(level);
        LoggerType logType = LoggerType.valueOf(level.levelStr);
        if (logType == null) {
            return LoggerType.UNKNOWN;
        }
        return logType;
    }

    public void startLogger(XcsLogger xcsLogger) {
        LifeCycleUtils.start(xcsLogger, false);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Configure getConfig() {
        return config;
    }

    public void setConfig(Configure config) {
        this.config = config;
    }

    public XcsLogger getXcsLogger() {
        return xcsLogger;
    }

    public void setXcsLogger(XcsLogger xcsLogger) {
        this.xcsLogger = xcsLogger;
    }
    
}
