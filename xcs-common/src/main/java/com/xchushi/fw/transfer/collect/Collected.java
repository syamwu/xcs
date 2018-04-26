package com.xchushi.fw.transfer.collect;

/**
 * 收集类统一接口
 * 
 * 
 * @author: SamJoker
 * @date: 2018
 */
public interface Collected<T extends Splice<R>, R> {
    
    /**
     * 获取收集内容
     * 
     * @return
     * @throws Exception
     */
    T collect() throws Exception;
    
    /**
     * 存入待收集内容
     * 
     * @param t
     * @throws Exception
     */
    void collect(R t) throws Exception;
    
}
