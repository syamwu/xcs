package syamwu.xchushi.fw.common.entity;

import java.util.List;

import syamwu.xchushi.fw.transfer.collect.Splice;

public class ListEntity extends Entity<List<Object>> implements Splice<List<Object>>{

    public ListEntity(List<Object> data, EntityType entityType) {
        super(data, entityType);
    }
    
    public ListEntity(List<Object> data) {
        super(data, EntityType.nomal);
    }

    @Override
    public Splice<List<Object>> splice(List<Object> t) {
        data.addAll(t);
        return this;
    }

    @Override
    public List<Object> value() {
        return data;
    }

    
    
}
