package syamwu.xchushi.fw.transfer;

public interface CallBackAble {

    /**
     * 成功回调
     * 
     * @param obj
     */
    void callBack(Object obj);
    
    /**
     * 失败回调
     * 
     * @param message
     * @param e
     */
    void sendingFailed(Object message, Throwable e);
    
}
