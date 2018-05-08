package com.xchushi.fw.common.exception;

/**
 * 
 * @author syam_wu
 * @date 2018-1-26
 */
public class SenderFailException extends RuntimeException {

    private static final long serialVersionUID = -5920202034559973287L;

    public SenderFailException() {
        super();
    }

    public SenderFailException(String message) {
        super(message);
    }

    public SenderFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public SenderFailException(Throwable cause) {
        super(cause);
    }

    protected SenderFailException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}