package test.io.nio.to.response;

import java.nio.charset.Charset;
import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;
import test.io.nio.to.HttpBodyType;
import test.io.nio.to.HttpHeaderNode;

public class NioHttpResponse extends NioResponse{

    private String header;
    
    private HttpBodyType httpBodyType;

    private Object body;
    
    private String charsetName;
    
    public NioHttpResponse(String header, Object body){
        this.header = header;
        this.body = body;
    }
    
    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public HttpBodyType getHttpBodyType() {
        return httpBodyType;
    }

    public void setHttpBodyType(HttpBodyType httpBodyType) {
        this.httpBodyType = httpBodyType;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
    
    public static NioHttpResponse initNioHttpResponse(Object obj,HttpHeaderNode... headerNodes){
        String header = response2Header(createResponseHeader(headerNodes));
        return new NioHttpResponse(header, obj);
    }
    
    public static FullHttpResponse createResponseHeader(HttpHeaderNode... headerNodes){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("", Charset.forName("UTF-8")));
        for (int i = 0; i < headerNodes.length; i++) {
            response.headers().set(headerNodes[i].getNodeName(), headerNodes[i].getNodeValue());
        }
        return response;
    }
    
    public static String response2Header(FullHttpResponse fr){
        StringBuffer result = new StringBuffer();
        DefaultFullHttpResponse hh = (DefaultFullHttpResponse)fr;
        result.append(hh.protocolVersion());
        result.append(' ');
        result.append(hh.status());
        result.append(StringUtil.NEWLINE);
        for (Map.Entry<String, String> e: hh.headers()) {
            result.append(e.getKey());
            result.append(": ");
            result.append(e.getValue());
            result.append(StringUtil.NEWLINE);
        }
        result.append(StringUtil.NEWLINE);
        return result.toString();
    }
    
}
