package com.xchushi.fw.log.elasticsearch.changer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.annotation.ConfigSetting;
import com.xchushi.fw.common.environment.Configurable;
import com.xchushi.fw.common.environment.Configure;
import com.xchushi.fw.config.ConfigureFactory;
import com.xchushi.fw.log.SysLoggerFactory;
import com.xchushi.fw.log.constant.EsLoggerConstant;
import com.xchushi.fw.log.constant.LoggerType;

@ConfigSetting(prefix = "eslogger")
public class NomalChanger implements Changer, Configurable {

    private static Logger logger = SysLoggerFactory.getLogger(NomalChanger.class);

    private Map<String, Object> normalParams;

    private Configure config;

    private String appname = "application";

    private String docVersion = "1";

    private String ipAddress = "";
    
    public NomalChanger(){
    }
    
    private NomalChanger(Map<String, Object> normalParams) {
        this(normalParams, ConfigureFactory.getConfigure(NomalChanger.class));
    }

    private NomalChanger(Map<String, Object> normalParams, Configure config) {
        this.normalParams = normalParams;
        this.config = config;
        if (this.config != null) {
            appname = this.config.getProperty("appname", appname);
            docVersion = this.config.getProperty("doc_version", docVersion);
            try {
                ipAddress = this.config.getProperty("ip_address", InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
    public static Changer getChanger(Map<String, Object> normalParams) {
        NomalChanger changer = new NomalChanger(normalParams);
        return changer;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object change(LoggerType loggerType, Thread thread, StackTraceElement st, Map<String, ?> threadParams,
            String message, Throwable t, Object... args) throws Exception {
        Map allParamsMap = new LinkedHashMap<>();
        if (normalParams != null) {
            allParamsMap.putAll(normalParams);
        }
        if (threadParams != null) {
            allParamsMap.putAll(threadParams);
        }
        allParamsMap.put(EsLoggerConstant.APPNAME, appname);
        allParamsMap.put(EsLoggerConstant.DOC_VERSION, docVersion);
        allParamsMap.put(EsLoggerConstant.IP_ADDRESS, ipAddress);
        if (thread != null){
            allParamsMap.put(EsLoggerConstant._THREAD, buildThreadId(thread));
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
                //Object params = allParamsMap.get(EsLoggerConstant.PARAMS);
//                List paramsList = null;
//                if (params == null) {
//                    paramsList = new ArrayList<>();
//                    allParamsMap.put(EsLoggerConstant.PARAMS, paramsList);
//                } else {
//                    paramsList = (List) params;
//                }
                if (isBaseDataType(object.getClass())) {
                    message = format(message, object.toString());
//                    if (String.class.isAssignableFrom(object.getClass())) {
//                        Map objMap = isJsonStr(object.toString());
//                        if (objMap != null) {
//                            paramsList.add(objMap);
//                            continue;
//                        }
//                    }
                    //paramsList.add(new StringType(object));
                } else {
                    message = format(message, JSON.toJSONString(object));
                    //paramsList.add(object);
                }
            }
            allParamsMap.put(EsLoggerConstant._MESSAGE, message);
        }
        if (t != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(out));
            allParamsMap.put(EsLoggerConstant.STACKTRACE, out.toString());
        }
        return allParamsMap;
    }

    private String format(String format, String str) {
        if (format.indexOf(EsLoggerConstant.FORMAT_STR) < 0) {
            return format;
        }
        return format.replaceFirst(EsLoggerConstant.FORMAT_STR_PAT, str);
    }

    @SuppressWarnings({ "finally", "rawtypes" })
    public static Map isJsonStr(String str) {
        if (str == null) {
            return null;
        }
        String trim = str.trim();
        if (trim.indexOf("{") == 0 || trim.indexOf("[") == 0) {
            Map result = null;
            try {
                result = JSON.parseObject(str, Map.class);
            } catch (Exception e) {
                result = null;
            } finally {
                return result;
            }
        } else {
            return null;
        }
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

}
