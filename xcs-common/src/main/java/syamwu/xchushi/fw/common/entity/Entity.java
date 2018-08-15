package syamwu.xchushi.fw.common.entity;

import syamwu.xchushi.fw.common.util.JsonUtils;

/**
 * 基础实体类，用于方法间传输调用
 * 
 * @author: syam_wu
 * @date: 2018
 */
public abstract class Entity<T>{

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
    
    @SuppressWarnings("rawtypes")
    public static <T extends Entity> T bulidEntity(String data, Class<T> cls){
        return JsonUtils.parseObject(data, cls);
    }
    
    public static <T> String bulidEntityString(Entity<T> entity){
        return JsonUtils.toJSONString(entity);
    }

    public T getData() {
        return data;
    }
    
}
