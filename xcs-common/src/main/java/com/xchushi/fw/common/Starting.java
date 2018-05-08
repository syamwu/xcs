package com.xchushi.fw.common;

/**
 * 可启动和关闭接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Starting {

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
