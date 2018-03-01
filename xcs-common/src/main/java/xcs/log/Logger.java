package xcs.log;

public interface Logger {

    void info(String format, Object... args);
    
    void error(String message);
    
    void error(String message, Throwable e);
    
}
