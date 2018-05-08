package com.xchushi.fw.arithmetic.loadbalanc.balance;

/**
 * 均衡算法接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Balance<T> {

    /**
     * 根据loads[]权值比进行均衡算法，最终输出目标index
     * 
     * @param scaleBase  比例基数，用以精度计算
     * @param loads  权值比数组
     * @return
     */
    int balance(int scaleBase, int[] loads);

}
