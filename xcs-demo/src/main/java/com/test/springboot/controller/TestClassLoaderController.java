package com.test.springboot.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.springboot.utils.ServletUtils;
import com.xcs.utils.StreamUtils;

import tools.classloader.ByteClassLoader;

@Controller
public class TestClassLoaderController {

    @RequestMapping("/testclsloader")
    @ResponseBody
    public String testApi(HttpServletRequest request, HttpServletResponse response) {
        ServletUtils.printHttpServletRequest(request, false);
        try {
            Enumeration<String> headers = request.getHeaderNames();
            String clsName = "";
            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                String value = request.getHeader(key);
                if("clsname".equalsIgnoreCase(key)){
                    clsName = value;
                    break;
                }
            }
            if(clsName==null){
                return "类名为空，加载失败....";
            }
            byte[] bytes = StreamUtils.input2byte(request.getInputStream());
            //System.out.println(new String(bytes, "UTF-8"));
            try {
                Class cls = ByteClassLoader.loaderClass(clsName, bytes);
                System.out.println(cls.getClassLoader());
                System.out.println(cls.getClassLoader().getParent());
                Method[] mthds = cls.getMethods();
                for (Method mthd : mthds) {
                    String methodName = mthd.getName();
                    System.out.println("mthd.name=" + methodName);
                    if(methodName.equals("run")){
                        Object obj = cls.newInstance();
                        System.out.println(obj.getClass());
                        mthd.invoke(obj, new Object[]{});
                    }
                }
                return cls.getName()+"加载成功";
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Hello World!";
    }
    
}
