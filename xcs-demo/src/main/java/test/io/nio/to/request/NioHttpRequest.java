package test.io.nio.to.request;

import test.io.nio.to.HttpBodyType;
import test.io.nio.to.HttpHeaderNode;
import test.io.nio.to.response.NioHttpResponse;
import test.io.nio.to.response.NioResponse;

public class NioHttpRequest extends NioRequest {

    private String method;
    private String charset;
    private int contentLength;

    private NioHttpHeader nioHeaderNode;

    private byte[] bodyBytes;

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    public NioHttpHeader getNioHeaderNode() {
        return nioHeaderNode;
    }

    public void setNioHeaderNode(NioHttpHeader nioHeaderNode) {
        this.nioHeaderNode = nioHeaderNode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }


    public static NioHttpRequest initHttpRequest(NioHttpHeader nioHeaderNode) {
        NioHttpRequest nhr = new NioHttpRequest();
        nhr.setNioHeaderNode(nioHeaderNode);
        return nhr;
    }

    @Override
    public NioResponse doRequest(NioRequest nioRequest) {
        HttpHeaderNode hhn = new HttpHeaderNode( "Content-Type", "application/json;charset=UTF-8");
        NioHttpResponse nioResponse = NioHttpResponse.initNioHttpResponse("hello 欢迎请求我的NIO", hhn);
        nioResponse.setHttpBodyType(HttpBodyType.TEXT);
        nioResponse.setCharsetName("UTF-8");
        return nioResponse;
    }

}
