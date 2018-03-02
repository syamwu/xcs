package com.xchushi.transfer.collect;

import java.util.Queue;

import com.xchushi.common.constant.StringConstant;
import com.xchushi.common.container.LockAbleQueue;
import com.xchushi.common.entity.StringSpliceEntity;
import com.xchushi.common.environment.Configure;

public class StringQueueCollector extends LockAbleQueue<String> implements Collectible<StringSpliceEntity, String> {

    private int queueLoopCount = 10;

    private int maxSendLength = 30_000;

    /**
     * 队列最大值
     */
    private int maxQueueCount = Integer.MAX_VALUE - 1;

    /**
     * 字符集
     */
    private String charset = "UTF-8";

    public StringQueueCollector(Configure config, Queue<String> queue) {
        super(queue);
        if (config != null) {
            queueLoopCount = config.getProperty("queueLoopCount", Integer.class, 30);
            maxSendLength = config.getProperty("maxSendLength", Integer.class, 30_000_000);
            maxQueueCount = config.getProperty("maxQueueCount", Integer.class, 100_000);
            charset = config.getProperty("charset", String.class, "UTF-8");
        }
    }

    @Override
    public void collect(String t) throws Exception {
        try {
            lock.lock();
            if (this.size() < maxQueueCount) {
                this.offer(t);
            } else {
                throw new RuntimeException("The size of the queue is more than maxQueueCount:" + maxQueueCount);
            }
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public StringSpliceEntity collect(int count, long waitTime) throws Exception {
        return collect(null, count, waitTime);
    }

    @Override
    public StringSpliceEntity collect() throws Exception {
        return collect(queueLoopCount, 0);
    }

    @Override
    public StringSpliceEntity collect(StringSpliceEntity t) throws Exception {
        return collect(t, queueLoopCount, 0);
    }
    
    private StringSpliceEntity collect(StringSpliceEntity entity, int count, long waitTime) throws Exception {
        try {
            lock.lock();
            StringSpliceEntity tmp = entity;
            int loopCount = 0;
            long length = 0l;
            while (true) {
                String item = null;
                if (count < 0 && this.isEmpty() && loopCount < queueLoopCount) {
                    Thread.sleep(waitTime);
                } else if (loopCount >= queueLoopCount || length > maxSendLength) {
                    String message = tmp == null ? null : tmp.getMessage();
                    if (message == null || message.length() < 1) {
                        return null;
                    }
                    return tmp;
                }
                loopCount++;
                item = this.poll(true);
                if (item == null) {
                    continue;
                }
                length = length + item.getBytes(charset).length;
                if (tmp == null) {
                    tmp = new StringSpliceEntity(item + StringConstant.NEWLINE);
                }else{
                    tmp.splice(item + StringConstant.NEWLINE);
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
