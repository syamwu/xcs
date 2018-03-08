package com.xchushi.fw.log.logback;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.util.StringUtil;
import com.xchushi.fw.config.XcsConfigure;
import com.xchushi.fw.log.XcsLogger;
import com.xchushi.fw.log.XcsLoggerFactory;
import com.xchushi.fw.log.constant.LoggerType;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

public class XcsLogbackAppender extends AppenderBase<LoggingEvent> {

    protected String fileName;

    protected Configure config;

    protected XcsLogger xcsLogger;

    protected boolean isInit = false;

    @Override
    protected void append(LoggingEvent eventObject) {
        try {
            if (!isInit) {
                initXcsLogbackLogger();
            }
            StackTraceElement[] sts = eventObject.getCallerData();
            if (!this.started || StringConstant.ROOTPACKAGE.equals(StringUtil.getRootPacke(eventObject.getLoggerName()))
                    || StringUtil.isRelevantRootPacke(sts, StringConstant.ROOTPACKAGE,
                            StringConstant.HTTPTOOLSPACKAGE)) {
                return;
            }
            Thread thread = Thread.currentThread();
            LoggerType loggerType = exchangeLevel(eventObject.getLevel());
            Throwable t = eventObject.getThrowableProxy() == null ? null
                    : ((ThrowableProxy) eventObject.getThrowableProxy()).getThrowable();
            xcsLogger.append(loggerType, thread,
                    StringUtil.isRelevantRootPacke(sts, StringConstant.ROOTPACKAGE) ? sts[1] : sts[0],
                    eventObject.getMessage(), t, eventObject.getArgumentArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void initXcsLogbackLogger() {
        if (!isInit && (this.config == null || this.xcsLogger == null)) {
            this.config = XcsConfigure.initConfigureAndGet(XcsLogbackAppender.class, fileName);
            this.xcsLogger = XcsLoggerFactory.getLogger(XcsLogbackAppender.class);
        }
        isInit = true;
    }

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

}
