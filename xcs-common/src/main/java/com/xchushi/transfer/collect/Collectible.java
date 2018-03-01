package com.xchushi.transfer.collect;

import com.xchushi.common.entity.Entity;

public interface Collectible<T> {
    
    Entity<T> collect() throws Exception;
    
    void collect(T t) throws Exception;
    
}
