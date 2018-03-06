package com.xchushi.fw.arithmetic.loadbalanc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.xchushi.fw.arithmetic.loadbalanc.balance.Balance;
import com.xchushi.fw.arithmetic.loadbalanc.code.HashUniqueCode;
import com.xchushi.fw.arithmetic.loadbalanc.code.UniqueCode;
import com.xchushi.fw.arithmetic.loadbalanc.exception.LoadBalanceException;
import com.xchushi.fw.arithmetic.loadbalanc.load.DynamicLoad;
import com.xchushi.fw.arithmetic.loadbalanc.load.Load;

public abstract class AbstractLoadBalance<T> implements LoadBalance<T> {

    /**
     * 负载器
     */
    Load<T> load;

    /**
     * 均衡器
     */
    Balance<T> balance;

    /**
     * 权值数组
     */
    int[] loads;

    /**
     * 权值分母
     */
    int scaleBase;

    /**
     * 可get()的对象数组
     */
    UniqueCode[] ucs;

    /**
     * 对象数组
     */
    T[] objs;

    /**
     * 锁，用以应变数组的个数、对象内容、权值数组、负载器和均衡器的变动操作
     */
    Lock lbLock = new ReentrantLock();

    public AbstractLoadBalance(int[] loads) {
        this.loads = loads;
    }

    @Override
    public synchronized T loadBalance(int[] loads, T[] objs) {
        try {
            lbLock.lock();
            if (objs == null || objs.length < 1 || loads == null || loads.length < 1 || objs.length != loads.length) {
                throw new LoadBalanceException("Load create fail,please check objs and loads.");
            }
            UniqueCode[] us = new UniqueCode[objs.length];
            for (int i = 0; i < objs.length; i++) {
                us[i] = new HashUniqueCode(objs[i]);
            }
            this.load = new DynamicLoad<T>(this, us, loads);
            return loadBalance();
        } finally {
            lbLock.unlock();
        }
    }

    public AbstractLoadBalance<T> setBanlanc(Balance<T> balance) {
        try {
            lbLock.lock();
            this.balance = balance;
            return this;
        } finally {
            lbLock.unlock();
        }
    }

    public AbstractLoadBalance<T> setLoad(Load<T> load) {
        try {
            lbLock.lock();
            this.load = load;
            return this;
        } finally {
            lbLock.unlock();
        }
    }
}
