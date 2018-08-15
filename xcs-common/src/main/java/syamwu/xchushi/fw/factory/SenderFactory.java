package syamwu.xchushi.fw.factory;

import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.transfer.sender.HttpAndHttpsSender;
import syamwu.xchushi.fw.transfer.sender.Sender;

/**
 * 数据传输器工厂类
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class SenderFactory extends AbstractFactory<Sender>{

    /**
     * 根据配置获取响应的数据传输器
     * 
     * @param cls
     * @param configure
     * @return
     */
    public static Sender getSender(Class<?> cls, Configure configure) {
        if (configure == null)
            return HttpAndHttpsSender.getSender(cls);
        String protocol = configure.getProperty("sender.protocol");
        if ("http".equals(protocol) || "https".equals(protocol)) {
            return HttpAndHttpsSender.getSender(cls);
        }
        return HttpAndHttpsSender.getSender(cls);
    }

//    @Override
//    public void initInstance(Sender instance) {
//    }

    @Override
    public Sender getInstance(Class<?> exer, Object... objs) {
        return getSender(exer, null);
    }
    
}
