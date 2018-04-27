package com.xchushi.fw.common.util;

import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.Starting;

public class StartingUtils {

    public static void start(Object obj) {
        start(obj, true);
    }
    
    public static void start(Object obj, boolean must) {
        Asset.notNull(obj);
        if (must) {
            Asset.isAssignableFrom(Starting.class, obj.getClass());
            ((Starting) obj).start();
        } else {
            if (Starting.class.isAssignableFrom(obj.getClass())) {
                ((Starting) obj).start();
            }
        }
    }
    
    public static void stop(Object obj) {
        stop(obj, true);
    }
    
    public static void stop(Object obj, boolean must) {
        Asset.notNull(obj);
        if (must) {
            Asset.isAssignableFrom(Starting.class, obj.getClass());
            ((Starting) obj).stop();
        } else {
            if (Starting.class.isAssignableFrom(obj.getClass())) {
                ((Starting) obj).stop();
            }
        }
    }
    
    public static boolean started(Object obj) {
        Asset.notNull(obj);
        Asset.isAssignableFrom(Starting.class, obj.getClass());
        return ((Starting) obj).started();
    }
    
}
