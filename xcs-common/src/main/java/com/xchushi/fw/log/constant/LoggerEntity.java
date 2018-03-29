package com.xchushi.fw.log.constant;

import com.xchushi.fw.common.entity.Entity;

public class LoggerEntity extends Entity<LoggerEvent>{

    private LoggerEvent loggerEvent;
    
    public LoggerEntity(LoggerEvent message, EntityType entityType) {
        super(message, entityType);
    }

    @Override
    public LoggerEvent getData() {
        return null;
    }

    public LoggerEvent getLoggerEvent() {
        return loggerEvent;
    }

    public void setLoggerEvent(LoggerEvent loggerEvent) {
        this.loggerEvent = loggerEvent;
    }
    
}
