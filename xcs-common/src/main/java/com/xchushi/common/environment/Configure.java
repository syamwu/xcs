package com.xchushi.common.environment;

public interface Configure {

    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> targetType);

    Configure setPrefix(String prefix);

    Configure addPrefix(String prefix);

    /**
     * 根据key的value(类名)返回指定T类的实例
     * 
     * @param key
     * @param cls
     * @return
     * @author SamJoker
     */
    <T> T getBean(String key, Object... args) throws Exception;

    String getPrefix();

}
