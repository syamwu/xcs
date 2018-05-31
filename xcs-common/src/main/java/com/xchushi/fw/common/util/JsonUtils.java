package com.xchushi.fw.common.util;

import com.alibaba.fastjson.JSON;

public class JsonUtils {

    public static String toJSONString(Object obj){
        return JSON.toJSONString(obj);
    }
    
    public static <T> T parseObject(String jsonStr, Class<T> clazz){
        return JSON.parseObject(jsonStr, clazz);
    }
    
}
