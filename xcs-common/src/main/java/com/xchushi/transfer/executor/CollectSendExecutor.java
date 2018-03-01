package com.xchushi.transfer.executor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xchushi.common.constant.StringConstant;
import com.xchushi.common.entity.Entity;
import com.xchushi.common.entity.HttpClientResponseEntity;
import com.xchushi.common.entity.Entity.EntityType;
import com.xchushi.common.environment.Configure;
import com.xchushi.common.exception.InitException;
import com.xchushi.common.exception.SenderFailException;
import com.xchushi.common.util.MessageUtil;
import com.xchushi.common.util.SimpleFileQueue;
import com.xchushi.log.SysLogger;
import com.xchushi.log.elasticsearch.exception.EsLoggerInitException;
import com.xchushi.transfer.collect.Collectible;
import com.xchushi.transfer.collect.QueueCollector;
import com.xchushi.transfer.sender.AbstractSender;

public class CollectSendExecutor  implements Collectible<Object>,Runnable {

    /**
     * 日志队列最大值
     */
    private int maxQueueCount;

    /**
     * 保存发送失败内容的文件地址
     */
    private String failSendFile;

    /**
     * 是否启用失败保存
     */
    private boolean failSendFileEnable;

    /**
     * 发送超时时间
     */
    private int sendTimeOut;

    private static AbstractSender sender;

    private static ThreadPoolExecutor tpe;

    private static QueueCollector mainQueue = null;

    private static QueueCollector fastFailQueue = null;

    private static LinkedBlockingQueue<Entity<String>> failQueue = new LinkedBlockingQueue<Entity<String>>(
            Integer.MAX_VALUE);

    private static SimpleFileQueue failFileQueue = null;

    private static Logger logger = SysLogger.getLogger(CollectSendExecutor.class);
    
    public static void initAndStart(Configure config, AbstractSender sender, ThreadPoolExecutor threadPoolExecutor){
        if (sender == null) {
            throw new InitException("sd can't be null");
        }
        if (threadPoolExecutor == null) {
            throw new InitException("threadPoolExecutor can't be null");
        }
        tpe = threadPoolExecutor;
        CollectSendExecutor collectSendExecutor = new CollectSendExecutor(config, sender, tpe);
        sender.setCollectible(collectSendExecutor);
        tpe.execute(collectSendExecutor);
    }

    public CollectSendExecutor(Configure config, AbstractSender sd, ThreadPoolExecutor threadPoolExecutor) {
        sender = sd;
        try {
            initCollectSendExecutor(config);
        } catch (Exception e) {
            throw new InitException("initCollectSendExecutor fail:" + e.getMessage(), e);
        }
    }

    void initCollectSendExecutor(Configure config) throws IOException {
        mainQueue = new QueueCollector(config, new LinkedBlockingQueue<String>(Integer.MAX_VALUE));
        fastFailQueue = new QueueCollector(config, new LinkedBlockingQueue<String>(Integer.MAX_VALUE));
        tpe.execute(new FastFailCollectExecutor(fastFailQueue, this));
        if (config != null) {
            this.maxQueueCount = config.getProperty("sendTimeOut", Integer.class, 100_000);
            this.failSendFileEnable = config.getProperty("failSendFileEnable", Boolean.class, true);
            this.failSendFile = config.getProperty("failSendFile", String.class, "D:\\upload\\data18.txt");
            //this.asynSendEnable = config.getProperty("asynSendEnable", Boolean.class, true);
            this.sendTimeOut = config.getProperty("sendTimeOut", Integer.class, 10_000);
        }
        if (failSendFileEnable) {
            failFileQueue = new SimpleFileQueue(failSendFile);
            initFailQueue(failSendFile);
        }
    }
    
    private static void initFailQueue(String filePath) {
        if (filePath == null || filePath.length() < 1) {
            throw new EsLoggerInitException("filePath can't be null");
        }
        try {
            failFileQueue = new SimpleFileQueue(filePath);
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
                    failQueue.offer(new Entity<String>(strBuff.toString(), EntityType.reSend));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
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
                if (sendEntity == null || sendEntity.getMessage() == null || sendEntity.getMessage().length() < 1) {
                    Thread.sleep(1);
                    continue;
                }
                try {
                    if (failSendFileEnable && EntityType.reSend == sendEntity.getEntityType()) {
                        Future<HttpClientResponseEntity> taskResult = tpe.submit(new SendTask(sendEntity, this));
                        HttpClientResponseEntity response = taskResult.get(sendTimeOut, TimeUnit.MILLISECONDS);
                        if (response == null) {
                            sender.sendingFailed(sendEntity, null);
                        }
                    } else {
                        tpe.submit(new SendTask(sendEntity, this));
                    }
                } catch (Exception e) {
                    sender.sendingFailed(sendEntity, e);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void collect(Object message) {
        if (message == null) {
            return;
        }
        if (mainQueue.size() < maxQueueCount) {
            try {
                if (failQueue.isEmpty()) {
                    mainQueue.offer((String) message);
                } else {
                    fastFailQueue.offer((String) message);
                }
            } catch (Exception e) {
                sender.sendingFailed(message, new SenderFailException("offer the queue failure!!"));
            }
        } else {
            sender.sendingFailed(message, new SenderFailException("oversize of queue length!!"));
        }
    }

    public void callBack(Object obj) {
        if (obj.getClass().isAssignableFrom(HttpClientResponseEntity.class)) {
            logger.info("succes failQueue.size:" + failQueue.size());
            HttpClientResponseEntity response = (HttpClientResponseEntity) obj;
            if (failSendFileEnable && EntityType.reSend == response.getEntityType()) {
                tpe.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            failFileQueue.poll();
                            failQueue.poll();
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
        if (failSendFileEnable && message.getClass().isAssignableFrom(Entity.class)) {
            Entity<String> senEntity = (Entity<String>) message;
            if (EntityType.nomal == senEntity.getEntityType()) {
                String msgList = MessageUtil.messageToListStr(senEntity.getMessage(), StringConstant.NEWLINE);
                if (msgList == null) {
                    throw new SenderFailException("exchange senEntity.message fail!");
                }
                failFileQueue.offer(msgList);
                failQueue.offer(new Entity<String>(senEntity.getMessage(), EntityType.reSend));
            }
        }
    }

    static class SendTask implements Callable<HttpClientResponseEntity> {

        private Entity<String> msg;
        private CollectSendExecutor collectSendExecutor;

        SendTask(Entity<String> message, CollectSendExecutor collectSendExecutor) {
            this.msg = message;
            this.collectSendExecutor = collectSendExecutor;
        }

        @Override
        public HttpClientResponseEntity call() throws Exception {
            HttpClientResponseEntity obj = null;
            try {
                obj = (HttpClientResponseEntity) sender.synSend(msg);
                collectSendExecutor.callBack(obj);
            } catch (Exception e) {
                collectSendExecutor.sendingFailed(msg, e);
            }
            return obj;
        }
    }

    @Override
    public Entity<Object> collect() throws Exception {
        return null;
    }

}
