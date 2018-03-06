package com.xchushi.fw.log.elasticsearch;

public interface EsLoggerBuilder {

    EsLogger logger(Class<?> cls);
    
}
