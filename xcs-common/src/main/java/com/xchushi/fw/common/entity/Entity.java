package com.xchushi.fw.common.entity;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity<T> implements Value<T>{

    protected T message;

    protected EntityType entityType;
    
    protected AtomicInteger count = new AtomicInteger(0);
    
    public Entity(T message, EntityType entityType) {
        this.message = message;
        this.entityType = entityType;
    }

    public static enum EntityType {
        nomal, reSend
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    
    public int count(){
        return count.get();
    }

}
