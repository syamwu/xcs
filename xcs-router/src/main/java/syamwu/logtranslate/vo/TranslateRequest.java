package syamwu.logtranslate.vo;

import javax.servlet.http.HttpServletRequest;

public class TranslateRequest {

    private HttpServletRequest request;
    
    public TranslateRequest(HttpServletRequest request){
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest requestBody) {
        this.request = requestBody;
    }
    
}
