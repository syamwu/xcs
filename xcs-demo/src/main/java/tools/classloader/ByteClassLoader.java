package tools.classloader;

import java.util.HashMap;
import java.util.Map;

public class ByteClassLoader extends ClassLoader {

    private final static Map<String, ClassBytes> CLASSBYTESMAP = new HashMap<>();
    
    private static ByteClassLoader byteClassLoader = null;
    
    private static ByteClassLoader instance(){
        if(byteClassLoader == null){
            return new ByteClassLoader();
        }
        return byteClassLoader;
    }

    public static Class loaderClass(String name) throws ClassNotFoundException{
        return instance().loadClass(name);
    }
    
    public static Class loaderClass(String name, byte[] bytes) throws ClassNotFoundException{
        putBytes(name, bytes);
        return instance().loadClass(name);
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = null;
        System.out.println("ByteClassLoader正在寻找类:" + name);
        classData = getClassBytes(name);
        Class c = super.defineClass(name, classData, 0, classData.length);
        resolveClass(c);
        return c;
    }

    public byte[] getClassBytes(String name) {
        ClassBytes cassBytes = CLASSBYTESMAP.get(name);
        if(cassBytes == null){
            throw new NullPointerException(name+"类还没被加载");
        }
        return cassBytes.getBytes();
    }

    public static void putBytes(String clsName, byte[] bytes) {
        CLASSBYTESMAP.put(clsName, new ClassBytes(clsName, bytes));
    }

    private static class ClassBytes {
        private String name;
        private byte[] bytes;

        ClassBytes(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }

        public String getName() {
            return name;
        }

        public byte[] getBytes() {
            return bytes;
        }

    }

}