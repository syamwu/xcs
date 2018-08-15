package syamwu.xchushi.fw.common.container;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAbleQueue<E> {

    protected final Lock queueLock = new ReentrantLock();

    protected final Condition notEmpty = queueLock.newCondition();

    protected Queue<E> queue;

    public LockAbleQueue(Queue<E> queue) {
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
        return poll(false, 10l);
    }

    public E poll(boolean fastReturn, long waitTime) throws InterruptedException {
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

    public boolean offer(E e) throws InterruptedException {
        try {
            queueLock.lock();
            notEmpty.signal();
            return queue.offer(e);
        } finally {
            queueLock.unlock();
        }
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
