package com.xchushi.log.constant;

public enum LEVEL {

    INFO("INFO", 5000), 
    ERROR("ERROR", 1000),
    ;

    private String name;

    private int val;

    LEVEL(String name, int val) {
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