package com.xchushi.fw.config;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.environment.Configure;

public class ConfigureFactory {

    public static Configure getConfigure(Class<?> cls) {
        Asset.notNull(cls);
        return XcsConfigure.getConfigure(cls);
    }

}
