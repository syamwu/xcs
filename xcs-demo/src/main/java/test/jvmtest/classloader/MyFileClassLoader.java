package test.jvmtest.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyFileClassLoader extends ClassLoader {

    private String classPath;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        MyFileClassLoader fileClsLoader = new MyFileClassLoader();
        fileClsLoader.setClassPath("E:\\work\\xcs-demo");
        fileClsLoader.resolveClass(fileClsLoader.findClass("cn.yunyichina.TestLoader"));
        Class cls = fileClsLoader.loadClass("cn.yunyichina.TestLoader");
        Object obj = cls.newInstance();
        Method[] mthds = cls.getMethods();
        for (Method mthd : mthds) {
            String methodName = mthd.getName();
            System.out.println("mthd.name=" + methodName);
            if(methodName.equals("run")){
                mthd.invoke(obj, new Object[]{});
            }
        }
        //TestInterfaces tt = (TestInterfaces) obj;
        //cls.getFields();
        System.out.println("obj.class=" + obj.getClass().getName());
        System.out.println("obj.class=" + cls.getClassLoader().toString());
        System.out.println("obj.class=" + cls.getClassLoader().getParent().toString());
        Class cls1 = fileClsLoader.loadClass("java.lang.String");
    }

    /**
     * 根据类名字符串从指定的目录查找类，并返回类对象
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = null;
        try {
            //classData = loadClassData(name);
            System.out.println("MyFileClassLoader寻找类:" + name);
            File file = getFile(name);
            FileInputStream fis = new FileInputStream(file);
            classData = new byte[(int) file.length()];
            fis.read(classData);
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
//    private byte[] loadClassData(String name) throws IOException {
//        File file = getFile(name);
//        FileInputStream fis = new FileInputStream(file);
//        byte[] arrData = new byte[(int) file.length()];
//        fis.read(arrData);
//        return arrData;
//    }

    /**
     * 根据类名字符串返回一个 File 对象
     * 
     * @param name
     *            类名字符串
     * @return File 对象
     * @throws FileNotFoundException
     */
    private File getFile(String name) throws FileNotFoundException {
        File dir = new File(classPath);
        if (!dir.exists())
            throw new FileNotFoundException(classPath + " 目录不存在！");
        String _classPath = classPath.replaceAll("[\\\\]", "/");
        int offset = _classPath.lastIndexOf("/");
        name = name.replaceAll("[.]", "/");
        if (offset != -1 && offset < _classPath.length() - 1) {
            _classPath += "/";
        }
        _classPath += name + ".class";
        dir = new File(_classPath);
        if (!dir.exists())
            throw new FileNotFoundException(dir + " 不存在！");
        return dir;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
    this.classPath = classPath;
    }
}
