package com.xchushi.fw.common.environment;

/**
 * 获取配置信息接口
 * 
 * @author: SamJoker
 * @date: 2018-03-14
 */
public interface Configure {

    /**
     * 
     * @param key
     *            配置键
     * @param targetType
     *            类型
     * @param defaultValue
     *            默认值
     * @return
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);
    
    <T> T getProperty(String key, Class<T> targetType);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    /**
     * 根据key的value(类名)返回指定T类的实例
     * 
     * @param key
     * @param cls
     * @return
     * @author SamJoker
     */
    <T> T getBean(String key, Object... args) throws Exception;

}
