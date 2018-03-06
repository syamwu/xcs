package com.xchushi.fw.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysLoggerFactory {
    
    public static Logger getLogger(Class<?> cls){
        return LoggerFactory.getLogger(cls);
    }

}
