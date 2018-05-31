package syamwu.xchushi.fw.transfer.sender;

import syamwu.xchushi.fw.transfer.CallBackAble;

/**
 * 数据传输统一接口
 * 
 * @author: syam_wu
 * @date: 2018
 */
public interface Sender {

    /**
     * 传输message
     * 
     * @param message
     * @throws Exception
     */
    void send(Object message , CallBackAble callBackAble) throws Exception;
    
}
