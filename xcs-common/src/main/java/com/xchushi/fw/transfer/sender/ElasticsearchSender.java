package com.xchushi.fw.transfer.sender;

import org.slf4j.Logger;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.log.SysLoggerFactory;

/**
 * elasticsearch传输器
 * 
 * @author: SamJoker
 * @date: 2018
 */
@ConfigSetting(prefix = "sender")
public class ElasticsearchSender extends AbstractSender implements Sender  {
    
    //private static Logger logger = SysLoggerFactory.getLogger(ElasticsearchSender.class);
    
    private static ElasticsearchSender sender = null;
    
    public ElasticsearchSender() {
        super(null);
        if (sender == null) {
            sender = this;
        }
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void send(Object message) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void callBack(Object obj) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object synSend(Object obj) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
