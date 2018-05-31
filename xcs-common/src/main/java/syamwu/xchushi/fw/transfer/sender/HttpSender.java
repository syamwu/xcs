package syamwu.xchushi.fw.transfer.sender;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;

import syamwu.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import syamwu.xchushi.fw.arithmetic.loadbalanc.SimpleDynamicLoadBalance;
import syamwu.xchushi.fw.arithmetic.loadbalanc.load.DynamicAble;
import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.Starting;
import syamwu.xchushi.fw.common.annotation.ConfigSetting;
import syamwu.xchushi.fw.common.constant.StringConstant;
import syamwu.xchushi.fw.common.entity.Entity;
import syamwu.xchushi.fw.common.entity.HttpClientResponseEntity;
import syamwu.xchushi.fw.common.entity.SimpleEntity;
import syamwu.xchushi.fw.common.entity.StringSpliceEntity;
import syamwu.xchushi.fw.common.entity.Entity.EntityType;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.exception.InitException;
import syamwu.xchushi.fw.common.exception.SenderFailException;
import syamwu.xchushi.fw.common.executor.DefaultExecutor;
import syamwu.xchushi.fw.common.executor.Executor;
import syamwu.xchushi.fw.common.util.HttpClientUtils;
import syamwu.xchushi.fw.common.util.StreamUtils;
import syamwu.xchushi.fw.config.ConfigureFactory;
import syamwu.xchushi.fw.log.SysLoggerFactory;
import syamwu.xchushi.fw.transfer.CallBackAble;
import syamwu.xchushi.fw.transfer.collect.StringQueueCollector;
import syamwu.xchushi.fw.transfer.runner.AbstractSenderRunner;
import syamwu.xchushi.fw.transfer.runner.DefalutCollectSendRunner;

/**
 * HTTP传输器
 * 
 * @author: syam_wu
 * @date: 2018
 */
@Deprecated
@ConfigSetting(prefix = "sender")
public class HttpSender extends AbstractSender implements Starting  {

    private ThreadPoolExecutor ex;

    /**
     * 是否进行负载均衡操作
     */
    private boolean loadBalanceEnable = false;

    private int sendTimeOut = 10_000;

    /**
     * 负载均衡器
     */
    private LoadBalance<String> loadBanlanc;

    @SuppressWarnings("rawtypes")
    private DynamicAble dynamicAble;

    private String[] serverHostsArray;
    
    /**
     * 远程服务器hosts，用","分隔则使用负载均衡器去选取地址
     */
    private String serverHosts = "127.0.0.1";

    /**
     * 传输的字符集
     */
    private String charSet = "UTF-8";

    /**
     * 远程地址uri
     */
    private String uri = "";

    /**
     * 传输协议
     */
    private String protocol = "http";
    
    /**
     * 收集器
     */
    private AbstractSenderRunner abstractSenderRunner;
    
    private boolean collectEnable = true;

    private static HttpSender sender;
    
    private static Lock senderLock = new ReentrantLock();
    
    private static Logger logger = SysLoggerFactory.getLogger(HttpSender.class);
    
    public HttpSender() {
        super(null);
        if (sender == null) {
            sender = this;
        }
    }

    private HttpSender(Configure configure) {
        this(configure, getThreadPoolExecutorByConfigure(configure));
    }

    private HttpSender(Configure configure, ThreadPoolExecutor threadPoolExecutor) {
        super(configure);
        this.ex = threadPoolExecutor;
    }
    
    private void initHttpSender(Configure configure, ThreadPoolExecutor threadPoolExecutor) throws Exception {
        this.configure = configure;
        this.ex = threadPoolExecutor;
        initHttpSender();
    }

    private void initHttpSender() throws Exception {
        collectEnable = getProperty("collectEnable", Boolean.class, collectEnable);
        protocol = getProperty("protocol", protocol);
        charSet = getProperty("charSet", charSet);
        String serverUrlStr = getProperty("serverHosts", serverHosts);
        uri = getProperty("uri", uri);
        serverHostsArray = serverUrlStr.split(",");
        sendTimeOut = getProperty("sendTimeOut", Integer.class, 10_000);
        loadBalanceEnable = getProperty("loadBalanc.endable", Boolean.class, true);
        if (loadBalanceEnable) {
            String serverUrlLoads = getProperty("loadBalanc.loads", "");
            int scaleBase = getProperty("loadBalanc.scaleBase", Integer.class, 1000);
            int[] loads = new int[serverHostsArray.length];
            if (serverUrlLoads == null || serverUrlLoads.length() < 1) {
                for (int i = 0; i < loads.length; i++) {
                    loads[i] = 1;
                }
            } else {
                String[] serverUrlLoadsAr = serverUrlLoads.split(",");
                if (serverUrlLoadsAr.length != serverHostsArray.length) {
                    throw new InitException("eslogger.serverHostsLoads length isn't equal eslogger.serverHosts length");
                }
                for (int i = 0; i < loads.length; i++) {
                    loads[i] = Integer.valueOf(serverUrlLoadsAr[i]);
                }
            }
            SimpleDynamicLoadBalance<String> slb = new SimpleDynamicLoadBalance<String>(serverHostsArray, loads, scaleBase);
            loadBanlanc = slb;
            dynamicAble = slb.getDynamicLoad();
        }
        
        if (collectEnable) {
            if (ex == null)
                ex = getThreadPoolExecutorByConfigure(
                        configure == null ? ConfigureFactory.getConfigure(HttpSender.class) : configure);
            if (abstractSenderRunner != null) {
                abstractSenderRunner.setConfigure(configure);
                abstractSenderRunner.setTpe(ex);
                abstractSenderRunner.setSender(this);
            } else {
                abstractSenderRunner = configure == null ? null
                        : (AbstractSenderRunner) configure.getBean(StringConstant.COLLECT_CLASS, configure, this, ex);
                if (abstractSenderRunner == null) {
                    abstractSenderRunner = new DefalutCollectSendRunner<String>(this,
                            new StringQueueCollector(configure, new LinkedBlockingQueue<String>(Integer.MAX_VALUE)),
                            ex, new StringSpliceEntity("",EntityType.nomal));
                }
            }
            abstractSenderRunner.start();
        }
    }
    
    private void stopHttpSender() {
        if (abstractSenderRunner != null && abstractSenderRunner.started()) {
            abstractSenderRunner.stop();
        }
    }

    private static ThreadPoolExecutor getThreadPoolExecutorByConfigure(Configure configure) {
        ThreadPoolExecutor threadPoolExecutor = null;
        if (configure != null) {
            try {
                Executor ex = configure.getBean(StringConstant.EXECUTOR_CLASS);
                if (ex != null) {
                    threadPoolExecutor = ex.getThreadPoolExecutor(configure, HttpSender.class);
                }else{
                    threadPoolExecutor = new DefaultExecutor().getThreadPoolExecutor(configure, HttpSender.class);
                }
            } catch (Exception e) {
                logger.error("HttpSender getThreadPoolExecutorByConfigure fail:" + e.getMessage(), e);
            }
        }
        return threadPoolExecutor;
    }
    
    private String getProperty(String key, String defaultValue){
        if(configure == null){
            return defaultValue;
        }
        return getProperty(key, String.class, defaultValue);
    }
    
    private <T> T getProperty(String key, Class<T> targetType, T defaultValue){
        if(configure == null){
            return defaultValue;
        }
        return configure.getProperty(key, targetType, defaultValue);
    }

    public synchronized static HttpSender getSender(Configure configure, ThreadPoolExecutor threadPoolExecutor) {
        try {
            senderLock.lock();
            if (sender == null) {
                sender = new HttpSender(configure, threadPoolExecutor);
                try {
                    sender.start();
                } catch (Exception e) {
                    logger.error("initHttpSender fail:" + e.getMessage(), e);
                }
                return sender;
            } else {
                sender.initHttpSender(configure, threadPoolExecutor);
            }
        } catch (Exception e) {
            logger.error("HttpSender initFail:" + e.getMessage(), e);
        } finally {
            senderLock.unlock();
        }
        return sender;
    }

    public synchronized static Sender getSender(Class<?> cls) {
        try {
            senderLock.lock();
            if (sender == null) {
                sender = new HttpSender(ConfigureFactory.getConfigure(HttpSender.class));
                try {
                    sender.start();
                } catch (Exception e) {
                    logger.error("initHttpSender fail:" + e.getMessage(), e);
                }
                return sender;
            }
        } catch (Exception e) {
            logger.error("HttpSender initFail:" + e.getMessage(), e);
        } finally {
            senderLock.unlock();
        }
        return sender;
    }
    
    public String getServerHosts() {
        return serverHosts;
    }

    public void setServerHosts(String serverHosts) {
        this.serverHosts = serverHosts;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }
    
    public AbstractSenderRunner getCollectRunner() {
        return abstractSenderRunner;
    }

    public void setCollectRunner(AbstractSenderRunner abstractSenderRunner) {
        this.abstractSenderRunner = abstractSenderRunner;
    }

    @Override
    public synchronized void start() {
        if (started)
            return;
        started = true;
        try {
            initHttpSender();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    @Override
    public synchronized void stop() {
        started = false;
        stopHttpSender();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void send(final Object message, final CallBackAble callBackAble) {
        try {
            if (collected != null) {
                collected.collect(message);
                callBackAble.callBack(null);
            } else if (ex != null) {
                ex.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Object result = null;
                        try {
                            result = synSend(new SimpleEntity<String>((String) message, EntityType.nomal));
                        } catch (Exception e) {
                            callBackAble.sendingFailed(message, e);
                        }
                        return result;
                    }
                });
            } else {
                Object result = synSend(uri, new SimpleEntity<String>((String) message, EntityType.nomal));
                callBackAble.callBack(result);
            }
        } catch (Exception e) {
            callBackAble.sendingFailed(message, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object synSend(Object obj) throws Exception {
        return synSend(uri, (Entity<String>) obj);
    }

    /**
     * 用http或https协议发送message，根据loadBalanceEnable是否启用负载均衡器
     * 
     * @param uri
     * @param sendEntity
     * @return
     * @throws Exception
     * @author syam_wu
     */
    public Object synSend(String uri, Entity<String> sendEntity) throws Exception {
        Asset.notNull(sendEntity, "sendEntity can't be null");
        int hostIndex = loadBalanceEnable ? (loadBanlanc == null ? 0 : loadBanlanc.loadBalanceIndex()) : 0;
        String host = serverHostsArray[hostIndex];
        String url = protocol + "://" + host + "/" + uri;
        long time = System.currentTimeMillis();
        boolean sendFailed = false;
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtils.sendRequest(sendEntity.getValue(), url, charSet, sendTimeOut, true);
            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 202 ) {
                logger.debug("synSend status:200, response msg:"
                        + StreamUtils.inputStream2string(response.getEntity().getContent()));
            } else {
                throw new SenderFailException(url + " send fail:" + response.getStatusLine().getStatusCode()
                        + ", response msg:" + StreamUtils.inputStream2string(response.getEntity().getContent()));
            }
        } catch (Exception e) {
            sendFailed = true;
            throw e;
        } finally {
            // 动态负载,权值变动
            if (loadBalanceEnable) {
                dynamicAble.dynamicLoad(hostIndex,
                        sendFailed ? sendTimeOut : ((int) (System.currentTimeMillis() - time)));
            }
        }
        return new HttpClientResponseEntity(response, sendEntity.getEntityType());
    }

}
