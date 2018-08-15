package syamwu.xchushi.easylog;

public interface LoggerBuilder<T extends XcsLogger> {

    T buildLogger(Class<?> cls);
    
}
