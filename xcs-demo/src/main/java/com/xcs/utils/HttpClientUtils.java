package com.xcs.utils;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

public class HttpClientUtils {

    protected final int TIME_OUT = 30_000;
    protected final int MAX_LOG_LENGTH = 2000;

    protected final RequestConfig config = RequestConfig.custom().setConnectTimeout(TIME_OUT)
            .setConnectionRequestTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();

    protected CloseableHttpResponse sendRequest(String xml, String url) throws IOException {
        Header[] headers = { new BasicHeader("Content-Type", "text/xml;charset=UTF-8") };
        StringEntity entity = new StringEntity(xml, "UTF-8");
        return sendRequest(xml, url, headers, entity);
    }

    protected CloseableHttpResponse sendRequest(String xml, String url, Header[] headers, HttpEntity entity)
            throws IOException {
        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
        // TODO 这里其实不用每次都创建一个新的httpClient,可以使用PoolingHttpClientConnectionManager
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(entity);
        httpPost.setConfig(config);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        return response;
    }

}
