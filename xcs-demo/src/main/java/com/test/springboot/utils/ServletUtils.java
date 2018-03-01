package com.test.springboot.utils;

import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.xcs.utils.StreamUtils;

public class ServletUtils {

    public static void printHttpServletRequest(HttpServletRequest request, boolean showBody) {
        if (request == null) {
            return;
        }
        try {
            String uri = request.getRequestURI();
            String sessionId = request.getSession().getId();
            System.out.println(">>>>>>>>>>>" + (uri == null ? "" : uri) + ":" + sessionId + ">>>>>>>>>>>");
            Enumeration<String> headers = request.getHeaderNames();
            System.out.println(request.getProtocol() + " " + request.getMethod());
            System.out.println("Thread:" + Thread.currentThread().getName());
            System.out.println("URL:" + request.getRequestURL());
            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                System.out.println(key + ":" + request.getHeader(key));
            }
            if (showBody) {
                printHttpServletRequestBody(request);
            }
            System.out.println("<<<<<<<<<<<" + (uri == null ? "" : uri) + ":" + sessionId + "<<<<<<<<<<<");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] printHttpServletRequestBody(HttpServletRequest request) {
        try {
            InputStream in = request.getInputStream();
            byte[] byes = StreamUtils.input2byte(in);
            System.out.println(">>>>>>>>>>>>>>content:");
            System.out.println(new String(byes, "ISO-8859-1"));
            return byes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
