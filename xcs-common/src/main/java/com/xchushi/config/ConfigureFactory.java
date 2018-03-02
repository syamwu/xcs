package com.xchushi.config;

import com.xchushi.annotation.ConfigSetting;
import com.xchushi.common.Asset;
import com.xchushi.common.environment.Configure;

public class ConfigureFactory {

    public static Configure getConfigure(Class<?> cls) {
        Asset.notNull(cls);
        Configure configure = XcsConfigure.getConfigure(cls);
        ConfigSetting configSetting = cls.getAnnotation(ConfigSetting.class);
        if (configSetting != null) {
            configure.addPrefix(configSetting.prefix());
        }
        return configure;
    }

}
