package com.xchushi.arithmetic.loadbalanc.load;

import com.xchushi.arithmetic.loadbalanc.LoadBalance;
import com.xchushi.arithmetic.loadbalanc.code.UniqueCode;

public class StaticLoad<T> extends AbstractLoad<T> {

    public StaticLoad(LoadBalance<T> loadBanlanc, UniqueCode[] uniqueCodes, int[] loads) {
        super(loadBanlanc, uniqueCodes, loads);
    }
    
    @Override
    public int[] load() {
        return this.loads;
    }

}
