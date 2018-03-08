package com.xchushi.fw.transfer.sender;

import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.transfer.collect.Collectible;

public abstract class AbstractSender implements Sender {
    
    @SuppressWarnings("rawtypes")
    protected Collectible collectible;
    
    protected Configure configure;
    
    protected AbstractSender(Configure configure){
        this.configure = configure;
    }

    @SuppressWarnings({ "rawtypes" })
    public void setCollectible(Collectible collectible){
        this.collectible = collectible;
    }
    
    public abstract Object synSend(Object obj) throws Exception;
    
}