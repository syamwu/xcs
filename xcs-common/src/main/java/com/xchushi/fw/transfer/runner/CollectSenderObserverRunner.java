package com.xchushi.fw.transfer.runner;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.SpliceEntity;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.executor.DefaultExecutor;
import com.xchushi.fw.common.executor.Executor;
import com.xchushi.fw.common.observer.Observer;
import com.xchushi.fw.common.util.StartingUtils;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.CallBackAble;
import com.xchushi.fw.transfer.collect.Collected;
import com.xchushi.fw.transfer.collect.StringQueueCollector;
import com.xchushi.fw.transfer.sender.AbstractSender;
import com.xchushi.fw.transfer.sender.SenderFactory;

/**
 * 收集观察主题发送来的数据，并传输
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class CollectSenderObserverRunner extends AbstractSenderRunner implements CallBackAble, Observer<String> {
    
    private static Logger logger = SysLoggerFactory.getLogger(CollectSenderObserverRunner.class);
    
    /**
     * 收集器,用以收集来自collect(R t)的数据
     */
    private Collected<SpliceEntity<String>, String> mainCollecter = null;

    private LinkedBlockingQueue<Entity<?>> failQueue = new LinkedBlockingQueue<Entity<?>>(
            Integer.MAX_VALUE);
    
    protected final LinkedBlockingQueue<Boolean> lockQueue = new LinkedBlockingQueue<Boolean>(Integer.MAX_VALUE);
    
    private ThreadPoolExecutor getThreadPoolExecutorByConfigure(Configure configure) {
        ThreadPoolExecutor threadPoolExecutor = null;
        if (configure != null) {
            try {
                Executor ex = configure.getBean(StringConstant.EXECUTOR_CLASS);
                if (ex != null) {
                    threadPoolExecutor = ex.getThreadPoolExecutor(configure, getClass());
                } else {
                    threadPoolExecutor = new DefaultExecutor().getThreadPoolExecutor(configure, getClass());
                }
            } catch (Exception e) {
                logger.error("HttpSender getThreadPoolExecutorByConfigure fail:" + e.getMessage(), e);
            }
        } else {
            threadPoolExecutor = new DefaultExecutor().getThreadPoolExecutor(getClass());
        }
        return threadPoolExecutor;
    }
    
    @Override
    public void run() {
        if (!started) {
            throw new InitException(this.toString() + " don't started!!");
        }
        for(;;) {
            try {
                Entity<?> sendEntity = null;
                if (failQueue == null || failQueue.isEmpty()) {
                    sendEntity = mainCollecter.collect();
                } else {
                    sendEntity = failQueue.poll();
                }
                if (sendEntity == null) {
                    lockQueue.poll(1l, TimeUnit.MILLISECONDS);
                    continue;
                }
                try {
                    tpe.submit(new SendTask(sendEntity, this));
                } catch (Exception e) {
                    sendingFailed(sendEntity, e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
            }
        }
    }
    
    @Override
    public void callBack(Object obj) {
        logger.debug("callBack:" + obj);
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        logger.debug("sendingFailed:" + message, e);
        if (message != null && Entity.class.isAssignableFrom(message.getClass())) {
            failQueue.offer((Entity<?>) message);
            caNotEmpty(false);
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(String t) {
        try {
            mainCollecter.collect(t);
            caNotEmpty(true);
        } catch (Exception e) {
            Asset.throwRuntimeException(e);
        }
    }
    
    protected void caNotEmpty(boolean locked) {
        try {
            lockQueue.offer(locked, 1l, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("caNotEmpty fail:" + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean started() {
        return started;
    }
    
    @Override
    public synchronized void start() {
        if (started()) {
            throw new InitException(this.toString() + " had started, Can't start it again!!");
        }
        started = true;
        if(configure == null){
            configure = ConfigureFactory.getConfigure(getClass());
        }
        if(mainCollecter == null){
            mainCollecter = new StringQueueCollector(configure, new LinkedBlockingQueue<String>(Integer.MAX_VALUE));
        }
        if(tpe == null){
            tpe = getThreadPoolExecutorByConfigure(configure);
        }
        if (sender == null) {
            sender = SenderFactory.getSender(getClass());
        }
        if (sender != null) {
            StartingUtils.start(sender, false);
        }
        tpe.execute(this);
    }

    @Override
    public synchronized void stop() {
        if (!started()) {
            throw new InitException(this.toString() + " doesn't started, Can't stop it!!");
        }
        started = false;
        if (sender != null) {
            StartingUtils.stop(sender, false);
        }
    }
    
    @SuppressWarnings({ "rawtypes" })
    public  Entity<?> send(Entity<String> msg) throws Exception {
        if(AbstractSender.class.isAssignableFrom(sender.getClass())){
            return (Entity)((AbstractSender)sender).synSend(msg);
        }
        sender.send(msg, this);
        return Asset.getNull();
    }
    
}
