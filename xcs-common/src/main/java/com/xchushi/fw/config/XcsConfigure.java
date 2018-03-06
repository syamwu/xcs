package com.xchushi.fw.config;

import java.util.Properties;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.environment.Configure;

public class XcsConfigure extends AbstractConfigure implements Configure {

    private Properties properties;

    private static XcsConfigure xcsConfigure = null;

    private XcsConfigure(String fileName) {
        this.properties = new Properties();
        try {
            this.properties.load(XcsConfigure.class.getClassLoader().getResourceAsStream(fileName));
        } catch (Exception e) {
            this.properties = null;
            e.printStackTrace();
        }
    }

    private XcsConfigure(Class<?> cls) {
        this.properties = new Properties();
        try {
            this.properties.load(XcsConfigure.class.getClassLoader().getResourceAsStream("xcs.properties"));
        } catch (Exception e) {
            this.properties = null;
            e.printStackTrace();
        }
    }

    private XcsConfigure(Properties properties, Class<?> cls) {
        this.properties = properties;
        ConfigSetting configSetting = cls.getAnnotation(ConfigSetting.class);
        if (configSetting != null) {
            this.addPrefix(configSetting.prefix());
        }
    }

    public synchronized static Configure initConfigureAndGet(Class<?> cls, String fileName) {
        if (xcsConfigure != null) {
            return xcsConfigure;
        }
        xcsConfigure = new XcsConfigure(fileName);
        return getConfigure(cls);
    }

    public static Configure getConfigure(Class<?> cls) {
        if (cls == null) {
            return getConfigure(XcsConfigure.class);
        }
        XcsConfigure config = null;
        if (xcsConfigure != null) {
            config = new XcsConfigure(xcsConfigure.properties, cls);
        } else {
            xcsConfigure = new XcsConfigure(cls);
            config = xcsConfigure;
        }
        return config;
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
