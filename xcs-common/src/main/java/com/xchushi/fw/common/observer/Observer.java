package com.xchushi.fw.common.observer;

/**
 * 观察者统一接口
 * 
 * @author: SamJoker
 * @date: 2018
 */
public interface Observer<T> {

    /**
     * 被观察者变化
     * 
     * @param t
     */
    public void change(T t);
    
}
