package com.xchushi.fw.transfer.sender;

public interface Sender {

    void send(Object message) throws Exception;
    
    void callBack(Object obj);
    
    void sendingFailed(Object message, Throwable e);
    
}
