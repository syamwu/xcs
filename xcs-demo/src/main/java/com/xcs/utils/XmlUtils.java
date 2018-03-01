package com.xcs.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtils {

    @SuppressWarnings({ "rawtypes" })
    private static Element buildXMLWithTextElement(Object obj, String root, Element rootEle) throws Exception {
        if (StringUtils.isBlank(root)) {
            throw new NullPointerException("root can't empty");
        }
        Element xml = null;
        if (rootEle == null) {
            xml = DocumentHelper.createElement(root);
        } else {
            xml = rootEle;
        }
        if (obj == null) {
            xml.addText("");
        } else if (isBaseDataType(obj.getClass())) {
            xml.addText(obj.toString());
        } else if (Map.class.isAssignableFrom(obj.getClass())) {
            // map类型转map逻辑
            buildXMLWithTextElement((Map) obj, root, xml);
        } else if (List.class.isAssignableFrom(obj.getClass())) {
            // List类型逻辑
            for (Object row : (List) obj) {
                xml.add(buildXMLWithTextElement(row, root, null));
            }
        } else {
            List<Field> fieldList = getAllField(obj.getClass());
            for (Field field : fieldList) {
                field.setAccessible(true);
                if (List.class.isAssignableFrom(field.getType())) {
                    buildXMLWithTextElement(field.get(obj), field.getName(), xml);
                } else {
                    xml.add(buildXMLWithTextElement(field.get(obj), field.getName(), null));
                }
            }
        }
        return xml;
    }

    private static Element buildXMLWithTextElement(Map<?, ?> params, String root) throws Exception {
        return buildXMLWithTextElement(params, root, null);
    }

    @SuppressWarnings("rawtypes")
    private static Element buildXMLWithTextElement(Map<?, ?> params, String root, Element rootEle) throws Exception {
        if (StringUtils.isBlank(root)) {
            throw new NullPointerException("root can't empty");
        }
        Element xml = null;
        if (rootEle == null) {
            xml = DocumentHelper.createElement(root);
        } else {
            xml = rootEle;
        }
        for (Map.Entry<?, ?> entry : params.entrySet()) {
            String key = entry.getKey() == null ? "null" : entry.getKey().toString();
            Object val = entry.getValue();
            if (val == null) {
                xml.addText("");
            } else if (isBaseDataType(val.getClass())) {
                xml.addElement(key).addText((String) val);
            } else if (Map.class.isAssignableFrom(val.getClass())) {
                xml.add(buildXMLWithTextElement((Map) val, key));
            } else if (List.class.isAssignableFrom(val.getClass())) {
                buildXMLWithTextElement((List) val, key, xml);
            } else {
                xml.add(buildXMLWithTextElement(val, key, null));
            }
        }
        return xml;
    }

    /**
     * Object转xml字符串
     * 
     * @param obj
     * @param root
     *            根节点名
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @author SamJoker
     */
    public static String buildXMLWithTextElementString(Object obj, String root) throws Exception {
        return buildXMLWithTextElement(obj, root, null).asXML();
    }

    /**
     * xml字符串转map
     * 
     * @param str
     * @param keyName
     * @param map
     * @return
     * @throws Exception
     * @author SamJoker
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map xmlStringToMap(String str) throws Exception {
        Element root = null;
        Map map = new HashMap();
        Document doc = DocumentHelper.parseText(str);
        root = doc.getRootElement();
        Iterator<Element> it = root.elementIterator();
        while (it.hasNext()) {
            Element element = it.next();
            String kn = element.getName();
            map.put(kn, xmlStringToMap(element, kn, null));
        }
        return map;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object xmlStringToMap(Element element, String keyName, Map map) throws Exception {
        if (element.isTextOnly()) {
            return element.getText();
        }
        if (map == null) {
            map = new HashMap();
        }
        Iterator<Element> it = element.elementIterator();
        while (it.hasNext()) {
            Element row = it.next();
            String kn = row.getName();
            List<Element> list = element.elements(kn);
            if (list != null && list.size() > 1) {
                List mapList = new ArrayList<>();
                for (Element elerow : list) {
                    mapList.add(xmlStringToMap(elerow, null, null));
                }
                map.put(kn, mapList);
            } else {
                map.put(kn, xmlStringToMap(row, kn, null));
            }
        }
        return map;

    }

    public static void main(String[] args) {
        String xml = "<pp><function_id>bizh200102</function_id><akb020>002002</akb020><aaz218>002001160906098257</aaz218><aab299>441800</aab299><yab600>441800</yab600><akb026>111111</akb026><akb021>中山大学附属第一医院</akb021><aab301>441800</aab301><yab060></yab060><aac002>1234567890</aac002><aac043>1</aac043><aac044>1234567890</aac044><yzy003>1</yzy003><detail><row><yzy201>1</yzy201><yzy202>诊断类型</yzy202><akc185>疾病名称</akc185><akc196>ICD码</akc196><yzy205>IS0001</yzy205><yzy206>入院病情</yzy206></row><row><yzy201>1</yzy201><yzy202>诊断类型</yzy202><akc185>疾病名称</akc185><akc196>ICD码</akc196><yzy205>IS0001</yzy205><yzy206>入院病情</yzy206></row></detail><session_id>cd1e4bef-d498-41f7-b921-877b915eb8bf</session_id></pp>";
        try {
            System.out.println(xml);
            Object map = xmlStringToMap(xml);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将xml字符串转为指定类，该类必须有形参数为0的构造函数
     * 
     * @param str
     * @param cls
     * @return
     * @throws Exception
     * @author SamJoker
     */
    public static <T> T xmlStringToObj(String str, Class<T> cls) throws Exception {
        return xmlStringToObj(str, cls, cls.newInstance());
    }

    /**
     * 将xml字符串转为obj
     * 
     * @param str
     * @param cls
     * @param obj
     * @return
     * @throws Exception
     * @author SamJoker
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> T xmlStringToObj(String str, Class cls, T obj) throws Exception {
        Element root = DocumentHelper.parseText(str).getRootElement();
        Iterator<Element> it = root.elementIterator();
        while (it.hasNext()) {
            Element element = it.next();
            String name = element.getName();
            Map<String, Field> fieldMap = getAllFieldMap(cls);
            Field field = fieldMap.get(name);
            if (field == null) {
            } else {
                if (obj == null) {
                    obj = (T) cls.newInstance();
                }
                field.setAccessible(true);
                if (isBaseDataType(field.getType())) {
                    field.set(obj, element.getText());
                } else if (List.class.isAssignableFrom(field.getType())) {
                    Type type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                    } else {
                        throw new RuntimeException(cls.getName() + "." + field.getName() + "属性未指定泛型");
                    }
                    ParameterizedType pt = (ParameterizedType) type;
                    Type[] types = pt.getActualTypeArguments();
                    List<Element> eles = root.elements(field.getName());
                    List fieldObj = (List) field.get(obj);
                    for (Element ele : eles) {
                        if (fieldObj == null) {
                            fieldObj = new ArrayList<>();
                            field.set(obj, fieldObj);
                        }
                        Class arrayObjCls = (Class) types[0];
                        Object arrayObj = arrayObjCls.newInstance();
                        fieldObj.add(arrayObj);
                        xmlStringToObj(ele.asXML(), arrayObjCls, arrayObj);
                    }
                } else {
                    Object fieldObj = field.get(obj);
                    if (fieldObj == null) {
                        fieldObj = field.getType().newInstance();
                        field.set(obj, fieldObj);
                    }
                    xmlStringToObj(element.asXML(), field.getType(), fieldObj);
                }
            }
        }
        return obj;
    }

    private static List<Field> getAllField(Class<?> cls) {
        List<Field> fieldList = new ArrayList<Field>();
        while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object")) {// 当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass(); // 得到父类,然后赋给自己
        }
        return fieldList;
    }

    private static Map<String, Field> getAllFieldMap(Class<?> cls) {
        List<Field> fieldList = new ArrayList<Field>();
        while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object")) {// 当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass(); // 得到父类,然后赋给自己
        }
        Map<String, Field> map = new LinkedHashMap<String, Field>();
        for (Field field : fieldList) {
            map.put(field.getName(), field);
        }
        return map;
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
