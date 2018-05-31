package syamwu.xchushi.fw.transfer.runner;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import syamwu.xchushi.fw.common.Starting;
import syamwu.xchushi.fw.common.entity.Entity;
import syamwu.xchushi.fw.common.environment.Configurable;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.transfer.CallBackAble;
import syamwu.xchushi.fw.transfer.sender.Sender;

/**
 * 抽象收集器
 * 
 * @author: syam_wu
 * @date: 2018
 */
public abstract class AbstractSenderRunner implements Runnable, Starting, Configurable {

    protected Configure configure;

    protected Sender sender;

    protected ThreadPoolExecutor tpe;

    protected boolean started = false;
    
    public AbstractSenderRunner(){
    }

    protected AbstractSenderRunner(Configure configure, Sender sender, ThreadPoolExecutor threadPoolExecutor) {
        this.configure = configure;
        this.sender = sender;
        this.tpe = threadPoolExecutor;
    }

    public Configure getConfigure() {
        return configure;
    }

    public void setConfigure(Configure configure) {
        this.configure = configure;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public ThreadPoolExecutor getTpe() {
        return tpe;
    }

    public void setTpe(ThreadPoolExecutor tpe) {
        this.tpe = tpe;
    }
    
    @SuppressWarnings("rawtypes")
    class SendTask implements Callable<Entity<?>> {

        private Entity msg;
        private CallBackAble callBackAble;

        SendTask(Entity sendEntity, CallBackAble callBackAble) {
            this.msg = sendEntity;
            this.callBackAble = callBackAble;
        }

        @Override
        public Entity call() throws Exception {
            Entity obj = null;
            sender.send(msg, callBackAble);
            return obj;
        }
    }
    
}
