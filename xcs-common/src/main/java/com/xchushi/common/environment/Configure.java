package com.xchushi.common.environment;

public interface Configure {

    public <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    public String getProperty(String key);

    public String getProperty(String key, String defaultValue);

    public <T> T getProperty(String key, Class<T> targetType);
}
