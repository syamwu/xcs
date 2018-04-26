package com.xchushi.fw.transfer.sender;

import com.xchushi.fw.annotation.ConfigSetting;

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
        synSend(message);
    }

    @Override
    public void callBack(Object obj) {
        System.out.println("ElasticsearchSender call back!!!");
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        System.out.println("ElasticsearchSender sendingFailed!!!");
    }

    @Override
    public Object synSend(Object obj) throws Exception {
        System.out.println("ElasticsearchSender sendObj:"+obj);
        return null;
    }
}
