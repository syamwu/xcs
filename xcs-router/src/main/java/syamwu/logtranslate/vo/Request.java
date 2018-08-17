package syamwu.logtranslate.vo;

import java.util.Map;

public class Request {

    private String apiUri;

    private RequestMethod method;

    private Map<String, Object> requestParams;
    
    private String resourceId;

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public Request setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public String getApiUri() {
        return apiUri;
    }

    public Request setApiUri(String apiUri) {
        this.apiUri = apiUri;
        return this;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Request setMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Request setResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }
    
}
