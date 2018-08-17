package syamwu.logtranslate.aspect;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import syamwu.logtranslate.utils.ServletUtils;
import syamwu.xchushi.fw.common.util.StreamUtils;
import syamwu.xchushi.fw.common.util.UUIDUtils;

@Aspect
@Component
public class WebLogAspect {

    private Logger logger = LoggerFactory.getLogger(getClass());

//    @Pointcut("execution(* syamwu.logtranslate.controller.EsLogController.*(..))")
//    public void webLog(){}
//    
    
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void webLog(){}
    
    @Around("webLog()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String chaerset = request.getCharacterEncoding();
        String sessionId = request.getSession().getId();
        String traceId = UUIDUtils.getUUID32();
        
        MDC.put("session_id", sessionId);
        MDC.put("trace_id", traceId);
        
        // 记录下请求头内容
        printHttpServletRequest(logger, request, false, chaerset);
        
        Object returnValue = null;
        try {
            returnValue = joinPoint.proceed();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw e;
        }
        return returnValue;
    }

    public static void printHttpServletRequest(Logger logger, HttpServletRequest request, boolean showBody, String charset) {
        if (request == null) {
            return;
        }
        try {
            String uri = request.getRequestURI();
            StringBuilder append = new StringBuilder();
            append.append("\n>>>>>>>>>>>" + request.getProtocol() + " " + request.getMethod() + " "
                    + (uri == null ? "" : uri) + ">>>>>>>>>>>\n");
            Enumeration<String> headers = request.getHeaderNames();
            append.append("@url=" + request.getRequestURL() + "\n");
            append.append("@thread=" + Thread.currentThread().getName() + "\n");
            append.append("@headers:" + request.getRequestURL() + "\n");
            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                append.append(key + ":" + request.getHeader(key) + "\n");
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