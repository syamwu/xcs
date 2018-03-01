package xcs.arithmetic.loadbalanc.load;

import xcs.arithmetic.loadbalanc.LoadBalance;
import xcs.arithmetic.loadbalanc.code.UniqueCode;

public class StaticLoad<T> extends AbstractLoad<T> {

    public StaticLoad(LoadBalance<T> loadBanlanc, UniqueCode[] uniqueCodes, int[] loads) {
        super(loadBanlanc, uniqueCodes, loads);
    }
    
    @Override
    public int[] load() {
        return this.loads;
    }

}
