package test.SSL;

import java.io.File;

public class FileTest {

    
    public static void main(String[] args) {
        
        System.out.println(new File(".g").getAbsolutePath());
        System.out.println(FileTest.class.getResource(".g").getPath());
        System.out.println(new File(FileTest.class.getResource("").getPath()).getAbsolutePath());
        System.out.println(FileTest.class.getClassLoader().getResource("").getPath());
        System.out.println(new File(FileTest.class.getClassLoader().getResource("").getPath()).getAbsolutePath());
    }
}
