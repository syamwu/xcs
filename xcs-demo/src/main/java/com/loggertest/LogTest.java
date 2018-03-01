  package com.loggertest;

import java.net.URL;

public class LogTest {
//      public static void main(String[] args) {
//         Logger logger = LoggerFactory.getLogger(LogTest.class);
//         logger.info("hello {}",new Date());
//         logger.info("222");
//     }
//    public static void main(String[] args) {
//
//        List<Object> cases = new ArrayList<Object>();
//
//        while(true){
//
//            cases.add(new Object());
//
//        }
//
//  
//
//     }
    
    public static void main(String[] args) {
        URL url = LogTest.class.getClassLoader().getResource("com/xcs/utils/TestOCR.class");
        System.out.println(url.getPath());
        try {
            System.out.println(LogTest.class.getClassLoader().loadClass("com.xcs.utils.TestOCR"));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(LogTest.class.getClassLoader().getClass().getClassLoader());
        
    }
 } 