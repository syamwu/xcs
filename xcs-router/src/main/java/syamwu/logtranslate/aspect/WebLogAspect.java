package syamwu.logtranslate.aspect;
import javax.servlet.http.HttpServletRequest;

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

@Aspect
@Component
public class WebLogAspect {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* syamwu.logtranslate.controller..*.*(..))")
    public void webLog(){}
    
    @Around("webLog()")
    public void doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        request.getContentType();
        
        // 记录下请求内容
        ServletUtils.printHttpServletRequest(logger, request, false, "UTF-8");
        Object returnValue;
        try {
            returnValue = joinPoint.proceed();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw e;
        }
    }

//    @After("webLog()")
//    public void doAfter(Object ret) throws Throwable {
//        // 处理完请求，返回内容
//        logger.info("RESPONSE : " + JsonUtils.toJSONString(ret));
//    }

}