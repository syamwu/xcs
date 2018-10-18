package syamwu.xchushi.fw.transfer.runner;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.constant.StringConstant;
import syamwu.xchushi.fw.common.entity.Entity;
import syamwu.xchushi.fw.common.entity.Entity.EntityType;
import syamwu.xchushi.fw.common.entity.SpliceEntity;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.exception.InitException;
import syamwu.xchushi.fw.common.executor.DefaultExecutor;
import syamwu.xchushi.fw.common.executor.Executor;
import syamwu.xchushi.fw.common.observer.Observer;
import syamwu.xchushi.fw.common.util.LifeCycleUtils;
import syamwu.xchushi.fw.factory.FactoryProxy;
import syamwu.xchushi.fw.factory.SenderFactory;
import syamwu.xchushi.fw.log.SysLoggerFactory;
import syamwu.xchushi.fw.transfer.CallBackAble;
import syamwu.xchushi.fw.transfer.collect.Collected;
import syamwu.xchushi.fw.transfer.collect.StringQueueCollector;
import syamwu.xchushi.fw.transfer.sender.AbstractSender;

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

    /**
     * 失败队列
     */
    private LinkedBlockingQueue<Entity<?>> failQueue = new LinkedBlockingQueue<Entity<?>>(Integer.MAX_VALUE);

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

    /**
     * 数据收集和任务分发主线程
     * 
     * @author syam_wu
     */
    @Override
    public void run() {
        if (!started) {
            throw new InitException(this.toString() + " don't started!!");
        }
        for (;;) {
            try {
                Entity<?> sendEntity = null;
                if (failQueue == null || failQueue.isEmpty()) {
                    sendEntity = mainCollecter.collect();
                } else {
                    sendEntity = failQueue.poll();
                }
                if (sendEntity == null) {
                    continue;
                }
                try {
                    threadPoolExecutor.submit(new SendTask(sendEntity, this));
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
        //logger.debug("callBack:" + obj);
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        if (message != null && Entity.class.isAssignableFrom(message.getClass())) {
            ((Entity<?>) message).setEntityType(EntityType.reSend);
            failQueue.offer((Entity<?>) message);
            caNotEmpty(false);
        } else {
            logger.error("sendingFailed:" + message, e);
        }
    }

    @Override
    public void notify(String t) {
        try {
            mainCollecter.collect(t);
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
        if (configure == null) {
            configure = FactoryProxy.getFactory(Configure.class).getInstance(getClass());
        }
        if (mainCollecter == null) {
            mainCollecter = new StringQueueCollector(configure, new LinkedBlockingQueue<String>(Integer.MAX_VALUE));
        }
        if (threadPoolExecutor == null) {
            threadPoolExecutor = getThreadPoolExecutorByConfigure(configure);
        }
        if (sender == null) {
            sender = SenderFactory.getSender(getClass(), configure);
        }
        if (sender != null) {
            LifeCycleUtils.start(sender, false);
        }
        threadPoolExecutor.execute(this);
    }

    @Override
    public synchronized void stop() {
        if (!started()) {
            throw new InitException(this.toString() + " doesn't started, Can't stop it!!");
        }
        started = false;
        if (sender != null) {
            LifeCycleUtils.stop(sender, false);
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public Entity<?> send(Entity<String> msg) throws Exception {
        if (AbstractSender.class.isAssignableFrom(sender.getClass())) {
            return (Entity) ((AbstractSender) sender).synSend(msg);
        }
        sender.send(msg, this);
        return Asset.getNull();
    }

}
