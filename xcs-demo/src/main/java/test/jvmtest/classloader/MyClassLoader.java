package test.jvmtest.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.xcs.utils.StreamUtils;

public class MyClassLoader extends ClassLoader {
//    @Override
//    public Class<?> loadClass(String filePath) throws ClassNotFoundException {
//        try {
//            InputStream is = new FileInputStream(filePath);
//            byte[] b = new byte[is.available()];
//            is.read(b);
//            String clsName = "com.loggertest.LogTest";
//            return defineClass(clsName, b, 0, b.length);
//        } catch (IOException e) {
//            throw new ClassNotFoundException();
//        }
//    }
    
    public static void main(String[] args) {
        //E:\yunyigit\front_guangzhongyiyao\target\classes\cn\yunyichina
        MyClassLoader myLoader = new MyClassLoader();
        try {
            System.out.println(myLoader.getClass().getClassLoader());
            System.out.println(myLoader.loadClass("test.jvmtest.classloader.ClsLoaderTest"));
            //Object obj = myLoader.loadClass("E:\\work\\xcs-demo\\target\\classes\\com\\loggertest\\LogTest.class");
            //System.out.println(obj);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = null;
        try {
            classData = loadClassData(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.defineClass(name, classData, 0, classData.length);
    }

    /**
     * 根据类名字符串加载类 byte 数据流
     * 
     * @param name
     *            类名字符串 例如： com.cmw.entity.SysEntity
     * @return 返回类文件 byte 流数据
     * @throws IOException
     */
    private byte[] loadClassData(String name) throws IOException {
        byte[] arrData = StreamUtils.file2byte("");
        return arrData;
    }
    
}
