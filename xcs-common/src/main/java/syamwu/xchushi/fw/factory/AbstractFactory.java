package syamwu.xchushi.fw.factory;

import syamwu.xchushi.fw.common.environment.Configurable;
import syamwu.xchushi.fw.common.environment.Configure;

/**
 * 抽象工厂类
 * 
 * @author: syam_wu
 * @date: 2018
 */
public abstract class AbstractFactory<T> implements Configurable {
    
    /**
     * 工厂类配置文件
     */
    protected Configure configure;
    
    protected AbstractFactory(){
        this(null);
    }
    
    protected AbstractFactory(Configure configure){
        this.configure = configure;
    }
    
    public T getInstance(Class<?> exer) {
        return getInstance(exer, new Object[] {});
    }
    
    public void setConfigure(Configure configure) {
        this.configure = configure;
    }
    
    /**
     * 获取静态实例
     * 
     * @param exer
     * @return
     */
    public abstract T getInstance(Class<?> exer, Object... objs);
    
}
