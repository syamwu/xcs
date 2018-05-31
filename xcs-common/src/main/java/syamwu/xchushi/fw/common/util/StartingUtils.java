package syamwu.xchushi.fw.common.util;

import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.Starting;

/**
 * 启动类工具
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class StartingUtils {

    public static void start(Object obj) {
        start(obj, true);
    }
    
    /**
     * @param obj 对象
     * @param must  入参对象是否必须为启动类，若不为启动类则抛异常
     */
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
    
    /**
     * @param obj 对象
     * @param must  入参对象是否必须为启动类，若不为启动类则抛异常
     */
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
