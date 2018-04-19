package com.xchushi.fw.transfer.sender;

import com.xchushi.fw.common.Starting;

/**
 * 数据传输统一接口
 * 
 * @author: SamJoker
 * @date: 2018
 */
public interface Sender extends Starting {

    /**
     * 传输message
     * 
     * @param message
     * @throws Exception
     */
    void send(Object message) throws Exception;
    
    /**
     * 传输成功回调
     * 
     * @param obj
     */
    void callBack(Object obj);
    
    /**
     * 传输失败回调
     * 
     * @param message
     * @param e
     */
    void sendingFailed(Object message, Throwable e);
    
}
