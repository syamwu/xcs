package syamwu.logtranslate.utils;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.MapUtils;
import syamwu.xchushi.fw.common.util.StreamUtils;
import syamwu.xchushi.fw.common.util.StringUtil;

public class ServletUtils {

    static Logger logger = LoggerFactory.getLogger(ServletUtils.class);
    
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

    public static void printHttpServletRequest(HttpServletRequest request, boolean showBody, String charset) {
        printHttpServletRequest(logger, request, showBody, charset);
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

    public static <T> T getParamsByReqeust(HttpServletRequest request, Class<T> cls) throws Exception {
        String method = request.getMethod().toUpperCase();
        T result = null;
        switch (method) {
        case "GET":
            Map<String, String[]> params = request.getParameterMap();
            logger.info("request params:" + JsonUtils.toJSONString(params));
            result = MapUtils.convertMaplistToObj(params, cls);
            break;
        case "POST":
        case "PUT":
            String body = StreamUtils.inputStream2string(request.getInputStream(), "UTF-8");
            if (StringUtil.isBank(body)) {
                body = MapUtils.convertMaplistToString(request.getParameterMap());
            }
            logger.info("request body:" + body);
            result = JsonUtils.parseObject(body, cls);
            break;
        default:
            break;
        }
        return result;
    }

}
