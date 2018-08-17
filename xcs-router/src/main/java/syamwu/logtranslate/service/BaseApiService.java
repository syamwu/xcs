package syamwu.logtranslate.service;

import syamwu.logtranslate.vo.Request;
import syamwu.logtranslate.vo.Response;

public interface BaseApiService {

    @SuppressWarnings("rawtypes")
    Response invoke(Request request);
    
}
