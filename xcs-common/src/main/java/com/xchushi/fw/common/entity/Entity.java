package com.xchushi.fw.common.entity;

/**
 * 基础实体类，用于方法间传输调用
 * 
 * @author: SamJoker
 * @date: 2018
 */
public abstract class Entity<T> implements Value<T>{

    protected T data;

    protected EntityType entityType;
    
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
    
}
