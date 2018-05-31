package syamwu.xchushi.fw.log;

import syamwu.xchushi.fw.common.Starting;
import syamwu.xchushi.fw.log.elasticsearch.logger.ElasticSearchSubjectLogger;

/**
 * 日志工厂，用以构建logger实例
 * 
 * @author: syam_wu
 * @date: 2018-03-09
 */
public class XcsLoggerFactory {

    public static XcsLogger getLogger(Class<?> cls) {
        XcsLogger xcsLogger = ElasticSearchSubjectLogger.getLogger(cls);
        return xcsLogger;
    }

    public static <T extends XcsLogger> T getLogger(Class<?> cls, LoggerBuilder<T> builder) {
        return builder.buildLogger(cls);
    }
    
    public static XcsLogger getLoggerAndStart(Class<?> cls) {
        XcsLogger xcsLogger = ElasticSearchSubjectLogger.getLogger(cls);
        if(Starting.class.isAssignableFrom(xcsLogger.getClass())){
            ((Starting)xcsLogger).start();
        }
        return xcsLogger;
    }

}
