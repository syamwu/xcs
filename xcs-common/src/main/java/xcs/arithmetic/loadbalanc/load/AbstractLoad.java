package xcs.arithmetic.loadbalanc.load;

import java.math.BigDecimal;

import xcs.arithmetic.loadbalanc.LoadBalance;
import xcs.arithmetic.loadbalanc.code.UniqueCode;

public abstract class AbstractLoad<T> implements Load<T> {
    
    /**
     * 对象数组
     */
    UniqueCode[] uniqueCodes;

    /**
     * 动态权值数组,eg: 2,2,3,4
     */
    int[] loads;
    
    /**
     * 固定权值比例,eg: 2,2,3,4
     */
    int[] noChangeLoads;
    
    /**
     * 负载均衡器
     */
    LoadBalance<T> loadBanlanc;
    
    public AbstractLoad(LoadBalance<T> loadBanlanc, UniqueCode[] uniqueCodes, int[] loads) {
        this.loadBanlanc = loadBanlanc;
        this.uniqueCodes = uniqueCodes;
        this.loads = loads;
        this.noChangeLoads = new int[loads.length];
        System.arraycopy(loads, 0, noChangeLoads, 0, loads.length);
    }
    
    public long sum(long[] longs) {
        long sum = 0;
        for (int i = 0; i < longs.length; i++) {
            sum = sum + longs[i];
        }
        return sum;
    }

    public BigDecimal sum(BigDecimal[] doubles) {
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < doubles.length; i++) {
            sum = sum.add(doubles[i]);
        }
        return sum;
    }

    public long addAvg(long avg, long count, long value) {
        return count <= 0L ? avg : (avg * count + value) / (count + 1L);
    }
    
}
