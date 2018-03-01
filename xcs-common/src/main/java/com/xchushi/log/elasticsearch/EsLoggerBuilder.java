package com.xchushi.log.elasticsearch;

public interface EsLoggerBuilder {

    EsLogger logger(Class<?> cls);
    
}
