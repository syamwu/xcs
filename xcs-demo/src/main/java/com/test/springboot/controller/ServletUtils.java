package com.test.springboot.controller;

import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import syamwu.xchushi.fw.common.util.StreamUtils;

public class ServletUtils {
    
    static Logger logger = LoggerFactory.getLogger(ServletUtils.class);

    public static void printHttpServletRequest(HttpServletRequest request, boolean showBody, String charset) {
        if (request == null) {
            return;
        }
        try {
            String uri = request.getRequestURI();
            StringBuilder append = new StringBuilder();
            append.append("\n>>>>>>>>>>>"+request.getProtocol() + " " + request.getMethod() +" "+ (uri == null ? "" : uri) + ">>>>>>>>>>>\n");
            Enumeration<String> headers = request.getHeaderNames();
            append.append("@url=" + request.getRequestURL()+"\n");
            append.append("@thread=" + Thread.currentThread().getName()+"\n");
            append.append("@headers:" + request.getRequestURL()+"\n");
            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                append.append(key + ":" + request.getHeader(key)+"\n");
            }
            if (showBody) {
                append.append("@body:\n" + new String(printHttpServletRequestBody(request), charset));
            }
            append.append("\n<<<<<<<<<<<" + (uri == null ? "" : uri) + "<<<<<<<<<<<");
            logger.info(append.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static byte[] printHttpServletRequestBody(HttpServletRequest request) {
        try {
            InputStream in = request.getInputStream();
            byte[] byes = StreamUtils.input2byte(in);
            return byes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
