package com.xchushi.transfer.sender;

import com.xchushi.transfer.collect.Collectible;

public abstract class AbstractSender implements Sender {
    
    @SuppressWarnings("rawtypes")
    protected Collectible collectible;

    @SuppressWarnings({ "rawtypes" })
    public void setCollectible(Collectible collectible){
        this.collectible = collectible;
    }
    
    public abstract Object synSend(Object obj) throws Exception;
    
}
