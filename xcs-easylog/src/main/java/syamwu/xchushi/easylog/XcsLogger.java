package syamwu.xchushi.easylog;

import syamwu.xchushi.easylog.constant.LoggerEntity;
import syamwu.xchushi.fw.log.Logger;

/**
 * logger类，描述了easy logger的基本行为
 * 
 * @author: syam_wu
 * @date: 2018-03-09
 */
public interface XcsLogger extends Logger {

    /**
     * 保存日志<br>
     * 
     * loggerEvent->loggerType  日志级别<br>
     * loggerEvent->thread  保存日志的线程<br>
     * loggerEvent->st  输出日志信息的线程栈，用以查找输出日志的类和方法<br>
     * loggerEvent->message  日志信息<br>
     * loggerEvent->t  异常<br>
     * loggerEvent->MDCmap  MDC附带参数<br>
     * loggerEvent->args  日志附带的参数<br>
     * entityType  使用nomal<br>
     */
    void append(LoggerEntity loggerEntity);

}
