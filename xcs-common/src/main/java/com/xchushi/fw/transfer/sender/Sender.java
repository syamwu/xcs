package com.xchushi.fw.transfer.sender;

import com.xchushi.fw.transfer.CallBackAble;

/**
 * 数据传输统一接口
 * 
 * @author: SamJoker
 * @date: 2018
 */
public interface Sender {

    /**
     * 传输message
     * 
     * @param message
     * @throws Exception
     */
    void send(Object message , CallBackAble callBackAble) throws Exception;
    
}
