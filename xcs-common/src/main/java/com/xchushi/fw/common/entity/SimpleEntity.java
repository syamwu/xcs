package com.xchushi.fw.common.entity;

public class SimpleEntity<T> extends Entity<T> {

    public SimpleEntity(T message, com.xchushi.fw.common.entity.Entity.EntityType entityType) {
        super(message, entityType);
    }

    @Override
    public T getMessage() {
        return message;
    }

}
