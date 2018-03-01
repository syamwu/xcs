package com.xchushi.arithmetic.loadbalanc.load;

import com.xchushi.arithmetic.loadbalanc.code.UniqueCode;

public interface  DynamicAble<T> extends Load<T> {

    void dynamicLoad(int index, long weightCount);
    
    void dynamicLoad(UniqueCode uc, long weightCount);
    
}
