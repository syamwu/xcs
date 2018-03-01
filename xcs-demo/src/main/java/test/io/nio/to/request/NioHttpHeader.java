package test.io.nio.to.request;

import java.io.UnsupportedEncodingException;

public class NioHttpHeader extends NioHeader {

    private String method;
    private String charset;
    private int contentLength;

    public static NioHttpHeader initNioHeaderNode(byte[] bytes) throws UnsupportedEncodingException {
        return initNioHeaderNode(new String(bytes, "ASCII"));
    }

    public static NioHttpHeader initNioHeaderNode(String headStr) {
        NioHttpHeader nh = new NioHttpHeader();
        String[] strs = headStr.split("\r\n");
        for (int i = 0; i < strs.length; i++) {
            nh.parseHttp(strs[i]);
        }
        return nh;
    }

    public void parseHttp(String str) {
        if (str.startsWith("GET")) {
            this.method = "GET";
        } else if (str.startsWith("POST")) {
            this.method = "POST";
        } else if (str.startsWith("Content-Length:")) {
            //this.contentLength = Integer.valueOf(str.substring("Content-Length:".length() + 1));
        }
    }

}
