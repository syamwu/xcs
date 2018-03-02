package com.xchushi.transfer.collect;

public interface Collectible<T extends Splice<R>, R> {
    
    T collect() throws Exception;
    
    T collect(int count, long waitTime) throws Exception;
    
    void collect(R t) throws Exception;
    
    T collect(T t) throws Exception;
    
}
