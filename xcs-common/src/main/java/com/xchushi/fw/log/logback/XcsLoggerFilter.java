package com.xchushi.fw.log.logback;

import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.util.StringUtil;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class XcsLoggerFilter extends Filter<LoggingEvent>{

    @Override
    public FilterReply decide(LoggingEvent event) {
        StackTraceElement[] sts = event.getCallerData();
        // 因为框架本身也是使用了org.slf4j规范的log来输出运行日志，为了避免循环输出日志，这里需要屏蔽掉相应包路径的日志输出，同样的httpClient也是.
        // 若这里不进行筛选,则需要在logback.xml里面配置该包路径下的logger不输出至XcsLogbackAppender
        if (StringConstant.ROOTPACKAGE.equals(StringUtil.getRootPacke(event.getLoggerName(), 2))
                || StringConstant.HTTPTOOLSPACKAGE.equals(StringUtil.getRootPacke(event.getLoggerName(), 3))
                || StringUtil.isRelevantRootPacke(sts, StringConstant.ROOTPACKAGE, StringConstant.HTTPTOOLSPACKAGE)) {
            return FilterReply.DENY;
        }
        return FilterReply.ACCEPT;
    }

}
