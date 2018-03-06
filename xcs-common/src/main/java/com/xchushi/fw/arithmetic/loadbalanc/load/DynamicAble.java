package com.xchushi.fw.arithmetic.loadbalanc.load;

import com.xchushi.fw.arithmetic.loadbalanc.code.UniqueCode;

public interface  DynamicAble<T> extends Load<T> {

    void dynamicLoad(int index, long weightCount);
    
    void dynamicLoad(UniqueCode uc, long weightCount);
    
}
