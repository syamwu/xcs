package syamwu.xchushi.easylog.factory;

import syamwu.xchushi.easylog.LoggerBuilder;
import syamwu.xchushi.easylog.XcsLogger;
import syamwu.xchushi.easylog.elasticsearch.logger.ElasticSearchSubjectLogger;
import syamwu.xchushi.fw.common.LifeCycle;
import syamwu.xchushi.fw.factory.AbstractFactory;

public class LoggerFactory extends AbstractFactory<XcsLogger> {

    /**
     * 通过logger构造器获取Logger
     * 
     * @param cls
     * @param builder
     * @return
     */
    public <T extends XcsLogger> T getLogger(Class<?> cls, LoggerBuilder<T> builder) {
        return builder.buildLogger(cls);
    }

    /**
     * 获取Logger并执行开始
     * 
     * @param cls
     * @return
     */
    public XcsLogger getLoggerAndStart(Class<?> cls) {
        XcsLogger xcsLogger = getInstance(cls);
        if (LifeCycle.class.isAssignableFrom(xcsLogger.getClass())) {
            ((LifeCycle) xcsLogger).start();
        }
        return xcsLogger;
    }

    @Override
    public XcsLogger getInstance(Class<?> exer, Object... objs) {
        XcsLogger xcsLogger = ElasticSearchSubjectLogger.getLogger(exer);
        return xcsLogger;
    }

}
