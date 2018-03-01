package xcs.log.elasticsearch.changer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import xcs.log.constant.EsLoggerConstant;
import xcs.log.constant.LEVEL;
import xcs.log.constant.LoggerType;
import xcs.log.elasticsearch.MDCBus;
import xcs.log.elasticsearch.config.EsLoggerConfig;

public class NomalChanger implements Changer {

    private Map<String, Object> normalParams;

    NomalChanger(Map<String, Object> normalParams) {
        this.normalParams = normalParams;
    }

    public static Changer getChanger(Map<String, Object> normalParams) {
        return new NomalChanger(normalParams);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object changeInfo(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, Object> threadParams, String format, Object... args) {
        Map allParamsMap = new LinkedHashMap<>();
        if (normalParams != null) {
            allParamsMap.putAll(normalParams);
        }
        if (threadParams != null) {
            threadParams.putAll(threadParams);
        }
        return change(loggerType, thread, st, threadParams, format, LEVEL.INFO, args);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object changeError(LoggerType loggerType, Thread thread, StackTraceElement st,
            Map<String, Object> threadParams, String message, Throwable t) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(out));
        Map allParamsMap = new LinkedHashMap<>();
        if (normalParams != null) {
            allParamsMap.putAll(normalParams);
        }
        if (threadParams != null) {
            threadParams.putAll(threadParams);
        }
        allParamsMap = (Map) change(loggerType, thread, st, threadParams, message, LEVEL.ERROR);
        allParamsMap.put(EsLoggerConstant.STACKTRACE, out.toString());
        return allParamsMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object change(LoggerType loggerType, Thread thread, StackTraceElement st, Map<String, Object> threadParams,
            String format, LEVEL level, Object... args) {
        if (threadParams == null) {
            threadParams = new LinkedHashMap<>();
        }
        threadParams.put(EsLoggerConstant.APPNAME, EsLoggerConfig.getCfg().getAppname());
        threadParams.put(EsLoggerConstant.DOC_VERSION, EsLoggerConfig.getCfg().getDocVersion());
        threadParams.put(EsLoggerConstant.IP_ADDRESS, EsLoggerConfig.getCfg().getIpAddress());
        String sessionId = (String) MDCBus.get(thread, EsLoggerConstant.SESSION_ID);
        String processMethod = (String) MDCBus.get(thread, EsLoggerConstant.PROCESS_METHOD);
        if (sessionId != null)
            threadParams.put(EsLoggerConstant.SESSION_ID, sessionId);
        if (processMethod != null)
            threadParams.put(EsLoggerConstant.PROCESS_METHOD, processMethod);
        threadParams.put(EsLoggerConstant._THREAD, buildThreadId(thread));
        threadParams.put(EsLoggerConstant._CLASS, st.getClassName());
        threadParams.put(EsLoggerConstant._METHOD, st.getMethodName());
        threadParams.put(EsLoggerConstant._LINE, st.getLineNumber());
        threadParams.put(EsLoggerConstant.LEVEl, level.getName());
        threadParams.put(EsLoggerConstant.LEVEl_VAL, level.getVal());
        if (args == null) {
            threadParams.put(EsLoggerConstant._MESSAGE, format);
        } else {
            for (Object object : args) {
                if (object == null) {
                    format = format.replaceFirst(EsLoggerConstant.FORMAT_STR, EsLoggerConstant._NULL);
                    continue;
                }
                Object params = threadParams.get(EsLoggerConstant.PARAMS);
                List paramsList = null;
                if (params == null) {
                    paramsList = new ArrayList<>();
                    threadParams.put(EsLoggerConstant.PARAMS, paramsList);
                } else {
                    paramsList = (List) params;
                }
                if (isBaseDataType(object.getClass())) {
                    format = format(format, object.toString());
                    if (String.class.isAssignableFrom(object.getClass())) {
                        Map objMap = isJsonStr(object.toString());
                        if (objMap != null) {
                            paramsList.add(objMap);
                            continue;
                        }
                    }
                    paramsList.add(new StringType(object));
                } else {
                    format = format(format, JSON.toJSONString(object));
                    paramsList.add(object);
                }
            }
            threadParams.put(EsLoggerConstant._MESSAGE, format);
        }
        return threadParams;
    }

    public static String format(String format, String str) {
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

}
