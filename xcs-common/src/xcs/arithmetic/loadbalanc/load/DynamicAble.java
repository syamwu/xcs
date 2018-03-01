package xcs.arithmetic.loadbalanc.load;

import xcs.arithmetic.loadbalanc.code.UniqueCode;

public interface  DynamicAble<T> extends Load<T> {

    void dynamicLoad(int index, long weightCount);
    
    void dynamicLoad(UniqueCode uc, long weightCount);
    
}
