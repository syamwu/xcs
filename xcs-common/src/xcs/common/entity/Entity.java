package xcs.common.entity;

public class Entity<T>{

    private T message;

    private EntityType entityType;
    
    public Entity(T message, EntityType entityType) {
        this.message = message;
        this.entityType = entityType;
    }

    public static enum EntityType {
        nomal, reSend
    }

    public T getMessage() {
        return message;
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

}
