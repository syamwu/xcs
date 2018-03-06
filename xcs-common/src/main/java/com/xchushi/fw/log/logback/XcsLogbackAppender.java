package com.xchushi.fw.log.logback;

import org.slf4j.Logger;

import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.util.StringUtil;
import com.xchushi.fw.config.XcsConfigure;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.log.XcsLogger;
import com.xchushi.fw.log.XcsLoggerFactory;
import com.xchushi.fw.log.constant.LoggerType;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

public class XcsLogbackAppender extends AppenderBase<LoggingEvent> {

    public String fileName;

    Configure config;

    XcsLogger xcsLogger;

    boolean isInit = false;

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

    private LoggerType exchangeLevel(Level level) {
        if (level == Level.OFF) {
            return LoggerType.OFF;
        } else if (level == Level.ERROR) {
            return LoggerType.ERROR;
        } else if (level == Level.WARN) {
            return LoggerType.WARN;
        } else if (level == Level.INFO) {
            return LoggerType.INFO;
        } else if (level == Level.DEBUG) {
            return LoggerType.DEBUG;
        } else if (level == Level.TRACE) {
            return LoggerType.TRACE;
        } else if (level == Level.ALL) {
            return LoggerType.ALL;
        }
        return LoggerType.UNKNOWN;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
