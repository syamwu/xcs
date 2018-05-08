package com.xchushi.fw.common.observer;

/**
 * 观察者统一接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Observer<T> {

    /**
     * 被通知
     * 
     * @param t
     */
    public void notify(T t);
    
}
