package syamwu.xchushi.fw.log.elasticsearch.changer;

import java.util.Map;

import syamwu.xchushi.fw.log.constant.LoggerType;

/**
 * 日志数据构建接口
 * 
 * @author: syam_wu
 * @date: 2018-03-09
 */
public interface Changer {

    /**
     * 构建日志数据的格式和结构
     * 
     * @param loggerType
     * @param thread
     * @param st
     * @param threadParams
     * @param message
     * @param t
     * @param args
     * @return
     * @throws Exception
     */
    Map<String,?> change(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, ?> threadParams, String message, Throwable t,Object... args) throws Exception;

}