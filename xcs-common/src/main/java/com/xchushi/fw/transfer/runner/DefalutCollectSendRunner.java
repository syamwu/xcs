package com.xchushi.fw.transfer.runner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.Entity.EntityType;
import com.xchushi.fw.common.entity.SimpleEntity;
import com.xchushi.fw.common.entity.SpliceEntity;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.util.SimpleFileQueue;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.CallBackAble;
import com.xchushi.fw.transfer.collect.Collected;
import com.xchushi.fw.transfer.sender.AbstractSender;

@Deprecated
@ConfigSetting(prefix = "runner")
public final class DefalutCollectSendRunner<T> extends AbstractSenderRunner implements CallBackAble {

    /**
     * 保存发送失败内容的文件地址
     */
    private String failSendFile = "collect/sendFail.txt";

    /**
     * 是否启用失败保存
     */
    private boolean failSendFileEnable = true;

    /**
     * 发送超时时间
     */
    private int sendTimeOut = 10_000;
    
    public static AtomicInteger okCount = new AtomicInteger(0);

    /**
     * 收集器,用以收集来自collect(R t)的数据
     */
    private Collected<SpliceEntity<T>, T> mainCollecter = null;

    private LinkedBlockingQueue<Entity<T>> failQueue = new LinkedBlockingQueue<Entity<T>>(
            Integer.MAX_VALUE);

    /**
     * 文件队列
     */
    private SimpleFileQueue<T> failFileQueue = null;

    private static Logger logger = SysLoggerFactory.getLogger(DefalutCollectSendRunner.class);
    
    /**
     * 当需要使用文件队列failFileQueue的时候需要传入一个不为null的Entity类
     */
    private SpliceEntity<T> emptyT;
    
    public DefalutCollectSendRunner(AbstractSender sd, Collected<SpliceEntity<T>, T> collected,
            ThreadPoolExecutor threadPoolExecutor, SpliceEntity<T> emptyT) {
        super(ConfigureFactory.getConfigure(DefalutCollectSendRunner.class), sd, threadPoolExecutor);
        Asset.notNull(sd, "sender can't be null");
        Asset.notNull(threadPoolExecutor, "threadPoolExecutor can't be null");
        this.emptyT = emptyT;
        try {
            initCollectSendExecutor(sd, collected);
        } catch (Exception e) {
            throw new InitException("initCollectSendExecutor fail:" + e.getMessage(), e);
        }
        logger.debug("CollectSendRunner created");
    }

    public DefalutCollectSendRunner(AbstractSender sd, Collected<SpliceEntity<T>, T> collected,
            ThreadPoolExecutor threadPoolExecutor) {
        this(sd, collected, threadPoolExecutor, null);
    }

    private void initCollectSendExecutor(AbstractSender sd, Collected<SpliceEntity<T>, T> collected) throws IOException {
        mainCollecter = collected;
        if (configure == null) {
            configure = ConfigureFactory.getConfigure(DefalutCollectSendRunner.class);
        }
        if (configure != null) {
            failSendFileEnable = configure.getProperty("failSendFileEnable", Boolean.class, failSendFileEnable);
            failSendFile = configure.getProperty("failSendFile", String.class, failSendFile);
            sendTimeOut = configure.getProperty("sendTimeOut", Integer.class, sendTimeOut);
        }
        sd.setCollectible(mainCollecter);
    }

    @SuppressWarnings("unchecked")
    private void initFailQueue(SimpleFileQueue<T> failFileQueue) {
        Asset.notNull(failFileQueue);
        Asset.notNull(emptyT,"emptyT can't be null!!");
        try {
            long time = System.currentTimeMillis();
            List<String> msgs = failFileQueue.getMessages();
            if (msgs != null) {
                for (String msg : msgs) {
//                    StringBuffer strBuff = new StringBuffer();
//                    List<String> messages = JSON.parseArray(msg, String.class);
//                    if (messages == null) {
//                        continue;
//                    }
//                    for (String message : messages) {
//                        strBuff.append(message + StringConstant.NEWLINE);
//                    }
                    failQueue.offer((Entity<T>) Entity.bulidEntity(msg, emptyT.getClass()));
                }
            }
            logger.info("加载发送失败文件:" + failFileQueue.getFilePath() + ",用时:" + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    @Override
    public void stop() {
        started = false;
    }
    
    @Override
    public synchronized void start() {
        if (started)
            return;
        started = true;
        if (failSendFileEnable) {
            try {
                failFileQueue = new SimpleFileQueue<T>(failSendFile);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            tpe.execute(new Runnable() {
                @Override
                public void run() {
                    initFailQueue(failFileQueue);
                }
            });
        }
        tpe.execute(this);
    }

    @Override
    public void run() {
        while (true && started) {
            try {
                Entity<T> sendEntity = null;
                if (failQueue.isEmpty()) {
                    sendEntity = mainCollecter.collect();
                } else {
                    if (failSendFileEnable) {
//                        while (!mainCollecter.isEmpty()) {
//                            fastFailMessage(mainCollecter.collect());
//                        }
                        sendEntity = failQueue.peek();
                    } else {
                        sendEntity = failQueue.poll();
                    }
                }
                if (sendEntity == null) {
                    Thread.sleep(1);
                    continue;
                }
                try {
                    tpe.submit(new SendTask(sendEntity, this));
                } catch (Exception e) {
                    sendingFailed(sendEntity, e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void callBack(Object obj) {
        okCount.incrementAndGet();
        if (Entity.class.isAssignableFrom(obj.getClass())) {
            logger.debug("succes failQueue.size:" + failQueue.size());
            Entity response = (Entity) obj;
            if (failSendFileEnable && EntityType.reSend == response.getEntityType()) {
                failQueue.poll();
                tpe.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            failFileQueue.poll();
                        } catch (IOException e) {
                            logger.error("failFileQueue.poll fail:" + e.getMessage(), e);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        if (e != null) {
            logger.debug("sendingFailed:" + e.getClass() + "-" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        if (message == null) {
            return;
        }
        try {
            fastFailMessage(message);
        } catch (Exception e1) {
            logger.error(e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void fastFailMessage(Object message) throws IOException, InterruptedException {
        if (failSendFileEnable && Entity.class.isAssignableFrom(message.getClass())) {
            Entity<T> senEntity = (Entity<T>) message;
            if (EntityType.nomal == senEntity.getEntityType()) {
                failQueue.offer(new SimpleEntity<T>(senEntity.getValue(), EntityType.reSend));
                //String msgList = MessageUtil.messageToListStr((String) senEntity.getData(), StringConstant.NEWLINE);
                String msgList = Entity.bulidEntityString(senEntity);
                Asset.notNull(msgList, "exchange senEntity.message fail!");
                failFileQueue.offer(msgList);
            }
        }else if (Entity.class.isAssignableFrom(message.getClass())) {
            Entity<T> senEntity = (Entity<T>) message;
            failQueue.offer(new SimpleEntity<T>(senEntity.getValue(), EntityType.reSend));
        }
    }

    @SuppressWarnings("rawtypes")
    class SendTask implements Callable<Entity> {

        private Entity<T> msg;
        private DefalutCollectSendRunner defalutCollectSendRunner;

        SendTask(Entity<T> sendEntity, DefalutCollectSendRunner defalutCollectSendRunner) {
            this.msg = sendEntity;
            this.defalutCollectSendRunner = defalutCollectSendRunner;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Entity<T> call() throws Exception {
            Entity<T> obj = null;
            try {
                obj = defalutCollectSendRunner.send(msg);
                if (obj != null) {
                    defalutCollectSendRunner.callBack(obj);
                }
            } catch (Exception e) {
                defalutCollectSendRunner.sendingFailed(msg, e);
            }
            return obj;
        }
    }
    
    @SuppressWarnings("unchecked")
    public Entity<T> send(Entity<String> msg) throws Exception {
        if(AbstractSender.class.isAssignableFrom(sender.getClass())){
            return (Entity<T>)((AbstractSender)sender).synSend(msg);
        }
        sender.send(msg, this);
        return Asset.getNull();
    }

    @Override
    public boolean started() {
        // TODO Auto-generated method stub
        return false;
    }

}
