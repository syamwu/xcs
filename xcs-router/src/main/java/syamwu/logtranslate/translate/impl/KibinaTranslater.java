package syamwu.logtranslate.translate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import syamwu.logtranslate.dao.LogAppDao;
import syamwu.logtranslate.entity.LogNodeApp;
import syamwu.logtranslate.translate.LogTranslate;
import syamwu.logtranslate.vo.TranslateRequest;
import syamwu.logtranslate.vo.TranslateResponse;
import syamwu.xchushi.fw.common.util.JsonUtils;
import syamwu.xchushi.fw.common.util.MapUtils;
import syamwu.xchushi.fw.common.util.StreamUtils;
import syamwu.xchushi.fw.log.SysLoggerFactory;

@Service
public class KibinaTranslater implements LogTranslate {

    private static Logger logger = SysLoggerFactory.getLogger(KibinaTranslater.class);

    @Autowired
    private LogAppDao logAppDao;

    @Autowired
    private Environment env;

    /**
     * 解析来自kibana的查询请求，并进行路由转发
     * 
     * @param request
     * @return
     * @author syam_wu
     */
    @SuppressWarnings("rawtypes")
    @Override
    public TranslateResponse translate(TranslateRequest request) {
        String method = "【translate】";
        HttpServletRequest httpRequest = request.getRequest();
        try {
            String body = StreamUtils.inputStream2string(httpRequest.getInputStream());
            String[] searchs = body.split("\n");
            List<String> appCodes = new ArrayList<>();
            String storeyKey = env.getProperty("storey.key");
            for (String str : searchs) {
                Map map = JsonUtils.parseObject(str, Map.class);
                List<Object> keys = MapUtils.getFieldValueByStoreyKey(map, storeyKey);
                if (keys != null && !keys.isEmpty()) {
                    for (Object object : keys) {
                        if (object instanceof String) {
                            appCodes.add((String) object);
                        } else {
                            logger.warn(method + "【String cast warning】storeyKey:" + storeyKey + ",Object:"
                                    + JsonUtils.toJSONString(object));
                        }
                    }
                }
            }
            if (!appCodes.isEmpty()) {
                List<LogNodeApp> selectNode = logAppDao.selectNodeByAppCodes(appCodes);
                logger.info(JsonUtils.toJSONString(selectNode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TranslateResponse translateResponse = new TranslateResponse();
        translateResponse.setResult("hell");
        return translateResponse;
    }

}
