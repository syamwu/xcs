package syamwu.xchushi.fw.common.entity;

import syamwu.xchushi.fw.transfer.collect.Splice;

public class StringSpliceEntity extends SpliceEntity<String> {
    
    private StringBuffer stringbuffer = new StringBuffer();
    
    public StringSpliceEntity(String data, EntityType entityType) {
        super(data, entityType);
        stringbuffer.append(data);
    }
    
    public StringSpliceEntity(String message) {
        super(message, EntityType.nomal);
        stringbuffer.append(message);
    }

    @Override
    public Splice<String> splice(String value) {
        stringbuffer.append(value);
        return this;
    }

    @Override
    public String value() {
        return stringbuffer.toString();
    }
    
    @Override
    public String getValue() {
        return stringbuffer.toString();
    }
    
}
