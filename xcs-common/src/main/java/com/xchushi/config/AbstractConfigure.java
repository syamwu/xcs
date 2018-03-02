package com.xchushi.config;

import com.xchushi.common.environment.Configure;

public abstract class AbstractConfigure implements Configure {

    protected String prefix;

    @Override
    public String getProperty(String key) {
        return getProperty(key, String.class, null);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return getProperty(key, targetType, null);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getProperty(key, String.class, defaultValue);
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

}
