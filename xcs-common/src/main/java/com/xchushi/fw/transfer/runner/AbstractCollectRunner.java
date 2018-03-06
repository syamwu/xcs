package com.xchushi.fw.transfer.runner;

import java.util.concurrent.ThreadPoolExecutor;

import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.transfer.sender.AbstractSender;

public abstract class AbstractCollectRunner implements Runnable {

    protected Configure configure;

    protected AbstractSender sender;

    protected ThreadPoolExecutor tpe;
    
    protected AbstractCollectRunner(Configure configure, AbstractSender sender, ThreadPoolExecutor threadPoolExecutor){
        this.configure = configure;
        this.sender = sender;
        this.tpe = threadPoolExecutor;
    }
    
}
