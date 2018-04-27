package com.xchushi.fw.transfer.runner;

import java.util.concurrent.ThreadPoolExecutor;

import com.xchushi.fw.common.Starting;
import com.xchushi.fw.common.environment.Configurable;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.transfer.sender.AbstractSender;
import com.xchushi.fw.transfer.sender.Sender;

/**
 * 收集器
 * 
 * @author: SamJoker
 * @date: 2018
 */
public abstract class AbstractSenderRunner implements Runnable, Starting, Configurable {

    protected Configure configure;

    protected Sender sender;

    protected ThreadPoolExecutor tpe;

    protected boolean started = false;
    
    public AbstractSenderRunner(){
    }

    protected AbstractSenderRunner(Configure configure, Sender sender, ThreadPoolExecutor threadPoolExecutor) {
        this.configure = configure;
        this.sender = sender;
        this.tpe = threadPoolExecutor;
    }

    public Configure getConfigure() {
        return configure;
    }

    public void setConfigure(Configure configure) {
        this.configure = configure;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public ThreadPoolExecutor getTpe() {
        return tpe;
    }

    public void setTpe(ThreadPoolExecutor tpe) {
        this.tpe = tpe;
    }

}
