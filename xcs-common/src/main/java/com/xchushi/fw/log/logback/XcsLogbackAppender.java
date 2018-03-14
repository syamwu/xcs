package com.xchushi.fw.log.logback;

import java.io.IOException;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.util.StringUtil;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.config.FileProperties;
import com.xchushi.fw.config.XcsConfigure;
import com.xchushi.fw.log.XcsLogger;
import com.xchushi.fw.log.XcsLoggerFactory;
import com.xchushi.fw.log.constant.LoggerType;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * 与logback结合，输出日志数据
 * 
 * @author: SamJoker
 * @date: 2018-03-09
 */
public class XcsLogbackAppender extends AppenderBase<LoggingEvent> {

    protected String fileName;

    protected Configure config;

    protected XcsLogger xcsLogger;

    protected boolean isInit = false;

    @Override
    protected void append(LoggingEvent eventObject) {
        try {
            // 未进行初始化时则进行初始化操作
            if (!isInit) {
                initXcsLogbackLogger();
            }
            StackTraceElement[] sts = eventObject.getCallerData();
            // 因为框架本身也是使用了org.slf4j规范的log来输出运行日志，为了避免循环输出日志，这里需要屏蔽掉相应包路径的日志输出，同样的httpClient也是.
            // 若这里不进行筛选,则需要在logback.xml里面配置该包路径下的logger不输出至XcsLogbackAppender
            if (!this.started || StringConstant.ROOTPACKAGE.equals(StringUtil.getRootPacke(eventObject.getLoggerName()))
                    || StringUtil.isRelevantRootPacke(sts, StringConstant.ROOTPACKAGE,
                            StringConstant.HTTPTOOLSPACKAGE)) {
                return;
            }
            Thread thread = Thread.currentThread();
            LoggerType loggerType = exchangeLevel(eventObject.getLevel());
            Throwable t = eventObject.getThrowableProxy() == null ? null
                    : ((ThrowableProxy) eventObject.getThrowableProxy()).getThrowable();
            xcsLogger.append(loggerType, thread, sts[0], eventObject.getMessage(), t, eventObject.getArgumentArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void initXcsLogbackLogger() throws IOException {
        if (!isInit) {
            if (config != null) {
                ConfigureFactory.setConfigure(config);
            } else {
                config = XcsConfigure.initConfigureAndGet(
                        new FileProperties(fileName == null ? StringConstant.CONFIGFILE : fileName),
                        XcsLogbackAppender.class);
            }
            if (xcsLogger != null) {
                xcsLogger.start();
            } else {
                xcsLogger = XcsLoggerFactory.getLogger(XcsLogbackAppender.class);
                xcsLogger.start();
            }
        }
        isInit = true;
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
