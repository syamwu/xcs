package com.xchushi.fw.config;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.environment.Configure;

public class ConfigureFactory {

    private static Configure configure;

    public static Configure getConfigure(Class<?> cls) {
        Asset.notNull(cls);
        if (configure != null)
            return configure;
        return XcsConfigure.getConfigure(cls);
    }

    public static void setConfigure(Configure config) {
        Asset.notNull(config);
        configure = config;
    }

}
