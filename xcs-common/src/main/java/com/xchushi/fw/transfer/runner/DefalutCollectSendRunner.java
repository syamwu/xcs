package com.xchushi.fw.transfer.runner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.Entity.EntityType;
import com.xchushi.fw.common.entity.SimpleEntity;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.util.MessageUtil;
import com.xchushi.fw.common.util.SimpleFileQueue;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.collect.StringQueueCollector;
import com.xchushi.fw.transfer.sender.AbstractSender;
import com.xchushi.fw.transfer.sender.HttpSender;

public final class DefalutCollectSendRunner extends AbstractCollectRunner {

    /**
     * 保存发送失败内容的文件地址
     */
    private String failSendFile = "eslogger/logSendFail.txt";

    /**
     * 是否启用失败保存
     */
    private boolean failSendFileEnable = true;

    /**
     * 发送超时时间
     */
    private int sendTimeOut = 10_000;

    private static StringQueueCollector mainQueue = null;

    private static LinkedBlockingQueue<Entity<String>> failQueue = new LinkedBlockingQueue<Entity<String>>(
            Integer.MAX_VALUE);

    private static SimpleFileQueue failFileQueue = null;

    private static Logger logger = SysLoggerFactory.getLogger(DefalutCollectSendRunner.class);

    public static void initAndStart(Configure config, AbstractSender sender, ThreadPoolExecutor threadPoolExecutor) {
        Asset.notNull(sender, "sd can't be null");
        Asset.notNull(threadPoolExecutor, "threadPoolExecutor can't be null");
        DefalutCollectSendRunner defalutCollectSendRunner = new DefalutCollectSendRunner(sender,
                threadPoolExecutor);
        threadPoolExecutor.execute(defalutCollectSendRunner);
    }

    public DefalutCollectSendRunner(AbstractSender sd, ThreadPoolExecutor threadPoolExecutor) {
        super(ConfigureFactory.getConfigure(DefalutCollectSendRunner.class), sd, threadPoolExecutor);
        try {
            initCollectSendExecutor(sd);
        } catch (Exception e) {
            throw new InitException("initCollectSendExecutor fail:" + e.getMessage(), e);
        }
        logger.debug("CollectSendRunner created");
    }

    private void initCollectSendExecutor(AbstractSender sd) throws IOException {
        mainQueue = new StringQueueCollector(configure, new LinkedBlockingQueue<String>(Integer.MAX_VALUE));
        if (configure == null) {
            configure = ConfigureFactory.getConfigure(DefalutCollectSendRunner.class);
        }
        if (configure != null) {
            failSendFileEnable = configure.getProperty("failSendFileEnable", Boolean.class, failSendFileEnable);
            failSendFile = configure.getProperty("failSendFile", String.class, failSendFile);
            sendTimeOut = configure.getProperty("sendTimeOut", Integer.class, sendTimeOut);
        }
        if (failSendFileEnable) {
            failFileQueue = new SimpleFileQueue(failSendFile);
            tpe.execute(new Runnable() {
                @Override
                public void run() {
                    initFailQueue(failFileQueue);
                }
            });
        }
        sd.setCollectible(mainQueue);
    }

    private static void initFailQueue(SimpleFileQueue failFileQueue) {
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
                    failQueue.offer(new SimpleEntity<String>(strBuff.toString(), EntityType.reSend));
                }
            }
            logger.info("加载日志发送失败文件:" + failFileQueue.getFilePath() + ",用时:" + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void run() {
        while (true) {
            try {
                Entity<String> sendEntity = null;
                if (failQueue.isEmpty()) {
                    sendEntity = mainQueue.collect();
                } else {
                    while (!mainQueue.isEmpty()) {
                        saveFailMessage(mainQueue.collect());
                    }
                    sendEntity = failQueue.peek();
                }
                if (sendEntity == null) {
                    Thread.sleep(1);
                    continue;
                }
                try {
                    if (failSendFileEnable && EntityType.reSend == sendEntity.getEntityType()) {
                        Future<Entity> taskResult = tpe.submit(new SendTask(sendEntity, this));
                        Entity response = taskResult.get(sendTimeOut, TimeUnit.MILLISECONDS);
                        if (response == null) {
                            sendingFailed(sendEntity, null);
                        }
                    } else {
                        tpe.submit(new SendTask(sendEntity, this));
                    }
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
        saveFailMessage(message);
        sender.sendingFailed(message, e);
    }

    @SuppressWarnings("unchecked")
    public void saveFailMessage(Object message) throws IOException, InterruptedException {
        if (failSendFileEnable && Entity.class.isAssignableFrom(message.getClass())) {
            Entity<String> senEntity = (Entity<String>) message;
            if (EntityType.nomal == senEntity.getEntityType()) {
                String msgList = MessageUtil.messageToListStr(senEntity.getMessage(), StringConstant.NEWLINE);
                Asset.notNull(msgList, "exchange senEntity.message fail!");
                failFileQueue.offer(msgList);
                failQueue.offer(new SimpleEntity<String>(senEntity.getMessage(), EntityType.reSend));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    static class SendTask implements Callable<Entity> {

        private Entity<String> msg;
        private DefalutCollectSendRunner defalutCollectSendRunner;

        SendTask(Entity<String> message, DefalutCollectSendRunner defalutCollectSendRunner) {
            this.msg = message;
            this.defalutCollectSendRunner = defalutCollectSendRunner;
        }

        @Override
        public Entity call() throws Exception {
            Entity obj = null;
            try {
                obj = (Entity) defalutCollectSendRunner.sender.synSend(msg);
                int count = msg.count();
                HttpSender.okCount.addAndGet(count);
                defalutCollectSendRunner.callBack(obj);
            } catch (Exception e) {
                defalutCollectSendRunner.sendingFailed(msg, e);
            }
            return obj;
        }
    }

}
