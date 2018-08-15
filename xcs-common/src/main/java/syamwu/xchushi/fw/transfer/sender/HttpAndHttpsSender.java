package syamwu.xchushi.fw.transfer.sender;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

import syamwu.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import syamwu.xchushi.fw.arithmetic.loadbalanc.SimpleDynamicLoadBalance;
import syamwu.xchushi.fw.arithmetic.loadbalanc.load.DynamicAble;
import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.LifeCycle;
import syamwu.xchushi.fw.common.annotation.ConfigSetting;
import syamwu.xchushi.fw.common.constant.HttpContentType;
import syamwu.xchushi.fw.common.entity.Entity;
import syamwu.xchushi.fw.common.entity.TransferResponseEntity;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.exception.InitException;
import syamwu.xchushi.fw.common.exception.SenderFailException;
import syamwu.xchushi.fw.common.util.HttpUtils;
import syamwu.xchushi.fw.factory.FactoryProxy;
import syamwu.xchushi.fw.log.SysLoggerFactory;
import syamwu.xchushi.fw.transfer.CallBackAble;
import syamwu.xchushi.fw.transfer.response.OkHttpTransferResponse;
import syamwu.xchushi.fw.transfer.response.TransferResponse;

@ConfigSetting(prefix = "sender")
public class HttpAndHttpsSender extends AbstractSender implements LifeCycle {
    
    private static Logger logger = SysLoggerFactory.getLogger(HttpAndHttpsSender.class);

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
    private String charset = "UTF-8";

    /**
     * 远程地址uri
     */
    private String uri = "";

    /**
     * 传输协议
     */
    private String protocol = "http";
    
    /**
     * uzip压缩开启
     */
    private boolean gzipEnable = false;
    
    /**
     * 是否输出响应日志
     */
    private boolean showlog = false;
    
    /**
     * 失败请求缓冲阀值
     */
    private int failSendCount = 10;
    
    /**
     * 失败请求缓冲时间，避免在服务器宕了频繁请求
     */
    private long failSendTime = 100l;
    
    private boolean failSendEnable = false;
    
    private AtomicInteger failCount = new AtomicInteger();

    private static HttpAndHttpsSender sender;

    public HttpAndHttpsSender() {
        super(null);
        if (sender == null) {
            sender = this;
        }
    }

    protected HttpAndHttpsSender(Configure configure) {
        super(configure);
    }
    
    public synchronized static Sender getSender(Class<?> cls) {
        try {
            if (sender == null) {
                sender = new HttpAndHttpsSender(FactoryProxy.getFactory(Configure.class).getInstance(HttpAndHttpsSender.class));
                try {
                    sender.start();
                } catch (Exception e) {
                    logger.error("initHttpSender fail:" + e.getMessage(), e);
                }
                return sender;
            }
        } catch (Exception e) {
            logger.error("HttpAndHttpsSender initFail:" + e.getMessage(), e);
        }
        return sender;
    }

    private void initHttpSender() throws Exception {
        protocol = getProperty("protocol", protocol);
        charset = getProperty("charset", charset);
        gzipEnable = getProperty("gzipEnable", Boolean.class, false);
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
        showlog = getProperty("showlog", Boolean.class, showlog);
        failSendCount = getProperty("failSendCount", Integer.class, failSendCount);
        failSendTime = getProperty("failSendTime", Long.class, failSendTime);
    }
    
    @Override
    public void start() {
        try {
            initHttpSender();
        } catch (Exception e) {
            throw new InitException(e);
        }
    }

    @Override
    public void stop() {
    }
    
    @Override
    public void send(Object message, CallBackAble callBackAble) throws Exception {
        Asset.notNull(callBackAble);
        try {
            Object result = synSend(message);
            callBackAble.callBack(result);
        } catch (Exception e) {
            callBackAble.sendingFailed(e.getMessage(), e);
        }
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
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object synSend(Object obj) throws Exception {
        Asset.notNull(obj);
        Asset.isAssignableFrom(Entity.class, obj.getClass());
        Entity requestEntity = (Entity) obj;
        Asset.notNull(requestEntity, "sendEntity can't be null");
        Object requestBody = requestEntity.getData();
        Asset.notNull(requestBody, "requestBody can't be null");
        int hostIndex = loadBalanceEnable ? (loadBanlanc == null ? 0 : loadBanlanc.loadBalanceIndex()) : 0;
        String sendUrl = spliUrl(protocol, serverHostsArray[hostIndex], uri);
        long time = System.currentTimeMillis();
        boolean sendFailed = false;
        TransferResponse response = null;
        try {
            // 为了不频繁请求失败，但请求出现超过failCount次数的失败，这里启用失败等待时间。
            if (failSendEnable && failCount.get() >= failSendCount) {
                Thread.sleep(failSendTime);
            }
            String requestContent = getRequestBody(requestBody, String.class);
            response = sendRequest(requestContent, sendUrl, charset, sendTimeOut,
                    gzipEnable);
            if ((response.getResultCode() == 200 || response.getResultCode() == 202) ) {
                failSendEnable = false;
                failCount.set(0);
                if (showlog) {
                    logger.debug("synSend status:200, response msg:" + response.getResponseBody());
                }
            } else {
                throw new SenderFailException(sendUrl + " send fail:" + response.getResultCode()
                        + ", response msg:" + response.getResponseBody());
            }
        } catch (Exception e) {
            failCount.incrementAndGet();
            sendFailed = true;
            failSendEnable = true;
            throw e;
        } finally {
            // 动态负载,权值变动
            if (loadBalanceEnable) {
                dynamicAble.dynamicLoad(hostIndex, sendFailed ? sendTimeOut : (System.currentTimeMillis() - time));
            }
        }
        return new TransferResponseEntity(response, requestEntity.getEntityType());
    }
    
    private String spliUrl(String protocol, String host, String uri){
        return protocol + "://" + host + "/" + uri;
    }
    
//    private HttpClientTransferResponse sendRequest(String content, String url, String charset, int timeOut, boolean gzip) throws IOException{
//        return new HttpClientTransferResponse(HttpClientUtils.sendRequest(content, url, charset, timeOut, gzip));
//    }
    
    private OkHttpTransferResponse sendRequest(String content, String url, String charset, int timeOut, boolean gzip) throws IOException{
        return new OkHttpTransferResponse(HttpUtils.postResponse(url, content, HttpContentType.APPLICATIONJSON, charset, timeOut, gzip));
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getRequestBody(Object obj, Class<T> cls) {
        Asset.notNull(obj);
        if (cls.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }
        return (T) obj;
    }

    private String getProperty(String key, String defaultValue) {
        if (configure == null) {
            return defaultValue;
        }
        return getProperty(key, String.class, defaultValue);
    }

    private <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        if (configure == null) {
            return defaultValue;
        }
        return configure.getProperty(key, targetType, defaultValue);
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
        return charset;
    }

    public void setCharSet(String charSet) {
        this.charset = charSet;
    }

}
