package syamwu.logtranslate.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import syamwu.logtranslate.service.ResourcesService;
import syamwu.logtranslate.utils.ServletUtils;
import syamwu.logtranslate.vo.Request;
import syamwu.logtranslate.vo.RequestMethod;
import syamwu.logtranslate.vo.Response;
import syamwu.xchushi.fw.common.util.JsonUtils;

@Controller
@RequestMapping("rest")
public class ResourceController {

    @Autowired
    private ResourcesService resourcesService;

    static Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @SuppressWarnings("rawtypes")
    @RequestMapping("/**")
    public ResponseEntity<Response> resource(HttpServletRequest request, HttpServletResponse httpResponse) {
        Response response = null;
        try {
            Request apiRequest = new Request().setApiUri(request.getRequestURI())
                    .setMethod(getMethod(request.getMethod()))
                    .setRequestParams(ServletUtils.getParamsByReqeust(request))
                    .setResourceId(ServletUtils.getUriParams(request.getRequestURI(), 3));
            response = resourcesService.getApiService(apiRequest).invoke(apiRequest);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (response == null) {
                response = new Response();
            }
            response.setResultCodeAndMessage(Response.FAIL_CODE, "system exception:" + e.getMessage());
        } finally {
            logger.info("setting response->" + JsonUtils.toJSONString(response));
        }
        ResponseEntity<Response> result = new ResponseEntity<>(response, HttpStatus.valueOf(response.getResultCode()));
        return result;
    }

    private RequestMethod getMethod(String method) {
        return RequestMethod.valueOf(method);
    }
}
