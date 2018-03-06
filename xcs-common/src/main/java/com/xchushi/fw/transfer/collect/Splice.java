package com.xchushi.fw.transfer.collect;

public interface Splice<T> {
    
    Splice<T> splice(T t);
    
    T value();
    
}
