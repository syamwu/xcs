package com.xchushi.fw.arithmetic.loadbalanc;

public interface LoadBalance<T> {

    T loadBalance(int[] load, T[] ss);
    
    T loadBalance();
    
    int loadBalanceIndex();
    
    int scaleBase();
    
}
