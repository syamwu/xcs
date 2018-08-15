package syamwu.xchushi.fw.common.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {

    /**
     * Map的转换
     *
     * @param maps
     * @return
     * @throws Exception
     */
    public static Map<String, String> convertMaplistToMap(Map<String, String[]> maps) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (!maps.isEmpty()) {
            for (Entry<String, String[]> p : maps.entrySet()) {
                String key = p.getKey().trim();
                String[] vals = p.getValue();
                if (vals.length == 0) {
                    throw new RuntimeException("参数值不能为空");
                }
                if (vals.length > 1) {
                    throw new RuntimeException("重复参数错误");
                }
                params.put(key, vals[0].trim());
            }
        }
        return params;
    }

    /**
     * Map的Obj转换
     *
     * @param maps
     * @return
     * @throws Exception
     */
    public static <T> T convertMaplistToObj(Map<String, String[]> maps, Class<T> cls) throws Exception {
        Map<String, String> params = convertMaplistToMap(maps);
        return JsonUtils.parseObject(JsonUtils.toJSONString(params), cls);
    }

    /**
     * Map的String转换
     *
     * @param maps
     * @return
     * @throws Exception
     */
    public static String convertMaplistToString(Map<String, String[]> maps) throws Exception {
        Map<String, String> params = convertMaplistToMap(maps);
        return JsonUtils.toJSONString(params);
    }
    
    public static List<Object> getFieldValueByStoreyKey(Object obj, String[] storeyKeys) throws Exception {
        List<Object> list = new ArrayList<>();
        for (String storyKey : storeyKeys) {
            List<Object> row = getFieldValueByStoreyKey(obj, storyKey);
            if (row != null && !row.isEmpty()) {
                list.addAll(row);
            }
        }
        return list;
    }

    /**
     * 获取带层级关系的键值对(递归操作)
     * eg: person.man.body.leg
     * @param obj
     * @param storeyKey
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<Object> getFieldValueByStoreyKey(Object obj, String storeyKey) throws Exception {
        if (obj == null || StringUtil.isBank(storeyKey)) {
            List<Object> results = new ArrayList<>();
            results.add(obj);
            return results;
        }
        List<Object> results = new ArrayList<>();
        String nowKey = storeyKey.indexOf(".") < 0 ? storeyKey : storeyKey.substring(0, storeyKey.indexOf("."));
        String nextKeys = storeyKey.indexOf(".") < 0 ? null : storeyKey.substring(storeyKey.indexOf(".") + 1);
        if (isBaseDataType(obj.getClass())) {
            return null;
        } else if (obj instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) obj;
            for (Entry<Object, Object> entry : map.entrySet()) {
                Object itemKey = entry.getKey();
                Object value = entry.getValue();
                if (nowKey.equals(itemKey.toString())) {
                    List<Object> itemResult = getFieldValueByStoreyKey(value, nextKeys);
                    if (itemResult != null) {
                        results.addAll(itemResult);
                    }
                }
            }
        } else if (obj instanceof Collection) {
            for (Iterator item = ((Collection) obj).iterator(); item.hasNext();) {
                List<Object> itemResult = getFieldValueByStoreyKey(item.next(), storeyKey);
                if (itemResult != null) {
                    results.addAll(itemResult);
                }
            }
        } else {
            List<Field> fields = getAllField(obj.getClass());
            for (Field field : fields) {
                if (nowKey.equals(field.getName())) {
                    field.setAccessible(true);
                    List<Object> itemResult = getFieldValueByStoreyKey(field.get(obj), nextKeys);
                    if (itemResult != null) {
                        results.addAll(itemResult);
                    }
                    break;
                }
            }
        }
        return results;
    }

    private static List<Field> getAllField(Class<?> cls) {
        List<Field> fieldList = new ArrayList<Field>();
        while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object")) {// 当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return fieldList;
    }

    @SuppressWarnings("rawtypes")
    private static boolean isBaseDataType(Class clazz) throws Exception {
        return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
                || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class)
                || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.equals(Date.class) || clazz.isPrimitive());
    }

}
