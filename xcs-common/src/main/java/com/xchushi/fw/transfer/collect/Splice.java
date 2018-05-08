package com.xchushi.fw.transfer.collect;

/**
 * 
 * 可拼接实体统一接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Splice<T> {
    
    /**
     * 拼接实体
     * 
     * @param t
     * @return
     */
    Splice<T> splice(T t);
    
    /**
     * 获取实体内容
     * 
     * @return
     */
    T value();
    
}
