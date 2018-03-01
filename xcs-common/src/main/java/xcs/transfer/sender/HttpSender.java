package xcs.transfer.sender;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;

import xcs.arithmetic.loadbalanc.LoadBalance;
import xcs.arithmetic.loadbalanc.SimpleLoadBalance;
import xcs.arithmetic.loadbalanc.load.DynamicAble;
import xcs.common.entity.Entity;
import xcs.common.entity.HttpClientResponseEntity;
import xcs.common.environment.Configure;
import xcs.common.exception.InitException;
import xcs.common.exception.SenderFailException;
import xcs.common.util.HttpClientUtils;
import xcs.common.util.StreamUtils;
import xcs.log.SysLogger;

public class HttpSender extends AbstractSender implements Sender {
    
    private static Logger logger = SysLogger.getLogger(HttpSender.class);

    private static ThreadPoolExecutor ex;

    private static HttpSender sender;

    private static boolean loadBalanceEnable = false;

    private static int sendTimeOut = 10_000;

    private static LoadBalance<String> loadBanlanc;

    @SuppressWarnings("rawtypes")
    private static DynamicAble dynamicAble;

    private static String[] serverHosts;

    private static String charSet = "UTF-8";

    private static String uri = "";

    private static String protocol = "http";

    private static void initHttpSender(String uriq, Configure configure, ThreadPoolExecutor threadPoolExecutor) {
        ex = threadPoolExecutor;
        uri = uriq;
        if (configure != null) {
            protocol = configure.getProperty("protocol", "http");
            charSet = configure.getProperty("charSet", "UTF-8");
            String serverUrlStr = configure.getProperty("serverHosts", "127.0.0.1");
            serverHosts = serverUrlStr.split(",");
            sendTimeOut = configure.getProperty("sendTimeOut", Integer.class, 10_000);
            loadBalanceEnable = configure.getProperty("loadBalanc.endable", Boolean.class, false);
            if (loadBalanceEnable) {
                String serverUrlLoads = configure.getProperty("loadBalanc.loads", "");
                int scaleBase = configure.getProperty("loadBalanc.scaleBase", Integer.class, 1000);
                int[] loads = new int[serverHosts.length];
                if (serverUrlLoads == null || serverUrlLoads.length() < 1) {
                    for (int i = 0; i < loads.length; i++) {
                        loads[i] = 1;
                    }
                } else {
                    String[] serverUrlLoadsAr = serverUrlLoads.split(",");
                    if (serverUrlLoadsAr.length != serverHosts.length) {
                        throw new InitException(
                                "eslogger.serverHostsLoads length isn't equal eslogger.serverHosts length");
                    }
                    for (int i = 0; i < loads.length; i++) {
                        loads[i] = Integer.valueOf(serverUrlLoadsAr[i]);
                    }
                }
                SimpleLoadBalance<String> slb = new SimpleLoadBalance<String>(serverHosts, loads, scaleBase);
                loadBanlanc = slb;
                dynamicAble = slb.getDynamicLoad();
            }
        }
    }

    public static HttpSender getSender(String uri, Configure configure, ThreadPoolExecutor threadPoolExecutor) {
        initHttpSender(uri, configure, threadPoolExecutor);
        return sender;
    }

    public synchronized static Sender getSender() {
        if (sender == null) {
            sender = new HttpSender();
            return sender;
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
                            result = synSend(message);
                        } catch (Exception e) {
                            sender.sendingFailed(message, e);
                        }
                        return result;
                    }
                });
            } else {
                synSend(message);
            }
        } catch (Exception e) {
            sender.sendingFailed(message, e);
        }
    }
    
    @Override
    public void callBack(Object obj) {
        //logger.info("HttpSender.callBack:" + JSON.toJSONString(obj));
    }

    @Override
    public void sendingFailed(Object message, Throwable e) {
        //logger.info("sendingFailed:" + JSON.toJSONString(message));
        if (e != null) {
            logger.info("sendingFailed:" + e.getClass() + "-" + e.getMessage());
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
        if (sendEntity == null) {
            throw new SenderFailException("sendEntity can't be null");
        }
        int hostIndex = loadBalanceEnable ? loadBanlanc.loadBalanceIndex() : 0;
        String host = serverHosts[hostIndex];
        String url = protocol + "://" + host + "/" + uri;
        long time = System.currentTimeMillis();
        boolean sendFailed = false;
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtils.sendRequest(sendEntity.getMessage(), url, charSet, sendTimeOut);
            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("synSend status:200, response msg:"
                        + StreamUtils.inputStream2string(response.getEntity().getContent()));
            } else {
                throw new SenderFailException("send fail:" + response.getStatusLine().getStatusCode()
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
