package syamwu.xchushi.fw.log.constant;

import java.util.Map;

public class LoggerEvent {

    private LoggerType loggerType;

    private Thread thread;

    private StackTraceElement st;

    private String message;

    private Throwable t;

    private Object[] args;
    
    private Map<String,String> MDCmap;

    public LoggerEvent(LoggerType loggerType, Thread thread, StackTraceElement st, String message, Throwable t,Map<String,String> MDCmap,
            Object[] args) {
        this.loggerType = loggerType;
        this.thread = thread;
        this.st = st;
        this.message = message;
        this.t = t;
        this.MDCmap = MDCmap;
        this.args = args;
    }

    public LoggerType getLoggerType() {
        return loggerType;
    }

    public void setLoggerType(LoggerType loggerType) {
        this.loggerType = loggerType;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public StackTraceElement getSt() {
        return st;
    }

    public void setSt(StackTraceElement st) {
        this.st = st;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getT() {
        return t;
    }

    public void setT(Throwable t) {
        this.t = t;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Map<String, String> getMDCmap() {
        return MDCmap;
    }

    public void setMDCmap(Map<String, String> mDCmap) {
        MDCmap = mDCmap;
    }

}
