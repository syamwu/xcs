package com.xchushi.fw.common.util;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.environment.Configurable;
import com.xchushi.fw.common.environment.Configure;

public class ConfigureUtils {
    
    /**
     * 设置配置
     * 
     * @param obj 需要配置的对象
     * @param config  配置接口
     * @param must  是否必须为可配置类
     */
    public static void setConfigure(Object obj, Configure config, boolean must) {
        Asset.notNull(obj);
        if (must) {
            Asset.isAssignableFrom(Configurable.class, obj.getClass());
            ((Configurable) obj).setConfigure(config);
        } else {
            if (Configurable.class.isAssignableFrom(obj.getClass())) {
                ((Configurable) obj).setConfigure(config);
            }
        }
    }
    
}
