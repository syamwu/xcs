package com.xchushi.fw.transfer.runner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.Entity.EntityType;
import com.xchushi.fw.common.entity.SimpleEntity;
import com.xchushi.fw.common.entity.SpliceEntity;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.util.MessageUtil;
import com.xchushi.fw.common.util.SimpleFileQueue;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.collect.Collected;
import com.xchushi.fw.transfer.sender.AbstractSender;

public final class DefalutCollectSendRunner<T> extends CollectRunner {

    /**
     * 保存发送失败内容的文件地址
     */
    private String failSendFile = "collect/sendFail.txt";

    /**
     * 是否启用失败保存
     */
    private boolean failSendFileEnable = false;

    /**
     * 发送超时时间
     */
    private int sendTimeOut = 10_000;

    /**
     * 主队列,用以保存准备传输的字符串
     */
    private Collected<SpliceEntity<T>, T> mainCollecter = null;

    private LinkedBlockingQueue<Entity<T>> failQueue = new LinkedBlockingQueue<Entity<T>>(
            Integer.MAX_VALUE);

    /**
     * 文件队列
     */
    private SimpleFileQueue failFileQueue = null;

    private static Logger logger = SysLoggerFactory.getLogger(DefalutCollectSendRunner.class);

    public DefalutCollectSendRunner(AbstractSender sd, Collected<SpliceEntity<T>, T> collected,
            ThreadPoolExecutor threadPoolExecutor) {
        super(ConfigureFactory.getConfigure(DefalutCollectSendRunner.class), sd, threadPoolExecutor);
        Asset.notNull(sd, "sd can't be null");
        Asset.notNull(threadPoolExecutor, "threadPoolExecutor can't be null");
        try {
            initCollectSendExecutor(sd, collected);
        } catch (Exception e) {
            throw new InitException("initCollectSendExecutor fail:" + e.getMessage(), e);
        }
        logger.debug("CollectSendRunner created");
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
    private void initFailQueue(SimpleFileQueue failFileQueue) {
        Asset.notNull(failFileQueue);
        try {
            long time = System.currentTimeMillis();
            List<String> msgs = failFileQueue.getMessages();
            if (msgs != null) {
                for (String msg : msgs) {
                    StringBuffer strBuff = new StringBuffer();
                    List<String> messages = JSON.parseArray(msg, String.class);
                    if (messages == null) {
                        continue;
                    }
                    for (String message : messages) {
                        strBuff.append(message + StringConstant.NEWLINE);
                    }
                    failQueue.offer(new SimpleEntity<T>((T)strBuff.toString(), EntityType.reSend));
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
                failFileQueue = new SimpleFileQueue(failSendFile);
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
                    tpe.submit(new SendTask<T>(sendEntity, this));
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
        sender.callBack(obj);
    }

    public void sendingFailed(Object message, Throwable e) throws IOException, InterruptedException {
        fastFailMessage(message);
        sender.sendingFailed(message, e);
    }

    @SuppressWarnings("unchecked")
    public void fastFailMessage(Object message) throws IOException, InterruptedException {
        if (failSendFileEnable && Entity.class.isAssignableFrom(message.getClass())) {
            Entity<T> senEntity = (Entity<T>) message;
            if (EntityType.nomal == senEntity.getEntityType()) {
                String msgList = MessageUtil.messageToListStr((String) senEntity.getData(), StringConstant.NEWLINE);
                Asset.notNull(msgList, "exchange senEntity.message fail!");
                failFileQueue.offer(msgList);
                failQueue.offer(new SimpleEntity<T>(senEntity.getData(), EntityType.reSend));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    static class SendTask<T> implements Callable<Entity> {

        private Entity<T> msg;
        private DefalutCollectSendRunner defalutCollectSendRunner;

        SendTask(Entity<T> sendEntity, DefalutCollectSendRunner defalutCollectSendRunner) {
            this.msg = sendEntity;
            this.defalutCollectSendRunner = defalutCollectSendRunner;
        }

        @Override
        public Entity call() throws Exception {
            Entity obj = null;
            try {
                obj = (Entity) defalutCollectSendRunner.sender.synSend(msg);
                defalutCollectSendRunner.callBack(obj);
            } catch (Exception e) {
                defalutCollectSendRunner.sendingFailed(msg, e);
            }
            return obj;
        }
    }

}
