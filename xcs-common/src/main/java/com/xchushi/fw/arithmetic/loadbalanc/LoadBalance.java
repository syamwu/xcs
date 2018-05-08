package com.xchushi.fw.arithmetic.loadbalanc;

/**
 * 负载均衡统一接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface LoadBalance<T> {

    T loadBalance(int[] load, T[] ss);
    
    T loadBalance();
    
    int loadBalanceIndex();
    
    int scaleBase();
    
}
