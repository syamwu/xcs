package syamwu.xchushi.fw.arithmetic.loadbalanc.load;

import syamwu.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import syamwu.xchushi.fw.arithmetic.loadbalanc.code.UniqueCode;

/**
 * 静态负载器
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class StaticLoad<T> extends AbstractLoad<T> {

    public StaticLoad(LoadBalance<T> loadBanlanc, UniqueCode[] uniqueCodes, int[] loads) {
        super(loadBanlanc, uniqueCodes, loads);
    }
    
    @Override
    public int[] load() {
        return this.loads;
    }

}
