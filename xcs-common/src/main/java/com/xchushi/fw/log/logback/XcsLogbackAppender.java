package com.xchushi.fw.log.logback;

import java.io.IOException;
import java.util.Map;

import org.slf4j.MDC;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.util.ConfigureUtils;
import com.xchushi.fw.common.util.StartingUtils;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.config.FileProperties;
import com.xchushi.fw.config.XcsConfigure;
import com.xchushi.fw.log.XcsLogger;
import com.xchushi.fw.log.XcsLoggerFactory;
import com.xchushi.fw.log.constant.LoggerEntity;
import com.xchushi.fw.log.constant.LoggerEvent;
import com.xchushi.fw.log.constant.LoggerType;
import com.xchushi.fw.log.elasticsearch.TestMDCBus;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * 与logback结合，输出日志数据
 * 
 * @author: syam_wu
 * @date: 2018-03-09
 */
public class XcsLogbackAppender extends AppenderBase<LoggingEvent> {

    protected String fileName;

    protected Configure config;

    protected XcsLogger xcsLogger;

    protected boolean isInit = false;

    public XcsLogbackAppender() {
        super.addFilter(new XcsLoggerFilter());
    }

    @Override
    protected void append(LoggingEvent eventObject) {
        try {
            // 未进行初始化时则进行初始化操作
            if (!isInit) {
                initXcsLogbackLogger();
            }
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

    private synchronized void initXcsLogbackLogger() throws IOException {
        if (!isInit) {
            isInit = true;
            if (config != null) {
                ConfigureFactory.setConfigure(config);
            } else {
                config = XcsConfigure.initConfigureAndGet(
                        new FileProperties(fileName == null ? StringConstant.CONFIG_FILE : fileName),
                        XcsLogbackAppender.class);
            }
            if (xcsLogger != null) {
                startLogger(xcsLogger);
            } else {
                xcsLogger = XcsLoggerFactory.getLogger(XcsLogbackAppender.class);
                startLogger(xcsLogger);
            }
            if (xcsLogger != null && config != null) {
                ConfigureUtils.setConfigure(xcsLogger, config, false);
            }
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
        StartingUtils.start(xcsLogger, false);
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
