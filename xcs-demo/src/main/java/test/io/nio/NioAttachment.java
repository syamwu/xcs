package test.io.nio;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import com.xcs.utils.StreamUtils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import test.io.nio.to.request.NioHttpHeader;
import test.io.nio.to.request.NioHttpRequest;

public class NioAttachment {
    
    public boolean canr = true;
    
    private boolean canw = false;

    private String result = "";

    private long connetIndex = 0;
    
    private StringBuilder strBuilder = new StringBuilder();
    
    private byte[] bytebuffers;
    
    private boolean headerReady = false;
    
    private boolean badRequest = false;
    
    private NioHttpRequest nhr;
    
    public NioAttachment(long count) {
        this.connetIndex = count;
    }
    
    public byte[] getBytebuffers() {
        return bytebuffers;
    }


    public void setBytebuffers(byte[] bytebuffers) {
        this.bytebuffers = bytebuffers;
    }


    public StringBuilder getStrBuilder() {
        return strBuilder;
    }

    public void setStrBuilder(StringBuilder strBuilder) {
        this.strBuilder = strBuilder;
    }

    public long getConnetIndex() {
        return connetIndex;
    }

    public void setConnetIndex(long connetIndex) {
        this.connetIndex = connetIndex;
    }

    public boolean isCanw() {
        return canw;
    }

    public void setCanw(boolean canw) {
        this.canw = canw;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    public boolean checkHeader(){
        if(headerReady){
            return headerReady;
        }else{
            if(badRequest){
                throw new RuntimeException("错误请求头");
            }
            return false;
        }
    }
    
    public void initResponse(){
        FullHttpResponse response = createResponse(this.connetIndex);
        this.setResult(response2Str(response));
        this.setCanw(true);
    }
    
    public static FullHttpResponse createResponse(long count){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK , Unpooled.copiedBuffer(
                "I'm the " + count + "th request", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        return response;
    }
    
    public static String response2Str(FullHttpResponse fr){
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
        byte[] by = new byte[hh.content().readableBytes()];
        hh.content().readBytes(by);
        result.append(new String(by,Charset.forName("UTF-8")));
        return result.toString();
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
    
    public void initResponse(String fileName) throws IOException{
        FullHttpResponse response = createResponse(fileName);
        this.setResult(response2Header(response));
        this.bytebuffers = response.content().array();
        this.setCanw(true);
    }
    
    public static FullHttpResponse createResponse(String fileName) throws IOException{
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK , Unpooled.copiedBuffer(StreamUtils.file2byte(fileName)));
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        return response;
    }
    
    public static FullHttpResponse createResponseHeader(NioHttpHeader... headerNodes) throws IOException{
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, null);
        for (int i = 0; i < headerNodes.length; i++) {
           // response.headers().set(headerNodes[i].getNodeName(), headerNodes[i].getNodeValue());
        }
        return response;
    }

}
