package com.xchushi.fw.transfer.response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.xchushi.fw.common.constant.StringConstant;
import com.xchushi.fw.common.util.StreamUtils;
import com.xchushi.fw.common.util.StringUtil;

public class HttpClientTransferResponse implements TransferResponse {

    private CloseableHttpResponse response;

    public HttpClientTransferResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    @Override
    public int getResultCode() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public boolean getResponseStatus() {
        return response.getStatusLine().getStatusCode() == 200;
    }

    @Override
    public Object getResponseBody() throws Exception {
        String charset = "";
        Header header = response.getFirstHeader("Content-Type");
        if (header == null) {
            charset = StringConstant.DEFAULT_CHARSET;
        } else {
            charset = getCharset(header.getValue(), StringConstant.DEFAULT_CHARSET);
        }
        return StreamUtils.inputStream2string(response.getEntity().getContent(), charset);
    }

    public static String getCharset(String contentType, String defaultCharset) {
        String result = null;
        if (contentType == null || contentType.trim().equals("")) {
            return defaultCharset;
        }
        String regex = "charset=((.*?;)|.*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(contentType);
        if (m.find()) {
            String regGroup = m.group(0);
            if (regGroup != null && regGroup.indexOf(";") > -1) {
                regGroup = regGroup.substring(0, regGroup.length() - 1);
            }
            result = regGroup.substring(regGroup.indexOf("=") + 1, regGroup.length());
        }
        if (result == null || result.trim().equals("")) {
            result = defaultCharset;
        }
        return result;
    }
}
