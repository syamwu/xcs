package com.xchushi.fw.transfer.sender;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.SimpleLoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.load.DynamicAble;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.HttpClientResponseEntity;
import com.xchushi.fw.common.entity.SimpleEntity;
import com.xchushi.fw.common.entity.Entity.EntityType;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.exception.SenderFailException;
import com.xchushi.fw.common.executor.DefaultExecutor;
import com.xchushi.fw.common.executor.Executor;
import com.xchushi.fw.common.util.HttpClientUtils;
import com.xchushi.fw.common.util.StreamUtils;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.runner.AbstractCollectRunner;
import com.xchushi.fw.transfer.runner.DefalutCollectSendRunner;

@ConfigSetting(prefix = "sender")
public class HttpSender extends AbstractSender implements Sender {

    private ThreadPoolExecutor ex;

    private boolean loadBalanceEnable = false;

    private int sendTimeOut = 10_000;

    private LoadBalance<String> loadBanlanc;

    @SuppressWarnings("rawtypes")
    private DynamicAble dynamicAble;

    private String[] serverHosts;

    private String charSet = "UTF-8";

    private String uri = "";

    private String protocol = "http";

    private static HttpSender sender;

    private static Logger logger = SysLoggerFactory.getLogger(HttpSender.class);
    
    public static AtomicInteger okCount = new AtomicInteger(0);

    private HttpSender() {
        this(ConfigureFactory.getConfigure(HttpSender.class));
    }

    private HttpSender(Configure configure) {
        this(configure, getThreadPoolExecutorByConfigure(configure));
    }

    private HttpSender(Configure configure, ThreadPoolExecutor threadPoolExecutor) {
        super(configure);
        try {
            initHttpSender(configure, threadPoolExecutor);
        } catch (Exception e) {
            logger.error("initHttpSender fail:" + e.getMessage(), e);
        }
    }

    private void initHttpSender(Configure configure, ThreadPoolExecutor threadPoolExecutor) throws Exception {
        this.ex = threadPoolExecutor;
        boolean collectEnable = false;
        if (configure != null) {
            collectEnable = configure.getProperty("collectEnable", Boolean.class, true);
            this.protocol = configure.getProperty("protocol", "http");
            this.charSet = configure.getProperty("charSet", "UTF-8");
            String serverUrlStr = configure.getProperty("serverHosts", "127.0.0.1");
            this.uri = configure.getProperty("uri", "");
            this.serverHosts = serverUrlStr.split(",");
            this.sendTimeOut = configure.getProperty("sendTimeOut", Integer.class, 10_000);
            this.loadBalanceEnable = configure.getProperty("loadBalanc.endable", Boolean.class, true);
            if (loadBalanceEnable) {
                String serverUrlLoads = configure.getProperty("loadBalanc.loads", "");
                int scaleBase = configure.getProperty("loadBalanc.scaleBase", Integer.class, 1000);
                int[] loads = new int[this.serverHosts.length];
                if (serverUrlLoads == null || serverUrlLoads.length() < 1) {
                    for (int i = 0; i < loads.length; i++) {
                        loads[i] = 1;
                    }
                } else {
                    String[] serverUrlLoadsAr = serverUrlLoads.split(",");
                    if (serverUrlLoadsAr.length != this.serverHosts.length) {
                        throw new InitException(
                                "eslogger.serverHostsLoads length isn't equal eslogger.serverHosts length");
                    }
                    for (int i = 0; i < loads.length; i++) {
                        loads[i] = Integer.valueOf(serverUrlLoadsAr[i]);
                    }
                }
                SimpleLoadBalance<String> slb = new SimpleLoadBalance<String>(serverHosts, loads, scaleBase);
                this.loadBanlanc = slb;
                this.dynamicAble = slb.getDynamicLoad();
            }
        }
        if (collectEnable) {
            AbstractCollectRunner collectRunner = configure.getBean(StringConstant.COLLECTCLASS, configure, this, threadPoolExecutor);
            if(collectRunner == null){
                collectRunner = new DefalutCollectSendRunner(configure, this, threadPoolExecutor);
            }
            threadPoolExecutor.execute(collectRunner);
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
                logger.error("HttpSender load executorClass fail:" + e.getMessage(), e);
            }
        }
        return threadPoolExecutor;
    }

    public synchronized static HttpSender getSender(Configure configure, ThreadPoolExecutor threadPoolExecutor) {
        try {
            if (sender == null) {
                return new HttpSender(configure, threadPoolExecutor);
            } else {
                sender.initHttpSender(configure, threadPoolExecutor);
            }
        } catch (Exception e) {
            logger.error("HttpSender initFail:" + e.getMessage(), e);
        }
        return sender;
    }

    public synchronized static Sender getSender(Class<?> cls) {
        try {
            if (sender == null) {
                sender = new HttpSender();
                return sender;
            }
        } catch (Exception e) {
            logger.error("HttpSender initFail:" + e.getMessage(), e);
        }
        return sender;
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
        String host = serverHosts[hostIndex];
        String url = protocol + "://" + host + "/" + uri;
        long time = System.currentTimeMillis();
        boolean sendFailed = false;
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtils.sendRequest(sendEntity.getMessage(), url, charSet, sendTimeOut);
            if (response.getStatusLine().getStatusCode() == 200) {
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
