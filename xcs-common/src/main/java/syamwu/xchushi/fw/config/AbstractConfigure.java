package syamwu.xchushi.fw.config;

import syamwu.xchushi.fw.common.annotation.ConfigSetting;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.environment.Propertie;

public abstract class AbstractConfigure implements Configure {

    protected String prefix;

    protected Propertie properties;

    protected AbstractConfigure(Propertie properties, Class<?> cls) {
        this.properties = properties;
        ConfigSetting configSetting = cls.getAnnotation(ConfigSetting.class);
        if (configSetting != null) {
            this.addPrefix(configSetting.prefix());
        }
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, String.class, null, getStackTrace(Thread.currentThread().getStackTrace(), 2));
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return getProperty(key, targetType, null, getStackTrace(Thread.currentThread().getStackTrace(), 2));
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getProperty(key, String.class, defaultValue, getStackTrace(Thread.currentThread().getStackTrace(), 2));
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setProperties(Propertie properties) {
        this.properties = properties;
    }

    protected StackTraceElement getStackTrace(StackTraceElement[] sts, int index) {
        if (sts != null && sts.length > index) {
            return sts[index];
        }
        return null;
    }

    abstract Configure setPrefix(String prefix);

    abstract Configure addPrefix(String prefix);

    abstract <T> T getProperty(String key, Class<T> targetType, T defaultValue, StackTraceElement st);

}
