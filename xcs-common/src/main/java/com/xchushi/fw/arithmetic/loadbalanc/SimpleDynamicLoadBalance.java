
package com.xchushi.fw.arithmetic.loadbalanc;

import com.xchushi.fw.arithmetic.loadbalanc.balance.RandomBalance;
import com.xchushi.fw.arithmetic.loadbalanc.code.HashUniqueCode;
import com.xchushi.fw.arithmetic.loadbalanc.code.UniqueCode;
import com.xchushi.fw.arithmetic.loadbalanc.exception.LoadBalanceException;
import com.xchushi.fw.arithmetic.loadbalanc.load.DynamicAble;
import com.xchushi.fw.arithmetic.loadbalanc.load.DynamicLoad;

/**
 * 简单的动态负载均衡算法，负载算法使用动态负载DynamicLoad，均衡算法使用随机算法RandomBalance
 * 
 * @author syam_wu
 * @date 2018-2-5
 */
public class SimpleDynamicLoadBalance<T> extends AbstractLoadBalance<T> {
    
    public SimpleDynamicLoadBalance(T[] objs, int[] loads, int scaleBase) {
        super(loads);
        if (objs == null || objs.length < 1 || loads == null || loads.length < 1 || objs.length != loads.length) {
            throw new LoadBalanceException("Load create fail,please check objs and loads.");
        }
        this.objs = objs;
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] == null)
                throw new LoadBalanceException("objs[" + i + "] can't be null");
        }
        if (UniqueCode.class.isAssignableFrom(objs[0].getClass())) {
            this.ucs = (UniqueCode[]) objs;
        } else {
            this.ucs = new UniqueCode[objs.length];
            for (int i = 0; i < objs.length; i++) {
                ucs[i] = new HashUniqueCode(objs[i]);
            }
        }
        this.scaleBase = scaleBase;
        this.balance = new RandomBalance<T>();
        this.load = new DynamicLoad<T>(this, ucs, loads);
    }

    @Override
    public int scaleBase() {
        return scaleBase;
    }

    @Override
    public T loadBalance() {
        return objs[balance.balance(scaleBase, load.load())];
    }

    @Override
    public int loadBalanceIndex() {
        return balance.balance(scaleBase, load.load());
    }

    public void dynamicLoad(int index, int weightCount) {
        ((DynamicAble<T>) load).dynamicLoad(ucs[index], weightCount);
    }

    public void dynamicLoad(UniqueCode uc, int weightCount) {
        ((DynamicAble<T>) load).dynamicLoad(uc, weightCount);
    }

    public void dynamicLoad(T obj, int weightCount) {
        for (int i = 0; i < objs.length; i++) {
            if (obj.equals(objs[i])) {
                ((DynamicAble<T>) load).dynamicLoad(i, weightCount);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public DynamicLoad getDynamicLoad(){
        return (DynamicLoad)this.load;
    }
}
