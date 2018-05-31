package syamwu.xchushi.fw.common.entity;

public class SimpleEntity<T> extends Entity<T> {

    public SimpleEntity(T data, syamwu.xchushi.fw.common.entity.Entity.EntityType entityType) {
        super(data, entityType);
    }

    @Override
    public T getValue() {
        return data;
    }

}
