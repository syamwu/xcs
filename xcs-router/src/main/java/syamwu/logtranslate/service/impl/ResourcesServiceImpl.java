package syamwu.logtranslate.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.util.StringUtils;

import syamwu.logtranslate.config.SpringContextHandler;
import syamwu.logtranslate.exception.ApiNotFoundException;
import syamwu.logtranslate.service.BaseApiService;
import syamwu.logtranslate.service.ResourcesService;
import syamwu.logtranslate.utils.ServletUtils;
import syamwu.logtranslate.vo.Request;

public class ResourcesServiceImpl implements ResourcesService {

    private Properties prop = new Properties();

    public ResourcesServiceImpl(String apiConfig) throws IOException {
        InputStream is = ResourcesServiceImpl.class.getClassLoader().getResourceAsStream(apiConfig);
        try {
            prop.load(is);
        } catch (IOException e) {
            throw new IOException("加载api配置文件:" + apiConfig + "失败", e);
        }
    }

    @Override
    public BaseApiService getApiService(Request request) {
        String apiBeanName = ServletUtils.getUriParams(request.getApiUri(), 2);
        if (StringUtils.isEmpty(apiBeanName)) {
            throw new ApiNotFoundException("uri:" + request.getApiUri() + " can't found api service.");
        }
        BaseApiService apiService = SpringContextHandler.getBean(apiBeanName, BaseApiService.class);
        if (apiService == null)
            throw new ApiNotFoundException("uri:" + request.getApiUri() + " can't found api service.");
        return apiService;
    }

}
