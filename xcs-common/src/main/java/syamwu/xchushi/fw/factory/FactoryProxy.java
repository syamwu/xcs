package syamwu.xchushi.fw.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.proxy.InstanceProxy;

/**
 * 工厂代理类，用以获取工厂类 
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class FactoryProxy {
    
    /**
     * 用以缓存各个工厂类实例
     */
    private static Map<String,AbstractFactory<?>> factoryMap = new ConcurrentHashMap<>();

    public static <T> AbstractFactory<T> getFactory(Class<T> cls, boolean cache) {
        return getFactory(cls, null, cache);
    }
    
    public static <T> AbstractFactory<T> getFactory(Class<T> cls) {
        return getFactory(cls, null, true);
    }
    
    /**
     * 根据cls和proxy获取相应的工厂类
     * 
     * @param targetClass 对应工厂类的产品类型
     * @param instanceProxy 工厂代理接口，用于自定义获取实现工厂类
     * @param cache  是否获取缓存中的工厂类
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> AbstractFactory<T> getFactory(Class<T> targetClass, InstanceProxy instanceProxy, boolean cache) {
        Asset.notNull(targetClass);
        if (instanceProxy != null) {
            return (AbstractFactory<T>) instanceProxy.get(AbstractFactory.class, targetClass);
        }
        if (cache && factoryMap.containsKey(targetClass.getName()) && factoryMap.get(targetClass.getName()) != null) {
            return (AbstractFactory<T>) factoryMap.get(targetClass.getName());
        }
        AbstractFactory<T> abstractFactory = null;
        try {
            if (abstractFactory == null) {
                if (Configure.class.isAssignableFrom(targetClass)) {
                    abstractFactory = (AbstractFactory<T>) (new ConfigureFactory());
                }
            }
            return abstractFactory;
        } finally {
            if (abstractFactory != null) {
                factoryMap.put(targetClass.getName(), abstractFactory);
            }
        }
    }

}
