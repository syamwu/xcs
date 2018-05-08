package com.xchushi.fw.log.elasticsearch.exception;

/**
 * 
 * @author syam_wu
 * @date 2018-1-26
 */
public class EsLoggerInitException extends RuntimeException{

    private static final long serialVersionUID = -5920202034559973287L;
    
    
    public EsLoggerInitException() {
        super();
    }

    public EsLoggerInitException(String message) {
        super(message);
    }

    public EsLoggerInitException(String message, Throwable cause) {
        super(message, cause);
    }
    public EsLoggerInitException(Throwable cause) {
        super(cause);
    }

    protected EsLoggerInitException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}