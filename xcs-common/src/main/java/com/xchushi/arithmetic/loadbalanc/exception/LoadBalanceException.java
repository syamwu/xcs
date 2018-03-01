package com.xchushi.arithmetic.loadbalanc.exception;

public class LoadBalanceException extends RuntimeException {

    private static final long serialVersionUID = -5920202034559925427L;

    public LoadBalanceException() {
        super();
    }

    public LoadBalanceException(String message) {
        super(message);
    }

    public LoadBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadBalanceException(Throwable cause) {
        super(cause);
    }

    protected LoadBalanceException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}