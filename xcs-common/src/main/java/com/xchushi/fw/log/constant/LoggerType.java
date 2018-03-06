package com.xchushi.fw.log.constant;

import ch.qos.logback.classic.Level;

public enum LoggerType {

    OFF("OFF", Integer.MAX_VALUE), 
    ERROR("ERROR", 40000),
    WARN("WARN", 30000),
    INFO("INFO", 20000),
    DEBUG("DEBUG", 10000),
    TRACE("TRACE", 5000),
    ALL("ALL", Integer.MIN_VALUE),
    UNKNOWN("UNKNOWN", 1000),
    ;
    
    private String name;

    private int val;

    LoggerType(String name, int val) {
        this.name = name;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
    
}
