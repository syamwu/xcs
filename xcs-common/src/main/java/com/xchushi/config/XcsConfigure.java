package com.xchushi.config;

import java.util.Properties;

import com.xchushi.common.constant.StringConstant;
import com.xchushi.common.environment.Configure;

public class XcsConfigure extends AbstractConfigure implements Configure {

    private Properties properties;

    private ClassLoader classLoader;

    private static XcsConfigure xcsConfigure = null;

    private XcsConfigure(Class<?> cls) {
        this.properties = new Properties();
        try {
            this.classLoader = cls == null ? this.getClass().getClassLoader() : cls.getClassLoader();
            this.properties.load(this.classLoader.getResourceAsStream("xcs.properties"));
        } catch (Exception e) {
            this.properties = null;
        }
    }

    private XcsConfigure(Properties properties) {
        this.properties = properties;
        this.classLoader = this.getClass().getClassLoader();
    }

    public static Configure getConfigure(Class<?> cls) {
        if (cls == null) {
            return null;
        } else if (xcsConfigure == null) {
            xcsConfigure = new XcsConfigure(XcsConfigure.class);
            return getConfigure(cls);
        } else if (xcsConfigure.classLoader.equals(cls.getClassLoader())) {
            return xcsConfigure;
        }
        return new XcsConfigure(cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        if (properties == null || targetType == null) {
            return defaultValue;
        }
        String value = properties.getProperty(prefix == null ? key : prefix + StringConstant.POINT + key);
        if (value == null) {
            return defaultValue;
        }
        if (String.class.isAssignableFrom(targetType)) {
            return (T) value;
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return (T) Integer.valueOf(value);
        } else if (Long.class.isAssignableFrom(targetType)) {
            return (T) Long.valueOf(value);
        } else if (Double.class.isAssignableFrom(targetType)) {
            return (T) Double.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(targetType)) {
            return (T) Boolean.valueOf(value);
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String key, Object... args) throws Exception {
        String executorClass = getProperty(key);
        T result = null;
        if (executorClass != null) {
            Class<?> cls = Class.forName(executorClass);
            if (cls != null) {
                result = (T) cls.getDeclaredConstructor().newInstance(args);
                return result;
            }
        }
        return result;
    }

    @Override
    public Configure setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public Configure addPrefix(String prefix) {
        if (this.prefix != null) {
            this.prefix = this.prefix + "." + prefix;
        }
        this.prefix = prefix;
        return this;
    }

}
