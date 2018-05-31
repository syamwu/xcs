package syamwu.xchushi.fw.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

public class HttpClientUtils {

    protected static final int TIME_OUT = 30_000;
    protected static final int MAX_LOG_LENGTH = 2000;

    protected static RequestConfig config = null;

    /**
     * 发送http请求并进行gzip压缩
     * 
     * @param content
     * @param url
     * @return
     * @throws IOException
     * @author syam_wu
     */
    public static CloseableHttpResponse sendRequest(String content, String url, String charset, int timeOut, boolean gzip)
            throws IOException {
        if (gzip) {
            Header[] headers = { new BasicHeader("Content-Type", "text/plain"),
                    new BasicHeader("Content-Encoding", "gzip") };
            ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
            originalContent.write(content.getBytes(charset));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            originalContent.writeTo(gzipOut);
            gzipOut.finish();
            ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
            return sendRequest(content, url, headers, entity, timeOut);
        } else {
            Header[] headers = { new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),new BasicHeader("Accept", "*/*") };
            StringEntity entity = new StringEntity(content, charset);
            return sendRequest(content, url, headers, entity, timeOut);
        }
        
    }

    protected static CloseableHttpResponse sendRequest(String content, String url, Header[] headers, HttpEntity entity,
            int timeOut) throws IOException {
        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(entity);
        if (config == null) {
            config = RequestConfig.custom().setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut)
                    .setSocketTimeout(timeOut).build();
        }
        httpPost.setConfig(config);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        return response;
    }

}
