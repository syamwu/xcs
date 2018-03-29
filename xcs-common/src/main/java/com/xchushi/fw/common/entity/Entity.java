package com.xchushi.fw.common.entity;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity<T> implements Value<T>{

    protected T data;

    protected EntityType entityType;
    
    protected AtomicInteger count = new AtomicInteger(0);
    
    public Entity(T data, EntityType entityType) {
        this.data = data;
        this.entityType = entityType;
    }

    public static enum EntityType {
        nomal, reSend
    }

    public void setData(T data) {
        this.data = data;
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
