package syamwu.logtranslate.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import syamwu.logtranslate.dao.LogAppDao;
import syamwu.logtranslate.dao.LogNodeDao;
import syamwu.logtranslate.entity.LogApp;
import syamwu.logtranslate.entity.LogNode;
import syamwu.logtranslate.entity.LogNodeApp;
import syamwu.logtranslate.translate.impl.KibinaTranslater;
import syamwu.logtranslate.utils.ServletUtils;
import syamwu.logtranslate.vo.Response;
import syamwu.logtranslate.vo.TranslateRequest;
import syamwu.xchushi.fw.common.util.DateUtils;
import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.StringUtil;
import syamwu.xchushi.fw.common.util.UUIDUtils;

@Controller
@RequestMapping("_msearch")
public class EsLogController {

    static Logger logger = LoggerFactory.getLogger(EsLogController.class);

    @Autowired
    private LogAppDao logAppDao;

    @Autowired
    private LogNodeDao logNodeDao;

    @Autowired
    private KibinaTranslater kibinaTranslater;

    @RequestMapping("/**")
    @ResponseBody
    public String index(HttpServletRequest request, HttpServletResponse httpResponse) {
        return kibinaTranslater.translate(new TranslateRequest(request)).getResult();
    }

    @RequestMapping("/put_node")
    @ResponseBody
    public String putNode(HttpServletRequest request, HttpServletResponse httpResponse) {
        Response<String> response = new Response<String>();
        try {
            LogNodeApp logNodeApp = ServletUtils.getParamsByReqeust(request, LogNodeApp.class);
            if (StringUtil.isBank(logNodeApp.getPort()) || StringUtil.isBank(logNodeApp.getHost())) {
                response.setResultCodeAndMessage(Response.FAIL_CODE, "port和host不能为空");
                return JsonUtils.toJSONString(response);
            }
            LogNode logNode = new LogNode();
            logNode.setId(UUIDUtils.getUUID32());
            logNode.setPort(logNodeApp.getPort());
            logNode.setHost(logNodeApp.getHost());
            logNode.setSearchUrl(logNodeApp.getSearchUrl());
            logNode.setCreatedTime(DateUtils.dateToString(new Date()));
            logger.info("insert node:" + JsonUtils.toJSONString(logNode));
            logNodeDao.insert(logNode);
            response.setResult(JsonUtils.toJSONString(logNode));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setResultCodeAndMessage(Response.FAIL_CODE, "system exception:" + e.getMessage());
        } finally {
            logger.info("node insert response:" + JsonUtils.toJSONString(response));
        }
        return JsonUtils.toJSONString(response);
    }

    @RequestMapping("/put_app")
    @ResponseBody
    public String putApp(HttpServletRequest request, HttpServletResponse httpResponse) {
        Response<String> response = new Response<String>();
        try {
            LogNodeApp logNodeApp = ServletUtils.getParamsByReqeust(request, LogNodeApp.class);
            if (StringUtil.isBank(logNodeApp.getNodeId()) || StringUtil.isBank(logNodeApp.getAppCode())
                    || StringUtil.isBank(logNodeApp.getAppName())) {
                response.setResultCodeAndMessage(Response.FAIL_CODE, "nodeId、appCode和appName不能为空");
                return JsonUtils.toJSONString(response);
            }
            LogApp logApp = new LogApp();
            logApp.setId(UUIDUtils.getUUID32());
            logApp.setNodeId(logNodeApp.getNodeId());
            logApp.setAppCode(logNodeApp.getAppCode());
            logApp.setAppName(logNodeApp.getAppName());
            logApp.setAppSearchCode(StringUtil.isBank(logNodeApp.getAppSearchCode()) ? logNodeApp.getAppCode()
                    : logNodeApp.getAppSearchCode());
            logApp.setCreatedTime(DateUtils.dateToString(new Date()));
            logger.info("insert app:" + JsonUtils.toJSONString(logApp));
            logAppDao.insert(logApp);
            response.setResult(JsonUtils.toJSONString(logApp));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setResultCodeAndMessage(Response.FAIL_CODE, "system exception:" + e.getMessage());
        } finally {
            logger.info("app insert response:" + JsonUtils.toJSONString(response));
        }
        return JsonUtils.toJSONString(response);
    }

    @RequestMapping("/put_node_app")
    @ResponseBody
    public String putNodeAndApp(HttpServletRequest request, HttpServletResponse httpResponse) {
        Response<String> response = new Response<String>();
        try {
            LogNodeApp logNodeApp = ServletUtils.getParamsByReqeust(request, LogNodeApp.class);
            if (StringUtil.isBank(logNodeApp.getPort()) || StringUtil.isBank(logNodeApp.getHost())) {
                response.setResultCodeAndMessage(Response.FAIL_CODE, "port和host不能为空");
                return JsonUtils.toJSONString(response);
            }
            if (StringUtil.isBank(logNodeApp.getAppCode()) || StringUtil.isBank(logNodeApp.getAppName())) {
                response.setResultCodeAndMessage(Response.FAIL_CODE, "appCode和appName不能为空");
                return JsonUtils.toJSONString(response);
            }
            LogNode logNode = new LogNode();
            logNode.setId(UUIDUtils.getUUID32());
            logNode.setPort(logNodeApp.getPort());
            logNode.setHost(logNodeApp.getHost());
            logNode.setSearchUrl(logNodeApp.getSearchUrl());
            logNode.setCreatedTime(DateUtils.dateToString(new Date()));
            logger.info("insert node:" + JsonUtils.toJSONString(logNode));
            logNodeDao.insert(logNode);

            LogApp logApp = new LogApp();
            logApp.setId(UUIDUtils.getUUID32());
            logApp.setNodeId(logNode.getId());
            logApp.setAppCode(logNodeApp.getAppCode());
            logApp.setAppName(logNodeApp.getAppName());
            logApp.setAppSearchCode(StringUtil.isBank(logNodeApp.getAppSearchCode()) ? logNodeApp.getAppCode()
                    : logNodeApp.getAppSearchCode());
            logApp.setCreatedTime(DateUtils.dateToString(new Date()));
            logger.info("insert app:" + JsonUtils.toJSONString(logApp));
            logAppDao.insert(logApp);

            logNodeApp.setNodeId(logNode.getId());
            logNodeApp.setId(logApp.getId());
            response.setResult(JsonUtils.toJSONString(logNodeApp));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setResultCodeAndMessage(Response.FAIL_CODE, "system exception:" + e.getMessage());
        } finally {
            logger.info("putNodeAndApp insert response:" + JsonUtils.toJSONString(response));
        }
        return JsonUtils.toJSONString(response);
    }



}
