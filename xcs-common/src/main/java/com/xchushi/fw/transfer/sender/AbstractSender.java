package com.xchushi.fw.transfer.sender;

import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.transfer.collect.Collected;

/**
 * 抽象传输器
 * 
 * @author: SamJoker
 * @date: 2018
 */
public abstract class AbstractSender implements Sender {
    
    @SuppressWarnings("rawtypes")
    protected Collected collected;
    
    protected Configure configure;
    
    protected boolean started = false;
    
    protected AbstractSender(Configure configure){
        this.configure = configure;
    }

    @SuppressWarnings({ "rawtypes" })
    public void setCollectible(Collected collected){
        this.collected = collected;
    }
    
    public boolean started(){
        return started;
    }
    
    public abstract Object synSend(Object obj) throws Exception;
    
}
