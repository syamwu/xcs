package syamwu.xchushi.fw.common;

/**
 * 对象生命周期接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface LifeCycle {

    /**
     * 启动
     */
    void start();
    
    /**
     * 关闭
     */
    void stop();
    
    /**
     * 当前状态
     * 
     * @return
     */
    boolean started();
    
}
