package com.xchushi.fw.arithmetic.loadbalanc.load;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.code.UniqueCode;
import com.xchushi.fw.arithmetic.loadbalanc.exception.LoadBalanceException;
import com.xchushi.fw.log.SysLoggerFactory;

/**
 * 动态负载器
 * 
 * @author syam_wu
 * @date 2018-2-6
 */
public class DynamicLoad<T> extends AbstractLoad<T> implements DynamicAble<T> {
    
    private final long DEFAULT_AVGCOUNT = 30l;

    /**
     * 平均权值
     */
    private long[] avgValues;

    /**
     * 各个权值改变次数
     */
    private long[] loadCounts;

    /**
     * 计算平均权值的个数n(可用于缓冲权值的突增突减,该数值越大，缓冲能力越强)
     */
    private long avgCount;

    /**
     * 是否为初始化权值
     */
    private boolean firstCord = true;

    /**
     * 用以设置动态输入的weightCount是否越大代表的权值就越大,这里默认为false(eg：
     * 若输入的weightCount代表请求响应时间长度，则时间越长代表权值越小)
     */
    private boolean upsideDown = false;

    /**
     * 多线程下dynamicLoad方法必须加锁操作
     */
    private Lock avgLock = new ReentrantLock();

    /**
     * 多线程下loads数值的改变和获取必须加锁操作
     */
    private Lock loadLock = new ReentrantLock();
    
    private static Logger logger = SysLoggerFactory.getLogger(DynamicLoad.class);

    public DynamicLoad(LoadBalance<T> loadBanlanc, UniqueCode[] uniqueCodes, int[] loads, long avgCount,
            boolean upsideDown) {
        super(loadBanlanc, uniqueCodes, loads);
        this.avgValues = new long[loads.length];
        this.loadCounts = new long[loads.length];
        this.avgCount = avgCount;
        this.upsideDown = upsideDown;
    }

    public DynamicLoad(LoadBalance<T> loadBanlanc, UniqueCode[] uniqueCodes, int[] loads) {
        super(loadBanlanc, uniqueCodes, loads);
        this.avgValues = new long[loads.length];
        this.loadCounts = new long[loads.length];
        this.avgCount = DEFAULT_AVGCOUNT;
    }

    @Override
    public int[] load() {
        return loads();
    }

    public void dynamicLoad(UniqueCode uc, long weightCount) {
        if (uc == null) {
            throw new LoadBalanceException("UniqueCode can't be null");
        }
        int index = -1;
        for (int i = 0; i < loads.length; i++) {
            if (uniqueCodes[i].get() == uc.get()) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new LoadBalanceException("UniqueCode:" + uc.get() + ",din't exist");
        }
        dynamicLoad(index, weightCount);
    }
    
    @Override
    public void dynamicLoad(int index, long weightCount) {
        if (loads.length < 2) {
            return;
        }
        if (index >= loads.length) {
            throw new LoadBalanceException("index can't be equal to or bigger than loads.length");
        }
        if (weightCount <= 0) {
            return;
        }
        try {
            avgLock.lock();
            if (firstCord) {
                initAvgValuesAndCount(weightCount);
                firstCord = false;
                return;
            }
            if (Long.MAX_VALUE > loadCounts[index]) {
                loadCounts[index]++;
            }
            avgValues[index] = addAvg(avgValues[index], avgCount, weightCount);
            reloads(weightCount);
        } finally {
            avgLock.unlock();
        }
    }

    protected void reloads(long weightCount) {
        try {
            loadLock.lock();
            long[] avgValuesCp = new long[avgValues.length];
            System.arraycopy(avgValues, 0, avgValuesCp, 0, avgValues.length);
            BigDecimal[] newWeights = new BigDecimal[loads.length];
            long avgValuesSum = sumArray(avgValuesCp);
            int scaleBase = loadBanlanc.scaleBase();
            BigDecimal bscaleBase = new BigDecimal(scaleBase);
            for (int i = 0; i < noChangeLoads.length; i++) {
                BigDecimal bWeight = new BigDecimal(noChangeLoads[i]);
                newWeights[i] = new BigDecimal(upsideDownWeight() ? avgValuesCp[i] : avgValuesSum - avgValuesCp[i])
                        .multiply(bWeight);
                if (newWeights[i].doubleValue() <= 0) {
                    newWeights[i] = new BigDecimal(0);
                }
            }
            BigDecimal newWeightsSum = sumArray(newWeights);
            int[] oldLoads = new int[loads.length];
            System.arraycopy(loads, 0, oldLoads, 0, loads.length);
            for (int i = 0; i < loads.length; i++) {
                if (loads[i] > 0) {
                    loads[i] = newWeights[i].divide(newWeightsSum, 2, RoundingMode.HALF_UP).multiply(bscaleBase)
                            .intValue();
                    loads[i] = loads[i] <= 0 ? 1 : loads[i];
                }

            }

            // 输出动态负载结果
            logger.debug("weightCount:" + weightCount + ",loadCounts:" + Arrays.toString(loadCounts) + ",avgValues:"
                    + Arrays.toString(avgValues) + ",newWeights:" + Arrays.toString(newWeights)+ ",old_loads:"
                            + Arrays.toString(oldLoads) + ",new_loads:"
                    + Arrays.toString(loads));
        } finally {
            loadLock.unlock();
        }
    }

    protected void initAvgValuesAndCount(long value) {
        for (int i = 0; i < loads.length; i++) {
            avgValues[i] = value;
            loadCounts[i]++;
        }
    }

    protected int[] loads() {
        try {
            loadLock.lock();
            int[] result = new int[loads.length];
            System.arraycopy(loads, 0, result, 0, loads.length);
            return result;
        } finally {
            loadLock.unlock();
        }
    }

    protected boolean upsideDownWeight() {
        return upsideDown;
    }

}
