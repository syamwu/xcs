package xcs.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
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
     * @param xml
     * @param url
     * @return
     * @throws IOException
     * @author SamJoker
     */
    public static CloseableHttpResponse sendRequest(String xml, String url, String charset, int timeOut)
            throws IOException {
        Header[] headers = { new BasicHeader("Content-Type", "text/plain"), new BasicHeader("Accept", "text/plain"),
                new BasicHeader("Content-Encoding", "gzip") };
        ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
        originalContent.write(xml.getBytes(charset));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        originalContent.writeTo(gzipOut);
        gzipOut.finish();
        ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
        return sendRequest(xml, url, headers, entity, timeOut);
    }

    protected static CloseableHttpResponse sendRequest(String xml, String url, Header[] headers, HttpEntity entity,
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
