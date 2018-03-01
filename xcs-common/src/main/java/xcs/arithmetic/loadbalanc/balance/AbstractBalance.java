package xcs.arithmetic.loadbalanc.balance;

import xcs.arithmetic.loadbalanc.LoadBalance;

public abstract class AbstractBalance<T> implements Balance<T> {

    LoadBalance<T> loadBalance;

    int[] loads;

    AbstractBalance(LoadBalance<T> loadBalance, int[] loads) {
        this.loadBalance = loadBalance;
        this.loads = loads;
    }

}
