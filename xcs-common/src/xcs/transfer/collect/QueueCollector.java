package xcs.transfer.collect;

import java.util.Queue;

import xcs.common.constant.StringConstant;
import xcs.common.container.LockAbleQueue;
import xcs.common.entity.Entity;
import xcs.common.entity.Entity.EntityType;
import xcs.common.environment.Configure;

public class QueueCollector extends LockAbleQueue<String> implements Collectible<String> {

    private int queueLoopCount = 10;

    private int maxSendLength = 30_000;

    /**
     * 字符集
     */
    private String charset = "UTF-8";

    public QueueCollector(Configure config, Queue<String> queue) {
        super(queue);
        if (config != null) {
            queueLoopCount = config.getProperty("queueLoopCount", Integer.class, 30);
            maxSendLength = config.getProperty("maxSendLength", Integer.class, 30_000_000);
            charset = config.getProperty("charset", String.class, "UTF-8");
        }
    }

    @Override
    public Entity<String> collect() throws Exception {
        try {
            lock.lock();
            StringBuilder builder = new StringBuilder();
            int count = 0;
            long length = 0l;
            while (true) {
                if (count >= queueLoopCount || length > maxSendLength) {
                    String message = builder.toString();
                    if (message == null || message.length() < 1) {
                        return null;
                    }
                    return new Entity<String>(builder.toString(), EntityType.nomal);
                }
                count++;
                String item = null;
                item = this.poll(true);
                if (item == null) {
                    continue;
                }
                length = length + item.getBytes(charset).length;
                builder.append(item + StringConstant.NEWLINE);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void collect(String t) throws Exception {
        try {
            lock.lock();
            this.offer(t);
        } finally {
            lock.unlock();
        }
    }

}
