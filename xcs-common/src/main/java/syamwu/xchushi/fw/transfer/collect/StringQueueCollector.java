package syamwu.xchushi.fw.transfer.collect;

import java.util.Queue;

import syamwu.xchushi.fw.common.annotation.ConfigSetting;
import syamwu.xchushi.fw.common.constant.StringConstant;
import syamwu.xchushi.fw.common.container.LockAbleQueue;
import syamwu.xchushi.fw.common.entity.SpliceEntity;
import syamwu.xchushi.fw.common.entity.StringSpliceEntity;
import syamwu.xchushi.fw.common.environment.Configure;

/**
 * 字符串队列收集器
 * 
 * @author: syam_wu
 * @date: 2018
 */
@ConfigSetting(prefix = "collecter")
public class StringQueueCollector extends LockAbleQueue<String> implements Collected<SpliceEntity<String>, String> {

    /**
     * 每次最大收集数
     */
    private int queueLoopCount = 30;

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
        if (this.size() < maxQueueCount) {
            this.offer(t);
        } else {
            throw new RuntimeException("The size of the queue is more than maxQueueCount:" + maxQueueCount);
        }
    }
    

    @Override
    public StringSpliceEntity collect() throws Exception {
        return collect(null, queueLoopCount, 0);
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
                    String message = tmp == null ? null : tmp.getValue();
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
                    tmp = new StringSpliceEntity(item + StringConstant.NEW_LINE);
                }else{
                    tmp.splice(item + StringConstant.NEW_LINE);
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
