package syamwu.xchushi.fw.common.observer;

/**
 * 主题统一接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Subject<T> {

    /**
     * 注册观察者对象
     * @param observer    观察者对象
     */
    public void attach(Observer<T> observer);
    
    /**
     * 删除观察者对象
     * @param observer    观察者对象
     */
    public void detach(Observer<T> observer);
    
    /**
     * 通知所有注册的观察者对象
     */
    public void nodifyObservers(T newState);
    
}
