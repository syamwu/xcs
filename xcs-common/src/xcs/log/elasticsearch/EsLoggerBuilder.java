package xcs.log.elasticsearch;

public interface EsLoggerBuilder {

    EsLogger logger(Class<?> cls);
    
}
