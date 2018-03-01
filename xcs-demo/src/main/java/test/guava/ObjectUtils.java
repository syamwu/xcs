package test.guava;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import test.io.nio.to.request.NioHttpRequest;

public class ObjectUtils {

    public static String toString(Object obj){
        Class cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        ToStringHelper tos = MoreObjects.toStringHelper(cls);
        for (int i = 0; i < fields.length; i++) {
            try {
                tos.add(fields[i].getName(), getValue(fields[i],obj));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return tos.toString();
    }
    
    private static Object getValue(Field field, Object obj) throws IllegalArgumentException, IllegalAccessException{
        field.setAccessible(true);
        if(field.getType() == Double.class){
            return field.getDouble(obj);
        }
        if(field.getType() == Integer.class){
            return field.getDouble(obj);
        }
        if(field.getType() == Float.class){
            return field.getDouble(obj);
        }
        if(field.getType() == Boolean.class){
            return field.getDouble(obj);
        }
        if(field.getType() == Byte.class){
            return field.getByte(obj);
        }
        return field.get(obj);
        
    }
    
    public static void main(String[] args) {
        NioHttpRequest nio = new NioHttpRequest();
        nio.setCharset("hdsa\n");
        System.out.println(toString(nio));
//        try {
//            String str = "好";
//            for (int i = 0; i < str.getBytes("GBK").length; i++) {
//                System.out.println(str.getBytes("GBK")[i]);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
    }
    
}
