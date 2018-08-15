package syamwu.xchushi.easylog.elasticsearch.changer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import syamwu.xchushi.easylog.constant.EsLoggerConstant;
import syamwu.xchushi.easylog.constant.LoggerType;
import syamwu.xchushi.easylog.elasticsearch.exception.EsLoggerInitException;
import syamwu.xchushi.easylog.proxy.EasyLogProxy;
import syamwu.xchushi.fw.common.annotation.ConfigSetting;
import syamwu.xchushi.fw.common.constant.StringConstant;
import syamwu.xchushi.fw.common.environment.Configurable;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.StringUtil;
import syamwu.xchushi.fw.factory.FactoryProxy;
import syamwu.xchushi.fw.log.SysLoggerFactory;

@ConfigSetting(prefix = "eslogger")
public class ElasticsearchLogChanger implements Changer, Configurable {

    private static Logger logger = SysLoggerFactory.getLogger(ElasticsearchLogChanger.class);

    private Map<String, Object> normalParams;

    private Configure config;

    private String appname = "application";

    private String docVersion = "1";

    private String ipAddress = "";
    
    private String index = "application-log";
    
    private String type = "log";
    
    private boolean dateIndex = true;
    
    private Map<String,String> indexCacheMap = new ConcurrentHashMap<>();

    public ElasticsearchLogChanger() {
        this(null);
    }

    public ElasticsearchLogChanger(Map<String, Object> normalParams) {
        this(normalParams, FactoryProxy.getFactory(Configure.class, EasyLogProxy.getInstance(), true)
                .getInstance(ElasticsearchLogChanger.class));
    }

    private ElasticsearchLogChanger(Map<String, Object> normalParams, Configure config) {
        this.normalParams = normalParams;
        this.config = config;
        if (this.config != null) {
            appname = this.config.getProperty("appname", appname);
            docVersion = this.config.getProperty("docVersion", docVersion);
            index = this.config.getProperty("index", index);
            type = this.config.getProperty("type", type);
            dateIndex = this.config.getProperty("dateIndex", Boolean.class, dateIndex);
            try {
                ipAddress = this.config.getProperty("ipAddress", InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static Changer getChanger(Map<String, Object> normalParams) {
        ElasticsearchLogChanger changer = new ElasticsearchLogChanger(normalParams);
        return changer;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Map<String, ?> change(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, ?> threadParams, String message, Throwable t, Object... args) throws Exception {
        Map allParamsMap = new LinkedHashMap<>();
        if (normalParams != null) {
            allParamsMap.putAll(normalParams);
        }
        allParamsMap.put(EsLoggerConstant.APPNAME, appname);
        allParamsMap.put(EsLoggerConstant.DOC_VERSION, docVersion);
        allParamsMap.put(EsLoggerConstant.IP_ADDRESS, ipAddress);
        if (thread != null) {
            allParamsMap.put(EsLoggerConstant._THREAD, buildThreadId(thread));
        }
        if (threadParams != null) {
            allParamsMap.putAll(threadParams);
        }
        if (st != null) {
            allParamsMap.put(EsLoggerConstant._CLASS, st.getClassName());
            allParamsMap.put(EsLoggerConstant._METHOD, st.getMethodName());
            allParamsMap.put(EsLoggerConstant._LINE, st.getLineNumber());
        }
        if (loggerType != null) {
            allParamsMap.put(EsLoggerConstant.LEVEl, loggerType.getName());
            allParamsMap.put(EsLoggerConstant.LEVEl_VAL, loggerType.getVal());
        }
        if (args == null) {
            allParamsMap.put(EsLoggerConstant._MESSAGE, message);
        } else {
            for (Object object : args) {
                if (object == null) {
                    message = message.replaceFirst(EsLoggerConstant.FORMAT_STR_PAT, EsLoggerConstant._NULL);
                    continue;
                }
                if (isBaseDataType(object.getClass())) {
                    message = format(message, object.toString());
                } else {
                    message = format(message, JsonUtils.toJSONString(object));
                }
            }
            allParamsMap.put(EsLoggerConstant._MESSAGE, message);
        }
        if (t != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                t.printStackTrace(new PrintStream(out));
                if (message == null || message.trim().length() < 1) {
                    allParamsMap.put(EsLoggerConstant._MESSAGE, t.getMessage());
                }
                allParamsMap.put(EsLoggerConstant.STACKTRACE, out.toString());
            } finally {
                out.close();
            }
        }
        return allParamsMap;
    }

    private String format(String format, String str) {
        if (format.indexOf(EsLoggerConstant.FORMAT_STR) < 0) {
            return format;
        }
        return format.replaceFirst(EsLoggerConstant.FORMAT_STR_PAT, str);
    }

    private static boolean isBaseDataType(Class<?> clazz) {
        return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
                || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class)
                || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.equals(Date.class) || clazz.isPrimitive());
    }

    @SuppressWarnings("unused")
    private String buildThreadId() {
        return buildThreadId(Thread.currentThread());
    }

    private String buildThreadId(Thread thread) {
        ThreadGroup threadGroup = thread.getThreadGroup();
        String threadGroupName = "";
        if (threadGroup != null) {
            threadGroupName = threadGroup.getName();
        }
        return threadGroupName + "-" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId();
    }

    static class StringType {
        private Object val;

        public StringType(Object object) {
            this.val = object;
        }

        public Object getVal() {
            return val;
        }

        public void setVal(Object val) {
            this.val = val;
        }

    }

    public Map<String, Object> getNormalParams() {
        return normalParams;
    }

    public void setNormalParams(Map<String, Object> normalParams) {
        this.normalParams = normalParams;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void setConfigure(Configure configure) {
        config = configure;
    }

    @Override
    public String logString(Map<String, ?> logMap) throws Exception {
        String index = this.index;
        if (dateIndex) {
            index = index + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        return getBulkIndexHead(index, type) + StringConstant.NEW_LINE + JsonUtils.toJSONString(logMap);
    }
    
    private String getBulkIndexHead(String index, String type) {
        if (StringUtil.isBank(index) || StringUtil.isBank(type)) {
            throw new EsLoggerInitException("index or type can not be null");
        }
        String key = index + "_" + type;
        if (indexCacheMap.containsKey(key)) {
            return indexCacheMap.get(key);
        }
        indexCacheMap.clear();
        String head = "{ \"index\": { \"_index\": \"" + index + "\", \"_type\": \"" + type + "\" }}";
        indexCacheMap.put(key, head);
        return head;
    }

}
