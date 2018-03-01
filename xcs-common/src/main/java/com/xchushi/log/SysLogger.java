package com.xchushi.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysLogger {
    
    public static Logger getLogger(Class<?> cls){
        return LoggerFactory.getLogger(cls);
    }

}
