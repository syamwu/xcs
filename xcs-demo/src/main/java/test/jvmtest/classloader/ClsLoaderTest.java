package test.jvmtest.classloader;

import java.net.URL;

import com.loggertest.LogTest;

public class ClsLoaderTest{
    public static void main(String[] args) {
        URL url = LogTest.class.getClassLoader().getResource("com/xcs/utils/TestOCR.class");
        System.out.println(url.getPath());
        try {
            System.out.println(LogTest.class.getClassLoader().loadClass("com.xcs.utils.TestOCR"));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(LogTest.class.getClassLoader());
    }
}

