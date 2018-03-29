package com.xchushi.fw.transfer.sender;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.SimpleLoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.load.DynamicAble;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.Entity.EntityType;
import com.xchushi.fw.common.entity.HttpClientResponseEntity;
import com.xchushi.fw.common.entity.SimpleEntity;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.exception.SenderFailException;
import com.xchushi.fw.common.executor.DefaultExecutor;
import com.xchushi.fw.common.executor.Executor;
import com.xchushi.fw.common.util.HttpClientUtils;
import com.xchushi.fw.common.util.StreamUtils;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.runner.CollectRunner;
import com.xchushi.fw.transfer.runner.DefalutCollectSendRunner;

@ConfigSetting(prefix = "sender")
public class HttpSender extends AbstractSender implements Sender {

    private ThreadPoolExecutor ex;

    private boolean loadBalanceEnable = false;

    private int sendTimeOut = 10_000;

    private LoadBalance<String> loadBanlanc;

    @SuppressWarnings("rawtypes")
    private DynamicAble dynamicAble;

    private String[] serverHostsArray;
    
    private String serverHosts = "127.0.0.1";

    private String charSet = "UTF-8";

    private String uri = "";

    private String protocol = "http";
    
    private CollectRunner collectRunner;
    
    private boolean collectEnable = true;

    private static HttpSender sender;
    
    private static Lock senderLock = new ReentrantLock();
    
    private static Logger logger = SysLoggerFactory.getLogger(HttpSender.class);
    
    public static AtomicInteger okCount = new AtomicInteger(0);
    
    public HttpSender(){
        super(null);
        sender = this;
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
            SimpleLoadBalance<String> slb = new SimpleLoadBalance<String>(serverHostsArray, loads, scaleBase);
            loadBanlanc = slb;
            dynamicAble = slb.getDynamicLoad();
        }
        
        if (collectEnable) {
            if (ex == null)
                ex = getThreadPoolExecutorByConfigure(
                        configure == null ? ConfigureFactory.getConfigure(HttpSender.class) : configure);
            collectRunner = configure == null ? null
                    : (CollectRunner) configure.getBean(StringConstant.COLLECTCLASS, configure, this, ex);
            if (collectRunner == null) {
                collectRunner = new DefalutCollectSendRunner(this, ex);
            }
            collectRunner.start();
        }
    }
    
    private void stopHttpSender() {
        if (collectRunner != null && collectRunner.started()) {
            collectRunner.stop();
        }
    }

    private static ThreadPoolExecutor getThreadPoolExecutorByConfigure(Configure configure) {
        ThreadPoolExecutor threadPoolExecutor = null;
        if (configure != null) {
            try {
                Executor ex = configure.getBean(StringConstant.EXECUTORCLASS);
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
    public void send(final Object message) {
        try {
            if (collectible != null) {
                collectible.collect(message);
            } else if (ex != null) {
                ex.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Object result = null;
                        try {
                            result = synSend(new SimpleEntity<String>((String) message, EntityType.nomal));
                        } catch (Exception e) {
                            sender.sendingFailed(message, e);
                        }
                        return result;
                    }
                });
            } else {
                synSend(uri, new SimpleEntity<String>((String) message, EntityType.nomal));
            }
        } catch (Exception e) {
            sender.sendingFailed(message, e);
        }
    }

    @Override
    public void callBack(Object obj) {
        // logger.debug("HttpSender.callBack:" + JSON.toJSONString(obj));
        okCount.incrementAndGet();
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        // logger.debug("sendingFailed:" + JSON.toJSONString(message));
        if (e != null) {
            logger.debug("sendingFailed:" + e.getClass() + "-" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        if (message == null) {
            return;
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
     * @author SamJoker
     */
    public Object synSend(String uri, Entity<String> sendEntity) throws Exception {
        Asset.notNull(sendEntity, "sendEntity can't be null");
        int hostIndex = loadBalanceEnable ? loadBanlanc.loadBalanceIndex() : 0;
        String host = serverHostsArray[hostIndex];
        String url = protocol + "://" + host + "/" + uri;
        long time = System.currentTimeMillis();
        boolean sendFailed = false;
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtils.sendRequest(sendEntity.getData(), url, charSet, sendTimeOut, true);
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
            if (loadBalanceEnable) {
                dynamicAble.dynamicLoad(hostIndex,
                        sendFailed ? sendTimeOut : ((int) (System.currentTimeMillis() - time)));
            }
        }
        return new HttpClientResponseEntity(response, sendEntity.getEntityType());
    }

}
