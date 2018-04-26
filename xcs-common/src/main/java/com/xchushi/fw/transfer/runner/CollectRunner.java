package com.xchushi.fw.transfer.runner;

import java.util.concurrent.ThreadPoolExecutor;

import com.xchushi.fw.common.Starting;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.transfer.sender.AbstractSender;

/**
 * 收集器
 * 
 * @author: SamJoker
 * @date: 2018
 */
public abstract class CollectRunner implements Runnable, Starting {

    protected Configure configure;

    protected AbstractSender sender;

    protected ThreadPoolExecutor tpe;

    protected boolean started = false;
    
    public CollectRunner(){
    }

    protected CollectRunner(Configure configure, AbstractSender sender, ThreadPoolExecutor threadPoolExecutor) {
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

    public AbstractSender getSender() {
        return sender;
    }

    public void setSender(AbstractSender sender) {
        this.sender = sender;
    }

    public ThreadPoolExecutor getTpe() {
        return tpe;
    }

    public void setTpe(ThreadPoolExecutor tpe) {
        this.tpe = tpe;
    }

    public boolean started() {
        return started;
    }

    public void start() {
        if (started)
            return;
        started = true;
        tpe.execute(this);
    }

}
