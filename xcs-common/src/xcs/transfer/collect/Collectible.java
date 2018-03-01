package xcs.transfer.collect;

import xcs.common.entity.Entity;

public interface Collectible<T> {
    
    Entity<T> collect() throws Exception;
    
    void collect(T t) throws Exception;
    
}
