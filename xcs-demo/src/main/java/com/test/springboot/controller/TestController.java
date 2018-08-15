package com.test.springboot.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/**")
    @ResponseBody
    public String testApi(HttpServletRequest request, HttpServletResponse response) {
        ServletUtils.printHttpServletRequest(request, true, "UTF-8");
        return "Hello Easylog!";
    }

}
