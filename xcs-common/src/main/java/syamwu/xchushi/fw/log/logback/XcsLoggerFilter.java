package syamwu.xchushi.fw.log.logback;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import syamwu.xchushi.fw.common.constant.StringConstant;
import syamwu.xchushi.fw.common.util.StringUtil;

public class XcsLoggerFilter extends Filter<LoggingEvent> {

    /**
     * 
     * 因为框架本身也是使用了org.slf4j规范的log来输出运行日志，为了避免循环输出日志，这里需要屏蔽掉相应包路径的日志输出，同样的httpClient也是.
     * 若这里不进行筛选,则需要在logback.xml里面配置该包路径下的logger不输出至XcsLogbackAppender
     * @param event
     * @return
     * @author syam_wu
     */
    @Override
    public FilterReply decide(LoggingEvent event) {
        try {
            if (event == null) {
                return FilterReply.DENY;
            }
            StackTraceElement[] sts = event.getCallerData();
            String loggerName = event.getLoggerName();
            if (sts == null || sts.length < 1) {
                if (loggerName == null || loggerName.startsWith(StringConstant.ROOT_PACKAGE)
                        || loggerName.startsWith(StringConstant.HTTPTOOLS_PACKAGE)) {
                    return FilterReply.DENY;
                }
            } else {
                StackTraceElement st = sts[0];
                String currentClass = st.getClassName();
                if (currentClass == null || currentClass.startsWith(StringConstant.ROOT_PACKAGE)
                        || currentClass.startsWith(StringConstant.HTTPTOOLS_PACKAGE) || StringUtil.isRelevantRootPacke(
                                sts, StringConstant.ROOT_PACKAGE, StringConstant.HTTPTOOLS_PACKAGE)) {
                    return FilterReply.DENY;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FilterReply.ACCEPT;
    }

}
