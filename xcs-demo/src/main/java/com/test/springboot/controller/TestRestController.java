package com.test.springboot.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.springboot.utils.ServletUtils;
import com.xcs.utils.StreamUtils;

import xcs.log.elasticsearch.EsLogger;
import xcs.log.elasticsearch.EsLoggerFactory;
import xcs.log.elasticsearch.MDCBus;

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
    
    static EsLogger eslogger = EsLoggerFactory.getLogger(TestRestController.class);
    
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
        try{
            MDCBus.put("requestSessionId", request.getSession().getId());
            eslogger.error("232",new Throwable("12323"));
            eslogger.info(strs[new Random().nextInt(10)]);
        }finally{
            MDCBus.remove();
        }
//        }
        return "Hello World!!";
    }
    

}
