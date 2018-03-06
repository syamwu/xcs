package cn.yunyichina.front;

import org.springframework.core.env.Environment;

import com.xchushi.fw.common.environment.Configure;

public class SpringConfigure implements Configure {

    private Environment env;

    private String prefix;

    public SpringConfigure(Environment env, String prefix) {
        this.env = env;
        this.prefix = prefix;
    }

    public SpringConfigure(Environment env) {
        this.env = env;
        this.prefix = "";
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return env.getProperty(prefix + key, targetType, defaultValue);
    }

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
    public Configure addPrefix(String prefix) {
        return null;
    }

    @Override
    public <T> T getBean(String key,Object... objects) throws Exception {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public Configure setPrefix(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

}
