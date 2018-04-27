package com.xchushi.fw.common.observer;

import java.util.ArrayList;
import java.util.List;

import com.xchushi.fw.common.Asset;

public abstract class AbstractSubject<T> implements Subject<T> {

    /**
     * 用来保存注册的观察者对象
     */
    private List<Observer<T>> list = new ArrayList<Observer<T>>();

    public void attach(Observer<T> observer) {
        list.add(observer);
    }
    
    public void attach(Observer<T>[] observer) {
        Asset.notNull(observer);
        for (Observer<T> observer2 : observer) {
            attach(observer2);
        }
    }

    public void detach(Observer<T> observer) {
        list.remove(observer);
    }
    
    public void detach(Observer<T>[] observer) {
        Asset.notNull(observer);
        for (Observer<T> observer2 : observer) {
            detach(observer2);
        }
    }

    public void nodifyObservers(T changeData) {
        for (Observer<T> observer : list) {
            observer.change(changeData);
        }
    }

}
