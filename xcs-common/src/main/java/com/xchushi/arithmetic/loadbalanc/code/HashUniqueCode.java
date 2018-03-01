package com.xchushi.arithmetic.loadbalanc.code;

import com.xchushi.arithmetic.loadbalanc.exception.LoadBalanceException;

public class HashUniqueCode implements UniqueCode {

    private Object obj;

    public HashUniqueCode(Object obj) {
        if (obj == null)
            throw new LoadBalanceException("obj can't be null");
        this.obj = obj;
    }

    @Override
    public int get() {
        return obj.hashCode();
    }

}
