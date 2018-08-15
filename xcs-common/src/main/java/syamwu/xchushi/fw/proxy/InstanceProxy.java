package syamwu.xchushi.fw.proxy;

/**
 * 获取实例对象代理接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface InstanceProxy {

    /**
     * 
     * 获取targetClass实例对象
     * 
     * @param targetClass
     *            目标class
     * @param sourceClass
     *            请求源calss
     * @param args 业务参数
     * @return
     */
    <T> T get(Class<T> targetClass, Class<?> sourceClass);
    
    /**
     * 
     * 获取targetClass实例对象
     * 
     * @param targetClass
     *            目标class名称
     * @param sourceClass
     *            请求源calss
     * @param args 业务参数
     * @return
     * @throws ClassNotFoundException 
     */
    <T> T get(String className, Class<?> sourceClass) throws ClassNotFoundException;

}
