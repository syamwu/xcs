package syamwu.xchushi.fw.common.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;

public final class JsonUtils {

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }
    
    @SuppressWarnings("rawtypes")
    public static <T> T parseObject(Map map, Class<T> clazz) {
        return JSON.parseObject(toJSONString(map), clazz);
    }

    public static <T> BooleanData<T> isJsonStr(String str, Class<T> targetClass) {
        if (str == null) {
            return new BooleanData<T>(null, false);
        }
        String trim = str.trim();
        T t;
        if (trim.indexOf("{") == 0 || trim.indexOf("[") == 0) {
            try {
                t = JsonUtils.parseObject(str, targetClass);
            } catch (Exception e) {
                return new BooleanData<T>(null, false);
            }
        } else {
            return new BooleanData<T>(null, false);
        }
        return new BooleanData<T>(t, true);
    }
    
}
