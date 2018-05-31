package syamwu.xchushi.fw.transfer.sender;

import syamwu.xchushi.fw.common.Starting;
import syamwu.xchushi.fw.common.annotation.ConfigSetting;
import syamwu.xchushi.fw.transfer.CallBackAble;

/**
 * elasticsearch传输器
 * 
 * @author: syam_wu
 * @date: 2018
 */
@ConfigSetting(prefix = "sender")
public class ElasticsearchSender extends AbstractSender implements Sender, Starting  {
    
    //private static Logger logger = SysLoggerFactory.getLogger(ElasticsearchSender.class);
    
    private static ElasticsearchSender sender = null;
    
    public ElasticsearchSender() {
        super(null);
        if (sender == null) {
            sender = this;
        }
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    public void callBack(Object obj) {
        System.out.println("ElasticsearchSender call back!!!");
    }

    public void sendingFailed(Object message, Throwable e) {
        System.out.println("ElasticsearchSender sendingFailed!!!");
    }

    @Override
    public Object synSend(Object obj) throws Exception {
        System.out.println("ElasticsearchSender sendObj:"+obj);
        return null;
    }

    @Override
    public void send(Object message, CallBackAble callBackAble) throws Exception {
        synSend(message);
    }
}
