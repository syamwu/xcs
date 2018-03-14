package com.xchushi.fw.transfer.sender;

import com.xchushi.fw.common.Starting;

public interface Sender extends Starting {

    void send(Object message) throws Exception;
    
    void callBack(Object obj);
    
    void sendingFailed(Object message, Throwable e);
    
}
