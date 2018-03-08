package com.test.springboot.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xchushi.fw.log.XcsLoggerFactory;
import com.xchushi.fw.log.elasticsearch.EsLogger;
import com.xcs.utils.StreamUtils;

@Controller
@RequestMapping("/")
public class TestRestController {

//    @RequestMapping("/testApi")
//    @ResponseBody
//    public String testApi(HttpServletRequest request, HttpServletResponse response) {
//        ServletUtils.printHttpServletRequest(request);
//        System.out.println(this);
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return "Hello World!";
//    }
//    
    
    static Logger logger = LoggerFactory.getLogger(TestRestController.class);
    
     static String[] strs = new String[10];
    
    static{
        for (int i = 0; i < 10; i++) {
            strs[i] = StreamUtils.file2string("D:\\upload\\data"+i+".txt");
        }
    }
    
    @RequestMapping("/**")
    @ResponseBody
    public String testHome(HttpServletRequest request, HttpServletResponse response) {
        //ServletUtils.printHttpServletRequest(request,true);
//        for (int i = 0; i < 10; i++) {
//        try{
//            MDCBus.put("requestSessionId", request.getSession().getId());
//            eslogger.error("232",new Throwable("12323"));
//            eslogger.info(strs[new Random().nextInt(10)]);
//        }finally{
//            MDCBus.remove();
//        }
        MDC.put("sessionId", request.getSession().getId());
        long time = System.currentTimeMillis();
        logger.info(strs[new Random().nextInt(10)]);
        System.out.println(System.currentTimeMillis()-time);
       new Object();
//        }
        return "Hello World!!";
    }
    

}
