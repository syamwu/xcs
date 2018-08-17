package syamwu.logtranslate.service;

import syamwu.logtranslate.vo.Request;

public interface ResourcesService {

    BaseApiService getApiService(Request request);
    
}
