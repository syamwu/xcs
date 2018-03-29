package com.xchushi.fw.common.entity;

import com.xchushi.fw.transfer.collect.Splice;

public class StringSpliceEntity extends Entity<String> implements Splice<String> {
    
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
        count.incrementAndGet();
        stringbuffer.append(value);
        return this;
    }

    @Override
    public String value() {
        return stringbuffer.toString();
    }
    
    @Override
    public String getData() {
        return stringbuffer.toString();
    }
    
}
