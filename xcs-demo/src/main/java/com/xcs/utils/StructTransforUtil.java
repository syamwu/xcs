package com.xcs.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

public class StructTransforUtil {
    
    private static final String KEY_KEY = "$@$";
    
    private static final String STRUCT_KEY_KEY = ":_:";
    
    private static Map<String, Map<String, FieldCacheItem>> classFieldCacheItemCache = new HashMap<String, Map<String, FieldCacheItem>>();

    private static Map<String, List<Field>> classFieldCache = new HashMap<String, List<Field>>();

    private static Map<String, List<FieldCacheItem>> classTransforKeyCache = new HashMap<String, List<FieldCacheItem>>();

    static Logger logger = LoggerFactory.getLogger(StructTransforUtil.class);
    
    public static void main(String[] args) {
        try {
            //initClassField(Bizbizh200102Request.class);
            //System.out.println(JsonUtils.obj2JsonString(classFieldCacheItemCache));
            //System.out.println(JsonUtils.obj2JsonString(classTransforKeyCache));
            System.out.println("-----");
            String json = StreamUtils.file2string("D:\\upload\\data3.txt");
            System.out.println(json);
           // Map map = JsonUtils.jsonString2Map(json);
            //System.out.println(JsonUtils.obj2JsonString(map));
            //Object obj = transfor((List) (map.get("resultlist")), Bizbizh410003Request.class);
            //System.out.println(JsonUtils.obj2JsonString(obj));
            //System.out.println(XmlUtils.buildXMLWithTextElementString(obj, "ooooo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void initClassField(Class<?>... clss) throws Exception {
        List<Class<?>> list = new ArrayList<>();
        for (Class<?> cls : clss) {
            list.add(cls);
        }
        initClassField(list);
    }

    /**
     * 根据class类初始化结构模型
     * 
     * @param clss
     * @throws Exception
     * @author SamJoker
     */
    public static void initClassField(List<Class<?>> clss) throws Exception {
        for (Class<?> cls : clss) {
            String clsName = cls.getName();
            try {
                if (clsName.contains("$")) {
                    continue;
                }
                List<Field> fields = getAllField(cls);
                Map<String, FieldCacheItem> classFMap = new HashMap<String, FieldCacheItem>();
                for (Field field : fields) {
                    initClassField(classFMap, field, getPatrentStructKey("") + STRUCT_KEY_KEY + getFieldCacheItemKey(field));
                }
                classFieldCacheItemCache.put(clsName, classFMap);
            } catch (Exception e) {
                logger.error(clsName + "初始化失败:" + e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void initClassField(Map<String, FieldCacheItem> map, Field field, String structKey) throws Exception {
        // 是否是基础类型
        if (isBaseDataType(field.getType())) {
            FieldCacheItem fieldCacheItem = bulidFieldCacheItem(field, structKey);
            String fieldCacheItemKey = getFieldCacheItemKey(field);
            if(map.containsKey(fieldCacheItemKey)){
                throw new RuntimeException(field.getDeclaringClass().getName() + "." + fieldCacheItemKey
                        + "属性名重复，请更改属性名或使用@OtherKey或@OtherKeyPrefix注解添加别名");
            }
            map.put(fieldCacheItemKey, fieldCacheItem);
        } else {
            Class cls = null;
            if (List.class.isAssignableFrom(field.getType())) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                } else {
                    throw new RuntimeException(field.getDeclaringClass().getName() + "." + field.getName() + "属性未指定泛型");
                }
                ParameterizedType pt = (ParameterizedType) type;
                Type[] types = pt.getActualTypeArguments();
                cls = (Class) types[0];
            } else {
                cls = field.getType();
            }
            FieldCacheItem fieldCacheItem = bulidFieldCacheItem(field, structKey);
            fieldCacheItem.setSuperClass(true);
            map.put(structKey, fieldCacheItem);
            List<Field> fields = getAllField(cls);
            for (Field fld : fields) {
                initClassField(map, fld, structKey + STRUCT_KEY_KEY + getFieldCacheItemKey(fld));
            }
        }
    }

    private static FieldCacheItem bulidFieldCacheItem(Field field, String structKey) {
        FieldCacheItem fieldCacheItem = new FieldCacheItem();
        Annotation[] ans = field.getAnnotations();
        for (Annotation annotation : ans) {
            if (annotation.annotationType() == TransforKey.class) {
                TransforKey transforKey = (TransforKey) annotation;
                fieldCacheItem.setTransforKeyAble(true);
                fieldCacheItem.setTransforKeyDefalut(transforKey.defaultKey());
                List<FieldCacheItem> fieldCacheItems = classTransforKeyCache.get(field.getDeclaringClass().getName());
                if (fieldCacheItems == null) {
                    fieldCacheItems = new ArrayList<FieldCacheItem>();
                    classTransforKeyCache.put(field.getDeclaringClass().getName(), fieldCacheItems);
                }
                fieldCacheItems.add(fieldCacheItem);
            }
        }
        fieldCacheItem.setField(field);
        fieldCacheItem.setStructKey(structKey);
        fieldCacheItem.setParentStructKey(getPatrentStructKey(structKey));
        return fieldCacheItem;
    }

    /**
     * 根据cls的模板，对list数据进行结构化
     * 
     * @param list
     * @param cls
     * @return
     * @throws Exception
     * @author SamJoker
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T transfor(List<Map> list, Class<T> cls) throws Exception {
        Map<String, FieldCacheItem> itemMap = classFieldCacheItemCache.get(cls.getName());
        if (itemMap == null) {
            // throw new RuntimeException(cls.getName() +
            // "未进行initClassField初始化!!");
            initClassField(cls);
            itemMap = classFieldCacheItemCache.get(cls.getName());
            if (itemMap == null) {
                throw new RuntimeException(cls.getName() + "初始化失败!!");
            }
        }
        T resultObj = cls.newInstance();
        Map<String, Object> checkMap = new HashMap<String, Object>();
        for (Map<String, Object> map : list) {
            Map<String, Object> travalMap = new HashMap<String, Object>();
            for (Map.Entry<String, FieldCacheItem> entry : itemMap.entrySet()) {
                String key = entry.getKey();
                FieldCacheItem fieldCacheItem = entry.getValue();
                Field field = fieldCacheItem.getField();
                Object objValue = null;
                if (fieldCacheItem.isSuperClass()) {
                    if (List.class.isAssignableFrom(field.getType())) {
                    } else {
                        Object paObj = travalMap.get(fieldCacheItem.getStructKey());
                        if (paObj == null) {
                            paObj = field.getType().newInstance();
                            travalMap.put(fieldCacheItem.getStructKey(), paObj);
                        }
                    }
                    continue;
                }
                if (!isBaseDataType(field.getType())) {
                    if (List.class.isAssignableFrom(field.getType())) {
                        objValue = new ArrayList<>();
                    } else {
                        Object nowObj = travalMap.get(field.getType().getName());
                        if (nowObj != null) {
                            continue;
                        }
                        objValue = field.getType().newInstance();
                        travalMap.put(fieldCacheItem.getStructKey(), objValue);
                    }
                } else {
                    objValue = map.get(key);
                    if (objValue == null) {
                        objValue = "";
                    } else {
                        objValue = objValue.toString();
                    }
                }
                field.setAccessible(true);
                Object paObj = travalMap.get(fieldCacheItem.getParentStructKey());
                if (paObj == null) {
                    paObj = field.getDeclaringClass().newInstance();
                    field.set(paObj, objValue);
                    travalMap.put(fieldCacheItem.getParentStructKey(), paObj);
                } else {
                    field.set(paObj, objValue);
                }
            }
            Map<String, String> takeTravalMap = new HashMap<String, String>();
            Map<String, Object> newtravalMap = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : travalMap.entrySet()) {
                String newKey = getParentKey(entry.getKey(), travalMap);
                newtravalMap.put(newKey, entry.getValue());
                takeTravalMap.put(entry.getKey(), newKey);
            }
//            System.out.println("===============");
//            System.out.println("travalMap:" + JsonUtils.obj2JsonString(travalMap));
//            System.out.println("takeTravalMap:" + JsonUtils.obj2JsonString(takeTravalMap));
//            System.out.println("newtravalMap:" + JsonUtils.obj2JsonString(newtravalMap));
//            System.out.println("travalMap:" + JsonUtils.obj2JsonString(travalMap));
//            System.out.println("===============");
//            System.out.println(resultObj);
            setFieldValue(getPatrentStructKey(""), resultObj, newtravalMap, takeTravalMap, checkMap, resultObj);
//            System.out.println();
            for (Map.Entry<String, Object> entry : travalMap.entrySet()) {
                String key = takeTravalMap.get(entry.getKey());
                Object val = entry.getValue();
                if (checkMap.containsKey(key)) {
                    continue;
                }
                checkMap.put(key, val);
            }
        }
        // System.out.println(JsonUtils.obj2JsonString(checkMap));
        // System.out.println(JsonUtils.obj2JsonString(forMap));
//        System.out.println(JsonUtils.obj2JsonString(itemMap));
//        System.out.println(JsonUtils.obj2JsonString(resultObj));
        return resultObj;
    }
    
    
    private static String getFieldCacheItemKey(Field field) {
        Class<?> cls = field.getDeclaringClass();
        Annotation[] clsans = cls.getAnnotations();
        for (Annotation annotation : clsans) {
            if (annotation.annotationType() == OtherKeyPrefix.class) {
                OtherKeyPrefix otherKey = (OtherKeyPrefix) annotation;
                return otherKey.otherKeyPrefix() + getFieldKey(field);
            }
        }
        return getFieldKey(field);
    }
    
    private static String getFieldKey(Field field){
        Annotation[] ans = field.getAnnotations();
        for (Annotation annotation : ans) {
            if (annotation.annotationType() == OtherKey.class) {
                OtherKey otherKey = (OtherKey) annotation;
                return otherKey.otherKey();
            }
        }
        return field.getName();
    }

    public static String getParentKey(String pkey, Map<String, Object> travalMap) throws Exception {
        String newPKey = pkey;
        if (StringUtils.isBlank(pkey)) {
            return "";
        }
        if (travalMap.containsKey(newPKey)) {
            Object val = travalMap.get(newPKey);
            List<FieldCacheItem> transforKeyList = classTransforKeyCache.get(val.getClass().getName());
            if (transforKeyList == null || transforKeyList.isEmpty()) {
            } else {
                for (FieldCacheItem fieldCacheItem : transforKeyList) {
                    Object fval = fieldCacheItem.getField().get(val);
                    if (fval == null) {
                        fval = fieldCacheItem.getTransforKeyDefalut();
                    }
                    newPKey = newPKey + KEY_KEY + fval;
                }
            }
        }
        String lastKey = getParentKey(newPKey);
        if (StringUtils.isBlank(lastKey)) {
        } else {
            newPKey = getParentKey(lastKey, travalMap) + STRUCT_KEY_KEY + getSelfKey(newPKey);
        }
        return newPKey;
    }

    /**
     * 根据3个map将数据放进obj，重复数据则不进行更新
     * 
     * @param rootstructKey
     * @param obj
     * @param newtravalMap
     * @param takeTravalMap
     * @param checkMap
     * @param rootObj
     * @throws Exception
     * @author SamJoker
     */
    @SuppressWarnings("rawtypes")
    private static void setFieldValue(String rootstructKey, Object obj, Map newtravalMap, Map takeTravalMap,
            Map checkMap, Object rootObj) throws Exception {
        if (List.class.isAssignableFrom(obj.getClass())) {
            return;
        }
        List<Field> fields = getAllField(obj.getClass());
        for (Field field : fields) {
            setFieldValue(rootstructKey + STRUCT_KEY_KEY + getFieldCacheItemKey(field), field, obj, newtravalMap, takeTravalMap, checkMap,
                    rootObj);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void setFieldValue(String structKey, Field field, Object obj, Map newtravalMap, Map takeTravalMap,
            Map checkMap, Object rootObj) throws Exception {
        field.setAccessible(true);
        if (isBaseDataType(field.getType())) {
            String keyStr = (String) takeTravalMap.get(getPatrentStructKey(structKey));
            if (StringUtils.isBlank(keyStr)) {
                throw new RuntimeException("查找不到takeTravalMap." + structKey);
            }

            Object keyVal = (Object) newtravalMap.get(keyStr);
            if (keyVal == null) {
                throw new RuntimeException("查找不到newtravalMap." + keyStr);
            }

            Object checkVal = (Object) checkMap.get(keyStr);
            if (checkVal == null) {
                field.set(obj, field.get(keyVal));
            }
        } else if (List.class.isAssignableFrom(field.getType())) {
            String keyStr = (String) takeTravalMap.get(structKey);
            if (StringUtils.isBlank(keyStr)) {
                throw new RuntimeException("查找不到takeTravalMap." + structKey);
            }

            Object keyVal = (Object) newtravalMap.get(keyStr);
            if (keyVal == null) {
                throw new RuntimeException("查找不到newtravalMap." + keyStr);
            }

            Object checkVal = (Object) checkMap.get(keyStr);
            if (checkVal == null) {
                List list = (List) field.get(obj);
                if (list == null || list.isEmpty()) {
                    list = new ArrayList();
                    field.set(obj, list);
                }
                list.add(keyVal);
                setFieldValue(structKey, keyVal, newtravalMap, takeTravalMap, checkMap, rootObj);
            } else {
                List list = (List) field.get(obj);
                Object nkeyVal = getKeyVal(list, checkVal);
                if (nkeyVal == null) {
                    list.add(keyVal);
                    setFieldValue(structKey, keyVal, newtravalMap, takeTravalMap, checkMap, rootObj);
                } else {
                    setFieldValue(structKey, nkeyVal, newtravalMap, takeTravalMap, checkMap, rootObj);
                }
            }
        } else {
            String keyStr = (String) takeTravalMap.get(structKey);
            Object keyVal = null;
            if (StringUtils.isBlank(keyStr)) {
                keyVal = field.getType().newInstance();
            } else {
                keyVal = (Object) newtravalMap.get(keyStr);
            }
            if (keyVal == null) {
                throw new RuntimeException("查找不到newtravalMap." + keyStr);
            }
            if (field.get(obj) == null) {
                field.set(obj, keyVal);
            }
            setFieldValue(structKey, field.get(obj), newtravalMap, takeTravalMap, checkMap, rootObj);
        }
    }

    /**
     * 遍历List，获取和fieldObj匹配的Object
     * 
     * @param list
     * @param fieldObj
     * @return
     * @throws Exception
     * @author SamJoker
     */
    @SuppressWarnings("rawtypes")
    private static Object getKeyVal(List list, Object fieldObj) throws Exception {
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (Object object : list) {
            List<FieldCacheItem> fieldCacheItems = classTransforKeyCache.get(object.getClass().getName());
            if (fieldCacheItems == null || fieldCacheItems.isEmpty()) {
//                throw new RuntimeException(
//                        object.getClass().getName() + "未含有唯一键注解,请使用注解标记:" + TransforKey.class.getName());
                return null;
            }
            String str1 = "";
            String str2 = "";
            for (FieldCacheItem fieldCacheItem : fieldCacheItems) {
                Field field = fieldCacheItem.getField();
                str1 = str1 + KEY_KEY + field.get(object).toString();
                str2 = str2 + KEY_KEY + field.get(fieldObj).toString();
                if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2)) {
                    throw new RuntimeException(
                            field.getDeclaringClass().getName() + "." + field.getName() + "属性值不能为空!!");
                }
            }
            if (str1.equals(str2)) {
                return object;
            }
        }
        return list.get(0);
    }

    private static String getParentKey(String structKey) {
        String[] strs = structKey.split(STRUCT_KEY_KEY);
        if (strs.length > 1) {
            return structKey.substring(0, structKey.lastIndexOf(STRUCT_KEY_KEY));
        } else {
            return "";
        }
    }

    private static String getSelfKey(String structKey) {
        String[] strs = structKey.split(STRUCT_KEY_KEY);
        if (strs.length > 1) {
            return structKey.substring(structKey.lastIndexOf(STRUCT_KEY_KEY) + STRUCT_KEY_KEY.length(), structKey.length());
        } else {
            return "";
        }
    }
    
    @SuppressWarnings("rawtypes")
    private static boolean isBaseDataType(Class clazz) throws Exception {
        return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
                || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class)
                || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class)
                || clazz.equals(Date.class) || clazz.isPrimitive());
    }
    
    private static String getPatrentStructKey(String structKey) {
        String[] strs = structKey.split(STRUCT_KEY_KEY);
        if (strs.length > 1) {
            return structKey.substring(0, structKey.lastIndexOf(STRUCT_KEY_KEY));
        } else {
            return "root";
        }
    }
    

    private static List<Field> getAllField(Class<?> cls) {
        List<Field> fieldList = classFieldCache.get(cls.getName());
        if (fieldList == null || fieldList.isEmpty()) {
            fieldList = new ArrayList<Field>();
        } else {
            return fieldList;
        }
        while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object")) {// 当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass(); // 得到父类,然后赋给自己
        }
        classFieldCache.put(cls.getName(), fieldList);
        return fieldList;
    }
    
    /**
     * 结构唯一键注解，如果某属性为List而且需要定制唯一时，则List里面的泛型的属性必须有一个结构唯一键注解(若cansame为true则可以重复插入)
     * 
     * @author SamJoker
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface TransforKey {
        public String defaultKey() default "default_null";
    }
    
    /**
     * 属性别名
     * 
     * @author SamJoker
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface OtherKey {
        public String otherKey() default "";
    }
    
    /**
     * 类属性别名前缀
     * 
     * @author SamJoker
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface OtherKeyPrefix {
        public String otherKeyPrefix() default "other_";
    }

    /**
     * 属性编辑类，用以标记各个属性和结构
     * 
     * @author SamJoker
     */
    @Getter
    @Setter
    public static class FieldCacheItem {
        private boolean transforKeyAble = false;
        private boolean superClass = false;
        private String transforKeyDefalut;
        private String structKey;
        private String parentStructKey;
        private Field field;
    }

}
