package com.xchushi.fw.transfer.sender;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;

import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.arithmetic.loadbalanc.LoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.SimpleLoadBalance;
import com.xchushi.fw.arithmetic.loadbalanc.load.DynamicAble;
import com.xchushi.fw.common.Asset;
import com.xchushi.fw.common.Starting;
import com.xchushi.fw.common.entity.Entity;
import com.xchushi.fw.common.entity.HttpClientResponseEntity;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.common.exception.InitException;
import com.xchushi.fw.common.exception.SenderFailException;
import com.xchushi.fw.common.util.HttpClientUtils;
import com.xchushi.fw.common.util.StreamUtils;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.transfer.CallBackAble;

@ConfigSetting(prefix = "sender")
public class HttpAndHttpsSender extends AbstractSender implements Starting {
    
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
    private String charSet = "UTF-8";

    /**
     * 远程地址uri
     */
    private String uri = "";

    /**
     * 传输协议
     */
    private String protocol = "http";

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

    private void initHttpSender() throws Exception {
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
     * @author SamJoker
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object synSend(Object obj) throws Exception {
        Entity<String> sendEntity = (Entity<String>) obj;
        Asset.notNull(sendEntity, "sendEntity can't be null");
        int hostIndex = loadBalanceEnable ? (loadBanlanc == null ? 0 : loadBanlanc.loadBalanceIndex()) : 0;
        String host = serverHostsArray[hostIndex];
        String url = protocol + "://" + host + "/" + uri;
        long time = System.currentTimeMillis();
        boolean sendFailed = false;
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtils.sendRequest(sendEntity.getValue(), url, charSet, sendTimeOut, true);
            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 202) {
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
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
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

}
