package com.xchushi.fw.common.container;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAbleQueue<E> {

    protected final Lock lock = new ReentrantLock();

    protected final Lock queueLock = new ReentrantLock();

    protected final Condition notEmpty = queueLock.newCondition();

    protected Queue<E> queue;

    public LockAbleQueue(Queue<E> queue) {
        this.queue = queue;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public Lock getLock() {
        return lock;
    }

    public E poll() throws InterruptedException {
        return poll(false);
    }

    public E poll(boolean fastReturn) throws InterruptedException {
        try {
            queueLock.lock();
            while (queue.isEmpty() && !fastReturn) {
                notEmpty.await();
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
