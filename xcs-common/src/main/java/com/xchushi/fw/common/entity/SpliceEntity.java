package com.xchushi.fw.common.entity;

import com.xchushi.fw.transfer.collect.Splice;

public abstract class SpliceEntity<T> extends Entity<T> implements Splice<T>  {

    public SpliceEntity(T data, com.xchushi.fw.common.entity.Entity.EntityType entityType) {
        super(data, entityType);
    }

}
