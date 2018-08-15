package syamwu.xchushi.fw.common.constant;

public enum HttpContentType {

    TEXTHTML("text/html;","utf-8"),
    FORMURLENCODED("application/x-www-form-urlencoded;","utf-8"),
    APPLICATIONJSON("application/json;","utf-8"),
    ;

    private String type;
    
    private String charset;

    HttpContentType(String type, String charset) {
        this.type = type;
        this.charset = charset;
    }

    public String getContentType(String charset) {
        return this.type + " charset=" + charset;
    }
    
    public String getContentType() {
        return this.type + " charset=" + this.charset;
    }
    
    public String getType() {
        return this.type;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
