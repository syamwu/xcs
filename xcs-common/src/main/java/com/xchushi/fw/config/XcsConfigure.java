package com.xchushi.fw.config;

import java.io.IOException;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.environment.Propertie;

public class XcsConfigure extends AbstractConfigure implements Configure {

    private static XcsConfigure xcsConfigure = null;

    private XcsConfigure(String fileName) throws IOException {
        super(new FileProperties(fileName), null);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private XcsConfigure(Class<?> cls) throws IOException {
        super(new FileProperties("xcs.properties"), cls);
    }

    public XcsConfigure(Propertie properties, Class<?> cls) {
        super(properties, cls);
    }

    public synchronized static Configure initConfigureAndGet(Propertie properties, Class<?> cls) {
        if (xcsConfigure != null) {
            return xcsConfigure;
        }
        xcsConfigure = new XcsConfigure(properties, cls);
        return getConfigure(cls);
    }

    public synchronized static Configure getConfigure(Class<?> cls, Propertie properties) throws IOException {
        Asset.notNull(cls);
        XcsConfigure config = null;
        if (xcsConfigure != null) {
            config = new XcsConfigure(properties, cls);
        } else {
            xcsConfigure = new XcsConfigure(cls);
            config = xcsConfigure;
        }
        return config;
    }

    public synchronized static Configure getConfigure(Class<?> cls) {
        try {
            Asset.notNull(cls);
            XcsConfigure config = null;
            if (xcsConfigure != null) {
                config = new XcsConfigure(xcsConfigure.properties, cls);
            } else {
                xcsConfigure = new XcsConfigure(cls);
                config = xcsConfigure;
            }
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String key, Object... args) throws Exception {
        String executorClass = getProperty(key, String.class, null,
                getStackTrace(Thread.currentThread().getStackTrace(), 2));
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

    @Override
    <T> T getProperty(String key, Class<T> targetType, T defaultValue, StackTraceElement st) {
        if (st != null) {
            try {
                Class<?> cls = Class.forName(st.getClassName());
                ConfigSetting configSetting = cls.getAnnotation(ConfigSetting.class);
                T value = defaultValue;
                if (configSetting != null) {
                    String prefix = configSetting.prefix();
                    value = properties.get(prefix == null ? key : prefix + StringConstant.POINT + key, targetType);
                } else {
                    value = properties.get(prefix == null ? key : prefix + StringConstant.POINT + key, targetType);
                }
                if (value == null) {
                    return defaultValue;
                }
                return value;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return defaultValue;
        }
        return getProperty(key, targetType, defaultValue);
    }
    
    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        StackTraceElement st = getStackTrace(Thread.currentThread().getStackTrace(), 2);
        if (st != null && !XcsConfigure.class.getName().equals(st.getClassName())) {
            return getProperty(key, targetType, defaultValue, st);
        }
        if (properties == null || targetType == null) {
            return defaultValue;
        }
        T value = properties.get(prefix == null ? key : prefix + StringConstant.POINT + key, targetType);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

}
