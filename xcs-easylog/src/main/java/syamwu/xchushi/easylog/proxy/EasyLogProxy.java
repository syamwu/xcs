package syamwu.xchushi.easylog.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import syamwu.xchushi.easylog.XcsLogger;
import syamwu.xchushi.easylog.factory.LoggerFactory;
import syamwu.xchushi.easylog.factory.XcsLoggerConfigueFactory;
import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.factory.AbstractFactory;
import syamwu.xchushi.fw.proxy.InstanceProxy;

/**
 * easylog静态代理类，用以生成自定义工厂类
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class EasyLogProxy implements InstanceProxy {

    private static EasyLogProxy easyLogProxy;

    /**
     * 用以缓存各个工厂类实例
     */
    private static Map<String, AbstractFactory<?>> factoryMap = new ConcurrentHashMap<>();

    public synchronized static EasyLogProxy getInstance() {
        if (easyLogProxy == null) {
            easyLogProxy = new EasyLogProxy();
        }
        return easyLogProxy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> targetClass, Class<?> sourceClass) {
        Asset.notNull(targetClass);
        T result = null;
        if (AbstractFactory.class.isAssignableFrom(targetClass)) {
            if (factoryMap.containsKey(targetClass.getName())) {
                result = (T) factoryMap.get(targetClass.getName());
            } else {
                if (Configure.class.isAssignableFrom(sourceClass)) {
                    result = (T) new XcsLoggerConfigueFactory();
                }
                if (XcsLogger.class.isAssignableFrom(sourceClass)) {
                    result = (T) new LoggerFactory();
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String className, Class<?> sourceClass) throws ClassNotFoundException {
        return (T) get(getClass().getClassLoader().loadClass(className), sourceClass);
    }

}
