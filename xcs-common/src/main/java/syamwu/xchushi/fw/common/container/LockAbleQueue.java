package syamwu.xchushi.fw.common.container;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAbleQueue<E> {

    private final Lock queueLock = new ReentrantLock();

    private final Condition notEmpty = queueLock.newCondition();

    private Queue<E> queue;

    private boolean isLockQueue = false;

    public LockAbleQueue(Queue<E> queue) {
        if (queue instanceof BlockingDeque) {
            isLockQueue = true;
        }
        this.queue = queue;
    }

    public void lock() {
        queueLock.lock();
    }

    public void unlock() {
        queueLock.unlock();
    }

    public Lock getLock() {
        return queueLock;
    }

    public E poll() throws InterruptedException {
        return poll(false, 10l, isLockQueue);
    }

    public E poll(boolean fastReturn, long waitTime, boolean isLockQueue) throws InterruptedException {
        if (isLockQueue) {
            return ((BlockingDeque<E>) queue).poll(waitTime, TimeUnit.MILLISECONDS);
        } else {
            try {
                queueLock.lock();
                while (queue.isEmpty() && !fastReturn) {
                    notEmpty.await(waitTime, TimeUnit.MILLISECONDS);
                    if (queue.isEmpty()) {
                        return null;
                    }
                }
                return queue.poll();
            } finally {
                queueLock.unlock();
            }
        }
    }

    public boolean offer(E e) throws InterruptedException {
        if (isLockQueue) {
            return queue.offer(e);
        } else {
            try {
                queueLock.lock();
                notEmpty.signal();
                return queue.offer(e);
            } finally {
                queueLock.unlock();
            }
        }
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
