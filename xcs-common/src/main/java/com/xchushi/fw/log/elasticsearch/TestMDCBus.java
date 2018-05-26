package com.xchushi.fw.log.elasticsearch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.MDC;

/**
 * 用于传输同个线程下的日志打印公共参数
 * 
 * @author syam_wu
 * @date 2018-1-26
 */
public class TestMDCBus {

    private static final Map<String, Object> threadMap = new ConcurrentHashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void putSP(String sessionId, String processMethod) {
        String threadId = buildThreadId();
        Map params = (Map) threadMap.get(threadId);
        if (params == null) {
            params = new ConcurrentHashMap<>();
        }
        params.put("session_id", sessionId);
        params.put("process_method", processMethod);
        threadMap.put(threadId, params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void put(String key, Object val) {
        String threadId = buildThreadId();
        Map params = (Map) threadMap.get(threadId);
        if (params == null) {
            params = new ConcurrentHashMap<>();
        }
        params.put(key, val);
        threadMap.put(threadId, params);
    }

    @SuppressWarnings({ "rawtypes" })
    public static Object get(String key) {
        String threadId = buildThreadId();
        Map params = (Map) threadMap.get(threadId);
        if (params == null) {
            return null;
        }
        return params.get(key);
    }
    
    @SuppressWarnings({ "rawtypes" })
    public static Object get(Thread thread, String key) {
        String threadId = buildThreadId(thread);
        Map params = (Map) threadMap.get(threadId);
        if (params == null) {
            return null;
        }
        return params.get(key);
    }

    @SuppressWarnings({ "rawtypes" })
    public static Map getMap() {
        String threadId = buildThreadId();
        return (Map) threadMap.get(threadId);
    }

    @SuppressWarnings("rawtypes")
    public static void remove(Thread thread) {
        String threadId = buildThreadId(thread);
        Map params = (Map) threadMap.get(threadId);
        if (params == null) {
        } else {
            threadMap.put(threadId, new ConcurrentHashMap<>());
            params.clear();
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static void remove() {
        String threadId = buildThreadId();
        Map params = (Map) threadMap.get(threadId);
        if (params == null) {
        } else {
            threadMap.put(threadId, new ConcurrentHashMap<>());
            params.clear();
        }
    }

    private static String buildThreadId() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        String threadGroupName = "";
        if (threadGroup != null) {
            threadGroupName = threadGroup.getName();
        }
        return threadGroupName + "-" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId();
    }
    
    private static String buildThreadId(Thread thread) {
        ThreadGroup threadGroup = thread.getThreadGroup();
        String threadGroupName = "";
        if (threadGroup != null) {
            threadGroupName = threadGroup.getName();
        }
        return threadGroupName + "-" + thread.getName() + "-" + thread.getId();
    }

    public static void current(boolean begin) {
        if (begin) {
            MDC.put("time", System.currentTimeMillis() + "");
            MDC.put("index", 1 + "");
            return;
        }
        long oldIndex = Long.valueOf(MDC.get("index"));
        long oldTime = Long.valueOf(MDC.get("time"));
        long nowtime = System.currentTimeMillis();
        long nowIndex = oldIndex + 1;
        MDC.put("time", nowtime + "");
        MDC.put("index", nowIndex + "");
        System.out.println(oldIndex + "---------------" + (nowtime - oldTime));
    }
    
    public static void current() {
        current(false);
    }
    
}
